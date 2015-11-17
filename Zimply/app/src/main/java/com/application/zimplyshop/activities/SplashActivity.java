package com.application.zimplyshop.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.zimply.R;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.notikum.notifypassive.UninstallSession;
import com.notikum.notifypassive.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SplashActivity extends BaseActivity implements RequestTags,GetRequestListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    boolean isDestroyed;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String regId;
    int hardwareRegistered = 0;
    private boolean windowHasFocus = false;
    private ImageView imageView;
    private Activity mContext;

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
        mContext = this;
        prefs = getSharedPreferences(CommonLib.preferenceName, 0);
        imageView = (ImageView) findViewById(R.id.logo_image);
        int width = getDisplayMetrics().widthPixels;
        int height = getDisplayMetrics().heightPixels;
        try {
            imageView.setImageResource(R.drawable.ic_splash);
        } catch (OutOfMemoryError e) {
            imageView.setImageBitmap(CommonLib.getBitmap(this, R.drawable.ic_splash, width, height));
        }

        GetRequestManager.getInstance().addCallbacks(this);
//        loadProCatData();
        appConfig();
        imageView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        GetRequestManager.getInstance().removeCallbacks(this);
        isDestroyed = true;
        super.onDestroy();
    }

    private void moveToHomePage() {
        if (AppPreferences.isUserLogIn(this) || AppPreferences.isLoginSkipped(this)) {
            if(AppPreferences.isLocationSaved(this)) {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                this.finish();
            }else{
                Intent intent = new Intent(this, SelectCity.class);
                intent.putExtra("show_back", false);
                startActivity(intent);
                this.finish();
            }
        }else {

            Intent intent = new Intent(this, BaseLoginSignupActivity.class);
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

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
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

                    regId = gcm.register(CommonLib.GCM_SENDER_ID+","+ Constants.GCM_SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;
                    Log.i("SplashActivity",msg);
                    storeRegistrationId(SplashActivity.this, regId);

                    if (AppPreferences.isUserLogIn(SplashActivity.this) && !regId.equals(""))
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
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GCM_REGISTRATIONS
                + "?reg_id=" + regId + "&device_id=" + getIMEI();
        CommonLib.ZLog("url", url);
        GetRequestManager.getInstance().requestHTTPThenCache(url, GCM_REGISTRATION,
                ObjectTypes.OBJECT_TYPE_GCM_PUSH, GetRequestManager.THREE_HOURS);
    }

    //IMEISV
    private String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String imeisv = telephonyManager.getDeviceId();
        CommonLib.ZLog("Device Id", imeisv);
        if(imeisv==null)
            imeisv="Unknown";
        return imeisv;
    }

    @Override
    protected void onPause() {
        UninstallSession.appFocusChange();
        super.onPause();
    }

    @Override
    public void onRequestStarted(String requestTag) {

    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(APP_CONFIG)) {
            AppConfig appConfig = (AppConfig) obj;
            //set all the values here.
            if(appConfig != null ) {
                AllCategories.getInstance().getPhotoCateogryObjs().setArticle_category(appConfig.getPhotoCateogryObjs().getArticle_category());
                AllCategories.getInstance().getPhotoCateogryObjs().setPhoto_category(appConfig.getPhotoCateogryObjs().getPhoto_category());
                AllCategories.getInstance().getPhotoCateogryObjs().setExpert_category(appConfig.getPhotoCateogryObjs().getExpert_category());
                AllCategories.getInstance().getPhotoCateogryObjs().setExpert_base_category(appConfig.getPhotoCateogryObjs().getExpert_base_category());
                AllProducts.getInstance().setCategory_tree(appConfig.getCategoryTree());
               //AllCategories.getInstance().setCities(appConfig.getCities());
                AllCities.getInsance().setCities(appConfig.getCities());
            }
            if (appConfig != null && appConfig.isUpdateRequired() ){ //response[0] != null && (Boolean) ((Object[]) (response[0]))[0]) {
                new AlertDialog.Builder(mContext).setMessage(appConfig.getMessage())
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
                                            AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj(Integer.parseInt(cObj.getProductId()),cObj.getQuantity()));
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


                            AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj(Integer.parseInt(cObj.getProductId()),cObj.getQuantity()));
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
                        AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj(Integer.parseInt(cObj.getProductId()),cObj.getQuantity()));
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
                + "?userid="+AppPreferences.getUserID(this)
                + "&reg_id=" + regId + "&device_id=" + getIMEI();
        GetRequestManager.getInstance().requestCacheThenHTTP(finalUrl,
                RequestTags.APP_CONFIG,
                ObjectTypes.OBJECT_TYPE_APPCONFIG_2, GetRequestManager.THREE_DAYS);
    }

//    public void loadProCatData(){
//        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.PRO_CATEGORY_TREE;
//        GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.PRO_REQUEST_TAGS, ObjectTypes.OBJECT_TYPE_FILTER_PRODUCTS);
//    }

    /*public void loadUserData(){
        String url = AppApplication.getInstance().getBaseUrl()+AppConstants.GET_USER_DATA+"?userid="+AppPreferences.getUserID(this);
        GetRequestManager.getInstance().requestHTTPThenCache(url, RequestTags.GET_USER_DATA, ObjectTypes.OBJECT_USER_DETAILS, GetRequestManager.THREE_DAYS);
    }
*/

}
