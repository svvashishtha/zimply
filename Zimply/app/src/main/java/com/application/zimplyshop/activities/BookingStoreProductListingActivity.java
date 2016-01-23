package com.application.zimplyshop.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.BookedStoreProductListAdapter;
import com.application.zimplyshop.adapters.SubCategoryAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.BaseProductListObject;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.ProductListObject;
import com.application.zimplyshop.baseobjects.ShopSubCategoryObj;
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
import com.application.zimplyshop.widgets.CustomCheckBox;
import com.application.zimplyshop.widgets.CustomRadioButton;
import com.application.zimplyshop.widgets.RangeSeekBar;
import com.application.zimplyshop.widgets.SpaceGridItemDecorator;

import java.util.ArrayList;

public class BookingStoreProductListingActivity extends BaseActivity implements
        OnClickListener, GetRequestListener, RequestTags, AppConstants {

    String url, nextUrl;

    RecyclerView productList;
    boolean filterApplied;
    boolean isLoading, isRequestAllowed, isDestroyed, isFilterApplied;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    int width;

    int vendorId = 0, sortId = -1;
    long priceLte = 1, priceHigh = 500000;
    long FROM_VALUE = 1;
    long TO_VALUE = 500000;
    boolean isRefreshData;
    Context context;
    TextView titleText;

    boolean isNotification, isO2o;

    BaseProductListObject obj;

    TextView filterFromTextView, filterToTextView;
    ArrayList<RadioButton> priceRadioButtonsArrayList;
    SubCategoryAdapter subCategoryAdapter;
    ArrayList<ShopSubCategoryObj> subCategories;

    //    used to handle no items view when the user visits the screen for the first time without appliying any filters
    boolean setFilterDataToArbitraryDefaultsIfNoData;
    private boolean isRecyclerViewInLongItemMode;
    GridLayoutManager gridLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_toolbar_filter_layout);
        PAGE_TYPE = AppConstants.PAGE_TYPE_PRODUCT;
        filterFromTextView = (TextView) findViewById(R.id.tv_new_from_price_filter);
        filterToTextView = (TextView) findViewById(R.id.tv_new_to_price_filter);
        context = getApplicationContext();
        vendorId = getIntent().getIntExtra("vendor_id", 0);
        isNotification = getIntent().getBooleanExtra("is_notification", false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        productList = (RecyclerView) findViewById(R.id.categories_list);
        gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {
                if (position == 0)
                    return 2;
                else if (position == productList.getLayoutManager().getItemCount() - 1 && isRequestAllowed) {
                    return 2;

                } else if (!isRequestAllowed && position == productList.getLayoutManager().getItemCount()) {
                    return 2;
                } else return 1;
            }
        });
        productList.setLayoutManager(gridLayoutManager);
        productList.addItemDecoration(new SpaceGridItemDecorator(
                (int) getResources().getDimension(R.dimen.margin_small),
                (int) getResources().getDimension(R.dimen.margin_mini), true));

        // setProductsGrid();
        obj = (BaseProductListObject) getIntent().getSerializableExtra("booked_obj");
        url = getIntent().getStringExtra("url");
        isHideFilter = getIntent().getBooleanExtra("hide_filter", false);
        setStatusBarColor();
        setLoadingVariables();
        retryLayout.setOnClickListener(this);
        findViewById(R.id.null_case_image).setOnClickListener(this);
        // setFilterVariables();
        // setFiltersClick();
        width = (getDisplayMetrics().widthPixels - (int) (2 * getResources()
                .getDimension(R.dimen.font_small))) / 2;
        loadData();
    }

    private void resetFiltersDataRadioButtonsValues() {
        priceLte = 1;
        priceHigh = 10000;
        FROM_VALUE = 1;
        TO_VALUE = 10000;
        final RangeSeekBar seekBar = (RangeSeekBar<Integer>) findViewById(R.id.range_seekbar);
        seekBar.setRangeValues(priceLte, priceHigh);


        filterFromTextView.setText(priceLte + "");
        seekBar.setSelectedMinValue(priceLte);
        filterToTextView.setText(priceHigh + "");
        seekBar.setSelectedMaxValue(priceHigh);

        long diff = priceHigh - priceLte;

        final long[][] pricesList = new long[5][2];
        pricesList[0][0] = priceLte;
        pricesList[0][1] = (long) (priceLte + .10 * diff);
        pricesList[1][0] = (long) (priceLte + .10 * diff);
        pricesList[1][1] = (long) (priceLte + .25 * diff);
        pricesList[2][0] = (long) (priceLte + .25 * diff);
        pricesList[2][1] = (long) (priceLte + .50 * diff);
        pricesList[3][0] = (long) (priceLte + .50 * diff);
        pricesList[3][1] = (long) (priceLte + .80 * diff);
        pricesList[4][0] = (long) (priceLte + .80 * diff);
        pricesList[4][1] = priceHigh;

        priceRadioButtonsArrayList = new ArrayList<>();
        priceRadioButtonsArrayList.add(((RadioButton) findViewById(R.id.filterpriceradiobutton1)));
        priceRadioButtonsArrayList.add(((RadioButton) findViewById(R.id.filterpriceradiobutton2)));
        priceRadioButtonsArrayList.add(((RadioButton) findViewById(R.id.filterpriceradiobutton3)));
        priceRadioButtonsArrayList.add(((RadioButton) findViewById(R.id.filterpriceradiobutton4)));
        priceRadioButtonsArrayList.add(((RadioButton) findViewById(R.id.filterpriceradiobutton5)));

        for (int i = 0; i < priceRadioButtonsArrayList.size(); i++) {
            String radioButtonText = "₹" + pricesList[i][0] + " - ₹" + pricesList[i][1];
            priceRadioButtonsArrayList.get(i).setText(radioButtonText);
            priceRadioButtonsArrayList.get(i).setSelected(false);
        }

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroupfiltrprice);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index = group.indexOfChild(findViewById(group.getCheckedRadioButtonId()));
                seekBar.setSelectedMinValue(pricesList[index][0]);
                seekBar.setSelectedMaxValue(pricesList[index][1]);
                seekBar.setSelectedMinValue(pricesList[index][0]);

                filterToTextView.setText(pricesList[index][1] + "");
                filterFromTextView.setText(pricesList[index][0] + "");
            }
        });
    }

    private void setMinimumAndMaximumFilterPriceValues(ProductListObject obj) {
        if (obj.getMin_price() != null && obj.getMax_price() != null) {
            final RangeSeekBar seekBar = (RangeSeekBar<Integer>) findViewById(R.id.range_seekbar);
            priceLte = obj.getMin_price();
            priceHigh = obj.getMax_price();
            FROM_VALUE = obj.getMin_price();
            TO_VALUE = obj.getMax_price();
            seekBar.setRangeValues(priceLte, priceHigh);

            try {
                if (Long.parseLong(filterFromTextView.getText().toString()) < priceLte) {
                    filterFromTextView.setText(priceLte + "");
                    seekBar.setSelectedMinValue(priceLte);
                }
                if (Long.parseLong(filterToTextView.getText().toString()) > priceHigh) {
                    filterToTextView.setText(priceHigh + "");
                    seekBar.setSelectedMaxValue(priceHigh);
                }
            } catch (Exception e) {

            }

            long diff = obj.getMax_price() - obj.getMin_price();

            final long[][] pricesList = new long[5][2];
            pricesList[0][0] = priceLte;
            pricesList[0][1] = (long) (priceLte + .10 * diff);
            pricesList[1][0] = (long) (priceLte + .10 * diff);
            pricesList[1][1] = (long) (priceLte + .25 * diff);
            pricesList[2][0] = (long) (priceLte + .25 * diff);
            pricesList[2][1] = (long) (priceLte + .50 * diff);
            pricesList[3][0] = (long) (priceLte + .50 * diff);
            pricesList[3][1] = (long) (priceLte + .80 * diff);
            pricesList[4][0] = (long) (priceLte + .80 * diff);
            pricesList[4][1] = priceHigh;

            priceRadioButtonsArrayList = new ArrayList<>();
            priceRadioButtonsArrayList.add(((RadioButton) findViewById(R.id.filterpriceradiobutton1)));
            priceRadioButtonsArrayList.add(((RadioButton) findViewById(R.id.filterpriceradiobutton2)));
            priceRadioButtonsArrayList.add(((RadioButton) findViewById(R.id.filterpriceradiobutton3)));
            priceRadioButtonsArrayList.add(((RadioButton) findViewById(R.id.filterpriceradiobutton4)));
            priceRadioButtonsArrayList.add(((RadioButton) findViewById(R.id.filterpriceradiobutton5)));

            for (int i = 0; i < priceRadioButtonsArrayList.size(); i++) {
                String radioButtonText = "₹" + pricesList[i][0] + " - ₹" + pricesList[i][1];
                priceRadioButtonsArrayList.get(i).setText(radioButtonText);
                priceRadioButtonsArrayList.get(i).setSelected(false);
            }

            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroupfiltrprice);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int index = group.indexOfChild(findViewById(group.getCheckedRadioButtonId()));
                    seekBar.setSelectedMinValue(pricesList[index][0]);
                    seekBar.setSelectedMaxValue(pricesList[index][1]);
                    seekBar.setSelectedMinValue(pricesList[index][0]);

                    filterToTextView.setText(pricesList[index][1] + "");
                    filterFromTextView.setText(pricesList[index][0] + "");
                }
            });
        }
    }

    boolean isHideFilter;
    boolean isFiltersShown;

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
            int width = (getDisplayMetrics().widthPixels - (3 * getResources().getDimensionPixelSize(R.dimen.margin_small))) / 3;
            /*finalUrl = AppApplication.getInstance().getBaseUrl() + url + "?filter=0" + (AppPreferences.isUserLogIn(this) ? "&userid=" + AppPreferences.getUserID(this) : "")
                    + "&width=" + width + "&vendor__id__in=" + vendorId;*/
            finalUrl = AppApplication.getInstance().getBaseUrl() + url + "?filter=0" + (AppPreferences.isUserLogIn(this) ? "&userid=" + AppPreferences.getUserID(this) : "")
                    + "&width=" + width + (isO2o ? "&is_o2o=" + 1 : "")
                    + ((subCategoryAdapter != null && subCategoryAdapter.getSubCategorIds().size() > 0) ? "&subcategory__id__in=" +
                    getSubCategoryString() : "") + "&price__gte=" + priceLte + "&price__lte=" + priceHigh + (sortId != -1 ? "&low_to_high=" + sortId : "") +
                    "&vendor__id__in=" + vendorId;

        } else {
            finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
        }
        GetRequestManager.getInstance().addCallbacks(this);
        GetRequestManager.getInstance().makeAyncRequest(finalUrl,
                PRODUCT_LIST_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_PRODUCT_LIST_OBJECT);
    }

    public String getSubCategoryString() {
        String subCategory = "";
        for (int i = 0; i < subCategoryAdapter.getSubCategorIds().size(); i++) {
            subCategory += subCategoryAdapter.getSubCategorIds().get(i);
            if (i != subCategoryAdapter.getSubCategorIds().size() - 1)
                subCategory += ".";
        }
        return subCategory;
    }

    private void setAdapterData(ArrayList<BaseProductListObject> objs,ProductListObject object) {
        if (productList.getAdapter() == null) {
            int height = (getDisplayMetrics().widthPixels - 3 * ((int) getResources()
                    .getDimension(R.dimen.margin_mini))) / 2;
            BookedStoreProductListAdapter adapter = new BookedStoreProductListAdapter(
                    this, this, height, obj);
            adapter.setCheckLayoutOptionListener(new BookedStoreProductListAdapter.CheckLayoutOptions() {
                @Override
                public boolean checkIsRecyclerViewInLongItemMode() {
                    return isRecyclerViewInLongItemMode;
                }

                @Override
                public void switchRecyclerViewLayoutManager() {
                    if (isRecyclerViewInLongItemMode) {
                        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                            @Override
                            public int getSpanSize(int position) {
                                if (position == 0)
                                    return 2;
                                else if (position == productList.getLayoutManager().getItemCount() - 1 && isRequestAllowed) {
                                    return 2;

                                } else if (!isRequestAllowed && position == productList.getLayoutManager().getItemCount()) {
                                    return 2;
                                } else return 1;
                            }
                        });
                        productList.setLayoutManager(gridLayoutManager);
                        productList.getAdapter().notifyDataSetChanged();
                    } else {
                        productList.setLayoutManager(new LinearLayoutManager(BookingStoreProductListingActivity.this));
                        productList.getAdapter().notifyDataSetChanged();
                    }
                    isRecyclerViewInLongItemMode = !isRecyclerViewInLongItemMode;

                }
            });
            adapter.setCount(object.getCount());

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
                            new ImageLoaderManager(BookingStoreProductListingActivity.this)
                                    .setScrollState(newState);
                            super.onScrollStateChanged(recyclerView, newState);
                        }
                    });


        }
        ((BookedStoreProductListAdapter) productList.getAdapter())
                .addData(objs);
    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(
                R.layout.common_toolbar_text_layout, null);
        titleText = (TextView) view.findViewById(R.id.title_textview);
       /* titleText.setLines(2);
        titleText.setTextSize(14);*/
        if (getIntent().getStringExtra("vendor_name") != null) {
            titleText.setText("More products from " + getIntent().getStringExtra("vendor_name"));
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
        if (isHideFilter) {
            item.setVisible(false);
        } else {
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
                intent.putExtra("buying_channel", BUYING_CHANNEL_ONLINE);
                startActivity(intent);
                break;
            case R.id.search:
                Intent searchIntent = new Intent(this, NewSearchActivity.class);
                searchIntent.putExtra("position", 0);
                startActivity(searchIntent);
                break;
            case R.id.filter:
                if (!isLoading) {
                    //showFilterContent();
                    showFilterSlideIn();
                } else {
                    Toast.makeText(context, "Please wait while loading..", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showFilterSlideIn() {
        View view = findViewById(R.id.filter_layout);
        view.findViewById(R.id.close_filter).setOnClickListener(this);
        findViewById(R.id.reset_btn).setOnClickListener(this);
        animateViewRightIn(view);
    }

    public void animateViewRightIn(final View view) {
        isFiltersShown = true;
        view.setVisibility(View.VISIBLE);
        View transparentView = findViewById(R.id.transparent_view);

        transparentView.setVisibility(View.VISIBLE);
        transparentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateViewRightOut(view);
            }
        });
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, getDisplayMetrics().widthPixels, 0);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(transparentView, View.ALPHA, 0, 1);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(anim1, anim2);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(200);

        set.start();
        manageFiltersData();

    }

    public void animateViewRightOut(final View view) {

        final View transparentView = findViewById(R.id.transparent_view);

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, 0, getDisplayMetrics().widthPixels);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(transparentView, View.ALPHA, 1, 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(anim1, anim2);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(200);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                transparentView.setVisibility(View.GONE);
                isFiltersShown = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();

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
            bundle.putInt("selected_pos", vendorId);
            bundle.putInt("sort_id", sortId);
            bundle.putLong("price_high", priceHigh);
            bundle.putLong("price_low", priceLte);
            AllFilterClassActivity dialogFragment = AllFilterClassActivity.newInstance(bundle);
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.HJFullScreenDialogTheme);
            dialogFragment.setOnApplyClickListener(new AllFilterClassActivity.OnApplyClickListener() {
                @Override
                public void onApplyClick(Bundle bundle) {

                    vendorId = bundle.getInt("selected_pos");
                    sortId = bundle.getInt("sort_id");
                    priceLte = bundle.getInt("from_price");
                    priceHigh = bundle.getInt("to_price");
                    nextUrl = url + "?filter=0" + ((vendorId != 0) ? ("&category__id__in=" + Integer.parseInt(AllProducts.getInstance().getHomeProCatNBookingObj().getProduct_category()
                            .get(vendorId - 1).getId())) : "")
                            + ("&low_to_high=" + sortId) + ("&price__gte=" + priceLte) + ("&price__lte=" + priceHigh)
                            + (AppPreferences.isUserLogIn(BookingStoreProductListingActivity.this)
                            ? "&userid=" + AppPreferences.getUserID(BookingStoreProductListingActivity.this) : "");

                    isRefreshData = true;
                    if (vendorId != 0 || sortId != 1 || priceHigh != TO_VALUE || priceLte != FROM_VALUE)
                        isFilterApplied = true;
                    else isFilterApplied = false;
                    if (vendorId != 0)
                        titleText.setText(AllProducts.getInstance().getHomeProCatNBookingObj().getProduct_category().get(vendorId - 1).getName());
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
            case R.id.null_case_image:
            case R.id.retry_layout:
                if (isRequestFailed) {
                    loadData();
                }
                break;
            case R.id.reset_btn:
                resetFilterValues();
                break;
            case R.id.close_filter:
                animateViewRightOut(findViewById(R.id.filter_layout));
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
                    ((BookedStoreProductListAdapter) productList.getAdapter()).removePreviousData();
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
                    if (!setFilterDataToArbitraryDefaultsIfNoData && !isFilterApplied) {
                        setFilterDataToArbitraryDefaultsIfNoData = true;
                        resetFiltersDataRadioButtonsValues();
                    }
                } else {
                    showToast("No more Products");
                    ((BookedStoreProductListAdapter) productList.getAdapter())
                            .removeItem();
                }

                isRequestAllowed = false;
            } else {
                if (productList.getAdapter() == null) {
                    setMinimumAndMaximumFilterPriceValues(((ProductListObject) obj));
                }
                setAdapterData(((ProductListObject) obj).getProducts(),(ProductListObject) obj);
                nextUrl = ((ProductListObject) obj).getNext_url();
                showView();
                changeViewVisiblity(productList, View.VISIBLE);
                if (((ProductListObject) obj).getProducts().size() < 10) {
                    isRequestAllowed = false;
                    ((BookedStoreProductListAdapter) productList.getAdapter())
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

                ((BookedStoreProductListAdapter) productList.getAdapter())
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
        if (isNotification) {
            Intent intent = new Intent(this, HomeActivity.class);
            this.finish();
            startActivity(intent);
        }
        super.onBackPressed();
    }

    public void manageFiltersData() {

        if (sortId != -1) {
            if (sortId == 1) {
                ((CustomRadioButton) findViewById(R.id.high_to_low)).setChecked(false);
                ((CustomRadioButton) findViewById(R.id.low_to_high)).setChecked(true);
            } else {
                ((CustomRadioButton) findViewById(R.id.high_to_low)).setChecked(true);
                ((CustomRadioButton) findViewById(R.id.low_to_high)).setChecked(false);
            }

        }
        if (isO2o) {
            ((CheckBox) findViewById(R.id.zi_experience_tag)).setChecked(true);
        }
        ((CheckBox) findViewById(R.id.zi_experience_tag)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) findViewById(R.id.zi_experience_tag)).isChecked()) {
                    isFilterApplied = true;
                } else {
                    isFilterApplied = false;
                }
            }
        });
        ((CustomRadioButton) findViewById(R.id.high_to_low)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sortId = 2;
                ((CustomRadioButton) findViewById(R.id.low_to_high)).setChecked(false);
                isFilterApplied = true;
            }
        });
        /*((CheckBox)findViewById(R.id.high_to_low)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sortId = 1;
                ((CheckBox) findViewById(R.id.low_to_high)).setChecked(false);
            }
        });*/
        ((CustomRadioButton) findViewById(R.id.low_to_high)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sortId = 1;
                ((CustomRadioButton) findViewById(R.id.high_to_low)).setChecked(false);
                isFilterApplied = true;
            }
        });
        /*((CheckBox)findViewById(R.id.low_to_high)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sortId = -1;
                ((CheckBox) findViewById(R.id.high_to_low)).setChecked(false);
            }
        });*/
        //Add Logic for price
        filterFromTextView.setText(priceLte + "");
        filterToTextView.setText(priceHigh + "");

        RangeSeekBar seekBar = (RangeSeekBar<Integer>) findViewById(R.id.range_seekbar);
        seekBar.setNotifyWhileDragging(true);
        seekBar.setSelectedMaxValue(priceHigh);
        seekBar.setSelectedMinValue(priceLte);
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                filterFromTextView.setText(minValue + "");
                filterToTextView.setText(maxValue + "");
                isFilterApplied = true;
                for (RadioButton radioButton : priceRadioButtonsArrayList) {
                    radioButton.setChecked(false);
                }
            }
        });

        ListView subCategoriesListView = (ListView) findViewById(R.id.subcategory_list);
        if (subCategories != null && subCategories.size() > 0) {
            (findViewById(R.id.sub_categories_header)).setVisibility(View.VISIBLE);
            if (subCategoryAdapter == null) {
                subCategoryAdapter = new SubCategoryAdapter(this, subCategories);
                subCategoriesListView.setAdapter(subCategoryAdapter);
            }
            CommonLib.setListViewHeightBasedOnChildren(subCategoriesListView);

        } else {
            (findViewById(R.id.sub_categories_header)).setVisibility(View.GONE);
        }

        findViewById(R.id.apply_filter).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isO2o = ((CheckBox) findViewById(R.id.zi_experience_tag)).isChecked();
                priceLte = Long.parseLong(filterFromTextView.getText().toString());
                priceHigh = Long.parseLong(filterToTextView.getText().toString());
                nextUrl = null;
                isRefreshData = true;
                animateViewRightOut(findViewById(R.id.filter_layout));
                filterApplied = true;
