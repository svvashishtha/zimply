package com.application.zimplyshop.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.ProductsRecyclerViewGridAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.BaseProductListObject;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.ProductListObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.InputFilterMinMax;
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

public class SearchResultsActivity extends BaseActivity implements
        View.OnClickListener, GetRequestListener, RequestTags, AppConstants {

    String url, nextUrl;

    RecyclerView productList;

    boolean isLoading, isRequestAllowed, isDestroyed;

    int pastVisiblesItems, visibleItemCount, totalItemCount;

    int width;

    int sortId = -1;

    private boolean isO2o, isFilterApplied;

    boolean isRefreshData;
    ArrayList<BaseProductListObject> homeProductObjs;
    private String query;
    private String value = "";
    private int type = -1;
    private int pageNo = 1;

    long priceLte = 1, priceHigh = 500000;

    long FROM_VALUE = 1;
    long TO_VALUE = 500000;
    private double requestTime;

    TextView filterFromTextView, filterToTextView;
    ArrayList<RadioButton> priceRadioButtonsArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_toolbar_filter_layout);
        GetRequestManager.getInstance().addCallbacks(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        query = getIntent().getStringExtra("query");
        addToolbarView(toolbar);
        findViewById(R.id.cart_item_true).setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        filterFromTextView = (TextView) findViewById(R.id.tv_new_from_price_filter);
        filterToTextView = (TextView) findViewById(R.id.tv_new_to_price_filter);

        productList = (RecyclerView) findViewById(R.id.categories_list);

        productList.setLayoutManager(new GridLayoutManager(this, 2));
        productList.addItemDecoration(new SpaceGridItemDecorator(
                (int) getResources().getDimension(R.dimen.margin_small),
                (int) getResources().getDimension(R.dimen.margin_mini)));

        // setProductsGrid();
        url = getIntent().getStringExtra("url");
        if (getIntent().getExtras().containsKey("name"))
            value = getIntent().getStringExtra("name");
        if (getIntent().getExtras().containsKey("type"))
            type = getIntent().getIntExtra("type", CommonLib.CATEGORY);
        //      dupType = getIntent().getIntExtra("dup_type", CommonLib.DUP_TYPE_FALSE);

        setStatusBarColor();
        setLoadingVariables();
        // setFilterVariables();
        // setFiltersClick();
        retryLayout.setOnClickListener(this);
        width = (getDisplayMetrics().widthPixels - (int) (2 * getResources()
                .getDimension(R.dimen.font_small))) / 2;
        homeProductObjs = new ArrayList<>();
        loadData();
    }

    ArrayList<Integer> subCategoryId;

    public String getSubCategoryString() {
        String subCategory = "";
        for (int i = 0; i < subCategoryId.size(); i++) {
            subCategory += subCategoryId.get(i);
            if (i != subCategoryId.size() - 1)
                subCategory += ".";
        }
        return subCategory;
    }

    public int getSelectedCatgeoryId(int categoryId) {
        for (int i = 0; i < AllProducts.getInstance().getHomeProCatNBookingObj().getProduct_category().size(); i++) {
            CategoryObject obj = AllProducts.getInstance().getHomeProCatNBookingObj().getProduct_category().get(i);
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
        if (nextUrl == null) {

            String queryUrl = "&query=" + query;
            String field = (type == -1) ? "" : (type == CommonLib.CATEGORY ? "&field=cat" : type == CommonLib.SUB_CATEGORY ? "&field=subcat" : "");
            String valueQuery = (value == "") ? "" : "&value=" + value;
            finalUrl = AppApplication.getInstance().getBaseUrl() + url
                    + "?width=" + ((width / 2) - (width / 15))
                    + field
                    + queryUrl
                    + valueQuery +
                    (sortId != -1 ? "&low_to_high=" + sortId : "") + ("&price__gte=" + priceLte) + ("&price__lte=" + priceHigh)
                    + (AppPreferences.isUserLogIn(SearchResultsActivity.this)
                    ? "&userid=" + AppPreferences.getUserID(SearchResultsActivity.this) : "")
                    + (isO2o ? "&is_o2o=" + 1 : "");
        } else {
           /* String queryUrl = "&query=" + query;
            String field = (type == -1) ? "" : (type == CommonLib.CATEGORY ? "&field=cat" : type == CommonLib.SUB_CATEGORY ? "&field=subcat" : "");
            String valueQuery = (value == "") ? "" : "&value=" + value;*/
            finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
                    /*+ "&width=" + ((width / 2) - (width / 15))
                    + field
                    + queryUrl
                    + valueQuery
                    + "&page=" + pageNo+
                    ( sortId != -1 ? "&low_to_high=" + sortId:"") + ("&price__gte=" + priceLte) + ("&price__lte=" + priceHigh)
                    + (AppPreferences.isUserLogIn(SearchResultsActivity.this)
                    ? "&userid=" + AppPreferences.getUserID(SearchResultsActivity.this) : "")
                    +(isO2o?"&is_o2o="+1:"");*/
        }
        requestTime = System.currentTimeMillis();
        GetRequestManager.getInstance().makeAyncRequest(finalUrl,
                PRODUCT_LIST_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_PRODUCT_SEARCH_RESULT);
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
                                isRefreshData = true;
                            }
                            //               scrollToolbarAndHeaderBy(-dy);

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
            titleText.setText("Products in " + getIntent().getStringExtra("category_name"));
        } else {
            titleText.setText("Results for " + query);
        }
        view.findViewById(R.id.search_frame).setVisibility(View.GONE);
        toolbar.addView(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.cart).setVisible(true);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.filter).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.filter:
                if (!isLoading) {
                    //  showFilterContent();
                    showFilterSlideIn();
                } else {
                    Toast.makeText(this, "Please wait while loading..", Toast.LENGTH_SHORT).show();
                }
                //showFilterContent();
                break;
            case R.id.cart:
                Intent intent = new Intent(this, ProductCheckoutActivity.class);
                intent.putExtra("OrderSummaryFragment", false);
                intent.putExtra("buying_channel", BUYING_CHANNEL_ONLINE);
                startActivity(intent);
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

    boolean isFiltersShown;

    public void animateViewRightIn(final View view) {
        isFiltersShown = true;
        view.setVisibility(View.VISIBLE);
        View transparentView = findViewById(R.id.transparent_view);

        transparentView.setVisibility(View.VISIBLE);
        transparentView.setOnClickListener(new View.OnClickListener() {
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

    @Override
    protected void onResume() {

        if (AllProducts.getInstance().getCartCount() > 0) {
            findViewById(R.id.cart_item_true).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.cart_item_true)).setText(AllProducts.getInstance().getCartCount() + "");
        } else {
            findViewById(R.id.cart_item_true).setVisibility(View.GONE);
        }

        super.onResume();
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
//            bundle.putInt("selected_pos", categoryId);
            bundle.putInt("sort_id", sortId);
            bundle.putLong("price_high", priceHigh);
            bundle.putLong("price_low", priceLte);
            bundle.putBoolean("is_o2o", isO2o);
            AllFilterClassActivity dialogFragment = AllFilterClassActivity.newInstance(bundle);
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.HJFullScreenDialogTheme);
            dialogFragment.setOnApplyClickListener(new AllFilterClassActivity.OnApplyClickListener() {
                @Override
                public void onApplyClick(Bundle bundle) {

//                    categoryId = bundle.getInt("selected_pos");
                    sortId = bundle.getInt("sort_id");
                    priceLte = bundle.getInt("from_price");
                    priceHigh = bundle.getInt("to_price");
                    isO2o = bundle.getBoolean("is_o2o");

                    boolean isResetClicked = bundle.getBoolean("is_reset");
                    nextUrl = url + "?filter=0" + (sortId != -1 ? "&low_to_high=" + sortId : "") + ("&price__gte=" + priceLte) + ("&price__lte=" + priceHigh)
                            + (AppPreferences.isUserLogIn(SearchResultsActivity.this)
                            ? "&userid=" + AppPreferences.getUserID(SearchResultsActivity.this) : "")
                            + (isO2o ? "&is_o2o=" + 1 : "");

                    isRefreshData = true;
                    if (sortId == -1 && priceHigh == 500000 && priceLte == 1 && !isO2o) {
                        isFilterApplied = false;
                    } else {
                        isFilterApplied = true;
                    }
                    loadData();
                }
            });

            dialogFragment.show(getSupportFragmentManager(), ARTICLE_PHOTO_CATEGROY_DIALOG_TAG);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_btn:
                resetFilterValues();
                break;
            case R.id.close_filter:
                animateViewRightOut(findViewById(R.id.filter_layout));
                break;
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
                if (isRequestFailed) {
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
            CommonLib.ZLog("Request Time", "Product Listing Page from search Request :" + (System.currentTimeMillis() - requestTime) + " mS");
            CommonLib.writeRequestData("Product Listing Page from search Request :" + (System.currentTimeMillis() - requestTime) + " mS");

            if (isRefreshData) {
                homeProductObjs = null;
                homeProductObjs = new ArrayList<>();
            }
            homeProductObjs.addAll(((ProductListObject) obj).getProducts());

            if (homeProductObjs.size() == 0) {
                if (productList.getAdapter() == null
                        || productList.getAdapter().getItemCount() == 1) {
                    showNullCaseView("No Products");

                } else {
                    showToast("No more Products");
                    ((ProductsRecyclerViewGridAdapter) productList.getAdapter()).removeItem();

                }
                isRequestAllowed = false;
            } else {
                nextUrl = ((ProductListObject) obj).getNext_url();
                //homeProductObjs.addAll();
                // so that products are not repeated.
                if (productList.getAdapter() == null) {
                    setMinimumAndMaximumFilterPriceValues(((ProductListObject) obj));
                }
                setAdapterData(homeProductObjs);
                // nextUrl = ((ProductListObject) obj).getNext_url();
                //subCategories =((ProductListObject) obj).getSubcategory();
                showView();
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
        if (isFilterApplied) {
            if (!isDestroyed)
                findViewById(R.id.filter_applied).setVisibility(View.VISIBLE);
        } else {
            if (!isDestroyed)
                findViewById(R.id.filter_applied).setVisibility(View.GONE);
        }


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

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (!isDestroyed
                && requestTag.equalsIgnoreCase(PRODUCT_LIST_REQUEST_TAG)) {
            if (productList.getAdapter() == null
                    || productList.getAdapter().getItemCount() == 1) {
                if (CommonLib.isNetworkAvailable(SearchResultsActivity.this))
                    showToast("Please check your internet connection");
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
        ((CheckBox) findViewById(R.id.zi_experience_tag)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) findViewById(R.id.zi_experience_tag)).isChecked()) {
                    isFilterApplied = true;
                } else {
                    isFilterApplied = false;
                }
            }
        });
        ((CustomRadioButton) findViewById(R.id.high_to_low)).setOnClickListener(new View.OnClickListener() {
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
        ((CustomRadioButton) findViewById(R.id.low_to_high)).setOnClickListener(new View.OnClickListener() {
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


        findViewById(R.id.subcategory_list).setVisibility(View.GONE);
        (findViewById(R.id.sub_categories_header)).setVisibility(View.GONE);
        findViewById(R.id.apply_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNo = 1;
                isO2o = ((CheckBox) findViewById(R.id.zi_experience_tag)).isChecked();
                priceLte = Long.parseLong(filterFromTextView.getText().toString());
                priceHigh = Long.parseLong(filterToTextView.getText().toString());
                nextUrl = null;
                isRefreshData = true;
                animateViewRightOut(findViewById(R.id.filter_layout));
                loadData();
            }
        });
    }

    public void addMaxInputFilter() {
        //((EditText)findViewById(R.id.to_price)).setFilters(new InputFilter[]{new InputFilterMinMax( Long.parseLong((((EditText) findViewById(R.id.from_price)).getText()).toString()) + "",(TO_VALUE) + "")});
    }

    public void addMinInputFilter() {
        filterFromTextView.setFilters(new InputFilter[]{new InputFilterMinMax((FROM_VALUE) + "", (filterToTextView.getText()).toString().length() > 0 ? Long.parseLong((filterToTextView.getText()).toString()) + "" : TO_VALUE + "")});
    }

    public void resetFilterValues() {
        sortId = -1;
        isFilterApplied = false;
        ((CustomCheckBox) findViewById(R.id.zi_experience_tag)).setChecked(false);
        ((CustomRadioButton) findViewById(R.id.high_to_low)).setChecked(false);
        ((CustomRadioButton) findViewById(R.id.low_to_high)).setChecked(false);
        RangeSeekBar seekBar = (RangeSeekBar<Integer>) findViewById(R.id.range_seekbar);
        seekBar.setNotifyWhileDragging(true);
        seekBar.setSelectedMaxValue(TO_VALUE);
        seekBar.setSelectedMinValue(FROM_VALUE);

        filterFromTextView.setText(FROM_VALUE + "");
        filterToTextView.setText(TO_VALUE + "");

        for (RadioButton radioButton : priceRadioButtonsArrayList) {
            radioButton.setChecked(false);
        }
    }

}