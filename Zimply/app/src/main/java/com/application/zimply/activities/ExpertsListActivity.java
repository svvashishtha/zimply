package com.application.zimply.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
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

import com.application.zimply.R;
import com.application.zimply.adapters.ExpertsListRecyclerAdapter;
import com.application.zimply.adapters.ExpertsListRecyclerAdapter.OnItemClickListener;
import com.application.zimply.application.AppApplication;
import com.application.zimply.baseobjects.CategoryObject;
import com.application.zimply.baseobjects.ErrorObject;
import com.application.zimply.baseobjects.ExpertListObject;
import com.application.zimply.baseobjects.HomeExpertObj;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.extras.ObjectTypes;
import com.application.zimply.managers.GetRequestListener;
import com.application.zimply.managers.GetRequestManager;
import com.application.zimply.managers.ImageLoaderManager;
import com.application.zimply.objects.AllCategories;
import com.application.zimply.objects.AllCities;
import com.application.zimply.serverapis.RequestTags;
import com.application.zimply.utils.CommonLib;
import com.application.zimply.widgets.SpaceItemDecoration;

import java.util.ArrayList;

public class ExpertsListActivity extends BaseActivity
        implements OnClickListener, GetRequestListener, RequestTags, AppConstants {

    RecyclerView expertsList;

    String nextUrl;

    boolean isLoading;

    boolean isRequestAllowed;

    int pastVisiblesItems, visibleItemCount, totalItemCount;

    boolean isDestroyed;

    int categoryId = -1, cityId = -1, parentCategoryId = -1;

    boolean isRefreshData;

    boolean isProductFilterLoaded = true;
    boolean isArticleFilterLoaded = false;

    boolean showLoading;

    String cityName;
    ArrayList<CategoryObject> cities = new ArrayList<CategoryObject>();
    TextView titleText;

    String categoryName;
    View tickOnFilter;
    int expandGroup = -1;
    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_expert_list_layout);
        tickOnFilter = findViewById(R.id.filter_applied);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (AllCategories.getInstance() != null
                && AllCategories.getInstance().getPhotoCateogryObjs() != null) {

            if (AllCategories.getInstance().getCities() != null)
                cities.addAll(AllCategories.getInstance().getCities());
            else {
                String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_CITY_LIST;
                GetRequestManager.getInstance().makeAyncRequest(url, CommonLib.GET_CITY_LIST + "",
                        ObjectTypes.OBJECT_TYPE_CITY_LIST);
            }
        }
        expertsList = (RecyclerView) findViewById(R.id.experts_list);
        expertsList.setLayoutManager(new LinearLayoutManager(this));
        expertsList.addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.margin_small)));
        if (getIntent() != null) {
            if (getIntent().getStringExtra("parent_category_id") != null && !getIntent().getStringExtra("parent_category_id").equalsIgnoreCase("null")) {
                parentCategoryId = Integer.parseInt(getIntent().getStringExtra("parent_category_id"));
            } else if (getIntent().getStringExtra("category_id") != null && !getIntent().getStringExtra("category_id").equalsIgnoreCase("null")) {
                categoryId = Integer.parseInt(getIntent().getStringExtra("category_id")) + 1;
            }
            categoryName = getIntent().getStringExtra("category_name");
            if (getIntent().getStringExtra("city_id") != null && !getIntent().getStringExtra("city_id").equalsIgnoreCase("null")) {
                cityId = getCityPos(getIntent().getStringExtra("city_id")) + 1;
                cityName = (cityId == 0) ? "All" : AllCities.getInsance().getCities().get(cityId - 1).getName();
            }

			/*  setCategoryFilterText(getIntent().getStringExtra("category_name"));
            if(cityId == -1){
                setLocationFilterText("All");
            }else{
                setLocationFilterText(getIntent().getStringExtra("city"));
            }
*/
        }

        width = getWindowManager().getDefaultDisplay().getWidth();

        setStatusBarColor();
        setLoadingVariables();
        retryLayout.setOnClickListener(this);
        //	setFilterVariables();
        //	setFiltersClick();
        //setCategoryFilterText("All");
