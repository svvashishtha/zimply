package com.application.zimply.activities;

import com.application.zimply.R;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.fragments.FilterFiltersDialogFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;

public class FilterFiltersLayout extends BaseActivity implements AppConstants {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter_base_layout);
		setFinishOnTouchOutside(true);
		setBaseFilterFragment();
	}

	private void setBaseFilterFragment() {
		Fragment fragment = FilterFiltersDialogFragment.newInstance(null);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.container, fragment, FILTER_FILTER_FRAGMENT_TAG)
				.commit();

	}

	public void changeToPriceFragment() {
		
	}
}
