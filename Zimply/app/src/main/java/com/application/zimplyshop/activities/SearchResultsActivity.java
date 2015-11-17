package com.application.zimplyshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimplyshop.adapters.ProductsRecyclerViewGridAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.objects.AllCategories;
import com.application.zimplyshop.objects.AllProducts;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.widgets.SpaceGridItemDecorator;

import java.util.ArrayList;

public class SearchResultsActivity extends BaseActivity implements
        View.OnClickListener, GetRequestListener, RequestTags, AppConstants {

    String url, nextUrl;

    RecyclerView productList;

    boolean isLoading, isRequestAllowed, isDestroyed;

    int pastVisiblesItems, visibleItemCount, totalItemCount;

    int width;

    int sortId = 1, priceLte = 0, priceHigh = 50000;

    boolean isRefreshData;
    ArrayList<HomeProductObj> homeProductObjs;
    private String query, value;
    private int type = -1;
    private int pageNo = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_toolbar_filter_layout);
        GetRequestManager.getInstance().addCallbacks(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        findViewById(R.id.cart_item_true).setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        productList = (RecyclerView) findViewById(R.id.categories_list);

        productList.setLayoutManager(new GridLayoutManager(this, 2));
        productList.addItemDecoration(new SpaceGridItemDecorator(
                (int) getResources().getDimension(R.dimen.margin_small),
                (int) getResources().getDimension(R.dimen.margin_mini)));

        // setProductsGrid();
        url = getIntent().getStringExtra("url");
        query = getIntent().getStringExtra("query");
        if(getIntent().getExtras().containsKey("name"))
            value = getIntent().getStringExtra("name");
        if(getIntent().getExtras().containsKey("type"))
            type = getIntent().getIntExtra("type", CommonLib.CATEGORY);
        //      dupType = getIntent().getIntExtra("dup_type", CommonLib.DUP_TYPE_FALSE);

        setStatusBarColor();
        setLoadingVariables();
        setFilterVariables();
        setFiltersClick();
        width = (getDisplayMetrics().widthPixels - (int) (2 * getResources()
                .getDimension(R.dimen.font_small))) / 2;
        homeProductObjs = new ArrayList<>();
        loadData();
    }

    public int getSelectedCatgeoryId(int categoryId) {
        for (int i = 0; i < AllProducts.getInstance().getProduct_category().size(); i++) {
            CategoryObject obj = AllProducts.getInstance().getProduct_category().get(i);
            if (obj.getId().equalsIgnoreCase(categoryId + "")) {
                return i;
            }
        }
        return 0;
    }

    private void setFiltersClick() {
        filterLayout.setOnClickListener(this);
        categoriesLayout.setOnClickListener(this);
        selectfiltersLayout.setOnClickListener(this);
        sortByLayout.setOnClickListener(this);
    }

    private void loadData() {
        String finalUrl;
        String queryUrl = "&query=" + query;
        String field = (type == -1)? "" : (type == CommonLib.CATEGORY ? "&field=cat" : type == CommonLib.SUB_CATEGORY ? "&field=subcat" : "");
        String valueQuery = (value == null) ? "" : "&value="+value;
        finalUrl = AppApplication.getInstance().getBaseUrl() + url
                + "?width=" + ((width / 2) - (width / 15))
                + field
                + queryUrl
                + valueQuery
                + "&page=" + pageNo;

        GetRequestManager.getInstance().makeAyncRequest(finalUrl,
                PRODUCT_LIST_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_PRODUCT_SEARCH_RESULT);
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
                                isRefreshData = true;
                            }
                            scrollToolbarAndHeaderBy(-dy);

                        }

                        @Override
                        public void onScrollStateChanged(
                                RecyclerView recyclerView, int newState) {
                            new ImageLoaderManager(SearchResultsActivity.this)
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
        TextView titleText = (TextView) view.findViewById(R.id.title_textview);
        if (getIntent().getStringExtra("category_name") != null) {
            titleText.setText("Showing products in " + getIntent().getStringExtra("category_name"));
        } else {
            titleText.setText("Products");
        }
        toolbar.addView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.cart).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.filter:
                showFilterContent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showFilterContent() {
        if (AllCategories.getInstance().getPhotoCateogryObjs() == null) {
            if (!CommonLib.isNetworkAvailable(this))
                showToast("Please connect to internet");
            else
                showToast("Something went wrong. Please try again.");

        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("is_products", true);
            bundle.putInt("sort_id", sortId);
            bundle.putInt("price_high", priceHigh);
            bundle.putInt("price_low", priceLte);
            AllFilterClassActivity dialogFragment = AllFilterClassActivity.newInstance(bundle);
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.HJCustomDialogTheme);
            dialogFragment.setOnApplyClickListener(new AllFilterClassActivity.OnApplyClickListener() {
                @Override
                public void onApplyClick(Bundle bundle) {

                    sortId = bundle.getInt("sort_id");
                    priceLte = bundle.getInt("from_price");
                    priceHigh = bundle.getInt("to_price");
                    nextUrl = url + "?filter=0"
                            + ("&low_to_high=" + sortId) + ("&price__lte=" + priceLte) + ("&price__gte=" + priceHigh)
                            + (AppPreferences.isUserLogIn(SearchResultsActivity.this)
                            ? "&userid=" + AppPreferences.getUserID(SearchResultsActivity.this) : "");

                    isRefreshData = true;
                    loadData();
                }
            });

            dialogFragment.show(getSupportFragmentManager(), ARTICLE_PHOTO_CATEGROY_DIALOG_TAG);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        toolbar.findViewById(R.id.cart_item_true).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter_filter_layout:
                Intent intent = new Intent(this, FilterFiltersLayout.class);
                startActivity(intent);
                break;
            case R.id.cat_filter_layout:
                Intent catFilterIntent = new Intent(this,
                        FilterLayoutActivity.class);
                startActivity(catFilterIntent);
                break;
            case R.id.sort_filter_layout:

                break;

        }
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (!isDestroyed
                && requestTag.equalsIgnoreCase(PRODUCT_LIST_REQUEST_TAG)) {
            if (productList.getAdapter() == null
                    || productList.getAdapter().getItemCount() == 0 || isRefreshData) {
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
                && requestTag.equalsIgnoreCase(PRODUCT_LIST_REQUEST_TAG)) {


            if (isRefreshData) {
                homeProductObjs = null;
                homeProductObjs = new ArrayList<>((ArrayList<HomeProductObj>) obj);
            }
            homeProductObjs.addAll((ArrayList<HomeProductObj>) obj);
            if (homeProductObjs.size() == 0) {
                if (productList.getAdapter() == null
                        || productList.getAdapter().getItemCount() == 1) {
                    showNullCaseView("No Products");

                } else {
                    showToast("No more Products");

                }
                isRequestAllowed = false;
            } else {
                //homeProductObjs.addAll();
                // so that products are not repeated.
                setAdapterData(homeProductObjs);
                // nextUrl = ((ProductListObject) obj).getNext_url();
                showView();
                pageNo++;
                changeViewVisiblity(productList, View.VISIBLE);
                if (homeProductObjs.size() < 10) {
                    isRequestAllowed = false;
                    ((ProductsRecyclerViewGridAdapter) productList.getAdapter())
                            .removeItem();
                } else {
                    isRequestAllowed = true;
                }
            }
            if (isRefreshData) {
                isRefreshData = false;
            }

            isLoading = false;
        }

    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (!isDestroyed
                && requestTag.equalsIgnoreCase(PRODUCT_LIST_REQUEST_TAG)) {
            if (productList.getAdapter() == null
                    || productList.getAdapter().getItemCount() == 1) {
                showNetworkErrorView();
                changeViewVisiblity(productList, View.GONE);
                isRefreshData = false;
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
