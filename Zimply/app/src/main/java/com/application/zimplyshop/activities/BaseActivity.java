package com.application.zimplyshop.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimply.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.FacebookConnect;
import com.application.zimplyshop.utils.FacebookConnectCallback;
import com.application.zimplyshop.utils.UiUtils;
import com.facebook.Session;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;

public class BaseActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, FacebookConnectCallback, GetRequestListener {

    public static final int RC_SIGN_IN = 0;
    public Toolbar toolbar;
    public ProgressDialog z_ProgressDialog;
    public String TAG = "Google Plus Login";
    public SharedPreferences prefs;
    /* Should we automatically resolve ConnectionResults when possible? */
    protected boolean mShouldResolve = false;
    ProgressBar progress;
    TextView nullcaseText;
    TextView quoteText;
    boolean isRequestFailed;
    LinearLayout retryLayout;
    LinearLayout filterLayout;
    int toolbarHeight, searchBarLayoutHeight, searchBarHeight;
    LinearLayout categoriesLayout, selectfiltersLayout, sortByLayout;
    Toast toast;
    // Google Plus login
    GoogleApiClient mGoogleApiClient;
    int PROFILE_PIC_SIZE = 400;
    boolean isActivityRunning;
    String personName, personPhotoUrl, email, personId;
    AsyncTask task = new AsyncTask() {
        @Override
        protected String doInBackground(Object... params) {
            String scope = "oauth2:" + Scopes.PLUS_LOGIN;
            try {
                // We can retrieve the token to check via
                // tokeninfo or to pass to a service-side
                // application.
                String token = GoogleAuthUtil.getToken(BaseActivity.this,
                        Plus.AccountApi.getAccountName(mGoogleApiClient), scope);
                return token;
            } catch (UserRecoverableAuthException e) {
                // This error is recoverable, so we could fix this
                // by displaying the intent to the user.
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String token) {
            CommonLib.ZLog("Google Plus Access Tokem", token);

            if (z_ProgressDialog != null)
                z_ProgressDialog.dismiss();
            // onGoogleDataRetreived(personName, email, personPhotoUrl, token);
        }

    };
    private boolean mIsResolving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Returns display metrics
     *
     * @return
     */
    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    @SuppressLint("NewApi")
    public void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.app_primary_color_dark));
        }

    }

    public void showToast(String message) {
        if (toast == null) {
            toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        toast.setText(message);
        //toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show Loading for all activities
     */
    public void setLoadingVariables() {
        progress = (ProgressBar) findViewById(R.id.progress);
        nullcaseText = (TextView) findViewById(R.id.nullcase_text);
        retryLayout = (LinearLayout) findViewById(R.id.retry_layout);
        quoteText = (TextView) findViewById(R.id.quote);

    }

    public void setFilterVariables() {
        filterLayout = (LinearLayout) findViewById(R.id.filter_layout);
        categoriesLayout = (LinearLayout) findViewById(R.id.cat_filter_layout);
        selectfiltersLayout = (LinearLayout) findViewById(R.id.filter_filter_layout);
        sortByLayout = (LinearLayout) findViewById(R.id.sort_filter_layout);

        setToolbarHeight();
        setSearchBarHeight();
        setSearchBarLayoutHeight();

    }

    public void setSearchBarLayoutHeight() {
        searchBarLayoutHeight = getResources().getDimensionPixelSize(R.dimen.filter_layout_height);
    }

    public void setSearchBarHeight() {
        searchBarHeight = getResources().getDimensionPixelSize(R.dimen.padding_top_66);
    }

    public void setToolbarHeight() {
        this.toolbarHeight = getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material);
    }

    /**
     * Method shows the listview as soon as the data is successfully received
     * from the server and loaded into the adapter
     */
    public void showView() {
        progress.setVisibility(View.GONE);
        nullcaseText.setVisibility(View.GONE);
        retryLayout.setVisibility(View.GONE);
        quoteText.setVisibility(View.GONE);

    }

    /**
     * Method shows loading view when a server request is generated
     */
    public void showLoadingView() {
        progress.setVisibility(View.VISIBLE);
        nullcaseText.setVisibility(View.GONE);
        retryLayout.setVisibility(View.GONE);
        quoteText.setVisibility(View.VISIBLE);
        quoteText.setText(UiUtils.getTextFromRes(this));
        retryLayout.setVisibility(View.GONE);
    }

    /**
     * Method shows the Error view when the server request could not be
     * completed
     */
    public void showNetworkErrorView() {
        isRequestFailed = true;
        nullcaseText.setVisibility(View.VISIBLE);
        nullcaseText.setText("");
        //nullcaseText.setBackgroundResource(R.drawable.green_btn_rectangle_bg);
        nullcaseText.setTypeface(null, Typeface.NORMAL);
        nullcaseText.setTextColor(getResources().getColor(R.color.heading_text_color));
        //changeLeftDrawable(R.drawable.ic_navigation_refresh);
        quoteText.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        retryLayout.setVisibility(View.VISIBLE);
        retryLayout.setBackgroundResource(R.drawable.ic_navigation_refresh);
    }

    public void showNullCaseView(String text) {
        nullcaseText.setVisibility(View.VISIBLE);
        nullcaseText.setText(text);
        nullcaseText.setTypeface(null, Typeface.BOLD);
        nullcaseText.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        nullcaseText.setTextColor(getResources().getColor(R.color.heading_text_color));
        changeLeftDrawable(0);
        quoteText.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        retryLayout.setVisibility(View.VISIBLE);
        retryLayout.setBackgroundResource(R.drawable.ic_null_case);
    }

    /**
     * Method to change the Left drawbale of the nullcase text view
     *
     * @param drawable
     */
    public void changeLeftDrawable(int drawable) {
        if (drawable != 0) {
            Drawable drawableTop = getResources().getDrawable(drawable);
            if (drawableTop != null)
                drawableTop.setBounds(0, 0, drawableTop.getIntrinsicWidth(), drawableTop.getIntrinsicHeight());

            nullcaseText.setCompoundDrawables(null, drawableTop, null, null);
        } else {
            nullcaseText.setCompoundDrawables(null, null, null, null);
        }

    }

    public void changeViewVisiblity(View view, int visiblity) {
        view.setVisibility(visiblity);
    }

    protected void scrollToolbarAndHeaderBy(int dy) {
        // dy +ve means scrolling upward and -ve mean scrolling downward
        if (dy > 0) {
            float reqTrans = toolbar.getTranslationY() + dy;
            if (reqTrans < -toolbarHeight) {
                reqTrans = -toolbarHeight;
            } else if (reqTrans > 0)
                reqTrans = 0;
            toolbar.setTranslationY(reqTrans);
            if (toolbar.getTranslationY() == 0) {
                if (-filterLayout.getTranslationY() > (toolbarHeight
                        + getResources().getDimensionPixelSize(R.dimen.margin_small))) {
                    filterLayout.setTranslationY(
                            -toolbarHeight - getResources().getDimensionPixelSize(R.dimen.margin_small));
                }
                float reqTransSearch = filterLayout.getTranslationY() + dy;
                if (reqTransSearch < -searchBarLayoutHeight) {
                    reqTransSearch = -searchBarLayoutHeight;
                } else if (reqTransSearch > 0)
                    reqTransSearch = 0;
                filterLayout.setTranslationY(reqTransSearch);
            }
        } else {
            float reqTrans = filterLayout.getTranslationY() + dy;
            if (reqTrans < -searchBarLayoutHeight) {
                reqTrans = -searchBarLayoutHeight;
            } else if (reqTrans > 0)
                reqTrans = 0;
            filterLayout.setTranslationY(reqTrans);
            if (Math.abs(filterLayout.getTranslationY()) > searchBarHeight) {
                float toolbarTrans = toolbar.getTranslationY() + dy;
                if (toolbarTrans < -toolbarHeight) {
                    toolbarTrans = -toolbarHeight;
                } else if (toolbarTrans > 0)
                    toolbarTrans = 0;
                toolbar.setTranslationY(toolbarTrans);
            }
        }
    }

    protected void scrollToolbarBy(int dy) {
        // dy +ve means scrolling upward and -ve mean scrolling downward
        if (dy > 0) {
            float reqTrans = toolbar.getTranslationY() + dy;
            if (reqTrans < -toolbarHeight) {
                reqTrans = -toolbarHeight;
            } else if (reqTrans > 0)
                reqTrans = 0;
            toolbar.setTranslationY(reqTrans);

        } else {

            float toolbarTrans = toolbar.getTranslationY() + dy;
            if (toolbarTrans < -toolbarHeight) {
                toolbarTrans = -toolbarHeight;
            } else if (toolbarTrans > 0)
                toolbarTrans = 0;
            toolbar.setTranslationY(toolbarTrans);

        }
    }

    public void loadImage(String url, ImageView imageView, int width, int height, boolean isToBeRounded,
                          boolean isFastblurr) {
        new ImageLoaderManager(this).setImageFromUrl(url, imageView, "user", getDisplayMetrics().widthPixels / 2,
                getDisplayMetrics().heightPixels / 2, isToBeRounded, isFastblurr);

    }

    public void connectGoogleApiClient() {
        mGoogleApiClient.connect();
    }

    public void disconnectApiClient() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void registerGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE).build();
    }

    protected void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and
        // automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();
        z_ProgressDialog = ProgressDialog.show(this, null, "Connecting with google. Please wait...");
    }

    protected void removeDefaultGoogleLogin() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();

        }

    }

    // Facebook Response

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services. The user needs to select
        // an account,
        // grant permissions or resolve an error in order to sign in. Refer to
        // the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {

                try {

                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {

                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                if (z_ProgressDialog != null)
                    z_ProgressDialog.dismiss();
                //Toast.makeText(this, connectionResult.getResolution().describeContents(), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (z_ProgressDialog != null)
                z_ProgressDialog.dismiss();
            // ----------------------Show the signed-out UI

        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (isActivityRunning && z_ProgressDialog != null) {
            z_ProgressDialog.dismiss();
        }
        if ( isActivityRunning && z_ProgressDialog != null && !z_ProgressDialog.isShowing())
            z_ProgressDialog = ProgressDialog.show(this, null, "Fetching Details.Please wait..");
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        getProfileInformation();

    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {

                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

                personName = currentPerson.getDisplayName();
                personPhotoUrl = currentPerson.getImage().getUrl();
                personId = currentPerson.getId();

                email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                personPhotoUrl = personPhotoUrl.substring(0, personPhotoUrl.length() - 2) + PROFILE_PIC_SIZE;
                /*
                 * String accessToken =
				 * GoogleAuthUtil.getToken(getApplicationContext(), Plus),
				 * "oauth2:" + Scopes.PLUS_LOGIN + " " + Scopes.PROFILE +
				 * " https://www.googleapis.com/auth/plus.profile.emails.read");
				 */
                // task.execute();
                String token = "1234343432";
                onGoogleDataRetreived(personName, email, personPhotoUrl, token);
                //

            } else {
                if (z_ProgressDialog != null)
                    z_ProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "An error occured. Try again.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onGoogleDataRetreived(String name, String email, String photoUrl, String token) {

    }

    @Override
    public void onConnectionSuspended(int arg0) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == RC_SIGN_IN) {

            // If the error resolution was not successful we should not
            // resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
                if (z_ProgressDialog != null)
                    z_ProgressDialog.dismiss();
            }

            mIsResolving = false;
            if (mGoogleApiClient == null)
                registerGoogleApiClient();
            mGoogleApiClient.connect();
            if (z_ProgressDialog != null && z_ProgressDialog.isShowing())
                z_ProgressDialog = ProgressDialog.show(this, null, "Getting google login details.Please wait..");

        } else {
            // Facebook Login

            try {
                super.onActivityResult(requestCode, resultCode, intent);
                Session.getActiveSession().onActivityResult(this, requestCode, resultCode, intent);

            } catch (Exception w) {

                w.printStackTrace();

                try {
                    com.facebook.Session fbSession = com.facebook.Session.getActiveSession();
                    if (fbSession != null) {
                        fbSession.closeAndClearTokenInformation();
                    }
                    com.facebook.Session.setActiveSession(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void signinWithFbClicked() {
        String regId = prefs.getString("registration_id", "");
        FacebookConnect facebookConnect = new FacebookConnect(this, 1, "", true, regId);
        facebookConnect.execute();
        z_ProgressDialog = ProgressDialog.show(this, null, "Connecting with facebook. Please wait...");
    }

    @Override
    public void response(Bundle bundle) {
        /* Is there a ConnectionResult resolution in progress? */
        String error_responseCode = "";
        String error_exception = "";
        String error_stackTrace = "";
        String login_type = "";

        login_type = "FBLogin";
        error_exception = "";
        error_responseCode = "";
        error_stackTrace = "";

        if (bundle.containsKey("error_responseCode"))
            error_responseCode = bundle.getString("error_responseCode");

        if (bundle.containsKey("error_exception"))
            error_exception = bundle.getString("error_exception");

        if (bundle.containsKey("error_stackTrace"))
            error_stackTrace = bundle.getString("error_stackTrace");

        try {

            int status = bundle.getInt("status");

            if (status == 0) {

                if (!error_exception.equals("") || !error_responseCode.equals("") || !error_stackTrace.equals(""))
                    // BTODO
                    // sendFailedLogsToServer();

                    if (bundle.getString("errorMessage") != null) {
                        String errorMessage = bundle.getString("errorMessage");
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.err_occurred, Toast.LENGTH_SHORT).show();
                    }
                if (z_ProgressDialog != null && z_ProgressDialog.isShowing())
                    z_ProgressDialog.dismiss();
            } else {
                Editor editor = prefs.edit();
                editor.putInt("uid", bundle.getInt("uid"));
                if (bundle.containsKey("email"))
                    editor.putString("email", bundle.getString("email"));
                if (bundle.containsKey("username"))
                    editor.putString("username", bundle.getString("username"));
                if (bundle.containsKey("thumbUrl"))
                    editor.putString("thumbUrl", bundle.getString("thumbUrl"));
                String token = bundle.getString("access_token");
                System.out.println(token);
                editor.putString("access_token", bundle.getString("access_token"));
                editor.putBoolean("verifiedUser", bundle.getBoolean("verifiedUser"));
                editor.commit();

                CommonLib.ZLog("login", "FACEBOOK");

                // if (REQUEST_CODE == START_LOGIN_INTENT
                // || REQUEST_CODE == START_ACTIVITY_LOGIN_INTENT) {

                if (z_ProgressDialog != null && z_ProgressDialog.isShowing())
                    z_ProgressDialog.dismiss();
                onFacebookDataRetrieved(bundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onFacebookDataRetrieved(Bundle bundle) {

    }

    public void showAlertDialogForRating() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(
                R.layout.liked_idea_dialog_layout, null);
        alertDialog.setView(view);
        final AlertDialog dialog = alertDialog.create();
        Button hateBtn = (Button) view.findViewById(R.id.hate_it_btn);
        hateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AppPreferences.setIsAppHated(BaseActivity.this, true);
                AppPreferences.setIsRateLater(BaseActivity.this, false);
                AppPreferences.setOpenRateCount(BaseActivity.this, (AppPreferences.getOpenRateCount(BaseActivity.this) + 20));
                dialog.dismiss();
            }
        });
        Button likeBtn = (Button) view.findViewById(R.id.liked_it);
        // likeBtn.setBackgroundResource(R.drawable.z_green_btn_bg);
        likeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPlayStoreLink();
                AppPreferences.setIsAppRated(BaseActivity.this, true);
                dialog.dismiss();
            }
        });
        Button neutralBtn = (Button) view.findViewById(R.id.neutral);
        // neutralBtn .setBackgroundResource(R.drawable.z_green_btn_bg);
        neutralBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AppPreferences.setIsAppHated(BaseActivity.this, false);
                AppPreferences.setIsRateLater(BaseActivity.this, true);
                AppPreferences.setOpenRateCount(BaseActivity.this, getAppOpenRateCount());
                dialog.dismiss();
            }
        });

        ImageView image = (ImageView) view.findViewById(R.id.stars);
        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPlayStoreLink();
                AppPreferences.setIsAppRated(BaseActivity.this, true);
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AppPreferences.setIsAppHated(BaseActivity.this, false);
                AppPreferences.setIsRateLater(BaseActivity.this, true);
                AppPreferences.setOpenRateCount(BaseActivity.this, getAppOpenRateCount());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void loadUserData() {
        GetRequestManager.getInstance().addCallbacks(this);
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_USER_DATA + "?userid=" + AppPreferences.getUserID(this);
        GetRequestManager.getInstance().requestHTTPThenCache(url, RequestTags.GET_USER_DATA, ObjectTypes.OBJECT_USER_DETAILS, GetRequestManager.THREE_DAYS);
    }

    public int getAppOpenRateCount() {
        if (AppPreferences.getOpenRateCount(this) > 3 && AppPreferences.getOpenRateCount(this) < 9) {
            return AppPreferences.getOpenRateCount(this) + 8;
        } else if (AppPreferences.getOpenRateCount(this) > 15) {
            return AppPreferences.getOpenRateCount(this) + 10;
        } else {
            return AppPreferences.getOpenRateCount(this) + 5;
        }
    }

    protected void showPlayStoreLink() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="
                            + appPackageName)));
        }

    }

    public void userDataReceived() {

    }

    @Override
    public void onRequestStarted(String requestTag) {

    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (requestTag.equalsIgnoreCase(RequestTags.GET_USER_DATA)) {
            userDataReceived();
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {

    }
}
