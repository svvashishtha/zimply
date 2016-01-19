package com.application.zimplyshop.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.NewAppPaymentOptionsActivityListAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.baseobjects.CartObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.objects.AllProducts;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.JSONUtils;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.utils.ZTracker;
import com.application.zimplyshop.widgets.CustomTextView;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
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
public class NewAppPaymentOptionsActivity extends BaseActivity implements View.OnClickListener, RequestTags, UploadManagerCallback {


    String name, orderId, email;
    int totalPrice;

    AddressObject addressObj;

    int PAYMENT_TYPE_CASH = 1;
    int PAYMENT_TYPE_CARD = 2;

    int buyingChannel;

    boolean isCoc, isAllOnline, isCodNotAvailable;

    CartObject cartObj;

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    NewAppPaymentOptionsActivityListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_payment_options_layout);

        recyclerView = (RecyclerView) findViewById(R.id.apppaymentoptions);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (getIntent() != null) {
            orderId = getIntent().getStringExtra("order_id");
            name = getIntent().getStringExtra("name");
            email = getIntent().getStringExtra("email");
            totalPrice = getIntent().getIntExtra("total_amount", 0);
            addressObj = (AddressObject) getIntent().getSerializableExtra("address");
            buyingChannel = getIntent().getIntExtra("buying_channel", 0);
            isCoc = getIntent().getBooleanExtra("is_coc", false);
            isAllOnline = getIntent().getBooleanExtra("is_all_online", false);
            cartObj = (CartObject) getIntent().getSerializableExtra("cart_obj");
            isCodNotAvailable = getIntent().getBooleanExtra("is_cod_not_available", false);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UploadManager.getInstance().addCallback(this);

        adapter = new NewAppPaymentOptionsActivityListAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    public void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, toolbar, false);
        CustomTextView textView = (CustomTextView) view.findViewById(R.id.title_textview);
        textView.setText("Complete Your Payment");
        toolbar.addView(view);
    }


    public void makePaymentRequest() {
        if (paymentType == PAYMENT_TYPE_CASH) {
            sendPaymentSuccessFullCashRequest();

        } else {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("furl",
                    "https://dl.dropboxusercontent.com/s/z69y7fupciqzr7x/furlWithParams.html");
            params.put("surl",
                    "https://dl.dropboxusercontent.com/s/dtnvwz5p4uymjvg/success.html");
            transactionId = "0nf7" + System.currentTimeMillis();
            params.put(PayU.TXNID, transactionId);
            params.put(PayU.USER_CREDENTIALS, "test:test");
            params.put(PayU.PRODUCT_INFO, "My Product");
            params.put(PayU.FIRSTNAME, name);
            params.put(PayU.EMAIL, email);


            PayU.getInstance(this).startPaymentProcess(cartObj.getCart().getTotal_price()
                    , params, new PayU.PaymentMode[]{PayU.PaymentMode.CC,
                    PayU.PaymentMode.NB, PayU.PaymentMode.DC,
                    PayU.PaymentMode.EMI,
                    PayU.PaymentMode.STORED_CARDS});
        }
    }

    String transactionId;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_online:
                paymentType = PAYMENT_TYPE_CARD;
                findViewById(R.id.pay_online).setSelected(true);
                findViewById(R.id.cash_on_delivery).setSelected(false);
                break;
            case R.id.cash_on_delivery:
                paymentType = PAYMENT_TYPE_CASH;
                findViewById(R.id.pay_online).setSelected(false);
                findViewById(R.id.cash_on_delivery).setSelected(true);
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

            } else if (resultCode == RESULT_CANCELED) {
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
            PayU.setINSTANCE();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    int paymentType;

    public void sendPaymentSuccessFullRequest() {
        paymentType = PAYMENT_TYPE_CARD;
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.PLACE_ORDER_SUCCESS_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        list.add(new BasicNameValuePair("payment_method", PAYMENT_TYPE_CARD + ""));
        list.add(new BasicNameValuePair("total_price", totalPrice + ""));
        list.add(new BasicNameValuePair("order_id", orderId));
        list.add(new BasicNameValuePair("transaction_id", transactionId));
        list.add(new BasicNameValuePair("payment_status", ((paymentSuccess) ? 1 : 3) + ""));
        list.add(new BasicNameValuePair("buying_channel", buyingChannel + ""));
        UploadManager.getInstance().makeAyncRequest(url, PLACE_ORDER_SUCCESS_REQUEST_TAG, "", ObjectTypes.OBJECT_TYPE_PLACE_ORDER,
                null, list, null);

    }

    public void sendPaymentSuccessFullCashRequest() {
        paymentType = PAYMENT_TYPE_CASH;
        transactionId = "0nf7" + System.currentTimeMillis();
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.PLACE_ORDER_SUCCESS_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        list.add(new BasicNameValuePair("payment_method", PAYMENT_TYPE_CASH + ""));
        list.add(new BasicNameValuePair("total_price", totalPrice + ""));
        list.add(new BasicNameValuePair("order_id", orderId));
        list.add(new BasicNameValuePair("transaction_id", transactionId));
        list.add(new BasicNameValuePair("payment_status", 1 + ""));
        list.add(new BasicNameValuePair("buying_channel", buyingChannel + ""));
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
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if (!isDestroyed && requestType == PLACE_ORDER_SUCCESS_REQUEST_TAG) {
            if (zProgressDialog != null) {
                zProgressDialog.dismiss();
            }
            if (status) {
                if (JSONUtils.getIntegerfromJSON(((JSONObject) response), "payment_status") == 1) {
                    Toast.makeText(this, "Order placed successfully", Toast.LENGTH_LONG).show();
                    AllProducts.getInstance().setCartCount(JSONUtils.getIntegerfromJSON(((JSONObject) response), "cart_count"));
                    Intent intent = new Intent(this, CashOnCounterOrderCompletionActivity.class);
                    intent.putExtra("cart_obj", cartObj);
                    intent.putExtra("order_id", orderId);
                    intent.putExtra("address_obj", addressObj);
                    intent.putExtra("is_coc", (paymentType == PAYMENT_TYPE_CASH));
                    try {
                        for (int i = 0; i < cartObj.getCart().getDetail().size(); i++) {
                            try {
                                if (CommonLib.isNetworkAvailable(NewAppPaymentOptionsActivity.this)) {
                                    Product product = new Product()
                                            .setId(cartObj.getCart().getDetail().get(i).getProduct().getId() + "")
                                            .setName(cartObj.getCart().getDetail().get(i).getProduct().getName())
                                            .setPrice(cartObj.getCart().getDetail().get(i).getProduct().getPrice())
                                            .setQuantity(cartObj.getCart().getDetail().get(i).getQty());
// Add the step number and additional info about the checkout to the action.
                                    ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
                                            .setCheckoutStep(5)
                                            .setCheckoutOptions("Purchase Product with" + (paymentType == PAYMENT_TYPE_CASH ? " COD" : " Online payment"));
                                    ZTracker.checkOutGaEvents(productAction, product, NewAppPaymentOptionsActivity.this);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);


                } else {
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
