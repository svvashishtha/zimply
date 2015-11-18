package com.application.zimplyshop.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.fragments.BaseFragment;
import com.application.zimplyshop.fragments.FilterFragmentFirst;
import com.application.zimplyshop.fragments.FilterFragmentFourth;
import com.application.zimplyshop.fragments.FilterFragmentSecond;
import com.application.zimplyshop.fragments.FilterFragmentThird;
import com.application.zimplyshop.objects.ZFilter;

public class FilterActivity extends BaseActivity {

	LayoutInflater inflator;
	TextView titleText;
	int fragmentId;
	private BaseFragment fragment;
	private ZFilter zFilter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.filter_activity);
		inflator = LayoutInflater.from(this);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		zFilter = new ZFilter();

		addToolbarView(toolbar);


		if( getIntent() != null && getIntent().getExtras() != null ) {
			Bundle extras = getIntent().getExtras();
			if(extras.containsKey("type") && extras.get("type") instanceof Integer) {
				int type = extras.getInt("type");
				if(type == AppConstants.ITEM_TYPE_PHOTO) {
					if (extras.containsKey("photo_slug"))
						zFilter.setPhoto_slug(String.valueOf(getIntent().getExtras().get("photo_slug")));
					zFilter.setType(1);
				} else if(type == AppConstants.ITEM_TYPE_EXPERT) {
					if (extras.containsKey("pro_slug"))
						zFilter.setPro_slug(String.valueOf(getIntent().getExtras().get("pro_slug")));
					zFilter.setType(2);
				} else zFilter.setType(0);
			}
		}

		addToolbarView(toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		setFirstFragment();
	}


	public void setViewActivated(View view){
        view.setBackgroundColor(getResources().getColor(R.color.btn_green_color_normal));
	}
	public void setFirstFragment() {
		fragmentId = 1;
		setActionBarTitle("Step 1 of 3");
		fragment = new FilterFragmentFirst();
		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
				.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_left_exit,
						R.anim.fragment_slide_left_enter, R.anim.fragment_slide_right_exit)
				.commit();
	}

	public void setActionBarTitle(String text){
		titleText.setText(text);
		titleText.invalidate();
	}
	public void setSecondFragment() {

		fragmentId = 2;
		setActionBarTitle("Step 2 of 3");
		fragment = new FilterFragmentSecond();
		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
				.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_left_exit,
						R.anim.fragment_slide_left_enter, R.anim.fragment_slide_right_exit)
				.addToBackStack(null).commit();
        setViewActivated(findViewById(R.id.step1));
	}

	public void setThirdFragment() {
		fragmentId = 3;
		setActionBarTitle("Step 3 of 3");
		fragment = new FilterFragmentThird();
		fragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
				.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_left_exit,
						R.anim.fragment_slide_left_enter, R.anim.fragment_slide_right_exit)
				.addToBackStack(null).commit();
        setViewActivated(findViewById(R.id.step2));
	}

	@Override
	public void onBackPressed() {
        checkKeyBoard();
		if(fragmentId == 1 || fragmentId == 4) {
			this.finish();
			//overridePendingTransition(R.anim.animate_bottom_out, R.anim.animate_bottom_in);
		}else {
			if(fragmentId == 2){
				setActionBarTitle("Step 1 of 3");
				fragmentId = 1;
			}else if(fragmentId == 3){
					fragmentId = 2;
				setActionBarTitle("Step 2 of 3");
			}
		}
		super.onBackPressed();
	}

	public void setFourthFragment(String queryId) {
		fragmentId = 4;
		setActionBarTitle("Details");
		fragment = new FilterFragmentFourth();
		Bundle bundle = getIntent().getExtras();
		bundle.putString("query_id",queryId);
		fragment.setArguments(bundle);
		FragmentManager fm = getSupportFragmentManager();
		for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
			fm.popBackStack();
		}

		fm.beginTransaction().replace(R.id.fragment_container, fragment)
				.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_left_exit,
						R.anim.fragment_slide_left_enter, R.anim.fragment_slide_right_exit)
				.addToBackStack(null).commit();
        setViewActivated(findViewById(R.id.step3));
	}

	private void addToolbarView(Toolbar toolbar) {
		View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, null);
		titleText = (TextView) view.findViewById(R.id.title_textview);
	//	titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX ,getResources().getDimension(R.dimen.font_small));
		toolbar.addView(view);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;
		}
        return true;
	}

	public void checkKeyBoard(){
		View view = getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public ZFilter getFilter() {
		return zFilter;
	}

	public void setFilter(ZFilter filter) {
		this.zFilter = filter;
	}

}
