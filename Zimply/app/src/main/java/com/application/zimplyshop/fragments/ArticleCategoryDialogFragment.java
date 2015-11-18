package com.application.zimplyshop.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.PhotoArticleFilterAdapter;
import com.application.zimplyshop.objects.AllCategories;
import com.application.zimplyshop.objects.AllCities;
import com.application.zimplyshop.objects.AllProducts;

public class ArticleCategoryDialogFragment extends BaseFragment implements OnClickListener {

    int selectedPos;

    boolean isLoc;

    public static ArticleCategoryDialogFragment newInstance(Bundle bundle) {
		ArticleCategoryDialogFragment fragment = new ArticleCategoryDialogFragment();
        fragment.setArguments(bundle);
		return fragment;
	}


    public void setIsLoc(boolean isLoc) {
        this.isLoc = isLoc;
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
        adapter.setSelectedPos(selectedPos);
    }
    PhotoArticleFilterAdapter adapter;
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.category_filter_layout, container,false);

		Bundle bundle = getArguments();
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.categories_list);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		if (bundle.getBoolean("is_photos")) {
			adapter = new PhotoArticleFilterAdapter(getActivity(),
					AllCategories.getInstance().getPhotoCateogryObjs().getPhoto_category(),
					bundle.getInt("selected_pos"));
            selectedPos = bundle.getInt("selected_pos");
        }  else if (!isLoc && bundle.getBoolean("is_expert")) {
			adapter = new PhotoArticleFilterAdapter(getActivity(),
					AllCategories.getInstance().getPhotoCateogryObjs().getExpert_category(),
					bundle.getInt("selected_pos"));
            selectedPos = bundle.getInt("selected_pos");
		}else if (bundle.getBoolean("is_location")) {
            adapter = new PhotoArticleFilterAdapter(getActivity(),
					AllCities.getInsance().getCities(),
                    bundle.getInt("selected_loc"));
            selectedPos = bundle.getInt("selected_loc");
        } else if(bundle.getBoolean("is_products")){
            adapter = new PhotoArticleFilterAdapter(getActivity(),
					AllProducts.getInstance().getProduct_category(),
                    bundle.getInt("selected_pos"));
            selectedPos = bundle.getInt("selected_pos");
		}else{
			adapter = new PhotoArticleFilterAdapter(getActivity(),
					AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category(),
					bundle.getInt("selected_pos"));
            selectedPos = bundle.getInt("selected_pos");
		}

		recyclerView.setAdapter(adapter);
		adapter.setOnItemClickListener(new PhotoArticleFilterAdapter.OnItemClickListener() {

			@Override
            public void onItemClick(int pos) {
                adapter.setSelectedPos(pos);
                selectedPos = pos;
			}
		});
		//CustomTextView headerTitle = (CustomTextView) view.findViewById(R.id.header_text);
		//headerTitle.setText(getString(R.string.categories_text));

		/*
		 * topImg1.setImageBitmap(CommonLib.getBitmap(getActivity(),
		 * R.drawable.ic_cross,
		 * getResources().getDimensionPixelSize(R.dimen.header_item_size),
		 * getResources().getDimensionPixelSize(R.dimen.header_item_size)));
		 */



		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.reset_btn:
            selectedPos = 0;
            break;

		}
	}

    public int getSelectedPos() {
        return selectedPos;
    }
}