//                requestTime = System.currentTimeMillis();
                loadData();
                if ((subCategoryAdapter != null && subCategoryAdapter.getSubCategorIds().size() > 0) || isO2o || sortId == 1 || sortId == 2 || priceLte != FROM_VALUE || priceHigh != TO_VALUE) {
                    isFilterApplied = true;
                    filterApplied = true;
                } else {
                    isFilterApplied = false;
                }
            }
        });
    }

    public void resetFilterValues() {
        sortId = -1;
        isFilterApplied = false;

        for (RadioButton radioButton : priceRadioButtonsArrayList) {
            radioButton.setChecked(false);
        }

        ((CustomCheckBox) findViewById(R.id.zi_experience_tag)).setChecked(false);
        ((CustomRadioButton) findViewById(R.id.high_to_low)).setChecked(false);
        ((CustomRadioButton) findViewById(R.id.low_to_high)).setChecked(false);
        RangeSeekBar seekBar = (RangeSeekBar<Integer>) findViewById(R.id.range_seekbar);
        seekBar.setNotifyWhileDragging(true);
        seekBar.setSelectedMinValue(FROM_VALUE);
        seekBar.setSelectedMaxValue(TO_VALUE);
        seekBar.setSelectedMinValue(FROM_VALUE);
        if (subCategoryAdapter != null)
            subCategoryAdapter.clearAll();
        filterFromTextView.setText(FROM_VALUE + "");
        filterToTextView.setText(TO_VALUE + "");
        filterApplied = false;
    }
}
