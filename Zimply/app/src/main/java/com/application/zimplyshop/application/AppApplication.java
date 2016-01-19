package com.application.zimplyshop.application;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.application.zimplyshop.R;
import com.application.zimplyshop.db.RecentProductsDBWrapper;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.utils.CacheCleanerService;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.CommonLib.TrackerName;
import com.application.zimplyshop.utils.LruCache;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.location.ZLocationListener;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Application class for the app
 *
 * @author Umesh
 */
public class AppApplication extends Application {

    public static AppApplication sInstance;

    //Object id cache list
    public static ArrayList<Long> Update_Rest = new ArrayList<Long>();
    public static String contactNumber = "011-";
    //Photo cache
    public LruCache<String, Bitmap> cache;
    public ZLocationListener zll = new ZLocationListener(this);
    public LocationManager locationManager = null;
    public int state = CommonLib.LOCATION_DETECTION_RUNNING;
    public double lat = 0;
    public double lon = 0;
    public String location = "";
    public boolean isNetworkProviderEnabled = false;
    public boolean isGpsProviderEnabled = false;
    // GA
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    //Location fundamentals
    private CheckLocationTimeoutAsync checkLocationTimeoutThread;

    public static AppApplication getInstance() {
        return sInstance;
    }

    public static String getContactNumber() {
        return contactNumber;
    }

    public static void setContactNumber(String number) {
        contactNumber = number;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        cache = new LruCache<String, Bitmap>(30);
        SharedPreferences prefs = getSharedPreferences(CommonLib.preferenceName, 0);

        try {
            lat = Double.parseDouble(prefs.getString("lat1", "0"));
            lon = Double.parseDouble(prefs.getString("lon1", "0"));
        } catch (ClassCastException e) {
        } catch (Exception e) {
        }
        boolean firstLaunch = false;

        if (prefs.getInt("version", 0) < CommonLib.VERSION) {

            firstLaunch = true;

            SharedPreferences.Editor edit = prefs.edit();

            //logging out user with app version < 3.2
            if (prefs.getInt("version", 0) < 40) {
                edit.putInt("uid", 0);
            }

            edit.putBoolean("firstLaunch", true);
            edit.putInt("version", CommonLib.VERSION);
            edit.commit();

            deleteDatabase("PRODUCTSDB");
            deleteDatabase("CACHE");
            startCacheCleanerService();

        } else {
            firstLaunch = prefs.getBoolean("firstLaunch", true);
        }


        //  new ThirdPartyInitAsync().execute();


        // run the cache cleaner service
        try {
            if (!isMyServiceRunning(CacheCleanerService.class)) {
                boolean alarmUp = (PendingIntent.getService(this, 0, new Intent(this, CacheCleanerService.class),
                        PendingIntent.FLAG_NO_CREATE) != null);

                if (!alarmUp)
                    startCacheCleanerService();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        RecentProductsDBWrapper.Initialize(getApplicationContext());
        UploadManager.getInstance().setContext(getApplicationContext());
        GetRequestManager.getInstance().setContext(getApplicationContext());
        getTracker(CommonLib.TrackerName.GLOBAL_TRACKER);

    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startCacheCleanerService() {

        Intent intent = new Intent(this, CacheCleanerService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 04);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pintent);
    }

    public String getBaseUrl() {
        //return "http://10.0.0.112/";
//        return "http://api.zimply.in/";
        return "http://test.zimply.in/";
        // return "http://192.168.43.36:8000/";
        //	return "http://52.64.127.88/";
        //return "http://52.64.12.26/";//test IP
        //return "http://54.79.118.6/";
        // return "http://10.0.0.131:8000/";
    }

    public String getWebUrl() {
        return "http://zimply.in/";
        // return "http://52.64.12.26/";
    }

	/*@Override
    public void onTrimMemory(int level) {
		CommonLib.ZLog("Trim", "Trim down memory : " +level);
		if ( level == TRIM_MEMORY_RUNNING_MODERATE || level == TRIM_MEMORY_BACKGROUND ) {
//			cache.clear();
		} else if ( level == TRIM_MEMORY_RUNNING_LOW || level == TRIM_MEMORY_RUNNING_CRITICAL ) {
//			cache.clear();
			// run the cache cleaner service
			try {
				if (!isMyServiceRunning(CacheCleanerService.class)) {
					boolean alarmUp = (PendingIntent.getService(this, 0, new Intent(this, CacheCleanerService.class),
							PendingIntent.FLAG_NO_CREATE) != null);

					if (!alarmUp)
						startCacheCleanerService();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onTrimMemory(level);
	}*/

    public synchronized Tracker getTracker(TrackerName trackerId) {

        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = trackerId == CommonLib.TrackerName.APPLICATION_TRACKER ? analytics.newTracker("UA-64477399-7")
                    : analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    public String getLocationString() {
        return location;
    }

    public void setLocationString(String lstr) {
        location = lstr;
        SharedPreferences prefs = getSharedPreferences("application_settings", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("location", location);
        editor.commit();
    }

    public void interruptLocationTimeout() {
        //checkLocationTimeoutThread.interrupt();
        if (checkLocationTimeoutThread != null)
            checkLocationTimeoutThread.interrupt = false;
    }

    public void startLocationCheck() {

        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (result == ConnectionResult.SUCCESS) {
            zll.getFusedLocation(this);
        } else {
            getAndroidLocation();
        }
    }

    public void getAndroidLocation() {

        CommonLib.ZLog("zll", "getAndroidLocation");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);

        if (providers != null) {
            for (String providerName : providers) {
                if (providerName.equals(LocationManager.GPS_PROVIDER))
                    isGpsProviderEnabled = true;
                if (providerName.equals(LocationManager.NETWORK_PROVIDER))
                    isNetworkProviderEnabled = true;
            }
        }

        if (isNetworkProviderEnabled || isGpsProviderEnabled) {

            state = CommonLib.LOCATION_DETECTION_RUNNING;

            if (isGpsProviderEnabled)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, zll);
            if (isNetworkProviderEnabled)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 500.0f, zll);


            if (checkLocationTimeoutThread != null) {
                checkLocationTimeoutThread.interrupt = false;
            }

            checkLocationTimeoutThread = new CheckLocationTimeoutAsync();
            checkLocationTimeoutThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else {
            state = CommonLib.LOCATION_NOT_ENABLED;
            zll.locationNotEnabled();

        }
    }

    public boolean isLocationAvailable() {
        return (isNetworkProviderEnabled || isGpsProviderEnabled);
    }

    private class ThirdPartyInitAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                // Crashlytics Initialize
                Fabric.with(getApplicationContext(), new Crashlytics());
                Crashlytics.getInstance().core.setString("Version", CommonLib.CRASHLYTICS_VERSION_STRING);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
        }
    }

    private class CheckLocationTimeoutAsync extends AsyncTask<Void, Void, Void> {
        boolean interrupt = true;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            if (interrupt) {
                zll.interruptProcess();
            }
        }
    }

}