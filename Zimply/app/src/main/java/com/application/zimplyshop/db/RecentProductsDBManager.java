package com.application.zimplyshop.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.utils.CommonLib;

import java.util.ArrayList;

public class RecentProductsDBManager extends SQLiteOpenHelper {

    private static final String ID = "ID";
    private static final String USERID = "UserID";
    private static final String TYPE = "Type";
    private static final String TIMESTAMP = "Timestamp";
    private static final String BUNDLE = "Bundle";
    SQLiteDatabase db;

    private static final int DATABASE_VERSION = 2;
    private static final String CACHE_TABLE_NAME = "PRODUCTS";
    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + CACHE_TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    USERID + " INTEGER, " +
                    TIMESTAMP + " INTEGER, " +
                    TYPE + " INTEGER, " +
                    BUNDLE + " BLOB);";
    private static final String DATABASE_NAME = "PRODUCTSDB";
    Context ctx;

    public RecentProductsDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;
    }

    public RecentProductsDBManager(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int addProduct(HomeProductObj user, int userId, long timestamp) {

        ArrayList<HomeProductObj> users = getProducts(userId);
        int result = -1;

        try {

            this.getReadableDatabase();

            SQLiteDatabase db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/" + DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
            ContentValues values = new ContentValues();
            values.put(TIMESTAMP, timestamp);

            boolean exists = false;

            for(HomeProductObj product: users) {
                if(product.getProduct().getId() == user.getProduct().getId())
                    exists = true;
            }


            if(exists) {
                result = (int) db.update(CACHE_TABLE_NAME, values, TYPE + "=?", new String[] {user.getProduct().getId()+""});

                CommonLib.ZLog("zuser addusers if ", userId + " : " +  user.getProduct().getId() +"=?");

            } else {

                byte[] bundle = GetRequestManager.Serialize_Object(user);

                values.put(USERID, userId);
                values.put(TYPE, user.getProduct().getId());
                values.put(BUNDLE, bundle);

                // 	Inserting Row
                result = (int) db.insert(CACHE_TABLE_NAME, null, values);
                CommonLib.ZLog("zuser addusers else ", userId + " . " +  user.getProduct().getId());
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

    public ArrayList<HomeProductObj> getProducts(int userId) {
        HomeProductObj location;
        this.getReadableDatabase();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        ArrayList<HomeProductObj> queries = new ArrayList<HomeProductObj>();

        try{
            db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/" + DATABASE_NAME, SQLiteDatabase.OPEN_READONLY, null);
            cursor = db.query(CACHE_TABLE_NAME, new String[] { ID, USERID,TIMESTAMP,TYPE,BUNDLE }, /*CITYID + "=? AND " +*/ USERID + "=?",
                    new String[] { /*Integer.toString(cityId),*/ Integer.toString(userId) }, null, null, TIMESTAMP + " DESC", "20");
            if (cursor != null)
                cursor.moveToFirst();

            for (int i=0;i<cursor.getCount();i++)
            {
                cursor.moveToPosition(i);
                location = (HomeProductObj) GetRequestManager.Deserialize_Object(cursor.getBlob(4), "");
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
