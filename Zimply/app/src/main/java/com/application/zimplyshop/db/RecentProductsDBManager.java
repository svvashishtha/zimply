package com.application.zimplyshop.db;

import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;

import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.utils.CommonLib;

public class RecentProductsDBManager extends SQLiteOpenHelper {

    private static final String ID = "ID";
    private static final String MESSAGEID = "MessageID";
    private static final String WISHID = "WishID";
    private static final String TYPE = "Type";
    private static final String TIMESTAMP = "Timestamp";
    private static final String BUNDLE = "Bundle";
    SQLiteDatabase db;

    private static final int DATABASE_VERSION = 2;
    private static final String CACHE_TABLE_NAME = "PRODUCTS";
    private static final String DICTIONARY_TABLE_CREATE = "CREATE TABLE " + CACHE_TABLE_NAME + " (" + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + MESSAGEID + " INTEGER, " + WISHID + " INTEGER, "
            + TIMESTAMP + " INTEGER, " + TYPE + " INTEGER, " + BUNDLE + " BLOB);";


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

    public int addProduct(HomeProductObj location, int userId, int wishId, long timestamp) {

        ArrayList<HomeProductObj> locations = getProducts(userId);
        int result = -1;

        try {

            this.getReadableDatabase();

            SQLiteDatabase db = ctx.openOrCreateDatabase("/data/data/com.application.zimply/databases/" + DATABASE_NAME,
                    SQLiteDatabase.OPEN_READWRITE, null);
            ContentValues values = new ContentValues();
            values.put(TIMESTAMP, timestamp);

            if (locations.contains(location)) {
                result = (int) db.update(CACHE_TABLE_NAME, values, TYPE + "=?",
                        new String[] { location.getId()+""});

                CommonLib.ZLog("zloc addlocations if ", userId + " : " + location.getId());

            } else {

                byte[] bundle = GetRequestManager.Serialize_Object(location);

                values.put(MESSAGEID, userId);
                values.put(WISHID, wishId);
                values.put(TYPE, location.getId());
                values.put(BUNDLE, bundle);

                // Inserting Row
                result = (int) db.insert(CACHE_TABLE_NAME, null, values);
                CommonLib.ZLog("zloc addlocations else ", userId + " . " + location.getId());
            }

            db.close();
            this.close();
        } catch (Exception E) {
            E.printStackTrace();
            try {
                this.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            result = -1;
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

        try {
            db = ctx.openOrCreateDatabase("/data/data/com.application.zimply/databases/" + DATABASE_NAME,
                    SQLiteDatabase.OPEN_READONLY, null);

            cursor = db.query(CACHE_TABLE_NAME, new String[] { ID, MESSAGEID, WISHID, TIMESTAMP, TYPE, BUNDLE },
                    MESSAGEID + "=?", new String[] {Integer.toString(userId)}, null, null, TIMESTAMP + " ASC", null);
            if (cursor != null)
                cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                location = (HomeProductObj) GetRequestManager.Deserialize_Object(cursor.getBlob(5), "");
                queries.add(location);
            }

            cursor.close();
            db.close();
            this.close();
            return queries;
        } catch (SQLiteException e) {

            e.printStackTrace();
            this.close();
        } catch (Exception E) {
            E.printStackTrace();
            try {
                cursor.close();
                db.close();
                this.close();
            } catch (Exception ec) {
                try {
                    ec.printStackTrace();
                    db.close();
                } catch (Exception e) {
                    this.close();
                }
                this.close();
            }
        }
        return queries;
    }

}
