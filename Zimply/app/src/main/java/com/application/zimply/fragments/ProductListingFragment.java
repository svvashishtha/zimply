package com.application.zimply.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.zimply.R;
import com.application.zimply.activities.FilterFiltersLayout;
import com.application.zimply.activities.FilterLayoutActivity;
import com.application.zimply.adapters.ProductsRecyclerViewGridAdapter;
import com.application.zimply.application.AppApplication;
import com.application.zimply.baseobjects.ErrorObject;
import com.application.zimply.baseobjects.HomeProductObj;
import com.application.zimply.baseobjects.ProductListObject;
import com.application.zimply.extras.ObjectTypes;
import com.application.zimply.managers.GetRequestListener;
import com.application.zimply.managers.GetRequestManager;
import com.application.zimply.managers.ImageLoaderManager;
import com.application.zimply.serverapis.RequestTags;
import com.application.zimply.widgets.SpaceGridItemDecorator;

import java.util.ArrayList;

/**
 * Created by apoorvarora on 06/10/15.
 */
public class ProductListingFragment extends BaseFragment implements View.OnClickListener, GetRequestListener, RequestTags {

    private View rootView;
    private LayoutInflater mInflater;
    RecyclerView productList;
    private Activity mActivity;

    String url, next;
    boolean isLoading, isRequestAllowed, isDestroyed;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    int width;


    public static ProductListingFragment newInstance(Bundle bundle){
        ProductListingFragment fragment = new ProductListingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.product_list_layout ,null);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        productList = (RecyclerView)rootView.findViewById(R.id.category_list);

        productList.setLayoutManager(new GridLayoutManager(mActivity, 2));
        productList.addItemDecoration(new SpaceGridItemDecorator(
                (int) getResources().getDimension(R.dimen.margin_small),
                (int) getResources().getDimension(R.dimen.margin_mini)));

        // setProductsGrid();
        url = mActivity.getIntent().getStringExtra("url");

        width = (getDisplayMetrics().widthPixels - (int) (2 * getResources()
                .getDimension(R.dimen.font_small))) / 2;
        loadData();

    }

    private void loadData() {
        String finalUrl;
        if (next == null) {
            finalUrl = AppApplication.getInstance().getBaseUrl() + url
                    + "?width=" + width + "&filter=0";
        } else {
            finalUrl = AppApplication.getInstance().getBaseUrl() + next;
        }
        GetRequestManager.getInstance().addCallbacks(this);
        GetRequestManager.getInstance().makeAyncRequest(finalUrl,
                PRODUCT_LIST_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_PRODUCT_LIST_OBJECT);
    }

    private void setAdapterData(ArrayList<HomeProductObj> objs) {
        if (productList.getAdapter() == null) {
            int height = (getDisplayMetrics().widthPixels - 3 * ((int) getResources()
                    .getDimension(R.dimen.margin_mini))) / 2;
            ProductsRecyclerViewGridAdapter adapter = new ProductsRecyclerViewGridAdapter(
                    mActivity, mActivity, height);
            productList.setAdapter(adapter);
            productList
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {

                            visibleItemCount = productList.getLayoutManager()
                                    .getChildCount();
                            totalItemCount = productList.getLayoutManager()
                                    .getItemCount();
                            pastVisiblesItems = ((LinearLayoutManager) productList
                                    .getLayoutManager())
                                    .findFirstVisibleItemPosition();

                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount
                                    && !isLoading && isRequestAllowed) {
                                loadData();
                            }
                            scrollToolbarAndHeaderBy(-dy);

                        }

                        @Override
                        public void onScrollStateChanged(
                                RecyclerView recyclerView, int newState) {
                            new ImageLoaderManager(mActivity)
                                    .setScrollState(newState);
                            super.onScrollStateChanged(recyclerView, newState);
                        }
                    });
            ((GridLayoutManager) productList.getLayoutManager())
                    .setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            switch (productList
                                    .getAdapter().getItemViewType(position)) {
                                case 0:
                                    return 1;
                                case 1:
                                    return 2;
                                default:
                                    return -1;
                            }
                        }
                    });

        }
        ((ProductsRecyclerViewGridAdapter) productList.getAdapter())
                .addData(objs);
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (!isDestroyed
                && requestTag.equalsIgnoreCase(PRODUCT_LIST_REQUEST_TAG)) {
            if (productList.getAdapter() == null
                    || productList.getAdapter().getItemCount() == 0) {
                showLoadingView();
                changeViewVisiblity(productList, View.GONE);
            } else {

            }
            isLoading = true;
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (!isDestroyed
                && requestTag.equalsIgnoreCase(PRODUCT_LIST_REQUEST_TAG)) {
            if (((ProductListObject) obj).getProducts().size() == 0) {
                if (productList.getAdapter() == null
                        || productList.getAdapter().getItemCount() == 0) {
                    showNullCaseView("No Products");

                } else {
                    showToast("No more Products");

                }
                isRequestAllowed = false;
            } else {
                setAdapterData(((ProductListObject) obj).getProducts());
                next = ((ProductListObject) obj).getNext_url();
                showView();
                changeViewVisiblity(productList, View.VISIBLE);
                if (((ProductListObject) obj).getProducts().size() < 10) {
                    isRequestAllowed = false;
                    ((ProductsRecyclerViewGridAdapter) productList.getAdapter())
                            .removeItem();
                } else {
                    isRequestAllowed = true;
                }
            }

            isLoading = false;
        }

    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (!isDestroyed
                && requestTag.equalsIgnoreCase(PRODUCT_LIST_REQUEST_TAG)) {
            if (productList.getAdapter() == null
                    || productList.getAdapter().getItemCount() == 0) {
                showNetworkErrorView();
                changeViewVisiblity(productList, View.GONE);
            } else {
                if (((ErrorObject) obj).getErrorCode() == 500) {
                    showToast("Could not load more data");

                } else {
                    showToast(((ErrorObject) obj).getErrorMessage());

                }

                ((ProductsRecyclerViewGridAdapter) productList.getAdapter())
                        .removeItem();
                isRequestAllowed = false;
            }
            isLoading = false;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter_filter_layout:
                Intent intent = new Intent(mActivity, FilterFiltersLayout.class);
                startActivity(intent);
                break;
            case R.id.cat_filter_layout:
                Intent catFilterIntent = new Intent(mActivity,FilterLayoutActivity.class);
                startActivity(catFilterIntent);
                break;
            case R.id.sort_filter_layout:

                break;

        }
    }
}
