package com.application.zimply.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimply.adapters.BookedHistoryAdapter;
import com.application.zimply.application.AppApplication;
import com.application.zimply.baseobjects.BookedProductHistoryObject;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.extras.ObjectTypes;
import com.application.zimply.managers.GetRequestListener;
import com.application.zimply.managers.GetRequestManager;
import com.application.zimply.preferences.AppPreferences;
import com.application.zimply.serverapis.RequestTags;
import com.application.zimply.utils.UploadManager;
import com.application.zimply.utils.UploadManagerCallback;
import com.application.zimply.widgets.SpaceItemDecoration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Umesh Lohani on 11/14/2015.
 */
public class BookedForReviewActivity extends BaseActivity implements GetRequestListener,UploadManagerCallback,View.OnClickListener,AppConstants {

    RecyclerView orderList;


    boolean isDestroyed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_toolbar_layout);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        orderList = (RecyclerView)findViewById(R.id.categories_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));
        orderList.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));
        GetRequestManager.getInstance().addCallbacks(this);
        UploadManager.getInstance().addCallback(this);
        setLoadingVariables();
        retryLayout.setOnClickListener(this);

        AppPreferences.setIsStartRating(this,true);

        loadData();
    }

    public void addToolbarView(Toolbar toolbar){
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, toolbar,false);
        ((TextView) view.findViewById(R.id.title_textview)).setText("Booked Products");
        toolbar.addView(view);
    }
    public void loadData(){
        String url = AppApplication.getInstance().getBaseUrl()+ AppConstants.BOOKED_PRODUCTS_URL+"?userid="+ AppPreferences.getUserID(this);
        GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.BOOKED_HISTORY_REQUEST_TAG, ObjectTypes.OBJECT_TYPE_ALL_BOOKED_PRODUCTS);
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if(!isDestroyed && requestType == RequestTags.CANCEL_PRODUCT_REVIEW_TAG){
            if(progressDialog!=null){
                progressDialog.dismiss();
            }

            if(status) {
                final AlertDialog logoutDialog;
                logoutDialog = new AlertDialog.Builder(BookedForReviewActivity.this)
                        .setTitle("Success")
                        .setCancelable(false)
                        .setMessage("Booking cancelled successfully")
                        .setPositiveButton("Okay",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        adapter.removePos(clickedPos);
                                    }
                                }).create();
                logoutDialog.show();
            }
        }
    }

    ProgressDialog  progressDialog;
    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {
        if(!isDestroyed && requestType == RequestTags.CANCEL_PRODUCT_REVIEW_TAG){
            progressDialog = ProgressDialog.show(this, null, "Loading.Please Wait..");
        }
    }


    @Override
    public void onRequestStarted(String requestTag) {
        if(!isDestroyed && requestTag.equalsIgnoreCase(RequestTags.BOOKED_HISTORY_REQUEST_TAG)){
            showLoadingView();
            changeViewVisiblity(orderList , View.GONE);
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if(!isDestroyed && requestTag.equalsIgnoreCase(RequestTags.BOOKED_HISTORY_REQUEST_TAG)){
            if(((ArrayList<BookedProductHistoryObject>)obj).size() == 0){
                showNullCaseView("Looks like you haven't ordered anything yet.");
                changeViewVisiblity(orderList, View.GONE);
            }else{
                setAdapterData(((ArrayList<BookedProductHistoryObject>) obj));
                showView();
                changeViewVisiblity(orderList, View.VISIBLE);
            }

        }
    }

    BookedHistoryAdapter adapter;

    int clickedPos;

    public void setAdapterData(ArrayList<BookedProductHistoryObject> obj){
        adapter = new BookedHistoryAdapter(this,obj);
        orderList.setAdapter(adapter);
        adapter.setOnItemClickListener(new BookedHistoryAdapter.OnItemClickListener() {
            @Override
            public void onCancelClick(int pos, int bookProductId) {
                clickedPos = pos;
                removeBookedItem(bookProductId);
            }
        });

    }

    public void removeBookedItem(int productId){
        String url = AppApplication.getInstance().getBaseUrl() + REMOVE_PRODUCT_REVIEW_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        list.add(new BasicNameValuePair("book_product_id", productId+""));
        UploadManager.getInstance().makeAyncRequest(url, RequestTags.CANCEL_PRODUCT_REVIEW_TAG, productId + "",
                ObjectTypes.OBJECT_TYPE_REMOVE_PRODUCT_REVIEW, productId, list, null);
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if(!isDestroyed && requestTag.equalsIgnoreCase(RequestTags.BOOKED_HISTORY_REQUEST_TAG)) {
            showNetworkErrorView();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.retry_layout:
                if(isRequestFailed){
                    loadData();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        isDestroyed=true;
        UploadManager.getInstance().removeCallback(this);
        super.onDestroy();

    }
}
