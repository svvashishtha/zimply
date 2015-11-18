package com.application.zimplyshop.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.OrderListAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.OrderItemObj;
import com.application.zimplyshop.baseobjects.OrderList;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.widgets.SpaceItemDecoration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Umesh Lohani on 10/9/2015.
 */
public class PurchaseListActivity extends BaseActivity implements GetRequestListener,View.OnClickListener,UploadManagerCallback {

    RecyclerView orderList;
    private boolean fromHome = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_toolbar_layout);

        findViewById(R.id.parent).setBackgroundColor(getResources().getColor(R.color.pager_bg));

        if( getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("fromHome"))
            fromHome = getIntent().getBooleanExtra("fromHome", false);

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
        loadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addToolbarView(Toolbar toolbar){
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, toolbar,false);
        if(fromHome)
            ((TextView) view.findViewById(R.id.title_textview)).setText("My Orders");
        else
            ((TextView) view.findViewById(R.id.title_textview)).setText("Order History");
        toolbar.addView(view);
    }
    public void loadData(){
        String url = AppApplication.getInstance().getBaseUrl()+ AppConstants.ORDER_LIST_URL+"?userid="+ AppPreferences.getUserID(this);
        GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.ORDERLISTREQUESTTAG, ObjectTypes.OBJECT_TYPE_ORDER_LIST);
    }


    int clickedOrder,orderItemId,status;

    OrderListAdapter adapter;

    public void setAdapterData(ArrayList<OrderItemObj> objs){
        adapter = new OrderListAdapter(this);
        orderList.setAdapter(adapter);
        adapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
            @Override
            public void onCancelClick(final int position, final int orderId) {
                final AlertDialog logoutDialog;
                logoutDialog = new AlertDialog.Builder(PurchaseListActivity.this)
                        .setTitle(getResources().getString(R.string.cancel_order))
                        .setMessage(getResources().getString(R.string.cancel_order_cofirm))
                        .setPositiveButton(getResources().getString(R.string.okay_text),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        clickedOrder = position;
                                        orderItemId = orderId;
                                        status = AppConstants.CANCEL_ORDER;
                                        sendServerRequest(AppConstants.CANCEL_ORDER, orderId);
                                        ;
                                    }
                                }).setNegativeButton(getResources().getString(R.string.dialog_cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();
                logoutDialog.show();

            }

            @Override
            public void onReturnClick(final int position, final int orderId) {
                final AlertDialog logoutDialog;
                logoutDialog = new AlertDialog.Builder(PurchaseListActivity.this)
                        .setTitle(getResources().getString(R.string.return_order))
                        .setMessage(getResources().getString(R.string.return_order_cofirm))
                        .setPositiveButton(getResources().getString(R.string.okay_text),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        clickedOrder = position;
                                        orderItemId = orderId;
                                        status = AppConstants.CANCEL_ORDER;
                                        sendServerRequest(AppConstants.CANCEL_ORDER, orderId);
                                        ;
                                    }
                                }).setNegativeButton(getResources().getString(R.string.dialog_cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();
                logoutDialog.show();

            }

        });
        adapter.addData(objs);
    }
    public void sendServerRequest(int status , int orderid){
        String url = AppApplication.getInstance().getBaseUrl()+"ecommerce/cancel-order/";
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("status", status+""));
        nameValuePairs.add(new BasicNameValuePair("orderitem_id", orderid+""));
        UploadManager.getInstance().makeAyncRequest(url, CommonLib.CANCEL_ORDER, "",
                ObjectTypes.OBJECT_TYPE_CANCEL_ORDER, null, nameValuePairs, null);
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if(!isDestroyed && requestTag.equalsIgnoreCase(RequestTags.ORDERLISTREQUESTTAG)){
            showLoadingView();
            changeViewVisiblity(orderList , View.GONE);
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if(!isDestroyed && requestTag.equalsIgnoreCase(RequestTags.ORDERLISTREQUESTTAG)){
            if(((OrderList)obj).getOrders().size() == 0){
                showNullCaseView("Looks like you haven't ordered anything yet.");
                changeViewVisiblity(orderList, View.GONE);
            }else{
                setAdapterData(((OrderList)obj).getOrders());
                showView();
                changeViewVisiblity(orderList, View.VISIBLE);
            }

        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if(!isDestroyed && requestTag.equalsIgnoreCase(RequestTags.ORDERLISTREQUESTTAG)) {
            showNetworkErrorView();
        }
    }

    boolean isDestroyed;
    @Override
    protected void onDestroy() {
        isDestroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        UploadManager.getInstance().removeCallback(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_layout:
                if(isRequestFailed)
                    loadData();
                break;
        }
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if(!isDestroyed && requestType == CommonLib.CANCEL_ORDER){
            if(dialog !=null)
                dialog.dismiss();
            if(status){
                adapter.changeStatus(clickedOrder,orderItemId,this.status);
                Toast.makeText(this,"Status successfully updated",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Could not process request.Please try again",Toast.LENGTH_SHORT).show();
            }
        }
    }

    ProgressDialog dialog;
    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {
        dialog = ProgressDialog.show(this,null,"Loading..Please wait");
    }
}
