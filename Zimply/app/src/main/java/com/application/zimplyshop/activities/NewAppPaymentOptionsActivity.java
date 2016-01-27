package com.application.zimplyshop.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.payu.india.Interfaces.DeleteCardApiListener;
import com.payu.india.Interfaces.PaymentRelatedDetailsListener;
import com.payu.india.Model.MerchantWebService;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PayuResponse;
import com.payu.india.Model.PostData;
import com.payu.india.Model.StoredCard;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.india.PostParams.MerchantWebServicePostParams;
import com.payu.india.PostParams.PaymentPostParams;
import com.payu.india.Tasks.DeleteCardTask;
import com.payu.india.Tasks.GetPaymentRelatedDetailsTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Umesh Lohani on 11/6/2015.
 */
public class NewAppPaymentOptionsActivity extends BaseActivity implements RequestTags, UploadManagerCallback, PaymentRelatedDetailsListener, DeleteCardApiListener {


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
    PaymentParams mPaymentParams;
    PayuConfig payuConfig;
    PayuHashes payuHashes;
    PayuResponse payuResponse;

    String transactionId;
    boolean paymentSuccess;
    int paymentType;

    final String PAYU_KEY_MANDATORY = "0MQaQP";

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

        setInitialDataForPaymentParamsObj();

