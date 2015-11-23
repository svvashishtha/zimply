package com.application.zimplyshop.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.ZWebView;
import com.google.android.gms.plus.PlusOneButton;

public class AboutUsPage extends BaseActivity {

	private int width;
	PlusOneButton mPlusOneButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		
		width = getWindowManager().getDefaultDisplay().getWidth();
		
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		addToolbarView(toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		setStatusBarColor();
		//setLoadingVariables();
		//setFilterVariables();
//		setUpActionBar();
		fixsizes();
		setListeners();
		
		mPlusOneButton = (PlusOneButton) findViewById(R.id.plus_one_button);
 
		ImageView zLogo = (ImageView) findViewById(R.id.zimply_logo);
		zLogo.setImageBitmap(CommonLib.getBitmap(this, R.drawable.ic_app_launcher_icon, width / 3, width / 3));
		zLogo.getLayoutParams().width = width / 3;
		zLogo.getLayoutParams().height = width / 3;
        try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            ((TextView)findViewById(R.id.home_version)).setText("v"+pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

	}
	
	private void addToolbarView(Toolbar toolbar) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.common_toolbar_text_layout, null);
		TextView titleText = (TextView) view.findViewById(R.id.title_textview);
		titleText.setText(getString(R.string.about_us));
		toolbar.addView(view);
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			if (mPlusOneButton != null)
				mPlusOneButton.initialize("https://market.android.com/details?id=" + getPackageName(), 0);
		} catch (Exception d) {

		}
	}
	
	void fixsizes() {

		findViewById(R.id.home_version).setPadding(width / 20, 0, 0, 0);
		findViewById(R.id.home_logo_container).setPadding(width / 20, width / 20, width / 20, width / 20);
		findViewById(R.id.about_us_body).setPadding(width / 20, width / 20, width / 20, width / 20);
		
		((LinearLayout.LayoutParams)findViewById(R.id.plus_one_button).getLayoutParams())
			.setMargins(width / 20, width / 40, width / 20, width / 40);

		findViewById(R.id.about_us_faq_conditions).setPadding(width / 20, width / 20, width / 20, width / 20);
		findViewById(R.id.about_us_terms_conditions).setPadding(width / 20, width / 20, width / 20, width / 20);
		findViewById(R.id.about_us_privacy_policy).setPadding(width / 20, width / 20, width / 20, width / 20);
	}
	
	public void setListeners() {

		LinearLayout btnFAQAndConditons = (LinearLayout) findViewById(R.id.about_us_faq_container);
		btnFAQAndConditons.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(AboutUsPage.this, ZWebView.class);
				intent.putExtra("title", getResources().getString(R.string.about_us_faq));
				intent.putExtra("url", "http://www.zimply.in/contact");
				startActivity(intent);

			}
		});

		LinearLayout btnTermsAndConditons = (LinearLayout) findViewById(R.id.about_us_terms_conditions_container);
		btnTermsAndConditons.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(AboutUsPage.this, ZWebView.class);
				intent.putExtra("title", getResources().getString(R.string.about_us_terms_of_use));
				intent.putExtra("url", "http://www.zimply.in/terms");
				startActivity(intent);

			}
		});

		LinearLayout privacyPolicyContainer = (LinearLayout) findViewById(R.id.about_us_privacy_policy_container);
		privacyPolicyContainer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(AboutUsPage.this, ZWebView.class);
				intent.putExtra("title", getResources().getString(R.string.privacy_policy));
				intent.putExtra("url", "http://www.zimply.in/privacy-policy");
				startActivity(intent);

			}
		});

	}

	public void goBack(View view) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
