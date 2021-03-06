package com.application.zimply.activities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimply.adapters.ProductsRecyclerViewGridAdapter;
import com.application.zimply.application.AppApplication;
import com.application.zimply.baseobjects.ErrorObject;
import com.application.zimply.baseobjects.HomeProductObj;
import com.application.zimply.baseobjects.MyWishListObject;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.extras.ObjectTypes;
import com.application.zimply.managers.GetRequestListener;
import com.application.zimply.managers.GetRequestManager;
import com.application.zimply.managers.ImageLoaderManager;
import com.application.zimply.preferences.AppPreferences;
import com.application.zimply.serverapis.RequestTags;
import com.application.zimply.utils.UploadManager;
import com.application.zimply.utils.UploadManagerCallback;
import com.application.zimply.widgets.SpaceGridItemDecorator;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 11/2/2015.
 */
public class MyWishlist extends BaseActivity implements GetRequestListener,View.OnClickListener, UploadManagerCallback{

    RecyclerView productList;

    String url, nextUrl;

    int pastVisiblesItems, visibleItemCount, totalItemCount;

    int width;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_toolbar_filter_layout);
        GetRequestManager.getInstance().addCallbacks(this);
        UploadManager.getInstance().addCallback(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        productList = (RecyclerView) findViewById(R.id.categories_list);

        productList.setLayoutManager(new GridLayoutManager(this, 2));
        productList.addItemDecoration(new SpaceGridItemDecorator(
                (int) getResources().getDimension(R.dimen.margin_small),
                (int) getResources().getDimension(R.dimen.margin_mini)));
        findViewById(R.id.cart_item_true).setVisibility(View.GONE);
        // setProductsGrid();
        url = getIntent().getStringExtra("url");

        setStatusBarColor();
        setLoadingVariables();
        retryLayout.setOnClickListener(this);

        width = (getDisplayMetrics().widthPixels - (int) (2 * getResources()
                .getDimension(R.dimen.font_small))) / 2;
        loadData();
    }

    private void loadData() {
        String finalUrl;
        if (nextUrl == null) {
            int width = (getDisplayMetrics().widthPixels-(3*getResources().getDimensionPixelSize(R.dimen.margin_small)))/3;
            finalUrl = AppApplication.getInstance().getBaseUrl() + AppConstants.USER_WISHLIST+"?userid="+AppPreferences.getUserID(this)+"&width="+width;
        } else {
            finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
        }
        GetRequestManager.getInstance().makeAyncRequest(finalUrl,
                RequestTags.USER_WISHLIST,
                ObjectTypes.OBJECT_TYPE_PRODUCT_WISHLIST);
    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(
                R.layout.common_toolbar_text_layout, null);
        TextView titleText = (TextView) view.findViewById(R.id.title_textview);
        titleText.setText("Wishlist");
        toolbar.addView(view);
    }

    boolean isLoading,isRequestAllowed;

    @Override
    public void onRequestStarted(String requestTag) {
        if(requestTag.equalsIgnoreCase(RequestTags.USER_WISHLIST)){
            if (productList.getAdapter() == null
                    || productList.getAdapter().getItemCount() == 0 ) {
                showLoadingView();
                changeViewVisiblity(productList, View.GONE);
                if (productList.getAdapter() != null)
                    ((ProductsRecyclerViewGridAdapter) productList.getAdapter()).removePreviousData();
            } else {

            }
            isLoading = true;
        }
    }

    boolean isDestroyed = false;


    @Override
    public void onDestroy() {
        isDestroyed = true;
        UploadManager.getInstance().removeCallback(this);
        GetRequestManager.getInstance().removeCallbacks(this);

        super.onDestroy();
    }
    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if(!isDestroyed && requestTag.equalsIgnoreCase(RequestTags.USER_WISHLIST)){
            if (((MyWishListObject) obj).getFavourite().size() == 0) {
                if (productList.getAdapter() == null
                        || productList.getAdapter().getItemCount() == 1) {
                    showNullCaseView("No Products");

                } else {
                    showToast("No more Products");
                    ((ProductsRecyclerViewGridAdapter) productList.getAdapter())
                            .removeItem();
                }
                isRequestAllowed = false;
            } else {
                setAdapterData(((MyWishListObject) obj).getFavourite());
                nextUrl = ((MyWishListObject) obj).getNext_url();
                showView();
                changeViewVisiblity(productList, View.VISIBLE);
                if (((MyWishListObject) obj).getFavourite().size() < 10) {
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
        if(!isDestroyed && requestTag.equalsIgnoreCase(RequestTags.USER_WISHLIST)){
            {
                if (productList.getAdapter() == null
                        || productList.getAdapter().getItemCount() == 1) {
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
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.retry_layout:
                if(isRequestFailed){
                    loadData();
                }
                break;
        }
    }

    private void setAdapterData(ArrayList<HomeProductObj> objs) {
        if (productList.getAdapter() == null) {
            int height = (getDisplayMetrics().widthPixels - 3 * ((int) getResources()
                    .getDimension(R.dimen.margin_mini))) / 2;
            ProductsRecyclerViewGridAdapter adapter = new ProductsRecyclerViewGridAdapter(
                    this, this, height);
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
                            //scrollToolbarAndHeaderBy(-dy);

                        }

                        @Override
                        public void onScrollStateChanged(
                                RecyclerView recyclerView, int newState) {
                            new ImageLoaderManager(MyWishlist.this)
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
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if(requestType == RequestTags.MARK_UN_FAVOURITE_REQUEST_TAG) {
            if(!isDestroyed && status) {
                ((ProductsRecyclerViewGridAdapter) productList.getAdapter())
                        .updateList(objectId, RequestTags.MARK_UN_FAVOURITE_REQUEST_TAG);

                if(productList.getAdapter().getItemCount()==0){
                    showNullCaseView("No Products");
                }
            }
        } else if(requestType == RequestTags.MARK_FAVOURITE_REQUEST_TAG) {
            if(!isDestroyed && status) {
                ((ProductsRecyclerViewGridAdapter) productList.getAdapter())
                        .updateList(data,  RequestTags.MARK_FAVOURITE_REQUEST_TAG);
            }
        }
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {

    }
}
