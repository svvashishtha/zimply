package com.application.zimplyshop.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.ProductsRecyclerViewGridAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.baseobjects.ProductListObject;
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

public class ProductListingActivity extends BaseActivity implements
        OnClickListener, GetRequestListener, RequestTags, AppConstants {

    String url, nextUrl;

    RecyclerView productList;

    boolean isLoading, isRequestAllowed, isDestroyed, isFilterApplied;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    int width;

    int categoryId = 0, sortId = 1, priceLte = 1, priceHigh = 100000;

    boolean isRefreshData;
    Context context;
    TextView titleText;

    boolean isNotification,isO2o;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_toolbar_filter_layout);
        context = getApplicationContext();
        if(getIntent().getStringExtra("category_id")!=null)
            categoryId = getSelectedCatgeoryId(Integer.parseInt(getIntent().getStringExtra("category_id"))) + 1;

        isNotification = getIntent().getBooleanExtra("is_notification",false);
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

        // setProductsGrid();

        url = getIntent().getStringExtra("url");
        isHideFilter = getIntent().getBooleanExtra("hide_filter",false);
        setStatusBarColor();
        setLoadingVariables();
        retryLayout.setOnClickListener(this);
        setFilterVariables();
        setFiltersClick();
        width = (getDisplayMetrics().widthPixels - (int) (2 * getResources()
                .getDimension(R.dimen.font_small))) / 2;
        loadData();
    }


    boolean isHideFilter;
    public int getSelectedCatgeoryId(int categoryId) {
        if (AllProducts.getInstance().getHomeProCatNBookingObj().getProduct_category() != null) {
            for (int i = 0; i < AllProducts.getInstance().getHomeProCatNBookingObj().getProduct_category().size(); i++) {
                CategoryObject obj = AllProducts.getInstance().getHomeProCatNBookingObj().getProduct_category().get(i);
                if (obj.getId().equalsIgnoreCase(categoryId + "")) {
                    return i;
                }
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
        if (nextUrl == null) {

            int width = (getDisplayMetrics().widthPixels-(3*getResources().getDimensionPixelSize(R.dimen.margin_small)))/3;
            if(!isHideFilter) {
                finalUrl = AppApplication.getInstance().getBaseUrl() + url + "?filter=0" + (AppPreferences.isUserLogIn(this) ? "&userid=" + AppPreferences.getUserID(this) : "")
                        + "&width=" + width + ((categoryId != 0) ? "&category__id__in=" + AllProducts.getInstance().getHomeProCatNBookingObj().getProduct_category().get(categoryId - 1).getId() : "")+(isO2o?"&is_o2o="+1:"");
            }else{
                finalUrl = AppApplication.getInstance().getBaseUrl() + url ;
            }
        } else {
            finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
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
                            new ImageLoaderManager(ProductListingActivity.this)
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
        if (getIntent().getStringExtra("category_name") != null) {
            titleText.setText(getIntent().getStringExtra("category_name"));
        } else {
            titleText.setText("Products");
        }
        toolbar.addView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.filter);
        if(isHideFilter){
            item.setVisible(false);
        }else {
            item.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.cart:
                Intent intent = new Intent(this, ProductCheckoutActivity.class);
                intent.putExtra("OrderSummaryFragment", false);
                intent.putExtra("buying_channel",BUYING_CHANNEL_ONLINE);
                startActivity(intent);
                break;
            case R.id.search:
                Intent searchIntent = new Intent(this, NewSearchActivity.class);
                searchIntent.putExtra("position",0);
                startActivity(searchIntent);
                break;
            case R.id.filter:
                if (!isLoading) {
                    showFilterContent();
                } else {
                    Toast.makeText(context, "Please wait while loading..", Toast.LENGTH_SHORT).show();
                }
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
            bundle.putInt("selected_pos", categoryId);
            bundle.putInt("sort_id", sortId);
            bundle.putInt("price_high", priceHigh);
            bundle.putInt("price_low", priceLte);
            bundle.putBoolean("is_o2o",isO2o);
            AllFilterClassActivity dialogFragment = AllFilterClassActivity.newInstance(bundle);
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.HJFullScreenDialogTheme);
            dialogFragment.setOnApplyClickListener(new AllFilterClassActivity.OnApplyClickListener() {
                @Override
                public void onApplyClick(Bundle bundle) {

                    categoryId = bundle.getInt("selected_pos");
                    sortId = bundle.getInt("sort_id");
                    priceLte = bundle.getInt("from_price");
                    priceHigh = bundle.getInt("to_price");
                    isO2o = bundle.getBoolean("is_o2o");
                    nextUrl = url + "?filter=0" + ((categoryId != 0) ? ("&category__id__in=" + Integer.parseInt(AllProducts.getInstance().getHomeProCatNBookingObj().getProduct_category()
                            .get(categoryId - 1).getId())) : "")
                            + ("&low_to_high=" + sortId) + ("&price__gte=" + priceLte) + ("&price__lte=" + priceHigh)
                            + (AppPreferences.isUserLogIn(ProductListingActivity.this)
                            ? "&userid=" + AppPreferences.getUserID(ProductListingActivity.this) : "")
                            +(isO2o?"&is_o2o="+1:"");

                    isRefreshData = true;
                    if (categoryId != 0 || sortId != 1 || priceHigh != 100000 || priceLte != 1)
                        isFilterApplied = true;
                    else isFilterApplied = false;
                    if (categoryId != 0)
                        titleText.setText(AllProducts.getInstance().getHomeProCatNBookingObj().getProduct_category().get(categoryId - 1).getName());
                    else
                        titleText.setText("All");
                    titleText.requestLayout();
                    loadData();
                }
            });

            dialogFragment.show(getSupportFragmentManager(), ARTICLE_PHOTO_CATEGROY_DIALOG_TAG);
        }
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
            case R.id.retry_layout:
                if(isRequestFailed) {
                    loadData();
                }
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
            if (isRefreshData) {
                isRefreshData = false;
            }

            isLoading = false;
        }
        if (isFilterApplied) {
            findViewById(R.id.filter_applied).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.filter_applied).setVisibility(View.GONE);

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
    protected void onResume() {

        if (AllProducts.getInstance().getCartCount() > 0) {
            findViewById(R.id.cart_item_true).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.cart_item_true)).setText(AllProducts.getInstance().getCartCount() + "");
        } else {
            findViewById(R.id.cart_item_true).setVisibility(View.GONE);
        }
        toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(isNotification){
            Intent intent = new Intent(this,HomeActivity.class);
            this.finish();
            startActivity(intent);
        }
        super.onBackPressed();
    }
}
