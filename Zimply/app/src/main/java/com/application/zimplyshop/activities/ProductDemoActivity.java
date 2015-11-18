package com.application.zimplyshop.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ProductVendorTimeObj;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.TimeUtils;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.widgets.CustomTextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Umesh Lohani on 11/14/2015.
 */
public class ProductDemoActivity extends BaseActivity implements AppConstants,UploadManagerCallback {




    ProductVendorTimeObj obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_demo_page);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        obj = (ProductVendorTimeObj)getIntent().getSerializableExtra("product_vendor_time");
        CustomTextView storeAddress = (CustomTextView)findViewById(R.id.store_address);


        storeAddress.setText(obj.getVendor()+"\n"+obj.getLine1()+"\n"+obj.getLine2()+"\n"+obj.getCity()+"\nPincode"+obj.getPincode());

        CustomTextView storeTime = (CustomTextView)findViewById(R.id.store_time);
        storeTime.setText(Html.fromHtml("Your request for the demo of the product has been registered. Kindly visit the store before "+"<b>"+ TimeUtils.getTimeStampDate(obj.getCreated_on(), TimeUtils.DATE_TYPE_DAY_MON_DD_YYYY)+"</b>"));
        ((CustomTextView) findViewById(R.id.cancel_booking)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBooking();
            }
        });

        AppPreferences.setIsStartRating(this, true);
        UploadManager.getInstance().addCallback(this);
    }

    public void removeBooking(){
        String url = AppApplication.getInstance().getBaseUrl() + REMOVE_PRODUCT_REVIEW_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        list.add(new BasicNameValuePair("book_product_id", obj.getBook_product_id()+""));
        UploadManager.getInstance().makeAyncRequest(url, RequestTags.CANCEL_PRODUCT_REVIEW_TAG, obj.getBook_product_id() + "",
                ObjectTypes.OBJECT_TYPE_REMOVE_PRODUCT_REVIEW, obj, list, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(
                R.layout.common_toolbar_text_layout, null);
        TextView titleText = (TextView) view.findViewById(R.id.title_textview);
        titleText.setText("Product Demo");
        toolbar.addView(view);
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if(!isDestroyed && requestType == RequestTags.CANCEL_PRODUCT_REVIEW_TAG){
            if(progressDialog!=null){
                progressDialog.dismiss();
            }

            if(status) {
                final AlertDialog logoutDialog;
                logoutDialog = new AlertDialog.Builder(ProductDemoActivity.this)
                        .setTitle("Success")
                        .setCancelable(false)
                        .setMessage("Booking cancelled successfully")
                        .setPositiveButton("Okay",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        ProductDemoActivity.this.finish();
                                    }
                                }).create();
                logoutDialog.show();
            }
        }
    }

    ProgressDialog progressDialog;
    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {
        if(!isDestroyed && requestType == RequestTags.CANCEL_PRODUCT_REVIEW_TAG){
            progressDialog =ProgressDialog.show(this,null,"Loading.Please Wait..");
        }
    }

    boolean isDestroyed;
    @Override
    protected void onDestroy() {
        isDestroyed = true;
        UploadManager.getInstance().removeCallback(this);
        super.onDestroy();
    }
}
