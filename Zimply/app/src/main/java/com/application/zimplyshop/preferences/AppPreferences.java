package com.application.zimplyshop.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.notikum.notifypassive.segmentIO.Traits;
import com.notikum.notifypassive.segmentIO.UninstallAnalytics;

public class AppPreferences {

	private static final String KEY = "hj.prefs";
	private static final String IS_USER_LOGIN = "is_user_login";
	private static final String USER_TOKEN = "user_token";
	private static final String USER_ID = "user_id";
	private static final String DEVICE_ID = "device_id";
	private static final String USER_NAME = "user_name";
	private static final String USER_EMAIL = "user_email";
	private static final String USER_PHOTO = "user_photo";
	private static final String IS_APP_RATED = "is_app_rated";
	private static final String IS_LOGIN_SKIPPED = "is_login_skipped";

	private static final String RATE_FLOW_TIME = "rate_flow_time";
	private static final String RATE_FLOW_COUNT = "rate_flow_count";
	private static final String PAGE_VISITED_COUNT = "page_visited_count";
	private static final String IS_APP_HATED = "IS_APP_HATED";
	private static final String IS_RATE_LATER = "is_rate_later";
	private static final String OPEN_RATE_COUNT = "open_rate_count";
	private static final String IS_START_RATING = "is_start_rating";
	private static final String IS_LOCATION_SAVED = "is_location_saved";
    private static final String USER_CITY = "user_city";
	private static final String USER_CITY_SERVE = "srvingCity";
	private static final String USER_LOCATION = "user_location";
	private static final String USER_CITY_ID = "user_city_id";
	private static final String USER_PINCODE = "user_pincode";
    private static final String IS_PINCODE_SAVED = "is_pincode_saved";




