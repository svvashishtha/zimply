package com.application.zimplyshop.activities;

import android.os.AsyncTask;
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
import com.application.zimplyshop.baseobjects.CacheProductListObject;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.ProductListObject;
import com.application.zimplyshop.db.RecentProductsDBWrapper;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.widgets.GridItemDecorator;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 1/22/2016.
 */
public class RecentProductsActivity extends BaseActivity implements
        View.OnClickListener, GetRequestListener, RequestTags, AppConstants {

    String url, nextUrl;
    RecyclerView productList;

    boolean isLoading, isRequestAllowed, isDestroyed;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    int width;
    TextView titleText;
    long productId;

    long timeStamp;
    private boolean isRecyclerViewInLongItemMode;
    private GridItemDecorator gridDecor, linearDecor;
    GridLayoutManager gridLayoutManager;

    int count = 0;

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

        gridDecor = new GridItemDecorator(
                (int) getResources().getDimension(R.dimen.margin_small),
                (int) getResources().getDimension(R.dimen.margin_mini), false);
        linearDecor = new GridItemDecorator(
                (int) getResources().getDimension(R.dimen.margin_small),
                (int) getResources().getDimension(R.dimen.margin_mini), true);
        gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                switch (((ProductsRecyclerViewGridAdapter) productList
                        .getAdapter()).getItemViewType(position)) {
                    case 0:
                        return 1;
                    case 1:
                        return 2;
                    case 2:
                        return 2;
                    default:
                        return -1;
                }
            }
        });

        productList.setLayoutManager(gridLayoutManager);
        productList.addItemDecoration(gridDecor);

        setStatusBarColor();
        setLoadingVariables();
        retryLayout.setOnClickListener(this);
        findViewById(R.id.null_case_image).setOnClickListener(this);

        (findViewById(R.id.cart_item_true)).setVisibility(View.GONE);
        width = (getDisplayMetrics().widthPixels - (int) (2 * getResources()
                .getDimension(R.dimen.font_small))) / 2;
        //productId = getIntent().getExtras().getLong("productid");
        if (AppPreferences.isUserLogIn(this)) {
            loadData();
        } else {
            timeStamp = System.currentTimeMillis();
            new GetDataFromCache().execute();
        }
    }

    private void loadData() {
        String finalUrl;
        if (nextUrl == null) {
            int width = (getDisplayMetrics().widthPixels - (3 * getResources().getDimensionPixelSize(R.dimen.margin_small))) / 3;
            url = PRODUCT_DESCRIPTION_RECENT_PRODUCTS_URL + "?userid=" + AppPreferences.getUserID(this)
                    + "&width=" + width + "&size=10&page=1";
            finalUrl = AppApplication.getInstance().getBaseUrl() + url;
        } else {
            finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
        }
        GetRequestManager.getInstance().addCallbacks(this);
        GetRequestManager.getInstance().makeAyncRequest(finalUrl,
                PRODUCT_LIST_REQUEST_TAG + 2,
                ObjectTypes.OBJECT_TYPE_PRODUCT_LIST_OBJECT);
    }

    private void setAdapterData(ArrayList<BaseProductListObject> objs, int count) {
        if (productList.getAdapter() == null) {
            int height = (getDisplayMetrics().widthPixels - 3 * ((int) getResources()
                    .getDimension(R.dimen.margin_mini))) / 2;
            ProductsRecyclerViewGridAdapter adapter = new ProductsRecyclerViewGridAdapter(
                    this, this, height);
            adapter.setCount(count);
            adapter.setCheckLayoutOptionListener(new ProductsRecyclerViewGridAdapter.CheckLayoutOptions() {
                @Override
                public boolean checkIsRecyclerViewInLongItemMode() {
                    return isRecyclerViewInLongItemMode;
                }

                @Override
                public void switchRecyclerViewLayoutManager() {
                    if (isRecyclerViewInLongItemMode) {
                        productList.removeItemDecoration(linearDecor);
                        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                            public int getSpanSize(int position) {
                                switch (((ProductsRecyclerViewGridAdapter) productList
                                        .getAdapter()).getItemViewType(position)) {
                                    case 0:
                                        return 1;
                                    case 1:
                                        return 2;
                                    case 2:
                                        return 2;
                                    default:
                                        return -1;
                                }
                            }
                        });
                        productList.setLayoutManager(gridLayoutManager);

                        productList.addItemDecoration(gridDecor);
                        productList.getAdapter().notifyDataSetChanged();
                    } else {
                        productList.removeItemDecoration(gridDecor);
                        productList.addItemDecoration(linearDecor);
                        productList.setLayoutManager(new LinearLayoutManager(RecentProductsActivity.this));
                        productList.getAdapter().notifyDataSetChanged();
                    }
                    isRecyclerViewInLongItemMode = !isRecyclerViewInLongItemMode;
                }
            });

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
                                if (AppPreferences.isUserLogIn(RecentProductsActivity.this)) {
                                    loadData();
                                } else {
                                    new GetDataFromCache().execute();
                                }
                            }
                            //scrollToolbarAndHeaderBy(-dy);

                        }

                        @Override
                        public void onScrollStateChanged(
                                RecyclerView recyclerView, int newState) {
                            new ImageLoaderManager(RecentProductsActivity.this)
                                    .setScrollState(newState);
                            super.onScrollStateChanged(recyclerView, newState);
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
        titleText.setText("Recently viewed");
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
                && requestTag.equalsIgnoreCase(PRODUCT_LIST_REQUEST_TAG + 2)) {
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
                && requestTag.equalsIgnoreCase(PRODUCT_LIST_REQUEST_TAG + 2)) {
            if (((ProductListObject) obj).getProducts().size() == 0) {
                isRequestAllowed = false;
                if (productList.getAdapter() == null
                        || productList.getAdapter().getItemCount() == 1) {
                    showNullCaseView("No Products");
                } else {
                    showToast("No more Products");
                    ((ProductsRecyclerViewGridAdapter) productList.getAdapter())
                            .removeItem();
                }

            } else {
                setAdapterData(((ProductListObject) obj).getProducts(), ((ProductListObject) obj).getCount());

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
                && requestTag.equalsIgnoreCase(PRODUCT_LIST_REQUEST_TAG + 2)) {
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

    public class GetDataFromCache extends AsyncTask<Void, Void, Object> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (productList.getAdapter() == null
                    || productList.getAdapter().getItemCount() == 0) {
                showLoadingView();
                changeViewVisiblity(productList, View.GONE);
            }
        }

        @Override
        protected Object doInBackground(Void... params) {
            CacheProductListObject result = null;
            count = RecentProductsDBWrapper.getProductsCount(1);
            /*try {
                int userId = Integer.parseInt(AppPreferences.getUserID(RecentProductsActivity.this));
                result = RecentProductsDBWrapper.getProducts(userId,timeStamp,10);
            } catch(NumberFormatException e) {*/
            result = RecentProductsDBWrapper.getProducts(1, timeStamp, 10);
            /*} catch (Exception e) {
                e.printStackTrace();
            }*/
            return result;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (!isDestroyed && result != null) {

                timeStamp = ((CacheProductListObject) result).getTimeStamp();
                setAdapterData(((CacheProductListObject) result).getObjects(),count);
                showView();
                changeViewVisiblity(productList, View.VISIBLE);
                if (((CacheProductListObject) result).getObjects().size() != 10) {
                    isRequestAllowed = false;
                    if (productList.getAdapter() != null)
                        ((ProductsRecyclerViewGridAdapter) productList.getAdapter())
                                .removeItem();
                } else {
                    isRequestAllowed = true;
                }
            }
        }

    }
}
