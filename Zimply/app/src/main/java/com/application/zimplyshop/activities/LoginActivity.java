
package com.application.zimplyshop.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.application.zimply.R;
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

public class LoginActivity extends BaseActivity
		implements OnClickListener, UploadManagerCallback, RequestTags, AppConstants {

	EditText email, password;

	private boolean fromInside = false;
	private boolean isLoggedOut=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		if( getIntent() != null && getIntent().getExtras() != null ) {
			Bundle extras = getIntent().getExtras();
			if(extras.containsKey("inside") && extras.get("inside") instanceof Boolean)
				fromInside = extras.getBoolean("inside");
			if(extras.containsKey("is_logout") && extras.get("is_logout") instanceof Boolean)
				isLoggedOut = extras.getBoolean("is_logout");
		}

		prefs = getSharedPreferences(CommonLib.preferenceName, 0);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		View view = LayoutInflater.from(this).inflate(R.layout.login_signup_toolbar_layout, toolbar, false);
		view.findViewById(R.id.cancel_action).setOnClickListener(this);
		((TextView) view.findViewById(R.id.signup_layout_text)).setText("LOGIN");
		toolbar.addView(view);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		mShouldResolve = false;
		findViewById(R.id.login_btn).setOnClickListener(this);
		findViewById(R.id.facebook_login).setOnClickListener(this);
		findViewById(R.id.google_login).setOnClickListener(this);
		findViewById(R.id.signup_layout_btn).setOnClickListener(this);
		TextView textView = (TextView) findViewById(R.id.forgot_password);
		textView.setOnClickListener(this);
		String udata = "Forgot password ?";
		SpannableString content = new SpannableString(udata);
		content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
		textView.setText(content);
		registerGoogleApiClient();
		UploadManager.getInstance().addCallback(this);

		addTermsString();
	}

	public void addTermsString(){
		TextView txt = (TextView)findViewById(R.id.terms_string);
		SpannableString string = new SpannableString(getString(R.string.agree_terms_click_logging));
        string.setSpan(new UnderlineSpan(),37,54,0);
        string.setSpan(new StyleSpan(Typeface.BOLD),37,54,0);
        string.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginActivity.this, ZWebView.class);
                intent.putExtra("title", getResources().getString(R.string.about_us_terms_of_use));
                intent.putExtra("url", "http://www.zimply.in/terms");
                startActivity(intent);
            }
        },37,54,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new UnderlineSpan(),58,string.length(),0);
        string.setSpan(new StyleSpan(Typeface.BOLD),58,string.length(),0);
        string.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginActivity.this, ZWebView.class);
                intent.putExtra("title", getResources().getString(R.string.privacy_policy));
                intent.putExtra("url", "http://www.zimply.in/privacy-policy");
                startActivity(intent);
            }
        },58,string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
			if(CommonLib.isNetworkAvailable(this)) {
			ZTracker.logGAEvent(this , "Login","Facebook","LoginPage");
			signinWithFbClicked();
			}else{
                showToast("Failed to load. Check your internet connection");
                //   Toast.makeText(this,"Failed to load. Check your internet connection",Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.google_login:
			if(CommonLib.isNetworkAvailable(this)) {
			ZTracker.logGAEvent(this , "Login","Google","LoginPage");
			onSignInClicked();
		}else{
                showToast("Failed to load. Check your internet connection");

		}
			break;
		case R.id.login_btn:
            if(CommonLib.isNetworkAvailable(this)) {
			checkLogin();
            }else{
                showToast("Failed to load. Check your internet connection");

            }
			break;
		case R.id.signup_layout_btn:
			Intent intent = new Intent(this, SignupActivity.class);
			intent.putExtra("inside", fromInside);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_right_exit);
			break;
		case R.id.forgot_password:
			Intent passwrdIntent = new Intent(this, ForgotPassword.class);
			startActivity(passwrdIntent);

			overridePendingTransition(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_left_exit);
			break;
		}
	}

	private void checkLogin() {

		if (email.getText().toString().trim().length() > 0 && checkEmailFormat(email.getText())) {
			if (password.getText().toString().trim().length() > 0) {
				if (password.getText().toString().trim().length() >= 6) {
					addEmailLoginRequest();
				} else {
                    showToast("Password has to be 6 characters long");

				}
			} else {
                showToast("Please enter a password");

			}
		} else {
			if (email.getText().toString().trim().length() > 0) {
                showToast("Please enter a valid email");

			} else {
                showToast("Please enter email");

			}
		}

	}

	private void addEmailLoginRequest() {
		ZTracker.logGAEvent(this , "Login Email","Zimply Login","Login Page");
		String url = AppApplication.getInstance().getBaseUrl() + SIGNUP_REQUEST_URL;
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("email", email.getText().toString()));
		list.add(new BasicNameValuePair("password", password.getText().toString()));
		UploadManager.getInstance().makeAyncRequest(url, SIGNUP_REQUEST_TAG_LOGIN, "", ObjectTypes.OBJECT_TYPE_SIGNUP,
				null, list, null);
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
		// Toast.makeText(this, "Facebook data retrieved",
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onGoogleDataRetreived(String name, String email, String photoUrl, String token) {
		addServerReuqest(name, email, photoUrl, token);
		// Toast.makeText(this, "Google Plus data received",
		// Toast.LENGTH_SHORT).show();
	}

	private void addServerReuqest(String name, String email, String photoUrl, String token) {
		String url = AppApplication.getInstance().getBaseUrl() + SIGNUP_REQUEST_URL;
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("name", name));
		list.add(new BasicNameValuePair("email", email));
		list.add(new BasicNameValuePair("photo", photoUrl));
		list.add(new BasicNameValuePair("token", token));

		UploadManager.getInstance().makeAyncRequest(url, SIGNUP_REQUEST_TAG_LOGIN, "", ObjectTypes.OBJECT_TYPE_SIGNUP,
				null, list, null);

	}

	@Override
	public void uploadFinished(int requestType, String objectId, Object data, Object respose, boolean status,
			int parserId) {

		if (requestType == SIGNUP_REQUEST_TAG_LOGIN) {
			if (status) {
				System.out.println("Signup response" + respose);
				z_ProgressDialog.dismiss();
				AppPreferences.setIsUserLogin(this, true);
				AppPreferences.setUserID(this, ((SignupObject) respose).getId());
				AppPreferences.setUserToken(this, ((SignupObject) respose).getToken());
                AppPreferences.setUserEmail(this, ((SignupObject) respose).getEmail());
				AppPreferences.setUserName(this, ((SignupObject) respose).getName());
				AppPreferences.setUserPhoto(this, ((SignupObject) respose).getPhoto());

				if(fromInside||isLoggedOut) {
                    this.finish();
                }
				else {if(AppPreferences.isLocationSaved(this)) {
					Intent intent = new Intent(this, HomeActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					this.finish();
					startActivity(intent);
				}else{
					Intent intent = new Intent(this, SelectCity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intent.putExtra("show_back", false);
					this.finish();
					startActivity(intent);
				}
				}
			} else {
				z_ProgressDialog.dismiss();
				if (respose != null) {
                    showToast(((ErrorObject) respose).getErrorMessage());
				} else {
                    showToast("Falied. Try again");
				}
			}
		}
	}

	@Override
	public void uploadStarted(int requestType, String objectId, int parserId, Object object) {
		if (requestType == SIGNUP_REQUEST_TAG_LOGIN) {
			if (z_ProgressDialog != null) {
				z_ProgressDialog.dismiss();
			}
			z_ProgressDialog = ProgressDialog.show(this, null, "Loading. Please wait..");
		}
	}


	@Override
	public void userDataReceived() {

		if(isLoggedOut){
			Intent intent = new Intent(this, HomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			this.finish();
			startActivity(intent);
		}else {
			this.finish();
		}
	}


	@Override
	protected void onDestroy() {
		UploadManager.getInstance().removeCallback(this);
		super.onDestroy();
	}
}