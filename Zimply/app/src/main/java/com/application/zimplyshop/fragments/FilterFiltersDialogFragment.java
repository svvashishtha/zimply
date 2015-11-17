package com.application.zimplyshop.fragments;

import com.application.zimply.R;
import com.application.zimplyshop.activities.FilterFiltersLayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FilterFiltersDialogFragment extends BaseFragment implements
		OnClickListener {

	TextView offers, priceText;

	public static FilterFiltersDialogFragment newInstance(Bundle bundle) {
		FilterFiltersDialogFragment fragment = new FilterFiltersDialogFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.filter_filters_layout, container, false);
		LinearLayout parentLayout = (LinearLayout) view
				.findViewById(R.id.parent_layout);
		LayoutParams lp = new LayoutParams(getDisplayMetrics().widthPixels - 4
				* getResources().getDimensionPixelSize(R.dimen.margin_large),
				(4 * getDisplayMetrics().heightPixels) / 5);
		parentLayout.setLayoutParams(lp);
		offers = (TextView) view.findViewById(R.id.offers);
		offers.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.offers:
			// changeTextDrawable();
			break;
		case R.id.price_layout:
			((FilterFiltersLayout)getActivity()).changeToPriceFragment();
			break;
		}
	}

	private void changeTextDrawable(int resourceId) {
		/* Drawable textDrawable = getResources().getDrawable(id, theme) */
	}
}
