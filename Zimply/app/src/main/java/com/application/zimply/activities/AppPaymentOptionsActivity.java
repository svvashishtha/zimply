package com.application.zimply.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.application.zimply.R;
import com.application.zimply.application.AppApplication;
import com.application.zimply.baseobjects.AddressObject;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.extras.ObjectTypes;
import com.application.zimply.objects.AllProducts;
import com.application.zimply.preferences.AppPreferences;
import com.application.zimply.serverapis.RequestTags;
import com.application.zimply.utils.JSONUtils;
import com.application.zimply.utils.UploadManager;
import com.application.zimply.utils.UploadManagerCallback;
import com.application.zimply.widgets.CustomTextView;
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

    int buyingChannel;

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
            buyingChannel = getIntent().getIntExtra("buying_channel",0);
        }
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((CustomTextView)findViewById(R.id.pay_cash_counter)).setOnClickListener(this);
        ((CustomTextView)findViewById(R.id.pay_online)).setOnClickListener(this);

        UploadManager.getInstance().addCallback(this);
    }

    public void addToolbarView(Toolbar toolbar){
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout,toolbar,false);
        CustomTextView textView = (CustomTextView)view.findViewById(R.id.title_textview);
        textView.setText("Choose Payment Type");
        toolbar.addView(view);
    }

    String transactionId;
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.pay_online:
//((ProductCheckoutActivity) mActivity).setAddressSelectionFragment(null);
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

//Double.parseDouble(cartObject.getCart().getTotal_price())
                PayU.getInstance(this).startPaymentProcess(1
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

                break;
            case R.id.pay_cash_counter:
                sendPaymentSuccessFullCashRequest();
                break;
        }
    }

    boolean paymentSuccess;
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayU.RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                // success
                if (data != null) {
                    Toast.makeText(this,
                            "Success:  " + data.getStringExtra("result"),
                            Toast.LENGTH_LONG).show();


                    paymentSuccess = true;
                    sendPaymentSuccessFullRequest();
                }

            }else if(resultCode == Activity.RESULT_CANCELED) {
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
            if ( status) {
                if (JSONUtils.getIntegerfromJSON(((JSONObject) response), "payment_status") == 1) {
                    AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartCount() - JSONUtils.getIntegerfromJSON(((JSONObject) response), "cart_count"));
                    Toast.makeText(this, "Order placed successfully", Toast.LENGTH_LONG).show();
                    Intent postPaymentIntent = new Intent(this, PostPaymentThankYouActivity.class);
                    postPaymentIntent.putExtra("address", addressObj);
                    //todo confirm price here
                    postPaymentIntent.putExtra("billing_amount", totalPrice+"");
                    postPaymentIntent.putExtra("payment_type", paymentType);
                    finish();
                    startActivity(postPaymentIntent);

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
