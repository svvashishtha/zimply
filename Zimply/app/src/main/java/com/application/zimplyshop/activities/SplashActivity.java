package com.application.zimplyshop.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.AppConfig;
import com.application.zimplyshop.baseobjects.BaseCartProdutQtyObj;
import com.application.zimplyshop.baseobjects.NonLoggedInCartObj;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.objects.AllCategories;
import com.application.zimplyshop.objects.AllCities;
import com.application.zimplyshop.objects.AllProducts;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.ZContainerHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.tagmanager.Container;
import com.google.android.gms.tagmanager.ContainerHolder;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Displays simple splash screen while GTM container is loading. Once the container is loaded,
 * launches the {@link HomeActivity}.
 */
public class SplashActivity extends BaseActivity implements RequestTags, GetRequestListener, ContainerHolder.ContainerAvailableListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    boolean isDestroyed;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String regId;
    int hardwareRegistered = 0;
    private boolean windowHasFocus = true;
    private ImageView imageView;
    private Activity mContext;

    private static final String TAG_CONTAINER_ID = "GTM_WRPWPV";

    private boolean containerLoaded = false;

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 1);
            return packageInfo.versionCode;

        } catch (Exception e) {
            CommonLib.ZLog("GCM", "EXCEPTION OCCURED" + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity_layout);

        TagManager tagManager = TagManager.getInstance(this);

        // Modify the log level of the logger to print out not only
        // warning and error messages, but also verbose, debug, info messages.
        tagManager.setVerboseLoggingEnabled(true);
        PendingResult<ContainerHolder> pending =
                tagManager.loadContainerPreferNonDefault(TAG_CONTAINER_ID,
                        R.raw.gtm_wrpwpv);

        // The onResult method will be called as soon as one of the following happens:
        //     1. a saved container is loaded
        //     2. if there is no saved container, a network container is loaded
        //     3. the request times out. The example below uses a constant to manage the timeout period.
        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(ContainerHolder containerHolder) {
                ZContainerHolder.setContainerHolder(containerHolder);
                Container container = containerHolder.getContainer();
                if (!containerHolder.getStatus().isSuccess()) {
                    Log.e("CuteAnimals", "failure loading container");
                    showToast(getResources().getString(R.string.load_error));
                    return;
                }
                ZContainerHolder.setContainerHolder(containerHolder);
//                ContainerLoadedCallback.registerCallbacksForContainer(container);
//                containerHolder.setContainerAvailableListener(new ContainerLoadedCallback());
                containerLoaded = true;
                SplashActivity.pushOpenScreenEvent(SplashActivity.this, "splash");
            }
        }, 2, TimeUnit.SECONDS);
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                CommonLib.writeRequestData("Session begins with WiFi");
            } else {
                CommonLib.writeRequestData("Session begins with Mobile Network");
                try {
                    int subtype = cm.getActiveNetworkInfo().getSubtype();
                    switch (subtype) {
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                            CommonLib.writeRequestData("2g Network"); // ~ 50-100 kbps
                            Log.i("network", "NETWORK_TYPE_1xRTT");
                            break;
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                            CommonLib.writeRequestData("2g Network"); // ~ 14-64 kbps
                            Log.i("network", "NETWORK_TYPE_CDMA");
                            break;
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_UMTS:
            /*
             * Above API level 7, make sure to set android:targetSdkVersion
             * to appropriate level to use these
             */
                        case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                            CommonLib.writeRequestData("2g Network");
                            break;
                        case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                        case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                        case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                            CommonLib.writeRequestData("3g Network"); // ~ 10+ Mbps
                            Log.i("network", "NETWORK_TYPE_LTE");
                            break;// ~ 50-100 kbps
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        startMainActivity();
    }

    private void startMainActivity() {

        GetRequestManager.getInstance().addCallbacks(this);

        //initialize params
        mContext = this;
        prefs = getSharedPreferences(CommonLib.preferenceName, 0);
        imageView = (ImageView) findViewById(R.id.logo_image);
        int width = getDisplayMetrics().widthPixels;
        int height = getDisplayMetrics().heightPixels;

        //
        appConfig();

        /*try {
           // imageView.setImageResource(R.drawable.ic_splash);
            imageView.setImageBitmap(CommonLib.getBitmap(this, R.drawable.ic_splash, width, height));
        } catch (OutOfMemoryError e) {
            imageView.setImageBitmap(CommonLib.getBitmap(this, R.drawable.ic_splash, width, height));
        }
*/
       // imageView.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onPause() {
        super.onPause();
        windowHasFocus = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        windowHasFocus = true;
    }

    @Override
    protected void onDestroy() {
        GetRequestManager.getInstance().removeCallbacks(this);
        isDestroyed = true;
        windowHasFocus = false;
        super.onDestroy();
    }

    private void moveToHomePage() {
        if (AppPreferences.isUserLogIn(this) || AppPreferences.isLoginSkipped(this)) {
            if (AppPreferences.isLocationSaved(this)) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                this.finish();
            } else {
                Intent intent = new Intent(this, SelectCity.class);
                intent.putExtra("show_back", false);
                intent.putExtra("fetch_location", true);
                startActivity(intent);
                this.finish();
            }
        } else {

            Intent intent = new Intent(this, BaseLoginSignupActivity.class);
            intent.putExtra("is_logout", true);
            startActivity(intent);
            this.finish();
        }
    }

    private void checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                CommonLib.ZLog("google-play-resultcode", resultCode);
                if (resultCode == 2 && !isFinishing()) {

                    if (windowHasFocus)
                        showDialog(PLAY_SERVICES_RESOLUTION_REQUEST);

                } else {
                    moveToHomePage();
                }

            } else {
                moveToHomePage();
            }

        } else {

            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            regId = getRegistrationId(this);

            if (hardwareRegistered == 0) {
                // Call
                if (AppPreferences.isUserLogIn(this) && !regId.equals("")) {
                    sendRegistrationIdToBackend();
                    Editor editor = prefs.edit();
                    editor.putInt("HARDWARE_REGISTERED", 1);
                    editor.commit();
                }
            }

            if (regId.isEmpty()) {
                CommonLib.ZLog("GCM", "RegID is empty");
                registerInBackground();
            } else {
                CommonLib.ZLog("GCM", "already registered : " + regId);
            }
            moveToHomePage();
        }

    }

    protected Dialog onCreateDialog(int id) {

        Dialog dialog;

        switch (id) {

            case PLAY_SERVICES_RESOLUTION_REQUEST:
                AlertDialog.Builder builder_google_play_services = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
                builder_google_play_services.setMessage(getResources().getString(R.string.update_google_play_services)).setCancelable(false)
                        .setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms"));
                                    startActivityForResult(browserIntent, PLAY_SERVICES_RESOLUTION_REQUEST);
                                } catch (ActivityNotFoundException e) {

                                } catch (Exception e) {

                                }
                            }
                        }).setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        moveToHomePage();
                    }
                });
                dialog = builder_google_play_services.create();
                break;
            default:
                dialog = null;
        }
        return dialog;
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {

        final SharedPreferences prefs = getSharedPreferences(
                CommonLib.preferenceName, 0);
        String registrationId = prefs.getString(CommonLib.PROPERTY_REG_ID, "");

        if (registrationId.isEmpty()) {
            CommonLib.ZLog("GCM", "Registration not found.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(SplashActivity.this);
                    }

                    regId = gcm.register(CommonLib.GCM_SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;
                    Log.i("SplashActivity", msg);
                    storeRegistrationId(SplashActivity.this, regId);

                    if (!regId.equals(""))
                        sendRegistrationIdToBackend();

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                CommonLib.ZLog("GCM msg", msg);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void storeRegistrationId(Context context, String regId) {

        int appVersion = getAppVersion(context);
        Editor editor = prefs.edit();
        editor.putString(CommonLib.PROPERTY_REG_ID, regId);
        editor.putInt(CommonLib.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void sendRegistrationIdToBackend() {
        String deviceId = getIMEI();
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GCM_REGISTRATIONS
                + "?reg_id=" + regId + "&device_id=" + deviceId;
        CommonLib.ZLog("url", url);
        GetRequestManager.getInstance().requestHTTPThenCache(url, GCM_REGISTRATION,
                ObjectTypes.OBJECT_TYPE_GCM_PUSH, GetRequestManager.THREE_HOURS);
    }

    //IMEISV
    private String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String imeisv = telephonyManager.getDeviceId();
        CommonLib.ZLog("Device Id", imeisv);
        if (imeisv == null)
            imeisv = "Unknown";
        AppPreferences.setDeviceID(this, imeisv);
        return imeisv;
    }

    @Override
    public void onRequestStarted(String requestTag) {

    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(APP_CONFIG)) {
            AppConfig appConfig = (AppConfig) obj;
            //set all the values here.
            if (appConfig != null) {
                AllCategories.getInstance().getPhotoCateogryObjs().setArticle_category(appConfig.getPhotoCateogryObjs().getArticle_category());
                AllCategories.getInstance().getPhotoCateogryObjs().setPhoto_category(appConfig.getPhotoCateogryObjs().getPhoto_category());
                AllCategories.getInstance().getPhotoCateogryObjs().setExpert_category(appConfig.getPhotoCateogryObjs().getExpert_category());
                AllCategories.getInstance().getPhotoCateogryObjs().setExpert_base_category(appConfig.getPhotoCateogryObjs().getExpert_base_category());
                AllProducts.getInstance().setCategory_tree(appConfig.getCategoryTree());
                //AllCategories.getInstance().setCities(appConfig.getCities());
                AllCities.getInsance().setCities(appConfig.getCities());
            }
            if (appConfig != null && appConfig.isUpdateRequired()) { //response[0] != null && (Boolean) ((Object[]) (response[0]))[0]) {
                new AlertDialog.Builder(mContext).setMessage(appConfig.getUpdateText())
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                if (AppPreferences.isUserLogIn(SplashActivity.this)) {
                                    loadUserData();
                                } else {
                                    ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(SplashActivity.this), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
                                    if (objs != null) {
                                        int count = 0;
                                        for (NonLoggedInCartObj cObj : objs) {
                                            AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj(Integer.parseInt(cObj.getProductId()), cObj.getQuantity()));
                                        }
                                        AllProducts.getInstance().setCartCount(objs.size());
                                    }
                                    checkPlayServices();
                                }
                            }
                        })
                        .setCancelable(!(appConfig.isForceUpdate()))
                        .setPositiveButton(getResources().getString(R.string.update),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        CommonLib.ZLog("Update", "update now");
                                        try {
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                                    Uri.parse("market://details?id=" + getPackageName()));
                                            startActivity(browserIntent);
                                        } catch (ActivityNotFoundException e) {
                                        } catch (Exception e) {
                                        }
                                        dialog.dismiss();
                                    }
                                })
                        .show();
            } else {
                if (AppPreferences.isUserLogIn(this)) {
                    loadUserData();
                } else {
                    ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(this), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
                    if (objs != null) {
                        int count = 0;
                        for (NonLoggedInCartObj cObj : objs) {
                            AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj(Integer.parseInt(cObj.getProductId()), cObj.getQuantity()));
                        }
                        AllProducts.getInstance().setCartCount(objs.size());
                    }
                    checkPlayServices();
                }
            }

        } else if (!isDestroyed && requestTag.equalsIgnoreCase(GET_USER_DATA)) {
            checkPlayServices();
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(PRO_REQUEST_TAGS)) {
            if (AppPreferences.isUserLogIn(this)) {
                loadUserData();
            } else {
                ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(this), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
                if (objs != null) {
                    int count = 0;
                    for (NonLoggedInCartObj cObj : objs) {
                        AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj(Integer.parseInt(cObj.getProductId()), cObj.getQuantity()));
                    }
                    AllProducts.getInstance().setCartCount(objs.size());
                }
                appConfig();
            }
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(APP_CONFIG)) {
            Toast.makeText(mContext, getResources().getString(R.string.error_text), Toast.LENGTH_SHORT).show();
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(GET_USER_DATA)) {
            appConfig();
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(PRO_REQUEST_TAGS)) {
            appConfig();
        }
    }

    private void appConfig() {
        String finalUrl = AppApplication.getInstance().getBaseUrl() + AppConstants.APP_CONFIG
                + "?userid=" + AppPreferences.getUserID(this)
                + "&reg_id=" + regId + "&device_id=" + getIMEI();
        GetRequestManager.getInstance().requestCacheThenHTTP(finalUrl,
                RequestTags.APP_CONFIG,
                ObjectTypes.OBJECT_TYPE_APPCONFIG_2, GetRequestManager.THREE_DAYS);
    }

    @Override
    public void onContainerAvailable(ContainerHolder containerHolder, String s) {

    }

    /**
     * Returns an integer representing a color.
     */
    private int getColor(String key) {
        return Color.parseColor(ZContainerHolder.getContainerHolder().getContainer().getString(key));
    }

    public static void pushOpenScreenEvent(Context context, String screenName) {
        DataLayer dataLayer = TagManager.getInstance(context).getDataLayer();
        dataLayer.pushEvent("OpenScreen", DataLayer.mapOf("screenName", screenName));
    }

    public static void refresh() {
        ZContainerHolder.getContainerHolder().refresh();
    }
}
