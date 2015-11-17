package com.application.zimplyshop.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.extras.ParserClass;
import com.application.zimplyshop.managers.HttpManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class UploadManager {

	private SharedPreferences prefs;
	private AppApplication zapp;
	private static volatile UploadManager sInstance;
	private Context mContext;
	private ArrayList<UploadManagerCallback> callbacks = new ArrayList<UploadManagerCallback>();

	/**
	 * Empty constructor to prevent multiple objects in memory
	 */
	private UploadManager() {
	}

	/**
	 * Implementation of double check'd locking scheme.
	 */
	public static UploadManager getInstance() {

		if (sInstance == null) {
			synchronized (UploadManager.class) {
				if (sInstance == null) {
					sInstance = new UploadManager();
				}
			}
		}
		return sInstance;
	}

	public void setContext(Context context) {
		this.mContext = context;
		prefs = context.getSharedPreferences("application_settings", 0);

		if (context instanceof AppApplication) {
			zapp = (AppApplication) context;
		}
	}

	public void addCallback(UploadManagerCallback callback) {
		if (!callbacks.contains(callback)) {
			callbacks.add(callback);
		}
	}

	public void removeCallback(UploadManagerCallback callback) {
		if (callbacks.contains(callback)) {
			callbacks.remove(callback);
		}
	}

	public void makeAyncRequest(String url, int requestType, String objectId, int stringId, Object object,
			List<NameValuePair> nameValuePairs, MultipartEntity entities) {
		new PostAsyncTask(url, requestType, objectId, stringId, object, nameValuePairs, entities).execute();
	}

	public class PostAsyncTask extends AsyncTask<Void, Void, Object> {

		String url;
		HttpResponse response;
		List<NameValuePair> nameValuePairs;
		int requestType, parserId;
		String objectId;
		Object object;
		MultipartEntity entities;

		public PostAsyncTask(String url, int requestType, String objectId, int parserId, Object object,
				List<NameValuePair> nameValuePairs, MultipartEntity entities) {
			this.url = url;
			this.requestType = requestType;
			this.objectId = objectId;
			this.parserId = parserId;
			this.nameValuePairs = nameValuePairs;
			this.object = object;
			this.entities = entities;
		}

		@Override
		protected void onPreExecute() {
			for (UploadManagerCallback callback : callbacks) {
				callback.uploadStarted(requestType, objectId, parserId, object);
			}
			super.onPreExecute();
		}

		@Override
		protected Object doInBackground(Void... params) {
			try {
				HttpPost httpPost = new HttpPost(this.url);
				// httpPost.addHeader(new BasicHeader("X-API-Key",
				// CommonLib.APIKEY));

				if (nameValuePairs != null)
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

				if (entities != null)
					httpPost.setEntity(entities);

				response = HttpManager.execute(httpPost);

				if (response.getStatusLine().getStatusCode() == HttpsURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

					StringBuilder builder = new StringBuilder();
					String aux = "";

					while ((aux = br.readLine()) != null) {
						builder.append(aux);
					}

					String text = builder.toString();

					return ParserClass.parseData(text, this.parserId);

				} else {
					BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
					StringBuilder builder = new StringBuilder();
					String aux = "";

					while ((aux = br.readLine()) != null) {
						builder.append(aux);
					}
					String text = builder.toString();
					ErrorObject obj = new ErrorObject(text, response.getStatusLine().getStatusCode());

					return obj;

				}
			} catch (Exception e) {
				e.printStackTrace();

			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {

			super.onPostExecute(result);
			if (result != null) {
				try {
					boolean status = response.getStatusLine().getStatusCode() == HttpsURLConnection.HTTP_OK;
					for (UploadManagerCallback callback : callbacks) {
						callback.uploadFinished(requestType, objectId, object, result, status, parserId);
					}
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				for (UploadManagerCallback callback : callbacks) {
					callback.uploadFinished(requestType, objectId,object, new ErrorObject("Invalid response from server", 500),
							false, parserId);
				}
				return;
			}
			for (UploadManagerCallback callback : callbacks) {
				callback.uploadFinished(requestType, objectId, object,new ErrorObject("Something went wrong", 500),
						false, parserId);
			}
			return;
		}
	}

}

