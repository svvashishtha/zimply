package com.application.zimplyshop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.zimplyshop.R;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.fragments.PhotoFilterBasicFragment.OnBasicItemClickListener;

public class PhotoFilterFragment extends BaseFragment implements OnBasicItemClickListener, AppConstants {

	public static PhotoFilterFragment newInstance(Bundle bundle){
        PhotoFilterFragment fragment = new PhotoFilterFragment();
        fragment.setArguments(bundle);
        return fragment;
	}
	int fragmentType;

	OnFinalValueSelectedListener mListener;

	int styleId, budgetId, sizeId;

	public void setOnFinalValueSelectedListener(OnFinalValueSelectedListener listener) {
		this.mListener = listener;
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.filter_base_layout , container,false);
		styleId = getArguments().getInt("style_id", 0);
        budgetId = getArguments().getInt("budget_id", 0);
		sizeId = getArguments().getInt("size_id", 0);
		setBasicCategoryFragment();
        return view;
	}

	public void setBasicCategoryFragment() {

		Bundle bundle = new Bundle();
		bundle.putInt("style_id", styleId);
		bundle.putInt("size_id", sizeId);
		bundle.putInt("budget_id", budgetId);
		fragmentType = TYPE_PHOTO_FILTER_TYPE;
		bundle.putInt("filter_type", TYPE_PHOTO_FILTER_TYPE);
		PhotoFilterBasicFragment fragment = new PhotoFilterBasicFragment();
		fragment.setArguments(bundle);
		fragment.setOnBasicItemClickListener(this);
		getChildFragmentManager().beginTransaction().add(R.id.container, fragment, "Basic")
				.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_left_exit,
						R.anim.fragment_slide_left_enter, R.anim.fragment_slide_right_exit)
				.commit();
	}

	public void setLinkedFragments(int type) {

		Bundle bundle = new Bundle();
		bundle.putInt("style_id", styleId);
		bundle.putInt("size_id", sizeId);
		bundle.putInt("budget_id", budgetId);
		fragmentType = type;
		bundle.putInt("filter_type", type);
		PhotoFilterBasicFragment fragment = new PhotoFilterBasicFragment();
		fragment.setArguments(bundle);
		fragment.setOnBasicItemClickListener(this);
        getChildFragmentManager().beginTransaction().replace(R.id.container, fragment, "Detail")
				.setCustomAnimations(R.anim.fragment_slide_right_enter, R.anim.fragment_slide_left_exit,
						R.anim.fragment_slide_left_enter, R.anim.fragment_slide_right_exit)
				.addToBackStack(null).commit();
	}

	@Override
	public void onItemCLicked(int id, int type) {
		switch (type) {

		case TYPE_PHOTO_FILTER_TYPE:
			if (id == 0) {
				setLinkedFragments(TYPE_PHOTO_FILTER_STYLE);
			} else if (id == 1) {
				setLinkedFragments(TYPE_PHOTO_FILTER_BUDGET);
			} else {
				setLinkedFragments(TYPE_PHOTO_FILTER_SIZE);
			}

			break;
		case TYPE_PHOTO_FILTER_RESET:
            sizeId = -1;
            budgetId = -1;
            styleId = -1;
            getChildFragmentManager().popBackStack();
            break;
		case TYPE_PHOTO_FILTER_BUDGET:
            budgetId = id;
            getChildFragmentManager().popBackStack();
            break;
		case TYPE_PHOTO_FILTER_STYLE:
            styleId= id;
            getChildFragmentManager().popBackStack();
            break;
		case TYPE_PHOTO_FILTER_SIZE:
            sizeId = id;
            getChildFragmentManager().popBackStack();
			break;
		}
	}

    public int getSizeId() {
        return sizeId;
    }

    public int getStyleId() {
        return styleId;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public interface OnFinalValueSelectedListener {
		void onFinalValueSelected(int type, int id);
	}


}
