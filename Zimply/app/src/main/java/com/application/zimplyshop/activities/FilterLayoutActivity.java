package com.application.zimplyshop.activities;

import com.application.zimplyshop.R;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.fragments.FilterCategoryDialogFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.Window;

public class FilterLayoutActivity extends BaseActivity {

	int TYPE_CATEGORY = 1;
	int TYPE_SUB_CATEGORY = 2;
	int TYPE_SUB_SUB_CATEGORY = 3;

	int fragmentType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		/*
		 * getWindow().setFlags(
		 * android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
		 * android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		 * getWindow() .setFlags(
		 * android.view.WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
		 * android.view.WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		 */
		setContentView(R.layout.filter_base_layout);
		setFinishOnTouchOutside(true);

		setCategoryFragment();
	}

	Fragment fragment;

	public void setCategoryFragment() {
		// FrameLayout container = (FrameLayout) findViewById(R.id.container);
		Bundle bundle = new Bundle();
		fragmentType = AppConstants.TYPE_CATEGORY;
		bundle.putInt("type", AppConstants.TYPE_CATEGORY);
		fragment = FilterCategoryDialogFragment.newInstance(bundle);
		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.container, fragment)
				.setCustomAnimations(R.anim.fragment_slide_right_enter,
						R.anim.fragment_slide_left_exit,
						R.anim.fragment_slide_left_enter,
						R.anim.fragment_slide_right_exit).commit();
	}

	public void setSubCategoryFragment(int category) {
		Bundle bundle = new Bundle();
		fragmentType = AppConstants.TYPE_SUB_CATEGORY;
		bundle.putInt("type", AppConstants.TYPE_SUB_CATEGORY);
		bundle.putInt("cat_position", category);
		fragment = FilterCategoryDialogFragment.newInstance(bundle);
		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.container, fragment)
				.setCustomAnimations(R.anim.fragment_slide_right_enter,
						R.anim.fragment_slide_left_exit,
						R.anim.fragment_slide_left_enter,
						R.anim.fragment_slide_right_exit).addToBackStack(null)
				.commit();
	}

	public void setSubSubCategoryFragment(int category, int subCategory) {
		fragmentType = AppConstants.TYPE_SUB_SUB_CATEGORY;
		Bundle bundle = new Bundle();
		bundle.putInt("type", AppConstants.TYPE_SUB_SUB_CATEGORY);
		bundle.putInt("cat_position", category);
		bundle.putInt("subcat_position", subCategory);
		fragment = FilterCategoryDialogFragment.newInstance(bundle);

		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.container, fragment)
				.setCustomAnimations(R.anim.fragment_slide_right_enter,
						R.anim.fragment_slide_left_exit,
						R.anim.fragment_slide_left_enter,
						R.anim.fragment_slide_right_exit).addToBackStack(null)
				.commit();

	}

	public void OnHeaderBackPressed() {
		if (fragmentType == AppConstants.TYPE_SUB_SUB_CATEGORY) {
			getSupportFragmentManager().popBackStack();
			fragmentType = AppConstants.TYPE_SUB_CATEGORY;
		} else if (fragmentType == AppConstants.TYPE_SUB_CATEGORY) {
			getSupportFragmentManager().popBackStack();
			fragmentType = AppConstants.TYPE_CATEGORY;
		} else {
			onBackPressed();
		}

	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// If we've received a touch notification that the user has touched
		// outside the app, finish the activity.
		if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
			finish();
			return true;
		}

		// Delegate everything else to Activity.
		return super.onTouchEvent(event);
	}
}
