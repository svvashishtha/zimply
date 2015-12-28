package com.application.zimplyshop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.BaseActivity;
import com.application.zimplyshop.adapters.ExpandableProductFilterFragment;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.objects.AllProducts;
import com.application.zimplyshop.serverapis.RequestTags;

public class ProductFilterFragment extends BaseFragment implements GetRequestListener {

    ExpandableProductFilterFragment expandableListAdapter;
    String selectedCategoryName;
    int parent_categoryId = -1, categoryId = -1;
    private View rootView;
    private BaseActivity mActivity;
    private int width;
    private boolean destroyed = false;
    private int expandGroup;

    public static ProductFilterFragment newInstance(Bundle bundle) {
        ProductFilterFragment fragment = new ProductFilterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.photo_filter_form_first, null);
        rootView.findViewById(R.id.label).setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
        width = mActivity.getWindowManager().getDefaultDisplay().getWidth();
        parent_categoryId = getArguments().getInt("parent_category_id");
        categoryId = getArguments().getInt("category_id");

        ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.expandable_list);
        expandableListAdapter = new ExpandableProductFilterFragment(mActivity, AllProducts.getInstance().getCategory_tree());
        listView.setAdapter(expandableListAdapter);
        expandableListAdapter.setSelectedChildId(categoryId);
        if (getArguments().containsKey("expand_group")) {
            expandGroup = getArguments().getInt("expand_group");
            listView.expandGroup(expandGroup);
        }
        expandableListAdapter.setSelectedCategoryListener(new ExpandableProductFilterFragment.selectedCategoryListener() {
            @Override
            public void setSelectedCategory(int parentId, int groupPos, int pos, String categoryId, String categoryName) {
                ProductFilterFragment.this.categoryId = Integer.parseInt(categoryId);
                selectedCategoryName = categoryName;
                parent_categoryId = -1;
                expandGroup = groupPos;
                expandableListAdapter.setSelectedChildId(ProductFilterFragment.this.categoryId);
                expandableListAdapter.setSelectedParentId(-1);

            }
        });
        /*listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                categoryId =Integer.parseInt(expandableListAdapter.getChildCategoryId(groupPosition,childPosition));
                expandableListAdapter.setSelectedChildId(categoryId);
                expandableListAdapter.setSelectedParentId(-1);
                return false;
            }
        });
        */
        if (parent_categoryId == -1) {
            expandableListAdapter.setSelectedChildId(categoryId);
        } else {
            expandableListAdapter.setSelectedParentId(parent_categoryId);
        }
        //  loadData() ;
    }

    public int getExpandGroup() {
        return expandGroup;
    }

    public int getSelectedCategoryId() {
        return categoryId;
    }

    public String getSelectedCategoryName() {
        return selectedCategoryName;
    }

    private void loadData() {
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.PRO_CATEGORY_TREE;
        GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.PRO_REQUEST_TAGS, ObjectTypes.OBJECT_TYPE_FILTER_PRODUCTS);
    }

    @Override
    public void onRequestStarted(String requestTag) {

    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (requestTag != null && requestTag.equals(RequestTags.PRO_REQUEST_TAGS)) {
            if (!destroyed) {

            }
        }

    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {

    }

    @Override
    public void onDestroy() {
        destroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }

    public int getSelectedParent_categoryId() {
        return parent_categoryId;
    }
}

