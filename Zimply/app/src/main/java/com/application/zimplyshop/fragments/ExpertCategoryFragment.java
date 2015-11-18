package com.application.zimplyshop.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.ExpertCategoryGridAdapter;
import com.application.zimplyshop.objects.AllCategories;
import com.application.zimplyshop.widgets.SpaceGridItemDecorator;

/**
 * Created by Umesh Lohani on 9/23/2015.
 */
public class ExpertCategoryFragment extends BaseFragment{

    private View rootView;
    RecyclerView cateoryList;

    public static ExpertCategoryFragment newInstance(Bundle bundle){
        ExpertCategoryFragment fragment = new ExpertCategoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.expert_category_layout ,null);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int height = (getDisplayMetrics().widthPixels-3*getResources().getDimensionPixelSize(R.dimen.margin_medium))/2;

        cateoryList = (RecyclerView)rootView.findViewById(R.id.category_list);
       /* rootView.findViewById(R.id.search_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SearchActivity.class);
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });*/
        cateoryList.setLayoutManager(new GridLayoutManager(getActivity(),2));
        cateoryList.addItemDecoration(new SpaceGridItemDecorator(
                (int) getResources().getDimension(R.dimen.margin_small),
                (int) getResources().getDimension(R.dimen.margin_mini)));
        ExpertCategoryGridAdapter adapter = new ExpertCategoryGridAdapter(getActivity() , AllCategories.getInstance().getPhotoCateogryObjs().getExpert_base_category(),height);
        ((GridLayoutManager) cateoryList.getLayoutManager())
                .setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        switch (cateoryList
                                .getAdapter().getItemViewType(position)) {
                            case 0:
                                return 2;
                            case 1:
                                return 1;
                            case 2:
                                return 2;
                            default:
                                return -1;
                        }
                    }
                });

        cateoryList.setAdapter(adapter);

    }
}
