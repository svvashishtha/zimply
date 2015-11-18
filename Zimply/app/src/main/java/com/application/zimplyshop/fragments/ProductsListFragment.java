package com.application.zimplyshop.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.ProductsCategoryGridAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.objects.AllProducts;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.widgets.SpaceItemDecoration;

import java.util.ArrayList;

public class ProductsListFragment extends BaseFragment implements GetRequestListener {

    RecyclerView cateoryList;
    ProductsCategoryGridAdapter productsCategoryGridAdapter;
    private LayoutInflater mInflater;
    private Activity mActivity;
    private boolean destroyed;
    private int height;

    public static ProductsListFragment newInstance(Bundle bundle) {
        ProductsListFragment fragment = new ProductsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        view = LayoutInflater.from(getActivity()).inflate(R.layout.products_list_layout, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();

        //add callbacks
        GetRequestManager.getInstance().addCallbacks(this);

        height = (getDisplayMetrics().widthPixels - 3 * getResources().getDimensionPixelSize(R.dimen.margin_medium)) / 2;
       // height = getDisplayMetrics().widthPixels ;
        cateoryList = (RecyclerView) view.findViewById(R.id.category_list);
        //cateoryList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        cateoryList.setLayoutManager(new LinearLayoutManager(getActivity()));
        cateoryList.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));
        //load products
        setLoadingVariables();
        retryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRequestFailed){
                    loadData();
                }
            }
        });
        loadData();
    }

    @Override
    public void onResume() {
        destroyed = false;
        if (productsCategoryGridAdapter != null)
            productsCategoryGridAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }

    private void loadData() {
        CommonLib.ZLog("Product_category" , "Requesting Product category "+System.currentTimeMillis());
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_PRODUCT_CATEGORY_LIST ;
        GetRequestManager.getInstance().requestCacheThenHTTP(url, RequestTags.PRODUCT_CATEGORY_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_PRODUCT_CATEGORY, GetRequestManager.ONE_HOUR);
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (requestTag != null && requestTag.equalsIgnoreCase(RequestTags.PRODUCT_CATEGORY_REQUEST_TAG)) {
            if (!destroyed) {
                showLoadingView();
                changeViewVisiblity(cateoryList, View.GONE);
            }
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (requestTag != null && requestTag.equalsIgnoreCase(RequestTags.PRODUCT_CATEGORY_REQUEST_TAG)) {
            if (!destroyed) {
                if (obj instanceof ArrayList<?>) {
                    if (((ArrayList<CategoryObject>) obj).size() == 0) {
                        showNullCaseView("No Product categories");
                        changeViewVisiblity(cateoryList, View.GONE);
                    } else {
                        AllProducts.getInstance().setProduct_category((ArrayList<CategoryObject>) obj);
                        int width = getDisplayMetrics().widthPixels - (2*(getResources().getDimensionPixelSize(R.dimen.margin_medium)));
                        productsCategoryGridAdapter = new ProductsCategoryGridAdapter(getActivity(), (ArrayList<CategoryObject>) obj, height,width);
                       /* ((GridLayoutManager) cateoryList.getLayoutManager())
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
                                });*/

                        cateoryList.setAdapter(productsCategoryGridAdapter);
                        showView();
                        changeViewVisiblity(cateoryList, View.VISIBLE);
                    }
                } else {
                    Toast.makeText(mActivity, "Something went wrong. Please try again..", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (requestTag != null && requestTag.equalsIgnoreCase(RequestTags.PRODUCT_CATEGORY_REQUEST_TAG)) {
            if (!destroyed) {
                showNetworkErrorView();
                changeViewVisiblity(cateoryList, View.GONE);
            }
        }
    }
}
