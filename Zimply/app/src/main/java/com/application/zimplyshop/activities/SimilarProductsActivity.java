package com.application.zimplyshop.activities;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.ProductsRecyclerViewGridAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.BaseProductListObject;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.ProductListObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.widgets.SpaceGridItemDecorator;

import java.util.ArrayList;

/**
 * Created by Ashish Goel on 1/8/2016.
 */
public class SimilarProductsActivity extends BaseActivity implements
        View.OnClickListener, GetRequestListener, RequestTags, AppConstants {

    String url, nextUrl;
    RecyclerView productList;

    boolean isLoading, isRequestAllowed, isDestroyed;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    int width;
    TextView titleText;
    long productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_toolbar_filter_layout);
        PAGE_TYPE = AppConstants.PAGE_TYPE_PRODUCT;

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

        setStatusBarColor();
        setLoadingVariables();
        retryLayout.setOnClickListener(this);
        findViewById(R.id.null_case_image).setOnClickListener(this);
        width = (getDisplayMetrics().widthPixels - (int) (2 * getResources()
                .getDimension(R.dimen.font_small))) / 2;
        productId = getIntent().getExtras().getLong("productid");

        loadData();
    }

    private void loadData() {
        String finalUrl;
        if (nextUrl == null) {
            int width = (getDisplayMetrics().widthPixels - (3 * getResources().getDimensionPixelSize(R.dimen.margin_small))) / 3;
            url = PRODUCT_DESCRIPTION_SIMILAR_PRODUCTS_URL + "?id=" + productId
                    + "&width=" + width + "&size=20&page=1";
            finalUrl = AppApplication.getInstance().getBaseUrl() + url;
        } else {
            finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
        }
        GetRequestManager.getInstance().addCallbacks(this);
        GetRequestManager.getInstance().makeAyncRequest(finalUrl,
                PRODUCT_LIST_REQUEST_TAG + 1,
                ObjectTypes.OBJECT_TYPE_PRODUCT_LIST_OBJECT);
    }

    private void setAdapterData(ArrayList<BaseProductListObject> objs) {
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
                            new ImageLoaderManager(SimilarProductsActivity.this)
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

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(
                R.layout.common_toolbar_text_layout, null);
        titleText = (TextView) view.findViewById(R.id.title_textview);
        titleText.setText("You May Also Like");
        toolbar.addView(view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.null_case_image:
            case R.id.retry_layout:
                if (isRequestFailed) {
                    loadData();
                }
                break;
        }
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (!isDestroyed
                && requestTag.equalsIgnoreCase(PRODUCT_LIST_REQUEST_TAG + 1)) {
            if (productList.getAdapter() == null
                    || productList.getAdapter().getItemCount() == 0) {
                showLoadingView();
                changeViewVisiblity(productList, View.GONE);
                if (productList.getAdapter() != null)
                    ((ProductsRecyclerViewGridAdapter) productList.getAdapter()).removePreviousData();
            } else {

            }
            isLoading = true;
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (!isDestroyed
                && requestTag.equalsIgnoreCase(PRODUCT_LIST_REQUEST_TAG + 1)) {
            if (((ProductListObject) obj).getProducts().size() == 0) {
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
                setAdapterData(((ProductListObject) obj).getProducts());

                nextUrl = ((ProductListObject) obj).getNext_url();
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
                && requestTag.equalsIgnoreCase(PRODUCT_LIST_REQUEST_TAG + 1)) {
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

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }
}
