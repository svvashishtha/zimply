package com.application.zimplyshop.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.managers.GetRequestManager;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class ResponseCacheManager extends SQLiteOpenHelper {

	private static final String URL = "Url";
	private static final String OBJECT = "Object";
	private static final String TTL = "TimeLive";
	private static final String ZOMID = "ZimplyID";
	private static final String TYPE = "Type";
	private static final String TIMESTAMP = "Timestamp";
	SQLiteDatabase db;

	private static final int DATABASE_VERSION = 2;
	private static final String CACHE_TABLE_NAME = "RESPONSECACHE";
	private static final String DICTIONARY_TABLE_CREATE = "CREATE TABLE " + CACHE_TABLE_NAME + " (" + URL + " TEXT, "
			+ TYPE + " TEXT, " + ZOMID + " INTEGER, " + TTL + " INTEGER, " + TIMESTAMP + " INTEGER, " + OBJECT
			+ " BLOB);";
	private static final String DATABASE_NAME = "CACHE";
	Context ctx;

	public ResponseCacheManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(DICTIONARY_TABLE_CREATE);
	}

	public boolean clearQuery(String s) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			this.getReadableDatabase();
			db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/CACHE", SQLiteDatabase.OPEN_READONLY,
					null);
			cursor = db.query(CACHE_TABLE_NAME, new String[] { URL }, TYPE + "=?",
					new String[] { GetRequestManager.QUERYRESTAURANTLIST }, null, null, TIMESTAMP + " DESC ", "25");

			if (cursor != null)
				cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				String query = cursor.getString(0);

				if (parse_query(query).equals(s)) {
					deleteQuery(query);
				}

			}
			cursor.close();
			db.close();
			this.close();
			return true;
		} catch (Exception E) {
			try {
				cursor.close();
				db.close();
				this.close();
			} catch (Exception ec) {
				try {
					db.close();
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					this.close();
				}
				return false;
			}
			return false;
		}
	}

	public boolean clearQueries() {

		ResponseQuery Queries[];
		Queries = null;
		this.getReadableDatabase();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/CACHE", SQLiteDatabase.OPEN_READONLY,
					null);
			cursor = db.query(CACHE_TABLE_NAME, new String[] { URL, TYPE, TTL, TIMESTAMP, ZOMID, OBJECT },
					TYPE + "!=? AND " + TYPE + "!=? AND " + TTL + "!=?",
					new String[] { GetRequestManager.USER, GetRequestManager.CITYDETAILLIST, "-1" }, null, null, null,
					null);

			if (cursor != null) {
				cursor.moveToFirst();

				Queries = new ResponseQuery[cursor.getCount()];
				for (int i = 0; i < cursor.getCount(); i++) {
					ResponseQuery query_result = new ResponseQuery(cursor.getString(0), cursor.getString(1),
							cursor.getLong(2), cursor.getLong(3), cursor.getLong(4), cursor.getBlob(5));
					Queries[i] = query_result;
					cursor.moveToNext();
					// cursor.moveToPosition(i);
				}
				for (int i = 0; i < Queries.length; i++) {
					if ((System.currentTimeMillis()) >= ((Queries[i].getTimestamp()) + (Queries[i].getTtl() * 1000)))
						this.deleteQuery(Queries[i].getUrl());
				}
			}

			cursor.close();
			db.close();
			this.close();
		} catch (Exception E) {
			try {

				cursor.close();
				db.close();
				this.close();
			} catch (Exception ex) {
				try {
					db.close();
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					this.close();
				}
				return false;
			}

			return false;
		}
		return true;

	}

	public ArrayList<String> recoverrecentqueries() {

		ArrayList<String> Queries = null;
		this.getReadableDatabase();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/CACHE", SQLiteDatabase.OPEN_READONLY,
					null);
			cursor = db.query(CACHE_TABLE_NAME, new String[] { URL }, TYPE + "=?",
					new String[] { GetRequestManager.QUERYRESTAURANTLIST }, null, null, TIMESTAMP + " DESC ", "25");

			if (cursor != null)
				cursor.moveToFirst();

			Queries = new ArrayList<String>();
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				String query = cursor.getString(0);
				if (!parse_query(query).equals("") && !query.contains("promotedrestaurants.xml")
						&& !Queries.contains(parse_query(query)))
					Queries.add(parse_query(query));

			}
			cursor.close();
			db.close();
			this.close();
		} catch (Exception E) {
			try {
				cursor.close();
				db.close();
				this.close();
			} catch (Exception ec) {
				try {
					db.close();
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					this.close();
				}
				return new ArrayList<String>();
			}
			ArrayList<String> results = new ArrayList<String>();
			return results;
		}
		return Queries;

	}

	public ResponseQuery[] recoverrecentrest() {

		ResponseQuery Queries[];
		Queries = null;
		this.getReadableDatabase();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/CACHE", SQLiteDatabase.OPEN_READONLY,
					null);

			cursor = db.query(CACHE_TABLE_NAME, new String[] { URL, TYPE, TTL, TIMESTAMP, ZOMID, OBJECT }, TYPE + "=?",
					new String[] { GetRequestManager.RESTAURANT }, null, null, TIMESTAMP + " DESC ", "25");

			if (cursor != null)
				cursor.moveToFirst();
			else {
				db.close();
				this.close();
				return null;
			}

			Queries = new ResponseQuery[cursor.getCount()];
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ResponseQuery query_result = new ResponseQuery(cursor.getString(0), cursor.getString(1),
						cursor.getLong(2), cursor.getLong(3), cursor.getLong(4), cursor.getBlob(5));
				Queries[i] = query_result;

				// return contact
			}
			cursor.close();
			db.close();
			this.close();
		}

		catch (Exception E) {
			try {
				cursor.close();
				db.close();
				this.close();
			} catch (Exception ex) {
				try {
					db.close();
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					this.close();
				}
				return null;
			}
			return null;
		}
		return Queries;

	}

	public void addQuery(ResponseQuery query) {
		this.getReadableDatabase();
		SQLiteDatabase db = null;

		try {
			db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/CACHE", SQLiteDatabase.OPEN_READWRITE,
					null);

			ContentValues values = new ContentValues();
			values.put(URL, query.getUrl());
			values.put(OBJECT, query.getObject());
			values.put(TTL, query.getTtl());
			values.put(TIMESTAMP, query.getTimestamp());
			values.put(ZOMID, query.getZomid());
			values.put(TYPE, query.getType());

			// Inserting Row
			db.insert(CACHE_TABLE_NAME, null, values);
			db.close(); // Closing database connection
			this.close();
		} catch (Exception e) {
			try {
				db.close();
				this.close();
			} catch (Exception ex) {
				this.close();
			}
		}
	}

	public void close_db() {
		SQLiteDatabase db = this.db;
		db.close();

	}

	// Getting single contact
	public ResponseQuery getQuery(String query) {

		SQLiteDatabase db = null;
		Cursor cursor = null;

		try {
			this.getReadableDatabase();

			db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/CACHE", SQLiteDatabase.OPEN_READONLY,
					null);
			cursor = db.query(CACHE_TABLE_NAME, new String[] { URL, TYPE, TTL, TIMESTAMP, ZOMID, OBJECT }, URL + "=?",
					new String[] { query }, null, null, null, null);

			if (cursor != null)
				cursor.moveToFirst();
			ResponseQuery query_result = new ResponseQuery(cursor.getString(0), cursor.getString(1),
					cursor.getLong(2), cursor.getLong(3), cursor.getLong(4), cursor.getBlob(5));
			// return contact
			cursor.close();
			db.close();

			this.close();
			return query_result;
		}

		catch (Exception e) {
			// TODO: handle exception
			try {
				cursor.close();
				db.close();
				this.close();
			} catch (Exception ec) {
				// TODO: handle exception

				return null;
			}
			return null;
		}

	}

	public ResponseQuery[] getQueries(String query) {

		ResponseQuery Queries[];
		Queries = null;

		SQLiteDatabase db = null;
		Cursor cursor = null;

		try {
			this.getReadableDatabase();
			db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/CACHE", SQLiteDatabase.OPEN_READONLY,
					null);
			cursor = db.query(CACHE_TABLE_NAME, new String[] { URL, TYPE, TTL, TIMESTAMP, ZOMID, OBJECT }, URL + "=?",
					new String[] { query }, null, null, null, null);

			if (cursor != null)
				cursor.moveToFirst();

			Queries = new ResponseQuery[cursor.getCount()];
			for (int i = 0; i < cursor.getCount(); i++) {
				ResponseQuery query_result = new ResponseQuery(cursor.getString(0), cursor.getString(1),
						cursor.getLong(2), cursor.getLong(3), cursor.getLong(4), cursor.getBlob(5));
				Queries[i] = query_result;
				cursor.moveToPosition(i);
				// return contact
			}
			cursor.close();
			db.close();
			this.close();
		}

		catch (Exception E) {
			try {
				cursor.close();
				db.close();
				this.close();
			} catch (Exception r) {
				try {
					db.close();
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					this.close();
				}
				return null;
			}

			return null;
		}
		return Queries;

	}

	// Getting All Contacts
	public List<ResponseQuery> getAllQueries() {
		return null;
	}

	// Getting contacts Count
	public long getCount() {

		long count = 0;
		try {
			String sql = "SELECT COUNT(*) FROM " + CACHE_TABLE_NAME;
			this.getReadableDatabase();
			SQLiteDatabase db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/CACHE",
					SQLiteDatabase.OPEN_READONLY, null);
			SQLiteStatement statement = db.compileStatement(sql);
			count = statement.simpleQueryForLong();
			db.close();
			this.close();
		} catch (Exception e) {
			// TODO: handle exception
			try {
				this.close();
			} catch (Exception ex) {

				return 0;
			}
			return 0;
		}
		return count;
	}

	// Updating single contact
	public int updateQuery(ResponseQuery query) {
		SQLiteDatabase db = null;
		try {
			this.getReadableDatabase();

			db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/CACHE", SQLiteDatabase.OPEN_READWRITE,
					null);

			ContentValues values = new ContentValues();
			values.put(URL, query.getUrl());
			values.put(OBJECT, query.getObject());
			values.put(TTL, query.getTtl());
			values.put(TIMESTAMP, query.getTimestamp());
			values.put(ZOMID, query.getZomid());
			values.put(TYPE, query.getType());
			// Inserting Row
			// db.insert(, null, values);
			// updating row
			int status = db.update(CACHE_TABLE_NAME, values, URL + " = ?", new String[] { query.getUrl() });
			db.close();
			this.close();
			return status;
		} catch (Exception e) {
			// Crashlytics.logException(e);
			try {
				this.close();
			} catch (Exception ec) {
				// Crashlytics.logException(ec);
				return 0;
			}
			return 0;
		}

	}

	// Deleting single contact
	public boolean deleteQuery(String query) {
		SQLiteDatabase db = null;
		try {
			this.getReadableDatabase();
			db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/CACHE", SQLiteDatabase.OPEN_READWRITE,
					null);
			db.delete(CACHE_TABLE_NAME, URL + " = ?", new String[] { query });
			db.close();
			this.close();
		} catch (Exception E) {
			try {
				db.close();
				this.close();
			} catch (Exception ex) {
				this.close();
				return false;
			}
			return false;
		}
		return true;

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public static String parse_query(String url) {
		String temp = "";
		String[] val = url.split("q=");
		if (val.length == 2) {
			String b = val[1];
			temp = b.substring(0, b.indexOf("&"));
		}
		temp = URLDecoder.decode(temp);
		return temp;

	}

	public String getRestList() {
		String Queries = "";
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			this.getReadableDatabase();
			db = ctx.openOrCreateDatabase("/data/data/com.application.zimplyshop/databases/CACHE", SQLiteDatabase.OPEN_READONLY,
					null);
			cursor = db.query(CACHE_TABLE_NAME, new String[] { ZOMID }, TYPE + "=?",
					new String[] { GetRequestManager.RESTAURANT }, null, null, null, null);

			if (cursor != null)
				cursor.moveToFirst();
			else
				return Queries;

			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				String query = cursor.getString(0);
				if (!(AppApplication.Update_Rest.contains(Integer.parseInt(query)))) {
					if (query != null && !query.equals("-1"))
						Queries = Queries + query + ",";
				}

			}
			// Queries=Queries.substring(5);
			cursor.close();
			db.close();
			this.close();
		} catch (Exception E) {
			try {
				cursor.close();
				db.close();
				this.close();
			} catch (Exception ec) {
				this.close();
				return Queries;
			}

			return Queries;
		}
		return Queries;
	}

}