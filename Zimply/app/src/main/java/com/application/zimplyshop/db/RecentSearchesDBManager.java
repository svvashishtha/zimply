package com.application.zimplyshop.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.utils.CommonLib;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 1/19/2016.
 */
public class RecentSearchesDBManager extends SQLiteOpenHelper {

    private static final String ID = "ID";
    private static final String USERID = "UserID";
    private static final String TYPE = "Type";
    private static final String TIMESTAMP = "Timestamp";
    private static final String BUNDLE = "Bundle";
    SQLiteDatabase db;

    private static final int DATABASE_VERSION = 2;
    private static final String CACHE_TABLE_NAME = "SEARCHES";
    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + CACHE_TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    USERID + " INTEGER, " +
                    TIMESTAMP + " INTEGER, " +
                    TYPE + " INTEGER, " +
                    BUNDLE + " BLOB);";
    private static final String DATABASE_NAME = "SEARCHESDB";
    Context ctx;

    public RecentSearchesDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;
    }

    public RecentSearchesDBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int addProduct(String currentTag, int userId, long timestamp) {

        ArrayList<String> users = getProducts(userId);
        int result = -1;

        try {

            this.getReadableDatabase();

            SQLiteDatabase db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/" + DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
            ContentValues values = new ContentValues();
            values.put(TIMESTAMP, timestamp);

            boolean exists = false;

            for(String tag: users) {
                if(tag.equalsIgnoreCase(currentTag))
                    exists = true;
            }


            if(exists) {
                result = (int) db.update(CACHE_TABLE_NAME, values, TYPE + "=?", new String[] {currentTag+""});

                //CommonLib.ZLog("zuser addusers if ", userId + " : " + userId + "=?");

            } else {

                byte[] bundle = GetRequestManager.Serialize_Object(currentTag);

                values.put(USERID, userId);
                values.put(TYPE, currentTag);
                values.put(BUNDLE, bundle);

                // 	Inserting Row
                result = (int) db.insert(CACHE_TABLE_NAME, null, values);
                //CommonLib.ZLog("zuser addusers else ", userId + " . " +  user.getProduct().getId());
            }

            db.close();
            this.close();
        }
        catch(Exception E) {
            try {
                this.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            result = - 1;
        }
        return result;
        // Closing database connection
    }

    public ArrayList<String> getProducts(int userId) {
        String location;
        this.getReadableDatabase();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        ArrayList<String> queries = new ArrayList<String>();

        try{
            db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/" + DATABASE_NAME, SQLiteDatabase.OPEN_READONLY, null);
            cursor = db.query(CACHE_TABLE_NAME, new String[] { ID, USERID,TIMESTAMP,TYPE,BUNDLE }, /*CITYID + "=? AND " +*/ USERID + "=?",
                    new String[] { /*Integer.toString(cityId),*/ Integer.toString(userId) }, null, null, TIMESTAMP + " DESC", "20");
            if (cursor != null)
                cursor.moveToFirst();

            for (int i=0;i<cursor.getCount();i++)
            {
                cursor.moveToPosition(i);
                location = (String) GetRequestManager.Deserialize_Object(cursor.getBlob(4), "");
                queries.add(location);
            }

            cursor.close();
            db.close();
            this.close();
            return queries;
        }
        catch (SQLiteException e) {

            this.close();
        }
        catch(Exception E)
        {
            try {
                cursor.close();
                db.close();
                this.close();
            }
            catch(Exception ec) {
                try {
                    db.close();
                }
                catch (Exception e) {
                    this.close();
                }
                this.close();
            }
        }
        return queries;
    }

    public int removeUsers(int userId) {

        int result = -1;
        try {
            this.getReadableDatabase();

            SQLiteDatabase db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/" + DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);

            result = db.delete(CACHE_TABLE_NAME, null, null);
            CommonLib.ZLog("zuser delete all users", userId );

            db.close();
            this.close();
        }
        catch(Exception E) {
            try {
                this.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            result = - 1;
        }
        return result;
    }

}