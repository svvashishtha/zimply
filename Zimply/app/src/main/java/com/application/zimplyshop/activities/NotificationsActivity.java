package com.application.zimplyshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.NotificationsAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.NotificationsBaseObj;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.objects.NotificationListObj;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.JSONUtils;
import com.application.zimplyshop.utils.ZWebView;
import com.application.zimplyshop.widgets.CustomTextView;
import com.application.zimplyshop.widgets.SpaceItemDecoration;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Umesh Lohani on 11/19/2015.
 */
public class NotificationsActivity extends BaseActivity implements GetRequestListener,AppConstants,View.OnClickListener,RequestTags{

    RecyclerView categoriesList;

    boolean isDestroyed;

    int visibleItemCount,pastVisiblesItems,totalItemCount;

    boolean isLoading;

    boolean isRefreshData,isRequestAllowed;

    String nextUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_toolbar_filter_layout);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        PAGE_TYPE = AppConstants.PAGE_TYPE_NETWORK_NO_WIFI;
        PAGE_TYPE =AppConstants.PAGE_TYPE_NOTIFICATION;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        categoriesList = (RecyclerView)findViewById(R.id.categories_list);
        categoriesList.setLayoutManager(new LinearLayoutManager(this));
        categoriesList.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));

        setLoadingVariables();
        retryLayout.setOnClickListener(this);
        GetRequestManager.getInstance().addCallbacks(this);
        ((CustomTextView)findViewById(R.id.cart_item_true)).setVisibility(View.GONE);
        AppPreferences.setNotifModDateTime(this,System.currentTimeMillis());
        loadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, null);
        TextView titleText = (TextView) view.findViewById(R.id.title_textview);
        titleText.setText(getString(R.string.z_notifications_text));
        toolbar.addView(view);
    }

    public void loadData(){
        String finalUrl;
        if (nextUrl == null) {
            finalUrl = AppApplication.getInstance().getBaseUrl() + NOTIFICATIONS_LIST ;
        } else {
            finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
        }
        GetRequestManager.getInstance().makeAyncRequest(finalUrl, NOTIFICATION_LIST_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_NOTIFICATION_LIST_OBJ);
    }

    public void setAdapterData(ArrayList<NotificationListObj> objs){
        int height = (3 * getDisplayMetrics().heightPixels) / 10;
        if(categoriesList.getAdapter() == null) {
            NotificationsAdapter adapter = new NotificationsAdapter(this, height, (int) (getDisplayMetrics().widthPixels - (2 * getResources().getDimension(R.dimen.margin_medium))));
            categoriesList.setAdapter(adapter);
            categoriesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    visibleItemCount = categoriesList.getLayoutManager().getChildCount();
                    totalItemCount = categoriesList.getLayoutManager().getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) categoriesList.getLayoutManager())
                            .findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && !isLoading && isRequestAllowed) {
                        loadData();
                    }
                    // scrollToolbarAndHeaderBy(-dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    new ImageLoaderManager(NotificationsActivity.this).setScrollState(newState);
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
            ((NotificationsAdapter) categoriesList.getAdapter()).setOnItemClickListener(new NotificationsAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(int pos) {
                    NotificationListObj obj = (NotificationListObj)((NotificationsAdapter)categoriesList.getAdapter()).getItem(pos);
                    manageNotifClick(obj);
                }

            });
        }
        ((NotificationsAdapter) categoriesList.getAdapter()).addData(objs);

    }

    public void manageNotifClick(NotificationListObj obj){
        switch(obj.getType()){
            case AppConstants.NOTIFICATION_TYPE_PRODUCT_DETAIL:
                JSONObject jsonObj = JSONUtils.getJSONObject(obj.getSlug());
                Intent intent = new Intent(this , NewProductDetailActivity.class);
                intent.putExtra("slug", JSONUtils.getStringfromJSON(jsonObj,"slug"));
                intent.putExtra("id",Integer.parseInt(JSONUtils.getStringfromJSON(jsonObj, "id")));
                startActivity(intent);
                break;
            case NOTIFICATION_TYPE_PRODUCT_LIST:
                Intent listIntent = new Intent(this, ProductListingActivity.class);
                listIntent .putExtra("category_id", "0");
                listIntent.putExtra("hide_filter", false);
                listIntent .putExtra("category_name", obj.getTitle());
                listIntent .putExtra("url", AppConstants.GET_PRODUCT_LIST);
                listIntent.putExtra("discount_id",Integer.parseInt(obj.getSlug()));
                startActivity(listIntent);
                break;
            case NOTIFICATION_TYPE_WEBVIEW:
                Intent  notificationIntent = new Intent(this, ZWebView.class);
                notificationIntent.putExtra("title",obj.getTitle() );
                notificationIntent.putExtra("url", obj.getSlug());
                startActivity(notificationIntent);
                break;
            case NOTIFICATION_TYPE_HOME_PAGE:
                Intent  homeIntent = new Intent(this, HomeActivity.class);
                startActivity(homeIntent);
                break;

        }
    }


    @Override
    public void onRequestStarted(String requestTag) {
        if (!isDestroyed
                && requestTag.equalsIgnoreCase(NOTIFICATION_LIST_REQUEST_TAG)) {
            if (categoriesList.getAdapter() == null
                    || categoriesList.getAdapter().getItemCount() == 0 || isRefreshData) {
                showLoadingView();
                changeViewVisiblity(categoriesList, View.GONE);
                if (categoriesList.getAdapter() != null)
                    ((NotificationsAdapter) categoriesList.getAdapter()).removePreviousData();
            } else {

            }
            isLoading = true;
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(NOTIFICATION_LIST_REQUEST_TAG)) {
            if (((NotificationsBaseObj) obj).getNotifications().size() == 0) {
                if (categoriesList.getAdapter() == null || categoriesList.getAdapter().getItemCount() == 1) {
                    showNullCaseView("No Notifications");
                    changeViewVisiblity(categoriesList, View.GONE);
                } else {
                    ((NotificationsAdapter) categoriesList.getAdapter()).removeItem();
                    showToast("No more Notifications");

                }
                isRequestAllowed = false;
            } else {
                setAdapterData(((NotificationsBaseObj) obj).getNotifications());
                nextUrl = ((NotificationsBaseObj) obj).getNext_url();
                showView();
                changeViewVisiblity(categoriesList, View.VISIBLE);
                if (((NotificationsBaseObj) obj).getNotifications().size() < 10) {
                    isRequestAllowed = false;
                    ((NotificationsAdapter) categoriesList.getAdapter()).removeItem();
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
        if (!isDestroyed && requestTag.equalsIgnoreCase(NOTIFICATION_LIST_REQUEST_TAG)) {
            if (categoriesList.getAdapter() == null || categoriesList.getAdapter().getItemCount() == 0 || isRefreshData) {
                showNetworkErrorView();
                changeViewVisiblity(categoriesList, View.GONE);
                isRefreshData = false;
            } else {
                if (((ErrorObject) obj).getErrorCode() == 500) {
                    showToast("Could not load more data");

                } else {
                    showToast(((ErrorObject) obj).getErrorMessage());

                }
                ((NotificationsAdapter) categoriesList.getAdapter()).removeItem();
                isRequestAllowed = false;
            }
            isLoading = false;
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

    @Override
    protected void onDestroy() {
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }
}
