package com.application.zimplyshop.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.application.zimply.R;

public class SortDialogFragment extends BaseFragment implements OnClickListener {

	public static SortDialogFragment newInstance(Bundle bundle) {
		SortDialogFragment fragment = new SortDialogFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	OnSortItemClickListener mListener;

	int sortId;


	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	public int getSortId() {
		return sortId;
	}

	public void setOnSortItemClickListener(OnSortItemClickListener listener) {
		this.mListener = listener;
	}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.article_sort_layout, container,false);
		sortId = getArguments().getInt("sort_id");

		/*((ImageView) view.findViewById(R.id.sort_cross)).setImageBitmap(CommonLib.getBitmap(getActivity(),
                R.drawable.ic_cross, getResources().getDimensionPixelSize(R.dimen.height48),
                getResources().getDimensionPixelSize(R.dimen.height48)));
		((ImageView) view.findViewById(R.id.sort_cross)).setOnClickListener(this);
*/

		changeSelectedValues();
		view.findViewById(R.id.featured_text).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sortId = 1;
				view.findViewById(R.id.featured_text).setSelected(true);
				view.findViewById(R.id.latest_text).setSelected(false);
			}
		});
        view.findViewById(R.id.latest_text).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sortId = 2;
				view.findViewById(R.id.latest_text).setSelected(true);
				view.findViewById(R.id.featured_text).setSelected(false);
			}
		});

		return view;
	}

public void changeSelectedValues(){
	if(sortId == 1){
		view.findViewById(R.id.featured_text).setSelected(true);
		view.findViewById(R.id.latest_text).setSelected(false);
	}else{
		view.findViewById(R.id.featured_text).setSelected(false);
		view.findViewById(R.id.latest_text).setSelected(true);
	}
}

	public interface OnSortItemClickListener {
		void onOptionsSelected(int id);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.featured_text:
			if (mListener != null) {
				mListener.onOptionsSelected(1);

			}
			break;

		case R.id.latest_text:
			if (mListener != null) {
				mListener.onOptionsSelected(2);

			}
			break;

		}
	}
}
