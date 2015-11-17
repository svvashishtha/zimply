package com.application.zimplyshop.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.application.zimply.R;
import com.application.zimplyshop.adapters.PhotoFilterCategoryAdapter;
import com.application.zimplyshop.adapters.PhotoFilterCategoryAdapter.OnItemClickListener;
import com.application.zimplyshop.extras.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;

public class PhotoFilterBasicFragment extends BaseFragment implements OnClickListener, AppConstants {

	RecyclerView recyclerView;


	OnBasicItemClickListener mListener;

	int type;

	int sizeId, budgetId, styleId;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.category_filter_layout, null);

		//((LinearLayout) view.findViewById(R.id.reset_layout)).setVisibility(View.VISIBLE);
		LinearLayout parentLayout = (LinearLayout) view.findViewById(R.id.parent_layout);
		LayoutParams lp = new LayoutParams(
				getDisplayMetrics().widthPixels - 2 * getResources().getDimensionPixelSize(R.dimen.margin_large),
				((4 * getDisplayMetrics().heightPixels) / 5));
		parentLayout.setLayoutParams(lp);
		recyclerView = (RecyclerView) view.findViewById(R.id.categories_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		type = getArguments().getInt("filter_type");
		sizeId = getArguments().getInt("size_id");
		budgetId = getArguments().getInt("budget_id");
		styleId = getArguments().getInt("style_id");
		ArrayList<String> names = new ArrayList<String>();
		PhotoFilterCategoryAdapter adapter;
		if (type == TYPE_PHOTO_FILTER_BUDGET) {
			names.add(getResources().getString(R.string.rs_text));
			names.add(getResources().getString(R.string.rs_text)+" "+getResources().getString(R.string.rs_text));
			names.add(getResources().getString(R.string.rs_text)+" "+getResources().getString(R.string.rs_text)+" "+getResources().getString(R.string.rs_text));
			names.add(getResources().getString(R.string.rs_text)+" "+getResources().getString(R.string.rs_text)+" "+getResources().getString(R.string.rs_text)+" "+getResources().getString(R.string.rs_text));

			adapter = new PhotoFilterCategoryAdapter(getActivity(), names, false);
			adapter.setSelectedItem(budgetId - 1);
			recyclerView.setAdapter(adapter);

		} else if (type == TYPE_PHOTO_FILTER_SIZE) {
			/*topImg1.setVisibility(View.VISIBLE);
			topImg1.setImageBitmap(CommonLib.getBitmap(getActivity(), R.drawable.ic_back,
					getResources().getDimensionPixelSize(R.dimen.height48),
					getResources().getDimensionPixelSize(R.dimen.height48)));
			topImg2.setImageBitmap(CommonLib.getBitmap(getActivity(), R.drawable.ic_cross,
					getResources().getDimensionPixelSize(R.dimen.height48),
					getResources().getDimensionPixelSize(R.dimen.height48)));
			headerTitle.setText("Size");*/
			names.add("Compact");
			names.add("Medium");
			names.add("Large");
			names.add("Expensive");
			adapter = new PhotoFilterCategoryAdapter(getActivity(), names, false);
			adapter.setSelectedItem(sizeId - 1);
			recyclerView.setAdapter(adapter);
		} else if (type == TYPE_PHOTO_FILTER_STYLE) {
			/*topImg1.setVisibility(View.VISIBLE);
			topImg1.setImageBitmap(CommonLib.getBitmap(getActivity(), R.drawable.ic_back,
					getResources().getDimensionPixelSize(R.dimen.height48),
					getResources().getDimensionPixelSize(R.dimen.height48)));
			topImg2.setImageBitmap(CommonLib.getBitmap(getActivity(), R.drawable.ic_cross,
					getResources().getDimensionPixelSize(R.dimen.height48),
					getResources().getDimensionPixelSize(R.dimen.height48)));
			headerTitle.setText("Style");*/
			names.add("Modern");
			names.add("Eclectic ( Mixture of Styles )");
			names.add("Traditional/Classic");
			names.add("Minimalist");
			adapter = new PhotoFilterCategoryAdapter(getActivity(), names, false);
			adapter.setSelectedItem(styleId - 1);
			recyclerView.setAdapter(adapter);
		} else {
			/*topImg1.setVisibility(View.GONE);

			topImg2.setImageBitmap(CommonLib.getBitmap(getActivity(), R.drawable.ic_cross,
					getResources().getDimensionPixelSize(R.dimen.height48),
					getResources().getDimensionPixelSize(R.dimen.height48)));
			headerTitle.setText("Filters");*/
			names.add("Style");
			names.add("Budget");
			names.add("Size");

			adapter = new PhotoFilterCategoryAdapter(getActivity(), names, true);
			adapter.addSubTitles(getSubtitleArray());
			recyclerView.setAdapter(adapter);
		}

		adapter.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(int pos) {
				if (mListener != null) {

					mListener.onItemCLicked(pos, type);
				}
			}
		});
		return view;
	}


	private HashMap<Integer, String> getSubtitleArray() {
        String[] photoBudget = {getResources().getString(R.string.rs_text), getResources().getString(R.string.rs_text) + " " +  getResources().getString(R.string.rs_text),  getResources().getString(R.string.rs_text) + " " +  getResources().getString(R.string.rs_text) + " " +  getResources().getString(R.string.rs_text),  getResources().getString(R.string.rs_text) + " " +  getResources().getString(R.string.rs_text) + " " +  getResources().getString(R.string.rs_text) + " " +  getResources().getString(R.string.rs_text) };
		HashMap<Integer, String> subTitles = new HashMap<Integer, String>();

		if (styleId != -1)
			subTitles.put(0, AppConstants.styles[styleId - 1]);
		if (budgetId != -1)
			subTitles.put(1,photoBudget[budgetId - 1]);
		if (sizeId != -1) {
			subTitles.put(2, AppConstants.photoSize[sizeId - 1]);
		}
		return subTitles;
	}

	private String getStyleName() {

		return null;
	}

	public void setOnBasicItemClickListener(OnBasicItemClickListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.retry_layout:
			break;
		case R.id.reset_btn:
			mListener.onItemCLicked(-1, TYPE_PHOTO_FILTER_RESET);
			break;
		/*case R.id.top_img_1:
			//((PhotoFilterFragment) getActivity()).onBackPressed();
			break;
		case R.id.top_img_2:
			//((PhotoFilterFragment) getActivity()).onClickedCancel();
			break;*/
		}
	}

	public interface OnBasicItemClickListener {
		void onItemCLicked(int id, int type);
	}
}