        generateHashFromServer();
    }

    public void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, toolbar, false);
        CustomTextView textView = (CustomTextView) view.findViewById(R.id.title_textview);
        textView.setText("Complete Your Payment");
        toolbar.addView(view);
    }

    void setInitialDataForPaymentParamsObj() {
        transactionId = "0nf7" + System.currentTimeMillis();
        mPaymentParams = new PaymentParams();
        mPaymentParams.setKey(PAYU_KEY_MANDATORY);
        mPaymentParams.setAmount(Integer.toString(totalPrice));
        mPaymentParams.setProductInfo("My Product");
        mPaymentParams.setFirstName(name);
        mPaymentParams.setEmail(email);
        mPaymentParams.setTxnId(transactionId);
        mPaymentParams.setSurl("httpss://payu.herokuapp.com/success");
        mPaymentParams.setFurl("httpss://payu.herokuapp.com/failure");
        mPaymentParams.setUdf1("");
        mPaymentParams.setUdf2("");
        mPaymentParams.setUdf3("");
        mPaymentParams.setUdf4("");
        mPaymentParams.setUdf5("");
        mPaymentParams.setUserCredentials(PAYU_KEY_MANDATORY + ":" + AppPreferences.getUserEmail(this));

        payuConfig = new PayuConfig();
        payuConfig.setEnvironment(PayuConstants.PRODUCTION_ENV);
    }

    public void openPaymentUsingNetBanking(String bankCode) {
        mPaymentParams.setBankCode(bankCode);
        PostData postData = new PaymentPostParams(mPaymentParams, PayuConstants.NB).getPaymentPostParams();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            // launch webview
            payuConfig.setData(postData.getResult());
            Intent intent = new Intent(this, PayUWebViewActivity.class);
            intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
            startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        } else {
            // something went wrong
            Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
        }
    }

    public void openPayUWebViewForCreditCard(String cardNumber, String cardName, String expiryMonth, String expiryYear, String cvv, boolean checked) {
        mPaymentParams.setCardNumber(cardNumber);
        mPaymentParams.setCardName(cardName);
        mPaymentParams.setNameOnCard(cardName);
        mPaymentParams.setExpiryMonth(expiryMonth);// MM
        mPaymentParams.setExpiryYear(expiryYear);// YYYY
        mPaymentParams.setCvv(cvv);

        if (checked) {
            mPaymentParams.setStoreCard(1);
        } else
            mPaymentParams.setStoreCard(0);

        PostData postData = new PaymentPostParams(mPaymentParams, PayuConstants.CC).getPaymentPostParams();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            // launch webview
            payuConfig.setData(postData.getResult());
            Intent intent = new Intent(this, PayUWebViewActivity.class);
            intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
            startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        } else {
            // something went wrong
            Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
        }
    }

    public void generateHashFromServer() {
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayuConstants.KEY, mPaymentParams.getKey()));
        postParamsBuffer.append(concatParams(PayuConstants.AMOUNT, mPaymentParams.getAmount()));
        postParamsBuffer.append(concatParams(PayuConstants.TXNID, mPaymentParams.getTxnId()));
        postParamsBuffer.append(concatParams(PayuConstants.EMAIL, null == mPaymentParams.getEmail() ? "" : mPaymentParams.getEmail()));
        postParamsBuffer.append(concatParams(PayuConstants.PRODUCT_INFO, mPaymentParams.getProductInfo()));
        postParamsBuffer.append(concatParams(PayuConstants.FIRST_NAME, null == mPaymentParams.getFirstName() ? "" : mPaymentParams.getFirstName()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF1, mPaymentParams.getUdf1() == null ? "" : mPaymentParams.getUdf1()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF2, mPaymentParams.getUdf2() == null ? "" : mPaymentParams.getUdf2()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF3, mPaymentParams.getUdf3() == null ? "" : mPaymentParams.getUdf3()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF4, mPaymentParams.getUdf4() == null ? "" : mPaymentParams.getUdf4()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF5, mPaymentParams.getUdf5() == null ? "" : mPaymentParams.getUdf5()));
        postParamsBuffer.append(concatParams(PayuConstants.USER_CREDENTIALS, mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials()));

        if (null != mPaymentParams.getOfferKey())
            postParamsBuffer.append(concatParams(PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey()));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1) : postParamsBuffer.toString();
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }

    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    public void makePaymentUsingSavedCard(StoredCard storedCard, String cvv) {
        mPaymentParams.setHash(payuHashes.getPaymentHash()); // make sure that you set payment hash
        mPaymentParams.setCardToken(storedCard.getCardToken());
        mPaymentParams.setCvv(cvv);
        mPaymentParams.setNameOnCard(storedCard.getNameOnCard());
        mPaymentParams.setCardName(storedCard.getCardName());
        mPaymentParams.setExpiryMonth(storedCard.getExpiryMonth());
        mPaymentParams.setExpiryYear(storedCard.getExpiryYear());

        PostData postData = new PaymentPostParams(mPaymentParams, PayuConstants.CC).getPaymentPostParams();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuConfig.setData(postData.getResult());
            Intent intent = new Intent(this, PayUWebViewActivity.class);
            intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
            startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
        } else {
            Toast.makeText(this, postData.getResult(), Toast.LENGTH_LONG).show();
        }
    }

    class GetHashesFromServerTask extends AsyncTask<String, String, PayuHashes> {

        @Override
        protected PayuHashes doInBackground(String... postParams) {
            PayuHashes payuHashes = new PayuHashes();
            try {
                URL url = new URL("https://payu.herokuapp.com/get_hash");
                String postParam = postParams[0];
                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        case "payment_hash":
                            payuHashes.setPaymentHash(response.getString(key));
                            break;
                        case "get_merchant_ibibo_codes_hash": //
                            payuHashes.setMerchantIbiboCodesHash(response.getString(key));
                            break;
                        case "vas_for_mobile_sdk_hash":
                            payuHashes.setVasForMobileSdkHash(response.getString(key));
                            break;
                        case "payment_related_details_for_mobile_sdk_hash":
                            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(response.getString(key));
                            break;
                        case "delete_user_card_hash":
                            payuHashes.setDeleteCardHash(response.getString(key));
                            break;
                        case "get_user_cards_hash":
                            payuHashes.setStoredCardsHash(response.getString(key));
                            break;
                        case "edit_user_card_hash":
                            payuHashes.setEditCardHash(response.getString(key));
                            break;
                        case "save_user_card_hash":
                            payuHashes.setSaveCardHash(response.getString(key));
                            break;
                        case "check_offer_status_hash":
                            payuHashes.setCheckOfferStatusHash(response.getString(key));
                            break;
                        case "check_isDomestic_hash":
                            payuHashes.setCheckIsDomesticHash(response.getString(key));
                            break;
                        default:
                            break;
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return payuHashes;
        }

        @Override
        protected void onPostExecute(PayuHashes payuHashes) {
            super.onPostExecute(payuHashes);
            NewAppPaymentOptionsActivity.this.payuHashes = payuHashes;
            mPaymentParams.setHash(payuHashes.getPaymentHash());

            getSavedCardsAndNetBanksInfo();
        }
    }

    private void getSavedCardsAndNetBanksInfo() {
        MerchantWebService merchantWebService = new MerchantWebService();
        merchantWebService.setKey(mPaymentParams.getKey());
        merchantWebService.setCommand(PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK);
        merchantWebService.setVar1(mPaymentParams.getUserCredentials() == null ? "default" : mPaymentParams.getUserCredentials());

        merchantWebService.setHash(payuHashes.getPaymentRelatedDetailsForMobileSdkHash());

        PostData postData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuConfig.setData(postData.getResult());

            GetPaymentRelatedDetailsTask task = new GetPaymentRelatedDetailsTask(this);
            task.execute(payuConfig);
        } else
            Toast.makeText(this, postData.getResult(), Toast.LENGTH_SHORT).show();
    }

    public void deleteSavedCardAskForConfirmation(final StoredCard storedCard) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Delete Saved Card");
        alertDialogBuilder
                .setMessage("Are you sure you want to delete this saved card?")
                .setCancelable(false)
                .setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                deleteSavedCardConfirmed(storedCard);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void deleteSavedCardConfirmed(StoredCard storedCard) {
        MerchantWebService merchantWebService = new MerchantWebService();
        merchantWebService.setKey(mPaymentParams.getKey());
        merchantWebService.setCommand(PayuConstants.DELETE_USER_CARD);
        merchantWebService.setVar1(mPaymentParams.getUserCredentials());
        merchantWebService.setVar2(storedCard.getCardToken());
        merchantWebService.setHash(payuHashes.getDeleteCardHash());

        PostData postData = null;
        postData = new MerchantWebServicePostParams(merchantWebService).getMerchantWebServicePostParams();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuConfig.setData(postData.getResult());
            payuConfig.setEnvironment(payuConfig.getEnvironment());

            DeleteCardTask deleteCardTask = new DeleteCardTask(this);
            deleteCardTask.execute(payuConfig);
        } else {
            Toast.makeText(this, postData.getResult(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteCardApiResponse(PayuResponse payuResponse) {
        if (adapter == null)
            return;
        if (payuResponse.getResponseStatus().getCode() == PayuErrors.NO_ERROR) {
            adapter.deletedSavedCardSuccessfully(payuResponse);
        } else {
            Toast.makeText(this, "Error While Deleting Card", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPaymentRelatedDetailsResponse(PayuResponse res) {
        payuResponse = res;
        adapter = new NewAppPaymentOptionsActivityListAdapter(this, cartObj, payuResponse, totalPrice, isCodNotAvailable);
        recyclerView.setAdapter(adapter);
    }

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
