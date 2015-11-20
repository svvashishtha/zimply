package com.application.zimplyshop.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.NotificationsAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ArticleListObject;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.NotificationsBaseObj;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.objects.NotificationListObj;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.widgets.SpaceItemDecoration;

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
        categoriesList = (RecyclerView)findViewById(R.id.categories_list);
        categoriesList.setLayoutManager(new LinearLayoutManager(this));
        categoriesList.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));

        setLoadingVariables();
        retryLayout.setOnClickListener(this);
        GetRequestManager.getInstance().addCallbacks(this);
        loadData();
    }

    public void loadData(){
        String url = AppApplication.getInstance().getBaseUrl() +NOTIFICATIONS_LIST;
        GetRequestManager.getInstance().makeAyncRequest(url, NOTIFICATION_LIST_REQUEST_TAG, ObjectTypes.OBJECT_TYPE_NOTIFICATION_LIST_OBJ);
    }

    public void setAdapterData(ArrayList<NotificationListObj> objs){
        int height = (4 * getDisplayMetrics().heightPixels) / 10;
        NotificationsAdapter adapter = new NotificationsAdapter(this, height);
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
                scrollToolbarAndHeaderBy(-dy);
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

            }

        });
        ((NotificationsAdapter) categoriesList.getAdapter()).addData(objs);

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
            if (((ArticleListObject) obj).getOutput().size() == 0) {
                if (categoriesList.getAdapter() == null || categoriesList.getAdapter().getItemCount() == 1) {
                    showNullCaseView("No Articles");
                    changeViewVisiblity(categoriesList, View.GONE);
                } else {
                    ((NotificationsAdapter) categoriesList.getAdapter()).removeItem();
                    showToast("No more Articles");

                }
                isRequestAllowed = false;
            } else {
                setAdapterData(((NotificationsBaseObj) obj).getNotifications());
                nextUrl = ((ArticleListObject) obj).getNext_url();
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


}
