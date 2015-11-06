package com.application.zimply.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimply.application.AppApplication;
import com.application.zimply.baseobjects.ErrorObject;
import com.application.zimply.baseobjects.SignupObject;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.extras.ObjectTypes;
import com.application.zimply.preferences.AppPreferences;
import com.application.zimply.serverapis.RequestTags;
import com.application.zimply.utils.CommonLib;
import com.application.zimply.utils.FacebookConnectCallback;
import com.application.zimply.utils.UploadManager;
import com.application.zimply.utils.UploadManagerCallback;
import com.application.zimply.utils.ZTracker;
import com.application.zimply.widgets.CirclePageIndicator;
import com.application.zimply.widgets.ParallaxPageTransformer;
import com.facebook.Session;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class BaseLoginSignupActivity extends BaseActivity
		implements FacebookConnectCallback, OnClickListener, UploadManagerCallback, AppConstants, RequestTags {

	int width, height;

	private boolean fromInside = false;

	private boolean isLoggedOut=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page_layout);
		prefs = getSharedPreferences(CommonLib.preferenceName, 0);

		if( getIntent() != null && getIntent().getExtras() != null ) {
			Bundle extras = getIntent().getExtras();
			if(extras.containsKey("inside") && extras.get("inside") instanceof Boolean)
				fromInside = extras.getBoolean("inside");
			if(extras.containsKey("is_logout") && extras.get("is_logout") instanceof Boolean)
				isLoggedOut = extras.getBoolean("is_logout");
		}

		ImageButton googleSignIn = (ImageButton) findViewById(R.id.sign_in_google_button);
		googleSignIn.setOnClickListener(this);
		ImageButton fbSignIn = (ImageButton) findViewById(R.id.sign_in_facebook_button);
		fbSignIn.setOnClickListener(this);

		findViewById(R.id.signup).setOnClickListener(this);
		findViewById(R.id.login).setOnClickListener(this);

		findViewById(R.id.skip_btn).setOnClickListener(this);
		ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
//		pager.setPageTransformer(true, new ZoomOutPageTransformer());
		pager.setPageTransformer(true, new ParallaxPageTransformer((float) .5, (float) .5, R.id.img));
		MyPagerAdapter adapter = new MyPagerAdapter();
		pager.setAdapter(adapter);
		((CirclePageIndicator) findViewById(R.id.pager_indicator)).setViewPager(pager);
		// Registering google API client
		mShouldResolve = false;
		registerGoogleApiClient();
		width = getDisplayMetrics().widthPixels;
		height = getDisplayMetrics().heightPixels;
		UploadManager.getInstance().addCallback(this);

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.sign_in_google_button:
            if(CommonLib.isNetworkAvailable(this)) {
			ZTracker.logGAEvent(this,"Login","Google","BaseLoginSignup");
			onSignInClicked();
        }else{
                showToast("Failed to load. Check your internet connection");

        }
			break;
		case R.id.sign_in_facebook_button:
            if(CommonLib.isNetworkAvailable(this)) {
                ZTracker.logGAEvent(this, "Login", "Facebook", "BaseLoginSignup");
                signinWithFbClicked();
            }else{
                showToast("Failed to load. Check your internet connection");

            }
			break;
		case R.id.signup:
			intent = new Intent(this, SignupActivity.class);
			intent.putExtra("inside", fromInside);
			startActivity(intent);
			overridePendingTransition(R.anim.animate_bottom_in, R.anim.abc_fade_out);
			break;
		case R.id.login:
			intent = new Intent(this, LoginActivity.class);
			intent.putExtra("inside", fromInside);
			startActivity(intent);
			overridePendingTransition(R.anim.animate_bottom_in, R.anim.abc_fade_out);
			break;
		case R.id.skip_btn:
            AppPreferences.setIsLoginSkipped(this, true);
			if(AppPreferences.isLocationSaved(this)) {
				if(fromInside) {
					this.finish();
				} else {
					intent = new Intent(this, HomeActivity.class);
					this.finish();
					startActivity(intent);
				}
            }else{
				if(fromInside) {
					this.finish();
				} else {
					intent = new Intent(this, SelectCity.class);
					intent.putExtra("show_back", false);
					this.finish();
					startActivity(intent);
				}
			}

			break;
		}
	}

	@Override
	public void onFacebookDataRetrieved(Bundle bundle) {
		addServerReuqest(bundle.getString("name"), bundle.getString("email"), bundle.getString("picture"),
				bundle.getString("token"));
		//Toast.makeText(this, "Facebook data retrieved", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() {
		super.onResume();
		isActivityRunning = true;
	}

	public void navigateToHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
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
	protected void onPause() {
		isActivityRunning = false;
		removeDefaultGoogleLogin();
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

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

	private void addServerReuqest(String name, String email, String photoUrl, String token) {
		String url = AppApplication.getInstance().getBaseUrl() + SIGNUP_REQUEST_URL;
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("name", name));
		list.add(new BasicNameValuePair("email", email));
		list.add(new BasicNameValuePair("photo", photoUrl));
		list.add(new BasicNameValuePair("token", token));

		UploadManager.getInstance().makeAyncRequest(url, SIGNUP_REQUEST_TAG_BASE, "", ObjectTypes.OBJECT_TYPE_SIGNUP,
				null, list, null);

	}

	@Override
	public void onGoogleDataRetreived(String name, String email, String photoUrl, String token) {
		addServerReuqest(name, email, photoUrl, token);
		//Toast.makeText(this, "Google Plus data received", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void uploadFinished(int requestType, String objectId, Object data, Object respose, boolean status,
			int parserId) {

		if (requestType == SIGNUP_REQUEST_TAG_BASE) {
			if (status) {
				z_ProgressDialog.dismiss();
				AppPreferences.setIsUserLogin(this, true);

				AppPreferences.setUserID(this, ((SignupObject) respose).getId());
				AppPreferences.setUserToken(this, ((SignupObject) respose).getToken());
				AppPreferences.setUserEmail(this, ((SignupObject) respose).getEmail());
				AppPreferences.setUserName(this, ((SignupObject) respose).getName());
				AppPreferences.setUserPhoto(this, ((SignupObject) respose).getPhoto());
                if(fromInside ||isLoggedOut) {
					loadUserData();
				}
				else {
					if(AppPreferences.isLocationSaved(this)) {
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
		if (requestType == SIGNUP_REQUEST_TAG_BASE) {
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

        if(isLoggedOut){
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.finish();
            startActivity(intent);
        }else {
            this.finish();
        }
	}

	public class MyPagerAdapter extends PagerAdapter {

		int[] resId = {R.drawable.ic_tut3, R.drawable.ic_tut2, R.drawable.ic_tut1, R.drawable.ic_tut4};
		String[] texts = {"", "Explore Photos & Articles", "Hire Right Home Expert", "Shop Exclusive Home Products"};

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v = LayoutInflater.from(BaseLoginSignupActivity.this).inflate(R.layout.login_pager_layout, container,
					false);
			ImageView img = (ImageView) v.findViewById(R.id.img);
			img.setImageBitmap(
					CommonLib.getBitmap(BaseLoginSignupActivity.this, resId[position], width, (4 * height) / 5));
			TextView text = (TextView) v.findViewById(R.id.text1);
			text.setText(texts[position]);
			container.addView(v, 0);
			return v;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}
}