//		loadData();
        GetRequestManager.getInstance().addCallbacks(this);
        if (AllCategories.getInstance().getPhotoCateogryObjs() == null || AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category() == null || (AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category() != null && AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category().size() == 0)) {
            showLoading = false;
            loadArticlePhotoCategories();
        } else {
            showLoading = true;
            loadData();
        }
        //loadArticlePhotoCategories();

    }

    public int getCityPos(String id) {
        for (int i = 0; i < AllCities.getInsance().getCities().size(); i++) {
            if (AllCities.getInsance().getCities().get(i).getId().equalsIgnoreCase(id))
                return i;
        }
        return 0;
    }

    public int getCateogryPos(String id) {
        for (int i = 0; i < AllCategories.getInstance().getPhotoCateogryObjs().getExpert_category().size(); i++) {
            if (AllCategories.getInstance().getPhotoCateogryObjs().getExpert_category().get(i).getId().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return 0;
    }

    private void setFiltersClick() {
        filterLayout.setVisibility(View.VISIBLE);
        categoriesLayout.setOnClickListener(this);
        sortByLayout.setVisibility(View.GONE);
        selectfiltersLayout.setVisibility(View.GONE);
//        findViewById(R.id.location_filter).setVisibility(View.VISIBLE);
//        findViewById(R.id.location_filter).setOnClickListener(this);
//		locationFiltersLayout.setVisibility(View.GONE);
        findViewById(R.id.separator2).setVisibility(View.GONE);
        findViewById(R.id.separator1).setVisibility(View.GONE);
    }

    public void loadData() {
        String finalUrl;
        if (nextUrl == null) {
            finalUrl = AppApplication.getInstance().getBaseUrl() + EXPERT_LIST_URL +
                    ((categoryId == 0 && cityId == 0 && parentCategoryId == -1) ? "?filter=0" : "?filter=1" +
                            ((parentCategoryId != -1 && categoryId < 1) ? "&parent_category_id=" + parentCategoryId : "") +
                            ((categoryId > 0) ? "&categories__id__in=" + categoryId : "") +
                            "&width=" + width / 2 +
                            ((cityId != 0) ? "&city=" + AllCities.getInsance().getCities().get(cityId - 1).getId() : ""));
        } else {
            finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
        }
        GetRequestManager.getInstance().makeAyncRequest(finalUrl, EXPERT_LIST_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_EXPERT_LIST_OBJECT);
    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, toolbar, false);
        titleText = (TextView) view.findViewById(R.id.title_textview);
        titleText.setTextSize(18);
        titleText.setText("Finding Experts");
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
                showFilterFragment();
                break;
            case R.id.cart:
                Intent intent = new Intent(this, ProductCheckoutActivity.class);
                intent.putExtra("OrderSummaryFragment", false);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showFilterFragment() {
        if (AllCategories.getInstance().getPhotoCateogryObjs() == null) {
            if (!CommonLib.isNetworkAvailable(this))
                showToast("Please connect to internet");
            else
                showToast("Something went wrong. Please try again");

        } else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("is_location", true);
            bundle.putInt("selected_loc", cityId);
            if (expandGroup != -1)
                bundle.putInt("expand_group", expandGroup);
            bundle.putBoolean("is_expert", true);
            bundle.putInt("category_id", categoryId);
            if (parentCategoryId != -1) {
                bundle.putInt("parent_category_id", parentCategoryId);
            }
            AllFilterClassActivity dialogFragment = AllFilterClassActivity.newInstance(bundle);
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.HJCustomDialogTheme);
            dialogFragment.setOnApplyClickListener(new AllFilterClassActivity.OnApplyClickListener() {
                @Override
                public void onApplyClick(Bundle bundle) {
                    categoryId = bundle.getInt("selected_pos");
                    cityId = bundle.getInt("city_id");
                    parentCategoryId = bundle.getInt("parent_category_id");
                    expandGroup = bundle.getInt("expand_group");
                    if (bundle.getString("selected_category") != null)
                        categoryName = bundle.getString("selected_category");
                    cityName = cityId == 0 ? "All" : AllCities.getInsance().getCities().get(cityId - 1).getName();
                    nextUrl = EXPERT_LIST_URL + ((categoryId == 0 && cityId == 0 && parentCategoryId == -1) ? "?filter=0" : "?filter=1" +
                            ((parentCategoryId != -1 && categoryId < 1) ? "&parent_category_id=" + parentCategoryId : "") + (categoryId > 0 ? "&categories__id__in=" + categoryId : "") + ((cityId != 0) ? "&city=" + AllCities.getInsance().getCities().get(cityId - 1).getId() :
                            ""));
                    isRefreshData = true;
                    tickOnFilter.setVisibility(View.VISIBLE);
                    loadData();
                }

            });
            dialogFragment.show(getSupportFragmentManager(), ARTICLE_PHOTO_CATEGROY_DIALOG_TAG);


        }
    }

    private void setAdapterData(ArrayList<HomeExpertObj> objs) {
        if (expertsList.getAdapter() == null) {
            int width = getDisplayMetrics().widthPixels - getResources().getDimensionPixelSize(R.dimen.margin_small);
            int height = getDisplayMetrics().widthPixels / 3;

            ExpertsListRecyclerAdapter adapter = new ExpertsListRecyclerAdapter(this, width, height);
            expertsList.setAdapter(adapter);
            expertsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    visibleItemCount = expertsList.getLayoutManager().getChildCount();
                    totalItemCount = expertsList.getLayoutManager().getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) expertsList.getLayoutManager())
                            .findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && !isLoading && isRequestAllowed) {
                        loadData();
                    }
                    //scrollToolbarAndHeaderBy(-dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    new ImageLoaderManager(ExpertsListActivity.this).setScrollState(newState);
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });

            ((ExpertsListRecyclerAdapter) expertsList.getAdapter()).setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(View view, int pos) {
                    startExpertActivity(view, pos);
                }

            });
        }

        ((ExpertsListRecyclerAdapter) expertsList.getAdapter()).addData(objs);

    }

    private void startExpertActivity(View view, int pos) {
        Intent intent = new Intent(this, ExpertProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("expert_obj", ((ExpertsListRecyclerAdapter) expertsList.getAdapter()).getItem(pos));
        intent.putExtras(bundle);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "profile");
        if (Build.VERSION.SDK_INT > 21) {
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.location_filter:
//
//                break;
            case R.id.retry_layout:
                if (isRequestFailed) {
                    if (AllCategories.getInstance().getPhotoCateogryObjs() == null || AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category() == null || (AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category() != null && AllCategories.getInstance().getPhotoCateogryObjs().getArticle_category().size() == 0)) {
                        loadArticlePhotoCategories();
                    } else {
                        loadData();
                    }
                }
                break;
            case R.id.cat_filter_layout:

        }
    }

    private void setCategoryFilterText(String text) {
        ((TextView) findViewById(R.id.category_name)).setText(text);
        if (categoryId == -1) {
            ((TextView) findViewById(R.id.category_name))
                    .setTextColor(getResources().getColor(R.color.text_color1));
        } else {
            ((TextView) findViewById(R.id.category_name))
                    .setTextColor(getResources().getColor(R.color.green_text_color));
        }
    }

    private void setLocationFilterText(String city) {
//        ((TextView) findViewById(R.id.location)).setText(city);
//        if (cityId == -1) {
//            ((TextView) findViewById(R.id.location))
//                    .setTextColor(getResources().getColor(R.color.text_color1));
//        } else {
//            ((TextView) findViewById(R.id.location))
//                    .setTextColor(getResources().getColor(R.color.green_text_color));
//        }
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(EXPERT_LIST_REQUEST_TAG)) {
            if (expertsList.getAdapter() == null || expertsList.getAdapter().getItemCount() == 0 || isRefreshData) {
                if (showLoading) {
                    showLoadingView();
                    changeViewVisiblity(expertsList, View.GONE);
                }
                if (isRefreshData) {
                    if (expertsList.getAdapter() != null)
                        ((ExpertsListRecyclerAdapter) expertsList.getAdapter()).removePreviousData();
                }
            } else {

            }
            isLoading = true;
        } else if (!isDestroyed && (requestTag.equalsIgnoreCase(COMPLETE_CATEGORY_LIST_REQUEST_TAG)
                || requestTag.equalsIgnoreCase(ARTICLE_PHOTO_CAT_REQUEST_TAG))) {
            showLoadingView();
            changeViewVisiblity(expertsList, View.GONE);
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(EXPERT_LIST_REQUEST_TAG)) {
            if (((ExpertListObject) obj).getSelected() != null/* &&((ExpertListObject) obj).getSelected().getSubcategories()!=null && ((ExpertListObject) obj).getSelected().getSubcategories().size()>0*/)
                //    categoryId = getCateogryPos(((ExpertListObject) obj).getSelected().getSubcategories().get(0).getId())+1;
                if (((ExpertListObject) obj).getOutput().size() == 0) {
                    if (expertsList.getAdapter() == null || expertsList.getAdapter().getItemCount() == 1) {
                        showNullCaseView("No Experts");

                    } else {
                        showToast("No more Experts");

                    }
                    isRequestAllowed = false;
                } else {
                    setAdapterData(((ExpertListObject) obj).getOutput());
                    nextUrl = ((ExpertListObject) obj).getNext_url();
                    showView();
                    changeViewVisiblity(expertsList, View.VISIBLE);
                    if (((ExpertListObject) obj).getOutput().size() < 10) {
                        isRequestAllowed = false;
                        if (expertsList.getAdapter() != null) {
                            ((ExpertsListRecyclerAdapter) expertsList.getAdapter()).removeItem();
                        }
                    } else {
                        isRequestAllowed = true;
                    }
                }
            if (isRefreshData) {
                isRefreshData = false;
            }
            isLoading = false;

            changeToolbarTitle(((ExpertListObject) obj).getCount(), cityName);
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(COMPLETE_CATEGORY_LIST_REQUEST_TAG)) {
            isProductFilterLoaded = true;
            if (isProductFilterLoaded && isArticleFilterLoaded)
                loadData();
        } else if (requestTag.equalsIgnoreCase(ARTICLE_PHOTO_CAT_REQUEST_TAG)) {
            isArticleFilterLoaded = true;
            if (isProductFilterLoaded && isArticleFilterLoaded)
                loadData();
        } else if (requestTag.equals(CommonLib.GET_CITY_LIST + "")) {
            if (!isDestroyed && obj != null && obj instanceof ArrayList<?>) {
                cities.clear();
                cities.addAll((ArrayList<CategoryObject>) obj);
            }
        }
    }

    public void changeToolbarTitle(int count, String cityName) {
        titleText.setText(count + " " + categoryName + "s in " + cityName);
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(EXPERT_LIST_REQUEST_TAG)) {
            if (expertsList.getAdapter() == null || expertsList.getAdapter().getItemCount() == 0) {
                showNetworkErrorView();
                changeViewVisiblity(expertsList, View.GONE);
                isRefreshData = false;
            } else {
                if (((ErrorObject) obj).getErrorCode() == 500) {
                    showToast("Could not load more data");

                    ((ExpertsListRecyclerAdapter) expertsList.getAdapter()).removeItem();
                } else {
                    showToast(((ErrorObject) obj).getErrorMessage());

                    ((ExpertsListRecyclerAdapter) expertsList.getAdapter()).removeItem();
                }
                isRequestAllowed = false;
            }
            isLoading = false;
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(COMPLETE_CATEGORY_LIST_REQUEST_TAG)) {
            isProductFilterLoaded = false;
            //loadProductCategories();
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(ARTICLE_PHOTO_CAT_REQUEST_TAG)) {
            isArticleFilterLoaded = false;
            showNetworkErrorView();
            changeViewVisiblity(expertsList, View.GONE);
        }

    }

    @Override
    protected void onResume() {
        isDestroyed = false;
        findViewById(R.id.cart_item_true).setVisibility(View.GONE);
        /*if (AllProducts.getInstance().getCartCount() > 0) {
            ((TextView) findViewById(R.id.cart_item_true)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.cart_item_true)).setText(AllProducts.getInstance().getCartCount() + "");
        } else {
            ((TextView) findViewById(R.id.cart_item_true)).setVisibility(View.GONE);
        }*/
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }

    private void loadArticlePhotoCategories() {

        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.ARTICLE_PHOTO_CATEGORY_REQUEST_URL;

        GetRequestManager.getInstance().makeAyncRequest(url, ARTICLE_PHOTO_CAT_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_ARTICLE_PHOTO_CATEGORY_LIST);

    }

    private void loadProductCategories() {
        String productCatUrl = AppApplication.getInstance().getBaseUrl() + AppConstants.COMPLETE_CATEGORY_LISTING_URL;

        GetRequestManager.getInstance().makeAyncRequest(productCatUrl, COMPLETE_CATEGORY_LIST_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_COMPLETE_CATEGORY_LIST);

    }
}
