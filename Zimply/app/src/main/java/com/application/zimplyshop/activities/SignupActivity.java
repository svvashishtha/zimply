package com.application.zimplyshop.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.SignupObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.utils.ZTracker;
import com.application.zimplyshop.utils.ZWebView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SignupActivity extends BaseActivity
        implements OnClickListener, UploadManagerCallback, RequestTags, AppConstants {

    EditText name, email, password;
    private boolean fromInside = false;
    private boolean isLoggedOut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras.containsKey("inside") && extras.get("inside") instanceof Boolean)
                fromInside = extras.getBoolean("inside");
            if (extras.containsKey("is_logout") && extras.get("is_logout") instanceof Boolean)
                isLoggedOut = extras.getBoolean("is_logout");
        }

        prefs = getSharedPreferences(CommonLib.preferenceName, 0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        View view = LayoutInflater.from(this).inflate(R.layout.login_signup_toolbar_layout, toolbar, false);
        view.findViewById(R.id.cancel_action).setOnClickListener(this);
        toolbar.addView(view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mShouldResolve = false;
        name = (EditText) findViewById(R.id.name);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                int length = text.length();

                if (!Pattern.matches("^[a-zA-Z\\s]*$", text) && length > 0) {
                    s.delete(length - 1, length);
                }
            }
        });
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //to ensure that there is no space in password field
                if (s.length() > 0) {
                    if (s.charAt(s.length() == 0 ? 0 : (s.length() - 1)) == ' ') {
                        password.setText(s.subSequence(0, s.length() == 0 ? 0 : (s.length() - 1)));
                        password.setSelection(password.getText().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ((CheckBox) findViewById(R.id.show_password_check)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password.setTransformationMethod(null);
                } else {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (CommonLib.isNetworkAvailable(SignupActivity.this)) {
                        checkLogin();
                    } else {
                        showToast("Failed to load. Check your internet connection");

                    }
                }
                return false;
            }
        });
        findViewById(R.id.facebook_login).setOnClickListener(this);
        findViewById(R.id.google_login).setOnClickListener(this);
        findViewById(R.id.signup_btn).setOnClickListener(this);
        findViewById(R.id.login_layout_btn).setOnClickListener(this);
        registerGoogleApiClient();
        UploadManager.getInstance().addCallback(this);
        addTermsString();
    }

    public void addTermsString() {
        TextView txt = (TextView) findViewById(R.id.terms_string);
        SpannableString string = new SpannableString(getString(R.string.agree_terms_click_signup));
        string.setSpan(new UnderlineSpan(), 46, 58, 0);
        string.setSpan(new StyleSpan(Typeface.BOLD), 46, 58, 0);
        string.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(SignupActivity.this, ZWebView.class);
                intent.putExtra("title", getResources().getString(R.string.about_us_terms_of_use));
                intent.putExtra("url", "http://www.zimply.in/terms");
                startActivity(intent);
            }
        }, 46, 58, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new UnderlineSpan(), 62, string.length(), 0);
        string.setSpan(new StyleSpan(Typeface.BOLD), 62, string.length(), 0);
        string.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(SignupActivity.this, ZWebView.class);
                intent.putExtra("title", getResources().getString(R.string.privacy_policy));
                intent.putExtra("url", "http://www.zimply.in/privacy-policy");
                startActivity(intent);
            }
        }, 62, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt.setText(string);
        txt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_action:
                finishActivtyMethod();
                break;
            case R.id.facebook_login:
                if (CommonLib.isNetworkAvailable(this)) {
                    ZTracker.logGAEvent(this, "Signup", "Facebook", "SignupPage");
                    signinWithFbClicked();
                } else {
                    showToast("Failed to load. Check your internet connection");

                }
                break;
            case R.id.google_login:
                if (CommonLib.isNetworkAvailable(this)) {
                    ZTracker.logGAEvent(this, "Signup", "Google", "SignupPage");
                    onSignInClicked();
                } else {
                    showToast("Failed to load. Check your internet connection");
                }
                break;
            case R.id.signup_btn:
                if (CommonLib.isNetworkAvailable(this)) {
                    checkLogin();
                } else {
                    showToast("Failed to load. Check your internet connection");
                }
                break;
            case R.id.login_layout_btn:
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("inside", fromInside);
                startActivity(intent);
                this.finish();
                overridePendingTransition(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_left_exit);
                break;
        }
    }

    private void checkLogin() {
        if (name.getText().toString().trim().length() > 0) {
            if (email.getText().toString().trim().length() > 0 && checkEmailFormat(email.getText())) {
                if (password.getText().toString().trim().length() > 0) {
                    if (password.getText().toString().trim().length() >= 8) {
                        addEmailLoginRequest();
                    } else {
                        showToast("Password has to be 8 characters long");

                    }
                } else {
                    showToast("Please enter a password");
                    //Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (email.getText().toString().length() > 0) {
                    showToast("Please enter a valid email");

                } else {
                    showToast("Please enter email");

                }
            }
        } else {
            showToast("Please enter name");

        }
    }

    private void addEmailLoginRequest() {
        ZTracker.logGAEvent(this, "Signup Email", "Zimply Signup", "Signup Page");
        String url = AppApplication.getInstance().getBaseUrl() + SIGNUP_REQUEST_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("name", name.getText().toString()));
        list.add(new BasicNameValuePair("email", email.getText().toString()));
        list.add(new BasicNameValuePair("password", password.getText().toString()));
        UploadManager.getInstance().makeAyncRequest(url, SIGNUP_REQUEST_TAG_SIGNUP, "",
                ObjectTypes.OBJECT_TYPE_SIGNUP, null, list, null);
        AppPreferences.setIsPasswordSet(SignupActivity.this, true);
    }

    private boolean checkEmailFormat(CharSequence target) {

        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();

        }
    }

    @Override
    public void onBackPressed() {
        finishActivtyMethod();
        super.onBackPressed();
    }

    private void finishActivtyMethod() {
        this.finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.animate_bottom_out);
    }

    @Override
    protected void onStart() {
        connectGoogleApiClient();
        super.onStart();

    }

    @Override
    protected void onStop() {
        disconnectApiClient();
        super.onStop();

    }

    @Override
    protected void onResume() {
        isActivityRunning = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isActivityRunning = false;
        removeDefaultGoogleLogin();
        super.onPause();
    }

    @Override
    public void onFacebookDataRetrieved(Bundle bundle) {
        addServerReuqest(bundle.getString("name"), bundle.getString("email"), bundle.getString("picture"),
                bundle.getString("token"));

    }

    @Override
    public void onGoogleDataRetreived(String name, String email, String photoUrl, String token) {
        addServerReuqest(name, email, photoUrl, token);

    }

    private void addServerReuqest(String name, String email, String photoUrl, String token) {
        if (z_ProgressDialog != null) {
            z_ProgressDialog.dismiss();
        }
        String url = AppApplication.getInstance().getBaseUrl() + SIGNUP_REQUEST_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("name", name));
        list.add(new BasicNameValuePair("email", email));
        list.add(new BasicNameValuePair("photo", photoUrl));
        list.add(new BasicNameValuePair("token", token));

        UploadManager.getInstance().makeAyncRequest(url, SIGNUP_REQUEST_TAG_SIGNUP, "",
                ObjectTypes.OBJECT_TYPE_SIGNUP, null, list, null);

    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object respose, boolean status, int parserId) {

        if (requestType == SIGNUP_REQUEST_TAG_SIGNUP) {
            if (status) {
                System.out.println("Signup response" + respose);
                z_ProgressDialog.dismiss();
                AppPreferences.setIsUserLogin(this, true);
                AppPreferences.setUserID(this, ((SignupObject) respose).getId());
                AppPreferences.setUserToken(this, ((SignupObject) respose).getToken());
                AppPreferences.setUserEmail(this, ((SignupObject) respose).getEmail());
                AppPreferences.setUserName(this, ((SignupObject) respose).getName());
                AppPreferences.setUserPhoto(this, ((SignupObject) respose).getPhoto());

                if (isLoggedOut) {
                    this.finish();
                } else {
                    if (AppPreferences.isLocationSaved(this)) {
                        Intent intent = new Intent(this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        this.finish();
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(this, SelectCity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("show_back", false);
                        this.finish();
                        startActivity(intent);
                    }
                }
            } else {
                if (respose != null) {
                    showToast(((ErrorObject) respose).getErrorMessage());

                } else {
                    showToast("Falied. Try again");

                }
                z_ProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object object) {
        if (requestType == SIGNUP_REQUEST_TAG_SIGNUP) {
            if (z_ProgressDialog != null) {
                z_ProgressDialog.dismiss();
            }
            z_ProgressDialog = ProgressDialog.show(this, null, "Loading. Please wait..");
        }
    }

    @Override
    protected void onDestroy() {
        UploadManager.getInstance().removeCallback(this);
        super.onDestroy();
    }

    @Override
    public void userDataReceived() {

        if (isLoggedOut) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.finish();
            startActivity(intent);
        } else {
            this.finish();
        }
    }
}
