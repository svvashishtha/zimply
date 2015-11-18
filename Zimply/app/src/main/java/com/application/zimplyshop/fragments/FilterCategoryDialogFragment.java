package com.application.zimplyshop.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.FilterLayoutActivity;
import com.application.zimplyshop.adapters.CompleteCategoryRecyclerAdapter;
import com.application.zimplyshop.adapters.CompleteSubCategoryRecyclerGridAdapter;
import com.application.zimplyshop.adapters.CompleteSubSubCategoryAdapter;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.objects.AllCategories;

public class FilterCategoryDialogFragment extends BaseFragment implements OnClickListener {

	RecyclerView recyclerView;

	TextView headerTitle;

	ImageView topImg1, topImg2;

	public static FilterCategoryDialogFragment newInstance(Bundle bundle) {
		FilterCategoryDialogFragment fragment = new FilterCategoryDialogFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = LayoutInflater.from(getActivity()).inflate(R.layout.category_filter_layout, null);

		LinearLayout parentLayout = (LinearLayout) view.findViewById(R.id.parent_layout);
		LayoutParams lp = new LayoutParams(
				getDisplayMetrics().widthPixels - 4 * getResources().getDimensionPixelSize(R.dimen.margin_large),
				(4 * getDisplayMetrics().heightPixels) / 5);
		parentLayout.setLayoutParams(lp);
		recyclerView = (RecyclerView) view.findViewById(R.id.categories_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		headerTitle = (TextView) view.findViewById(R.id.header_text);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		int type = 0;
		Bundle bundle = getArguments();
		if (bundle != null) {
			type = bundle.getInt("type");

			if (type == AppConstants.TYPE_CATEGORY) {
				addCategoryAdapter();
			} else if (type == AppConstants.TYPE_SUB_CATEGORY) {
				addSubCategoryArray(bundle.getInt("cat_position"));
			} else {
				addSubSubCategoryArray(bundle.getInt("cat_position"), bundle.getInt("subcat_position"));
			}
		} else {

		}

	}

	private void addSubSubCategoryArray(int catPos, int subCatPos) {
		CompleteSubSubCategoryAdapter adapter = new CompleteSubSubCategoryAdapter(getActivity(),
				AllCategories.getInstance().getCategories().getCategory().get(catPos).getSubcategory().get(subCatPos)
						.getSubsubcategory());
		recyclerView.setAdapter(adapter);
		adapter.setOnItemClickListener(new CompleteSubSubCategoryAdapter.SubSubCatOnItemClickListener() {

			@Override
			public void onItemClick(int pos) {

			}
		});
	}

	/**
	 * Adds sub category array
	 */
	private void addSubCategoryArray(final int catPos) {
		CompleteSubCategoryRecyclerGridAdapter adapter = new CompleteSubCategoryRecyclerGridAdapter(getActivity(),
				AllCategories.getInstance().getCategories().getCategory().get(catPos).getSubcategory());
		recyclerView.setAdapter(adapter);
		adapter.setOnItemClickListener(new CompleteSubCategoryRecyclerGridAdapter.SubCatOnItemClickListener() {

			@Override
			public void onItemClick(int pos) {
				((FilterLayoutActivity) getActivity()).setSubSubCategoryFragment(catPos, pos);
			}
		});
	}

	/**
	 * Adds Category array
	 */
	private void addCategoryAdapter() {
		CompleteCategoryRecyclerAdapter adapter = new CompleteCategoryRecyclerAdapter(getActivity(),
                AllCategories.getInstance().getCategories().getCategory());
		recyclerView.setAdapter(adapter);
		adapter.setOnItemClickListener(new CompleteCategoryRecyclerAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(int pos) {
				((FilterLayoutActivity) getActivity()).setSubCategoryFragment(pos);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		}
	}
}
