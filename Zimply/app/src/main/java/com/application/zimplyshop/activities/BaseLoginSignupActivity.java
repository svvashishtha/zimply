package com.application.zimplyshop.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.application.zimplyshop.utils.FacebookConnectCallback;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.utils.ZTracker;
import com.application.zimplyshop.widgets.CirclePageIndicator;
import com.application.zimplyshop.widgets.ParallaxPageTransformer;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class BaseLoginSignupActivity extends BaseActivity
		implements FacebookConnectCallback, OnClickListener, UploadManagerCallback, AppConstants, RequestTags {

	int width, height;

	private boolean fromInside = false;

	private boolean isLoggedOut=false;

	boolean isDestroyed;

	Runnable r2 = new Runnable() {

		@Override
		public void run() {
			if (!isDestroyed) {
				r2Running = true;
				animateArrowForward();
			}
			// fadeOutView((LinearLayout) findViewById(R.id.username_layout));
		}

	};

	boolean isArrowHidden;

	ImageView arrowRight;

	private boolean r2Running = false   ;

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


		TextView textView = (TextView)findViewById(R.id.skip_btn);
		SpannableString content = new SpannableString("Skip Login");
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		textView.setText(content);
		textView.setOnClickListener(this);

		final ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
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
		arrowRight = (ImageView) findViewById(R.id.arrow_right);
		arrowRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pager.setCurrentItem(pager.getCurrentItem()+1);
			}
		});
		arrowRight.setImageBitmap(CommonLib.getBitmap(this, R.drawable.ic_arrow_right,
				getResources().getDimensionPixelSize(R.dimen.height48),
				getResources().getDimensionPixelSize(R.dimen.height48)));


		final Handler arrowHandler = new Handler();
		if (!r2Running)
			arrowHandler.postDelayed(r2, 1000);

		pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if (positionOffsetPixels > 20 && arrowRight.isShown() && !isArrowHidden) {
					isArrowHidden = true;
					arrowHandler.removeCallbacks(r2);
					fadeOutView(arrowRight);
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	private void fadeOutView(final View view) {
		if (view != null) {
			ObjectAnimator anim = ObjectAnimator.ofFloat(view, View.ALPHA, 1, 0);
			anim.setDuration(500);

			anim.start();
			anim.addListener(new Animator.AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {

				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					view.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationCancel(Animator animation) {

				}
			});
		}

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.sign_in_google_button:
            if(CommonLib.isNetworkAvailable(this)) {
			ZTracker.logGAEvent(this,"Login","Google","BaseLoginSignup");
			DataLayer dataLayer = TagManager.getInstance(this).getDataLayer();
			dataLayer.pushEvent("SignUp", DataLayer.mapOf("SignUpDone", "Yes", "NetworkUsed", "G+"));
			onSignInClicked();
        }else{
                showToast("Failed to load. Check your internet connection");

        }
			break;
		case R.id.sign_in_facebook_button:
            if(CommonLib.isNetworkAvailable(this)) {
                ZTracker.logGAEvent(this, "Login", "Facebook", "BaseLoginSignup");
				DataLayer dataLayer = TagManager.getInstance(this).getDataLayer();
				dataLayer.pushEvent("SignUp", DataLayer.mapOf("SignUpDone", "Yes", "NetworkUsed", "FB"));
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
					intent.putExtra("fetch_location",true);
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

	private void addServerReuqest(String name, String email, String photoUrl, String token) {
		if(z_ProgressDialog!=null) {
            z_ProgressDialog.dismiss();
		}
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
						intent.putExtra("fetch_location",true);
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
			z_ProgressDialog = ProgressDialog.show(this, null, "Signing in. Please wait..");
		}
	}

	@Override
	protected void onDestroy() {
		isDestroyed=true;
		UploadManager.getInstance().removeCallback(this);
		super.onDestroy();
	}

	@Override
	public void userDataReceived() {

        if(isLoggedOut){
			if(AppPreferences.isLocationSaved(this)) {
				Intent intent = new Intent(this, HomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				this.finish();
				startActivity(intent);
			}else{
				Intent intent = new Intent(this, SelectCity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				intent.putExtra("show_back", false);
				intent.putExtra("fetch_location",true);
				this.finish();
				startActivity(intent);
			}
        }else {
            this.finish();
        }
	}

	public class MyPagerAdapter extends PagerAdapter {

		int[] resId = {R.drawable.ic_tut1,R.drawable.ic_tut2, R.drawable.ic_tut3,  R.drawable.ic_tut4,R.drawable.ic_tut5};
		String[] texts = {"","Shop for furniture & home decor products online.", "For products with the Zi Experience tag, you can visit the store before you buy.", "Super-fast delivery and installation.","Get Zimply-quality assured products at the best prices." };

		@Override
		public int getCount() {
			return 5;
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
			text.setVisibility(View.GONE);
			//text.setText(texts[position]);
			container.addView(v, 0);
			return v;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	private void animateArrowForward() {
		ObjectAnimator anim = ObjectAnimator.ofFloat(arrowRight, View.TRANSLATION_X, 0, 30f);
		anim.setDuration(500);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.addListener(new Animator.AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				animateArrowBackward();
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});
		anim.start();
	}

	private void animateArrowBackward() {
		ObjectAnimator anim = ObjectAnimator.ofFloat(arrowRight, View.TRANSLATION_X, 30, 0);
		anim.setDuration(500);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.addListener(new Animator.AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				animateArrowForward();
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});
		anim.start();
	}

}
