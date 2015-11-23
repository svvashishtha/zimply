package com.application.zimplyshop.managers;

import android.content.Context;
import android.os.AsyncTask;

import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.extras.ParserClass;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.ResponseCacheManager;
import com.application.zimplyshop.utils.ResponseQuery;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class GetRequestManager {

    private static volatile GetRequestManager sInstance;

    Context mContext;

    ArrayList<GetRequestListener> callbacks = new ArrayList<GetRequestListener>();

    /**
     * Request caching
     */
    public static final String QUERYRESTAURANTLIST = "QueryArrayList";
    public static final String USER = "UserData";
    public static final String CITYDETAILLIST = "CITYDETAIL";
    public static final String RESTAURANT = "Restaurant";
    public static ResponseCacheManager helper;
    /**
     * Time durations for request caching
     */
    public static final int FAV = -1;
    public static final int TEMP = 86400;
    public static final int CONSTANT = 1209600;
    public static final int ONE_HOUR = 3600;
    public static final int THREE_HOURS = 3600 * 3;
    public static final int THREE_DAYS = 3600 * 24 * 3;


    private GetRequestManager() {

    }

    public static GetRequestManager getInstance() {

        if (sInstance == null) {
            synchronized (GetRequestManager.class) {
                if (sInstance == null) {
                    sInstance = new GetRequestManager();
                }
            }
        }
        return sInstance;
    }

    public void setContext(Context context) {
        mContext = context;
        helper = new ResponseCacheManager(context);
    }

    public static boolean clear_cache() {
        boolean result = helper.clearQueries();
        return result;
    }

    public static long get_timestamp(String url) {
        ResponseQuery query;
        query = helper.getQuery(url);
        if (query == null)
            return 0;
        else
            return query.getTimestamp();
    }

    static boolean presentInCache(String url) {
        ResponseQuery query = helper.getQuery(url);
        return (query != null);
    }

    public void addCallbacks(GetRequestListener callback) {
        if (!callbacks.contains(callback))
            callbacks.add(callback);
    }

    public void removeCallbacks(GetRequestListener callback) {
        if (callbacks.contains(callback))
            callbacks.remove(callback);
    }

    public GetAsyncTask makeAyncRequest(String url, String requestTag, int objType) {
        GetAsyncTask asyncTask = (new GetAsyncTask(url, requestTag, objType, FAV, false));
        asyncTask.execute();
        return asyncTask;
    }

    public GetAsyncTask requestHTTPThenCache(String url, String requestTag, int objType, int status) {
        GetAsyncTask asyncTask = new GetAsyncTask(url, requestTag, objType, status, false);
        asyncTask.execute();
        return asyncTask;
    }

    public GetAsyncTask requestCacheThenHTTP(String url, String requestTag, int objType, int status) {
        GetAsyncTask asyncTask = new GetAsyncTask(url, requestTag, objType, status, true);
        asyncTask.execute();
        return asyncTask;
    }


    public UpdateCache updateAsync(String url, Object returnObj, String requestTag, int status) {
        UpdateCache asyncTask = new UpdateCache(url, returnObj, requestTag, status);
        asyncTask.execute();
        return asyncTask;
    }

    public class GetAsyncTask extends AsyncTask<Void, Void, Object> {

        String url;

        String requestTag;

        int objType;
        int status;
        private boolean returnValidObject = false;
        private boolean cacheFirst = false;

        public GetAsyncTask(String url, String requestTag, int objType, int status, boolean cacheFirst) {
            this.url = url;
            CommonLib.ZLog("Requesting Url ",url);
            this.requestTag = requestTag;
            this.objType = objType;
            this.status = status;
            this.cacheFirst = cacheFirst;
        }

        @Override
        protected void onPreExecute() {
            for (GetRequestListener callback : callbacks) {
                callback.onRequestStarted(requestTag);
            }
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Void... params) {
            Object[] result = null;
            if(!cacheFirst) {
                try {
                    result = fetchhttp(this.url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (result != null) {
                    Object returnObj = null;
                    if (result[0] != null && result[0] instanceof Boolean && ((Boolean) result[0])) {
                        returnValidObject = true;
                        returnObj = ParserClass.parseData(String.valueOf(result[1]), this.objType);
                    } else if (result.length > 0) {
                        returnValidObject = false;
                        returnObj = new ErrorObject(String.valueOf(result[1]), ((Boolean) result[0]) ? 1 : 0);
                    }
                    if (returnObj != null) {
                        // update the cache
                        updateAsync(url, returnObj, requestTag, this.status);
                        return returnObj;
                    }
                } else {
                    // get from the magical cache
                    Object o = Request(url, requestTag, status);
                    if (o != null) {
                        returnValidObject = true;
                    }
                    return o;
                }
            } else {
                // get from the magical cache
                Object o = Request(url, requestTag, status);
                if (o != null) {
                    returnValidObject = true;
                    return o;
                } else { //else fetch from HTTP
                    try {
                        result = fetchhttp(this.url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (result != null) {
                        Object returnObj = null;
                        if (result[0] != null && result[0] instanceof Boolean && ((Boolean) result[0])) {
                            returnValidObject = true;
                            returnObj = ParserClass.parseData(String.valueOf(result[1]), this.objType);
                        } else if (result.length > 0) {
                            returnValidObject = false;
                            returnObj = new ErrorObject(String.valueOf(result[1]), ((Boolean) result[0]) ? 1 : 0);
                        }
                        if (returnObj != null) {
                            // update the cache
                            updateAsync(url, returnObj, requestTag, this.status);
                            return returnObj;
                        }
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {

            super.onPostExecute(result);
            if (result != null) {
                try {
                    if (returnValidObject) {

                        for (GetRequestListener callback : callbacks) {
                            callback.onRequestCompleted(requestTag, result);
                        }

                        return;
                    } else {
                        for (GetRequestListener callback : callbacks) {
                            callback.onRequestFailed(requestTag, result);
                        }
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    for (GetRequestListener callback : callbacks) {
                        callback.onRequestFailed(requestTag, new ErrorObject("Network Error", 500, 1));

                    }
                    return;
                }

            } else {
                for (GetRequestListener callback : callbacks) {
                    callback.onRequestFailed(requestTag, new ErrorObject("Network Error", 500, 1));

                }
                return;
            }
        }
    }

    public static Object[] fetchhttp(String urlstring) throws Exception {
        HttpGet get = new HttpGet(urlstring);
        get.addHeader("accept", "application/json");
        HttpResponse response = HttpManager.execute(get);
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

        StringBuilder builder = new StringBuilder();
        String aux;

        while ((aux = br.readLine()) != null) {
            builder.append(aux);
        }

        String text = builder.toString();
        Object[] returnArr = new Object[]{(response.getStatusLine().getStatusCode() == HttpsURLConnection.HTTP_OK),
                text};
        return returnArr;
    }

    // Normal Backward Compatible
    public static Object Request(String url, String requestTag, int status) {

        long ZOM_ID = -1;
        Object o = null;
        byte[] result = null;
        ResponseQuery query = helper.getQuery(url);
        try {
            if (query == null) {
                CommonLib.ZLog("RequestWrapper", "Query Null");
                Object[] httpresult = null;
                try {
                    httpresult = fetchhttp(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (httpresult != null  && result!=null) {
                    long timestamp = System.currentTimeMillis();
                    if (httpresult[0] != null && httpresult[0] instanceof Boolean && ((Boolean) httpresult[0]))
                        o = ParserClass.parseData(String.valueOf(result[1]), status);

                    try {
                        result = Serialize_Object(o);
                        query = new ResponseQuery(url, requestTag, status, timestamp, ZOM_ID, result);

                        helper.addQuery(query);

                        if (o == null) {
                            CommonLib.ZLog("RequestWrapper", "Parsed Obj null");
                            helper.deleteQuery(url);
                        }
                        return o;
                    } catch (IOException e) {
                        CommonLib.ZLog("Error", "Serialization Error");
                    } catch (OutOfMemoryError e1) {
                    }
                }
            } else {
                CommonLib.ZLog("RequestWrapper", "Query Not Null");
                result = query.getObject();
                try {
                    o = Deserialize_Object(result, "");

                } catch (IOException e) {
                    CommonLib.ZLog("Error", "Deserialization Error IO 2");
                    e.printStackTrace();

                    Object[] httpresult = fetchhttp(url);
                    if (httpresult != null) {
                        if (httpresult[0] != null && httpresult[0] instanceof Boolean && ((Boolean) httpresult[0]))
                            o = ParserClass.parseData(String.valueOf(result[1]), status);

                        long timestamp = System.currentTimeMillis();

                        try {
                            result = Serialize_Object(o);
                            query = new ResponseQuery(url, requestTag, status, timestamp, ZOM_ID, result);
                            helper.addQuery(query);
                            return o;
                        } catch (IOException p) {
                            CommonLib.ZLog("Error", "Serialization Error");
                        } catch (OutOfMemoryError e1) {
                        }
                    }
                    return null;
                } catch (ClassNotFoundException e) {
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                if (query.getTtl() == FAV) {
                    return o;
                } else if ((System.currentTimeMillis()) >= ((query.getTimestamp()) + (query.getTtl() * 1000))) {
                    // helper.deleteQuery(url);
                    return o;
                } else if (o == null) {
                    helper.deleteQuery(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    public static byte[] Serialize_Object(Object O) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(O);
        out.close();

        // Get the bytes of the serialized object
        byte[] buf = bos.toByteArray();
        return buf;
    }

    public static Object Deserialize_Object(byte[] input, String Type) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(input));
        if (Type.equals("")) {
            Object o = in.readObject();
            in.close();
            return o;
        } else {
            in.close();
            return null;
        }
    }

    public static void Update(String url, Object obj, String Object_Type, int status) {

        byte[] result;
        ResponseQuery original = helper.getQuery(url);

        if (original != null) {
            try {

                result = Serialize_Object(obj);
                long timestamp = System.currentTimeMillis();
                ResponseQuery query = new ResponseQuery(url, Object_Type, status, timestamp, -1, result);
                helper.updateQuery(query);

            } catch (Exception e) {
                CommonLib.ZLog("Error", "Serialization Error");
            } catch (OutOfMemoryError e1) {
            }

        } else {
            try {
                result = Serialize_Object(obj);
                long timestamp = System.currentTimeMillis();
                ResponseQuery query = new ResponseQuery(url, Object_Type, status, timestamp, -1, result);
                helper.addQuery(query);

            } catch (Exception e) {
                CommonLib.ZLog("Error", "Serialization Error");
            } catch (OutOfMemoryError e1) {
            }
        }
    }

    static boolean clearRecentSearch(String s) {
        try {
            return helper.clearQuery(s);
        } catch (Exception e) {
        }
        return false;
    }

    static ArrayList<String> RecentSearches() {
        ArrayList<String> queries;
        try {
            queries = helper.recoverrecentqueries();
        } catch (Exception E) {
            CommonLib.ZLog("Error", "Error Recent Searches" + E.getMessage());
            ArrayList<String> result = new ArrayList<String>();
            return result;
        }
        return queries;
    }

    public class UpdateCache extends AsyncTask<Void, Void, Object> {

        String url;
        String requestTag;
        int status;
        Object returnObj = null;

        public UpdateCache(String url, Object returnObj, String requestTag, int status) {
            this.url = url;
            this.requestTag = requestTag;
            this.status = status;
        }

        @Override
        protected Object doInBackground(Void... params) {
            Update(url, returnObj, requestTag, this.status);
            return null;
        }
    }


}
