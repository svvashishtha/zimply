package com.application.zimplyshop.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.AppPaymentOptionsActivity;
import com.application.zimplyshop.activities.ProductCheckoutActivity;
import com.application.zimplyshop.adapters.CartItemListAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.baseobjects.CartObject;
import com.application.zimplyshop.baseobjects.NonLoggedInCartObj;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.objects.AllProducts;
import com.application.zimplyshop.objects.AllUsers;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.JSONUtils;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.widgets.CartSpaceItemDecoration;
import com.application.zimplyshop.widgets.CustomTextViewBold;
import com.payu.sdk.PayU;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderSummaryFragment extends ZFragment implements GetRequestListener, ObjectTypes,
        AppConstants, RequestTags, View.OnClickListener, UploadManagerCallback {

    LinearLayoutManager linearLayoutManager;
    CartItemListAdapter mAdapter;
    CartObject cartObject;
    ProgressDialog zProgressDialog;
    AddressObject billingAddress, shippingAddress;
    ArrayList<AddressObject> addressObjectArrayList;
    ProgressDialog progressDialog;
    int quantityUpdatePosition = -1, updatedQuantity = -1;
    String transactionId;
    String orderId;
    boolean paymentSuccess;
    private RecyclerView mListView;
    private Activity mActivity;
    private boolean isDestroyed, address;
    private boolean billing = false, shipping = false;
    private static OrderSummaryFragment fragment;

    int buyingChannel=-1;

    String productIds,quantity;

    public static OrderSummaryFragment newInstance(Bundle bundle) {
        OrderSummaryFragment fragment = new OrderSummaryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    LinearLayout buyLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.order_summary_fragment, container, false);
        buyLayout = (LinearLayout)view.findViewById(R.id.payment_layout);
        return view;
    }

    @Override
    public void onDestroyView() {

        isDestroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        UploadManager.getInstance().removeCallback(this);

        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if( !(getArguments() != null && getArguments().containsKey("from_checkout"))) {
            if(savedInstanceState != null) {
                if(savedInstanceState.containsKey("addressBilling"))
                    billingAddress = (AddressObject) savedInstanceState.getSerializable("addressBilling");
                if(savedInstanceState.containsKey("addressShipping"))
                    shippingAddress = (AddressObject) savedInstanceState.getSerializable("addressShipping");
                if(savedInstanceState.containsKey("cartObject"))
                    cartObject = (CartObject) savedInstanceState.getSerializable("cartObject");
                buyingChannel = savedInstanceState.getInt("buying_channel");
            } else if (getArguments() != null) {
                buyingChannel = getArguments().getInt("buying_channel");
                if(getArguments().containsKey("addressBilling"))
                    billingAddress = (AddressObject) getArguments().getSerializable("addressBilling");
                if(getArguments().containsKey("addressShipping"))
                    shippingAddress = (AddressObject) getArguments().getSerializable("addressShipping");
                if(getArguments().containsKey("cartObject"))
                    cartObject = (CartObject) getArguments().getSerializable("cartObject");

            }
        }
        if(getArguments()!=null){
            productIds = getArguments().getString("productids");
            quantity = getArguments().getString("quantity");
        }

        isDestroyed = false;

        GetRequestManager.getInstance().addCallbacks(this);

        UploadManager.getInstance().addCallback(this);

        mListView = (RecyclerView) view.findViewById(R.id.orders_listview);
        linearLayoutManager = new LinearLayoutManager(mActivity == null ? getActivity() : mActivity);
        mListView.setLayoutManager(linearLayoutManager);
        mListView.addItemDecoration(new CartSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));
        setLoadingVariables();
        retryLayout.setOnClickListener(this);

        if (billingAddress == null && shippingAddress == null)
            loadAddressData();
        else /*if (cartObject == null)*/
            loadCartData();
        //else {
           /* setAdapterData();
            showView();
            changeViewVisiblity(mListView, View.VISIBLE);*/
        //   }
    }

    private void loadAddressData() {
        address = true;
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_ADDRESSES + "?src=mob&userid=" + AppPreferences.getUserID(mActivity);
        GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.GET_ADDRESS_REQUEST_TAG, ObjectTypes.OBJECT_TYPE_GET_ADDRESSES);

    }

    public String getProductBuyingChannels( ArrayList<NonLoggedInCartObj> objs){
        StringBuilder s = new StringBuilder();
        for(int i=0;i<objs.size();i++){
            s.append(objs.get(i).getSellingChannel()+"");
            if(i!= objs.size()-1) {
                s.append(".");
            }
        }
        return s.toString();
    }

    private void loadCartData() {
        String url = AppApplication.getInstance().getBaseUrl() + GET_ORDER_SUMMARY_URL+"?src=mob&ids="+productIds+"&quantity="+quantity;

        GetRequestManager.getInstance().makeAyncRequest(url, GET_ORDER_SUMMARY, OBJECT_TYPE_CART);
    }

    public String getProductQuantityString(ArrayList<NonLoggedInCartObj> objs) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < objs.size(); i++) {
            s.append(objs.get(i).getQuantity() + "");
            if (i != objs.size() - 1) {
                s.append(".");
            }
        }
        return s.toString();
    }

    public String getProductIdStrings(ArrayList<NonLoggedInCartObj> objs) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < objs.size(); i++) {
            s.append(objs.get(i).getProductId() + "");
            if (i != objs.size() - 1) {
                s.append(".");
            }
        }
        return s.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*private void setListeners() {
        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextFragment();
            }
        });
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean onFragmentResult(Bundle bundle) {
        return false;
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (requestTag != null && requestTag.equals(RequestTags.GET_ADDRESS_REQUEST_TAG) && !isDestroyed) {
            {
                // view.findViewById(R.id.save).setVisibility(View.GONE);
                showLoadingView();
                changeViewVisiblity(mListView, View.GONE);
                changeViewVisiblity(buyLayout,View.GONE);
            }
        } else if (requestTag != null && requestTag.equals(GET_ORDER_SUMMARY)) {
            showLoadingView();
            changeViewVisiblity(mListView, View.GONE);
            changeViewVisiblity(buyLayout,View.GONE);
            //view.findViewById(R.id.save).setVisibility(View.GONE);
        }
    }


    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (zProgressDialog != null)
            zProgressDialog.dismiss();
        if (requestTag != null && requestTag.equals(RequestTags.GET_ADDRESS_REQUEST_TAG) && !isDestroyed) {

            addressObjectArrayList = (ArrayList<AddressObject>) obj;
            if (addressObjectArrayList.size() > 0) {
                if (billingAddress == null)
                    billingAddress = addressObjectArrayList.get(0);
                if (shippingAddress == null)
                    shippingAddress = addressObjectArrayList.get(0);
                address = false;
                /*if (cartObject == null)*/
                loadCartData();
                /*else {
                    setAdapterData();
                }*/
            } else {
                nextFragmentWithoutStack();
            }
        } else if (requestTag != null && requestTag.equals(GET_ORDER_SUMMARY)) {
            if (zProgressDialog != null)
                zProgressDialog.dismiss();

            if(((CartObject) obj).getCart().getDetail().size()>0){
                showView();
                changeViewVisiblity(mListView, View.VISIBLE);
                changeViewVisiblity(buyLayout,View.VISIBLE);

                cartObject = (CartObject) obj;
                if(!AppPreferences.isUserLogIn(getActivity()))
                    GetRequestManager.Update(AppPreferences.getDeviceID(getActivity()), null, RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
                setAdapterData();
                // AllProducts.getInstance().setCartCount(getCartQuantity());
            }else{
                showNullCaseView("No Items");
                changeViewVisiblity(mListView, View.GONE);
                changeViewVisiblity(buyLayout,View.GONE);
            }

        } else if (requestTag != null && requestTag.equals(REMOVE_FROM_CART)) {
            JSONObject jsonObject = (JSONObject) obj;
            try {
                String message = jsonObject.getString("success");
                if (message != null) {

                    Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    cartObject.getCart().setPrice(Float.parseFloat(cartObject.getCart().getPrice()) -
                            (Float.parseFloat(cartObject.getCart().getDetail().get(quantityUpdatePosition).getPrice())
                                    * Float.parseFloat(cartObject.getCart().getDetail().get(quantityUpdatePosition).getQuantity())) + "");
                    float shippingPrice = cartObject.getCart().getTotal_shipping().equalsIgnoreCase("Free")?0:Float.parseFloat(cartObject.getCart().getTotal_shipping()) -
                            (cartObject.getCart().getDetail().get(quantityUpdatePosition).getShipping_charges())
                                    * Integer.parseInt(cartObject.getCart().getDetail().get(quantityUpdatePosition).getQuantity());
                    cartObject.getCart().setTotal_shipping(shippingPrice + "");


//                    cartObject.getCart().setTotal_price(Float.parseFloat(cartObject.getCart().getTotal_price()) -
//                            (cartObject.getCart().getDetail().get(quantityUpdatePosition).getIndividualTotal_price())
//                                    * Integer.parseInt(cartObject.getCart().getDetail().get(quantityUpdatePosition).getQuantity()) + "");

                    float totalPrice = Float.parseFloat(cartObject.getCart().getPrice())+Float.parseFloat(cartObject.getCart().getTotal_shipping());
                    cartObject.getCart().setTotal_price( totalPrice+"");

                    AllProducts.getInstance().removeCartItem(Integer.parseInt(cartObject.getCart().getDetail().get(quantityUpdatePosition).getProduct_id()));
                    //  AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartObjs().size());

                    if(cartObject.getCart().getDetail().size()>1) {
                        cartObject.getCart().getDetail().remove(quantityUpdatePosition);
                        setAdapterData();
                    }else {
                        cartObject.getCart().getDetail().remove(quantityUpdatePosition);
                        showNullCaseView("No items in cart");
                        changeViewVisiblity(mListView, View.GONE);
                        changeViewVisiblity(buyLayout,View.GONE);
                    }


                    // AllProducts.getInstance().setCartCount(getCartQuantity());
                } else {
                    message = jsonObject.getString("error");
                    if (message != null) {

                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    }
                }
                //loadCartData();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(mActivity, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public int getCartQuantity(){
        return cartObject.getCart().getDetail().size();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){

        }
    }

    @Override
    public void onResume() {
        if (getActivity() != null) {
            ((ProductCheckoutActivity) getActivity()).setTitleText("Order Summary");
        }

        super.onResume();
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {

        if (requestTag != null && requestTag.equals(RequestTags.GET_ADDRESS_REQUEST_TAG) && !isDestroyed) {
            {
                Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else if (requestTag != null && requestTag.equals(GET_ORDER_SUMMARY)) {
            showNetworkErrorView();
            // changeViewVisiblity(view.findViewById(R.id.save), View.GONE);
        }else if(requestTag!=null && requestTag.equalsIgnoreCase(REMOVE_FROM_CART)&& !isDestroyed){
            if(zProgressDialog!=null)
                zProgressDialog.dismiss();
        }
    }

    private void nextFragment() {
        if (mActivity != null) {

            Intent intent = new Intent(getActivity() , AppPaymentOptionsActivity.class);
            intent.putExtra("total_amount",cartObject.getCart().getTotal_price());
            intent.putExtra("order_id",orderId);
            intent.putExtra("name",shippingAddress.getName());
            intent.putExtra("email",shippingAddress.getEmail());
            intent.putExtra("address",shippingAddress);
            intent.putExtra("buying_channel",buyingChannel);
            intent.putExtra("is_coc",isCoc());
            intent.putExtra("is_all_online",isAllOnline());
            intent.putExtra("cart_obj",cartObject);
            startActivity(intent);


        }
    }

    public boolean isCoc(){
        for(int i = 0;i<cartObject.getCart().getDetail().size();i++){
            if(!cartObject.getCart().getDetail().get(i).is_o2o()){
                return false;
            }
        }
        return true;
    }
    public boolean isAllOnline(){
        for(int i = 0;i<cartObject.getCart().getDetail().size();i++){
            if(cartObject.getCart().getDetail().get(i).is_o2o()){
                return false;
            }
        }
        return true;
    }

    public void updateAddress(){
        if(AllUsers.getInstance().getObjs()!=null &&AllUsers.getInstance().getObjs().size()>0 && mAdapter!=null){
            mAdapter.changeShippingBillingAddress(AllUsers.getInstance().getObjs().get(0));
        }
    }

    private void nextFragmentWithoutStack() {
        if (mActivity != null) {
            Bundle bundle = new Bundle();
            //bundle.putBoolean("stack", false);
            bundle.putBoolean("adding_first_fragment",true);
            ((ProductCheckoutActivity) mActivity).setEditAddressSelectionFragmentWithoutStack(bundle);
            // ((ProductCheckoutActivity) mActivity).setEditAddressFragmentWithBackstack(bundle);
        }
    }

    private void setAdapterData() {
        mAdapter = new CartItemListAdapter(mActivity, cartObject, billingAddress, shippingAddress);
        mAdapter.setCartEditListener(new CartItemListAdapter.cartEditListener() {
            @Override
            public void checkOut(int position) {
                sendOrderPlaceRequest();
            }

            @Override
            public void itemQuantityChanged(int position, int quantity) {
                String url = AppApplication.getInstance().getBaseUrl() + ADD_TO_CART_URL;
                List<NameValuePair> nameValuePair = new ArrayList<>();
                nameValuePair.add(new BasicNameValuePair("product_id", cartObject.getCart().getDetail().get(position).getProduct_id() + ""));
                nameValuePair.add(new BasicNameValuePair("quantity", quantity + ""));
                nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(mActivity)));
                quantityUpdatePosition = position;
                updatedQuantity = quantity;
                UploadManager.getInstance().makeAyncRequest(url, QUANTITY_UPDATE, cartObject.getCart().getDetail().get(position).getSlug(), OBJECT_ADD_TO_CART, null, nameValuePair, null);

                //mAdapter.notifyDataSetChanged();
            }

            @Override
            public void itemDeleted(int position) {
                quantityUpdatePosition = position;
                zProgressDialog = ProgressDialog.show(mActivity, null, "Removing Item. Please wait...");
                String url = AppApplication.getInstance().getBaseUrl() +
                        "ecommerce/remove-cart/" + cartObject.getCart().getDetail().get(position).getCart_item_id() + "/";
                GetRequestManager.getInstance().makeAyncRequest(url, REMOVE_FROM_CART, OBJECT_TYPE_ITEM_REMOVED);
                AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartCount() - Integer.parseInt(cartObject.getCart().getDetail().get(position).getQuantity()));

            }

            @Override
            public void changeAddressBilling() {
                billing = true;
                shipping = false;
                Bundle bundle = new Bundle();
                bundle.putSerializable("cartObject", cartObject);
                bundle.putSerializable("user_address",addressObjectArrayList);
                bundle.putSerializable("shippingAddress", shippingAddress);
                bundle.putBoolean("isBilling", true);
                ((ProductCheckoutActivity) mActivity).setAddressSelectionFragmentWithBackstack(bundle);
            }

            @Override
            public void changeAddressShipping() {
                shipping = true;
                billing = false;
                Bundle bundle = new Bundle();
                bundle.putSerializable("cartObject", cartObject);
                bundle.putSerializable("user_address",addressObjectArrayList);
                bundle.putSerializable("billingAddress", billingAddress);
                bundle.putBoolean("isBilling", false);
                ((ProductCheckoutActivity) mActivity).setAddressSelectionFragmentWithBackstack(bundle);
            }
        });
        mListView.setAdapter(mAdapter);
        ((CustomTextViewBold)view.findViewById(R.id.total_amount)).setText(Html.fromHtml("Total : " + "<font color=#0093b8>"+getResources().getString(R.string.rs_text) + " " + Math.round(Double.parseDouble(cartObject.getCart().getTotal_price()))+"</font>"));
        ((CustomTextViewBold)view.findViewById(R.id.buy_btn)).setText("Proceed to Pay");
        ((CustomTextViewBold)view.findViewById(R.id.buy_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOrderPlaceRequest();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_layout:
                if (address)
                    loadAddressData();
                else
                    loadCartData();
                break;
        }
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {

        if(!isDestroyed){
            //todo decide whether to show loading here or not\
            if (zProgressDialog != null)
                zProgressDialog.dismiss();
            if (requestType == QUANTITY_UPDATE) {
                if (status) {

                    JSONObject jsonObject = (JSONObject) response;
                    try {
                        long price = jsonObject.getInt("price");
                        if (price != -1) {
                            cartObject.getCart().setPrice(Float.parseFloat(cartObject.getCart().getPrice()) + price -
                                    (Float.parseFloat(cartObject.getCart().getDetail().get(quantityUpdatePosition).getPrice())
                                            * Float.parseFloat(cartObject.getCart().getDetail().get(quantityUpdatePosition).getQuantity())) + "");
                            cartObject.getCart().setTotal_shipping(Float.parseFloat(cartObject.getCart().getTotal_shipping()) +
                                    (updatedQuantity - Integer.parseInt(cartObject.getCart().getDetail().get(quantityUpdatePosition).getQuantity())) *
                                            cartObject.getCart().getDetail().get(quantityUpdatePosition).getIndividualShipping_charge() + "");
                        /*cartObject.getCart().setTotal_price((Float.parseFloat(cartObject.getCart().getTotal_price()) -
                                cartObject.getCart().getDetail().get(quantityUpdatePosition).getIndividualTotal_price()) +
                                ((updatedQuantity *
                                        cartObject.getCart().getDetail().get(quantityUpdatePosition).getIndividualShipping_charge())
                                        + price) + "");*/
                            cartObject.getCart().setTotal_price(Float.parseFloat(cartObject.getCart().getPrice()) +
                                    Float.parseFloat(cartObject.getCart().getTotal_shipping()) + "");

                            cartObject.getCart().getDetail().get(quantityUpdatePosition).setQuantity(updatedQuantity + "");
                            cartObject.getCart().getDetail().get(quantityUpdatePosition).setIndividualTotal_price(
                                    Float.parseFloat(cartObject.getCart().getDetail().get(quantityUpdatePosition).getPrice())
                                            * updatedQuantity);
                            setAdapterData();
                            quantityUpdatePosition = -1;
                            updatedQuantity = -1;

                            // AllProducts.getInstance().setCartCount(getCartQuantity());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(mActivity, "An error ocurred.", Toast.LENGTH_SHORT).show();


            } else if (requestType == PLACE_ORDER_REQUEST_TAG) {
                if(status){
                    orderId = JSONUtils.getStringfromJSON(((JSONObject) response), "order_id");

                    nextFragment();
                }else{
                    Toast.makeText(getActivity() , "Could not place order. Try again",Toast.LENGTH_SHORT).show();
                }
            } /*else if (requestType == PLACE_ORDER_SUCCESS_REQUEST_TAG) {
                if (status) {

                    if (JSONUtils.getIntegerfromJSON(((JSONObject) response), "payment_status") == 1) {
                        Toast.makeText(getActivity(), "Order placed successfully", Toast.LENGTH_LONG).show();
                        Intent postPaymentIntent = new Intent(getActivity(), PostPaymentThankYouActivity.class);
                        postPaymentIntent.putExtra("address", billingAddress);
                        //todo confirm price here
                        postPaymentIntent.putExtra("billing_amount", cartObject.getCart().getTotal_price());
                        startActivity(postPaymentIntent);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Could not place order. Try again", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Could not place order. Try again", Toast.LENGTH_LONG).show();
                }
            }*/
        }
    }



    public void onDestroy() {
        isDestroyed = true;
        UploadManager.getInstance().removeCallback(this);
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {
        if (!isDestroyed && isAdded() &&(requestType == QUANTITY_UPDATE || requestType == PLACE_ORDER_REQUEST_TAG))
            zProgressDialog = ProgressDialog.show(mActivity, null, "Loading...");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayU.RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                // success
                if (data != null) {
                    Toast.makeText(getActivity(),
                            "Success:  " + data.getStringExtra("result"),
                            Toast.LENGTH_LONG).show();


                    paymentSuccess = true;
                    sendPaymentSuccessFullRequest();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // failed
                if (data != null) {
                    Toast.makeText(getActivity(),
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

    public void sendOrderPlaceRequest() {

        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.PLACE_ORDER_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("src","mob"));
        list.add(new BasicNameValuePair("ids",productIds));
        list.add(new BasicNameValuePair("quantity",quantity));
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(getActivity())));
        list.add(new BasicNameValuePair("billing_address", shippingAddress.getName() + ", " + shippingAddress.getLine1() +
                ", " + shippingAddress.getLine2() + ", " + shippingAddress.getCity() + ", " + shippingAddress.getPincode()));
        list.add(new BasicNameValuePair("shipping_address", shippingAddress.getName() + ", " + shippingAddress.getLine1() +
                ", " + shippingAddress.getLine2() + ", " + shippingAddress.getCity() + ", " + shippingAddress.getPincode()));
        list.add(new BasicNameValuePair("shipping_address_id",shippingAddress.getId()+""));
        UploadManager.getInstance().makeAyncRequest(url, PLACE_ORDER_REQUEST_TAG, "", ObjectTypes.OBJECT_TYPE_PLACE_ORDER,
                null, list, null);

    }

    public void sendPaymentSuccessFullRequest() {
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.PLACE_ORDER_SUCCESS_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(getActivity())));
        list.add(new BasicNameValuePair("total_price",cartObject.getCart().getTotal_price()));
        list.add(new BasicNameValuePair("order_id",orderId));
        list.add(new BasicNameValuePair("transaction_id",transactionId));
        list.add(new BasicNameValuePair("payment_status",((paymentSuccess)?1:3)+"" ));
        UploadManager.getInstance().makeAyncRequest(url, PLACE_ORDER_SUCCESS_REQUEST_TAG, "", ObjectTypes.OBJECT_TYPE_PLACE_ORDER,
                null, list, null);

    }

}