	public static void setIsUserLogin(Context context, boolean isUserSignUp) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putBoolean(IS_USER_LOGIN, isUserSignUp);
		editor.commit();
	}

	public static boolean isUserLogIn(Context context) {

		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return savedSession.getBoolean(IS_USER_LOGIN, false);
	}

	public static void setUserToken(Context context, String token) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(USER_TOKEN, token);
		editor.commit();
	}

	public static String getUserToken(Context context) {

		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return savedSession.getString(USER_TOKEN, "");
	}

	public static void setUserID(Context context, String token) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();

		//send user-id
		UninstallAnalytics.with(context).identify(new Traits().putUsername(token));

		editor.putString(USER_ID, token);
		editor.commit();
	}

	public static String getUserID(Context context) {

		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return savedSession.getString(USER_ID, "");
	}

	public static void setDeviceID(Context context, String token) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(DEVICE_ID, token);
		editor.commit();
	}

	public static String getDeviceID(Context context) {

		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return savedSession.getString(DEVICE_ID, "");
	}

	public static void setUserName(Context context, String name) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(USER_NAME, name);
		editor.commit();
	}

	public static String getUserName(Context context) {

		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return savedSession.getString(USER_NAME, "");
	}

	public static void setUserPhoto(Context context, String photo) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(USER_PHOTO, photo);
		editor.commit();
	}

	public static String getUserPhoto(Context context) {

		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return savedSession.getString(USER_PHOTO, "");
	}

	public static void setUserEmail(Context context, String email) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		//Send email-id
		UninstallAnalytics.with(context).identify(new Traits().putEmail(email));
		editor.putString(USER_EMAIL, email);
		editor.commit();
	}

	public static String getUserEmail(Context context) {

		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return savedSession.getString(USER_EMAIL, "");
	}

	public static void setIsAppRated(Context context, boolean isAppRated) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putBoolean(IS_APP_RATED, isAppRated);
		editor.commit();
	}

	public static boolean isAppRated(Context context) {
		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return savedSession.getBoolean(IS_APP_RATED, false);
	}

	public static void setRateFlowTime(Context context, long time) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
				.edit();
		editor.putLong(RATE_FLOW_TIME, time);
		editor.commit();
	}

	public static long getRateFlowTime(Context context) {

		SharedPreferences savedSession = context.getSharedPreferences(KEY,
				Context.MODE_PRIVATE);
		return savedSession.getLong(RATE_FLOW_TIME, 0);
	}

	public static void setRateOpenCount(Context context, int count) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
				.edit();
		editor.putInt(RATE_FLOW_COUNT, count);
		editor.commit();
	}

	public static int getRateOpenCount(Context context) {

		SharedPreferences savedSession = context.getSharedPreferences(KEY,
				Context.MODE_PRIVATE);
		return savedSession.getInt(RATE_FLOW_COUNT, 0);
	}

	public static void setPageVisitedCount(Context context, int count) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
				.edit();
		editor.putInt(PAGE_VISITED_COUNT, count);
		editor.commit();
	}

	public static int getPageVisitedCount(Context context) {

		SharedPreferences savedSession = context.getSharedPreferences(KEY,
				Context.MODE_PRIVATE);
		return savedSession.getInt(PAGE_VISITED_COUNT, 0);
	}

	public static void setIsAppHated(Context context, boolean isAppHated) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putBoolean(IS_APP_HATED, isAppHated);
		editor.commit();
	}

	public static boolean isAppHated(Context context) {
		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return savedSession.getBoolean(IS_APP_HATED, false);
	}

	public static void setIsRateLater(Context context, boolean isAppRated) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putBoolean(IS_RATE_LATER, isAppRated);
		editor.commit();
	}

	public static boolean isRateLater(Context context) {
		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return savedSession.getBoolean(IS_RATE_LATER, false);
	}
	public static void setOpenRateCount(Context context, int openRateCount) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
				.edit();
		editor.putInt(OPEN_RATE_COUNT, openRateCount);
		editor.commit();
	}

	public static int getOpenRateCount(Context context) {

		SharedPreferences savedSession = context.getSharedPreferences(KEY,
				Context.MODE_PRIVATE);
		return savedSession.getInt(OPEN_RATE_COUNT, 0);
	}
	public static void setIsStartRating(Context context, boolean isStartRating) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
				.edit();
		editor.putBoolean(IS_START_RATING, isStartRating);
		editor.commit();
	}

	public static boolean isStartRating(Context context) {

		SharedPreferences savedSession = context.getSharedPreferences(KEY,
				Context.MODE_PRIVATE);
		return savedSession.getBoolean(IS_START_RATING, false);
	}


	public static void setIsLoginSkipped(Context context, boolean isUserSignUp) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putBoolean(IS_LOGIN_SKIPPED, isUserSignUp);
		editor.commit();
	}

	public static boolean isLoginSkipped(Context context) {

		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		return savedSession.getBoolean(IS_LOGIN_SKIPPED, false);
	}

    public static void setIsLocationSaved(Context context, boolean isLocationSaved) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(IS_LOCATION_SAVED, isLocationSaved);
        editor.commit();
    }

    public static boolean isLocationSaved(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return savedSession.getBoolean(IS_LOCATION_SAVED, false);
    }

    public static void setSavedCity(Context context, String text) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putString(USER_CITY, text);
        editor.commit();
    }

	public static void setSavedCityServe(Context context, boolean serve) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
				.edit();
		editor.putBoolean(USER_CITY_SERVE, serve);
		editor.commit();
	}

	public static boolean getSavedCityServe(Context context) {

		SharedPreferences savedSession = context.getSharedPreferences(KEY,
				Context.MODE_PRIVATE);
		return savedSession.getBoolean(USER_CITY_SERVE, false);
	}
	public static String getSavedCity(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        return savedSession.getString(USER_CITY, "");
    }

    public static void setSavedLocality(Context context, String text) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putString(USER_LOCATION, text);
        editor.commit();
    }

    public static String getSavedLocality(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        return savedSession.getString(USER_LOCATION, "");
    }
    public static void setSavedCityId(Context context, String text) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putString(USER_CITY_ID, text);
        editor.commit();
    }

    public static String getSavedCityId(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        return savedSession.getString(USER_CITY_ID, "");
    }
    public static void setSavedPincode(Context context, String text) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
                .edit();
        editor.putString(USER_PINCODE, text);
        editor.commit();
    }

    public static String getSavedPincode(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY,
                Context.MODE_PRIVATE);
        return savedSession.getString(USER_PINCODE, "");
    }

    public static void setIsPincodeSaved(Context context, boolean isLocationSaved) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(IS_PINCODE_SAVED, isLocationSaved);
        editor.commit();
    }

    public static boolean isPincodeSaved(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        return savedSession.getBoolean(IS_PINCODE_SAVED, false);
    }
}
