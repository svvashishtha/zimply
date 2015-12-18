package com.application.zimplyshop.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.baseobjects.CartObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.objects.AllProducts;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.JSONUtils;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.widgets.CustomTextView;
import com.application.zimplyshop.widgets.CustomTextViewBold;
import com.payu.sdk.PayU;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Umesh Lohani on 11/6/2015.
 */
public class AppPaymentOptionsActivity extends BaseActivity implements View.OnClickListener,RequestTags,UploadManagerCallback{


    String name,orderId,email;
    Double totalPrice;

    AddressObject addressObj;

    int PAYMENT_TYPE_CASH=4;
    int PAYMENT_TYPE_CARD = 2;
//    int PAYMENT_TYPE_COD = 2;



    int buyingChannel;

    boolean isCoc,isAllOnline;

    CartObject cartObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_options_layout);

        if(getIntent()!=null){
            orderId = getIntent().getStringExtra("order_id");
            name = getIntent().getStringExtra("name");
            email = getIntent().getStringExtra("email");
            totalPrice =Double.parseDouble(getIntent().getStringExtra("total_amount"));
            addressObj = (AddressObject)getIntent().getSerializableExtra("address");
            buyingChannel = getIntent().getIntExtra("buying_channel", 0);
            isCoc = getIntent().getBooleanExtra("is_coc", false);

            isAllOnline =  getIntent().getBooleanExtra("is_all_online",false);
            cartObj = (CartObject)getIntent().getSerializableExtra("cart_obj");
        }
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       /* if(buyingChannel == AppConstants.BUYING_CHANNEL_ONLINE) {
            ((CustomTextView)findViewById(R.id.pay_cash_counter)).setVisibility(View.GONE);
        }else{
            ((CustomTextView)findViewById(R.id.pay_cash_counter)).setOnClickListener(this);
        }*/
        ((CustomTextView)findViewById(R.id.pay_cash_counter)).setOnClickListener(this);
        ((CustomTextView)findViewById(R.id.pay_online)).setOnClickListener(this);
        LinearLayout buyLayout = (LinearLayout)findViewById(R.id.payment_layout);
        buyLayout.setVisibility(View.VISIBLE);
        ((CustomTextViewBold)findViewById(R.id.total_amount)).setText(Html.fromHtml("Total : " + "<font color=#0093b8>"+getResources().getString(R.string.rs_text) + " " + Math.round(totalPrice)+"</font>"));
        ((CustomTextViewBold)findViewById(R.id.buy_btn)).setText("Place Order");
        ((CustomTextViewBold)findViewById(R.id.buy_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePaymentRequest();
            }
        });
        UploadManager.getInstance().addCallback(this);

        paymentType = PAYMENT_TYPE_CARD;
        findViewById(R.id.pay_online).setSelected(true);
        findViewById(R.id.pay_cash_counter).setSelected(false);
        if( isAllOnline ){
            ((CustomTextView)findViewById(R.id.pay_cash_counter)).setVisibility(View.GONE);
        }
    }

    public void addToolbarView(Toolbar toolbar){
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout,toolbar,false);
        CustomTextView textView = (CustomTextView) view.findViewById(R.id.title_textview);
        textView.setText("Choose Payment Type");
        toolbar.addView(view);
    }


    public void makePaymentRequest(){
        if(paymentType == PAYMENT_TYPE_CASH){
            if(isCoc) {
                sendPaymentSuccessFullCashRequest();
            }else{
                Toast.makeText(this,"Cash-at-Counter is not available for one or more items. Please remove those items from the cart to use Cash-at-Counter or Pay Online.",Toast.LENGTH_SHORT).show();
            }
        }else{
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("furl",
                    "https://dl.dropboxusercontent.com/s/z69y7fupciqzr7x/furlWithParams.html");
            params.put("surl",
                    "https://dl.dropboxusercontent.com/s/dtnvwz5p4uymjvg/success.html");
            transactionId = "0nf7" + System.currentTimeMillis();
            params.put(PayU.TXNID, transactionId);
            params.put(PayU.USER_CREDENTIALS, "test:test");
            params.put(PayU.PRODUCT_INFO, "My Product");
            params.put(PayU.FIRSTNAME,name);
            params.put(PayU.EMAIL, email);

//
            PayU.getInstance(this).startPaymentProcess(Double.parseDouble(cartObj.getCart().getTotal_price())
                    , params, new PayU.PaymentMode[]{PayU.PaymentMode.CC,
                    PayU.PaymentMode.NB, PayU.PaymentMode.DC,
                    PayU.PaymentMode.EMI,
                    PayU.PaymentMode.STORED_CARDS});
//totalPrice
               /* PayU.getInstance(this).startPaymentProcess(
                        1, params, new PayU.PaymentMode[]{PayU.PaymentMode.CC,
                                PayU.PaymentMode.NB, PayU.PaymentMode.DC,
                                PayU.PaymentMode.EMI,
                                PayU.PaymentMode.STORED_CARDS});*/

        }
    }

    String transactionId;
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.pay_online:
                paymentType = PAYMENT_TYPE_CARD;
                findViewById(R.id.pay_online).setSelected(true);
                findViewById(R.id.pay_cash_counter).setSelected(false);
                break;
            case R.id.pay_cash_counter:
                paymentType = PAYMENT_TYPE_CASH;
                findViewById(R.id.pay_online).setSelected(false);
                findViewById(R.id.pay_cash_counter).setSelected(true);
                break;
        }
    }

    boolean paymentSuccess;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayU.RESULT) {
            if (resultCode == RESULT_OK) {
                // success
                if (data != null) {
                    Toast.makeText(this,
                            "Success:  " + data.getStringExtra("result"),
                            Toast.LENGTH_LONG).show();


                    paymentSuccess = true;
                    sendPaymentSuccessFullRequest();
                }

            }else if(resultCode == RESULT_CANCELED) {
                // failed
                if (data != null) {
                    Toast.makeText(this,
                            "Failed:  " + data.getStringExtra("result"),
                            Toast.LENGTH_LONG).show();
                    System.out.println("Payu Data::"
                            + data.getStringExtra("result"));
                }
                paymentSuccess = false;
                sendPaymentSuccessFullRequest();
            }
        }
    }


    int paymentType;

    public void sendPaymentSuccessFullRequest() {
        paymentType = PAYMENT_TYPE_CARD;
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.PLACE_ORDER_SUCCESS_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        list.add(new BasicNameValuePair("payment_method",PAYMENT_TYPE_CARD+""));
        list.add(new BasicNameValuePair("total_price",totalPrice+""));
        list.add(new BasicNameValuePair("order_id",orderId));
        list.add(new BasicNameValuePair("transaction_id",transactionId));
        list.add(new BasicNameValuePair("payment_status",((paymentSuccess)?1:3)+"" ));
        list.add(new BasicNameValuePair("buying_channel", buyingChannel+ ""));
        UploadManager.getInstance().makeAyncRequest(url, PLACE_ORDER_SUCCESS_REQUEST_TAG, "", ObjectTypes.OBJECT_TYPE_PLACE_ORDER,
                null, list, null);

    }

    public void sendPaymentSuccessFullCashRequest() {
        paymentType = PAYMENT_TYPE_CASH;
        transactionId = "0nf7" + System.currentTimeMillis();
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.PLACE_ORDER_SUCCESS_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        list.add(new BasicNameValuePair("payment_method",PAYMENT_TYPE_CASH+""));
        list.add(new BasicNameValuePair("total_price",totalPrice+""));
        list.add(new BasicNameValuePair("order_id",orderId));
        list.add(new BasicNameValuePair("transaction_id",transactionId));
        list.add(new BasicNameValuePair("payment_status", 1 + ""));
        list.add(new BasicNameValuePair("buying_channel", buyingChannel+ ""));
        UploadManager.getInstance().makeAyncRequest(url, PLACE_ORDER_SUCCESS_REQUEST_TAG, "", ObjectTypes.OBJECT_TYPE_PLACE_ORDER,
                null, list, null);

    }

    boolean isDestroyed;



    @Override
    protected void onDestroy() {
        isDestroyed = true;
        UploadManager.getInstance().removeCallback(this);
        super.onDestroy();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if (!isDestroyed && requestType == PLACE_ORDER_SUCCESS_REQUEST_TAG) {
            if(zProgressDialog!=null){
                zProgressDialog.dismiss();
            }
            if (status) {
                if (JSONUtils.getIntegerfromJSON(((JSONObject) response), "payment_status") == 1) {
                    Toast.makeText(this, "Order placed successfully", Toast.LENGTH_LONG).show();
                    AllProducts.getInstance().setCartCount(JSONUtils.getIntegerfromJSON(((JSONObject) response), "cart_count"));
                    Intent intent = new Intent(this ,CashOnCounterOrderCompletionActivity.class);
                    intent.putExtra("cart_obj",cartObj);
                    intent.putExtra("order_id",orderId);
                    intent.putExtra("address_obj",addressObj);
                    intent.putExtra("is_coc",(paymentType == PAYMENT_TYPE_CASH));
                    startActivity(intent);


                }else{
                    Toast.makeText(this, "Could not place order. Try again", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Could not place order. Try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
    ProgressDialog zProgressDialog;
    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {
        if (!isDestroyed) {
            zProgressDialog = ProgressDialog.show(this, null, "Loading...");
        }
    }
}
