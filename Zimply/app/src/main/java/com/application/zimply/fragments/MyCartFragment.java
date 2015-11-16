package com.application.zimply.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimply.R;
import com.application.zimply.activities.BaseLoginSignupActivity;
import com.application.zimply.activities.ProductCheckoutActivity;
import com.application.zimply.adapters.MyCartAdapter;
import com.application.zimply.application.AppApplication;
import com.application.zimply.baseobjects.CartObject;
import com.application.zimply.baseobjects.NonLoggedInCartObj;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.extras.ObjectTypes;
import com.application.zimply.managers.GetRequestListener;
import com.application.zimply.managers.GetRequestManager;
import com.application.zimply.objects.AllProducts;
import com.application.zimply.preferences.AppPreferences;
import com.application.zimply.serverapis.RequestTags;
import com.application.zimply.utils.UploadManager;
import com.application.zimply.utils.UploadManagerCallback;
import com.application.zimply.widgets.SpaceItemDecoration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Umesh Lohani on 10/15/2015.
 */
public class MyCartFragment extends ZFragment implements GetRequestListener, AppConstants, ObjectTypes, RequestTags, UploadManagerCallback {

    RecyclerView cartList;
    Activity mActivity;
    CartObject cartObject;
    int quantityUpdatePosition = -1, updatedQuantity = -1;
    private ProgressDialog zProgressDialog;

    int buyingChannel;

    public static MyCartFragment newInstance(Bundle bundle) {
        MyCartFragment fragment = new MyCartFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.experts_list_layout, null);
        cartList = (RecyclerView) view.findViewById(R.id.experts_list);
        cartList.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartList.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small), true));
        setLoadingVariables();
        return view;
    }

    public void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.common_toolbar_text_layout, toolbar, false);
        TextView titleText = (TextView) view.findViewById(R.id.title_textview);
        titleText.setText("My Cart");
        toolbar.addView(view);
    }

    Bundle bundle;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bundle = getArguments();
        buyingChannel = getArguments().getInt("buying_channel");
        UploadManager.getInstance().addCallback(this);
        GetRequestManager.getInstance().addCallbacks(this);

    }

    public String getProductQuantityString( ArrayList<NonLoggedInCartObj> objs){
        StringBuilder s = new StringBuilder();
        for(int i=0;i<objs.size();i++){
            s.append(objs.get(i).getQuantity()+"");
            if(i!= objs.size()-1) {
                s.append(".");
            }
        }
        return s.toString();
    }
    public String getProductIdStrings( ArrayList<NonLoggedInCartObj> objs){
        StringBuilder s = new StringBuilder();
        for(int i=0;i<objs.size();i++){
            s.append(objs.get(i).getProductId()+"");
            if(i!= objs.size()-1) {
                s.append(".");
            }
        }
        return s.toString();
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

    public void loadCartComputation(){
        ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj> )GetRequestManager.Request(AppPreferences.getDeviceID(getActivity()),RequestTags.NON_LOGGED_IN_CART_CACHE,GetRequestManager.CONSTANT);
        if(objs!=null && objs.size()>0){
            String url = AppApplication.getInstance().getBaseUrl()+AppConstants.GET_CART_COMPUTATION+"?src=mob"+"&ids="+getProductIdStrings(objs)+"&quantity="+getProductQuantityString(objs)+"&buying_channels="+getProductBuyingChannels(objs)+"&buying_channel="+buyingChannel;
            GetRequestManager.getInstance().makeAyncRequest(url ,RequestTags.GET_CART_COMPUTATION+buyingChannel,OBJECT_TYPE_CART);
        }else{
            showNullCaseView("No items in cart");
            changeViewVisiblity(cartList,View.GONE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public void loadData() {
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_CART_URL + "?src=mob&userid=" + AppPreferences.getUserID(getActivity())+"&buying_channel="+buyingChannel;
        ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj>)GetRequestManager.Request(AppPreferences.getDeviceID(getActivity()),RequestTags.NON_LOGGED_IN_CART_CACHE,GetRequestManager.CONSTANT);
        if(objs!=null){
            url+="&ids="+getProductIdStrings(objs)+"&quantity="+getProductQuantityString(objs)+"&buying_channels="+getProductBuyingChannels(objs);
        }
        GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.GET_CART_DETAILS+buyingChannel, OBJECT_TYPE_CART);

    }

    @Override
    public void onResume() {
        if (getActivity() != null) {
            ((ProductCheckoutActivity) getActivity()).setTitleText("My Cart");
        }

        if(AppPreferences.isUserLogIn(getActivity())) {
            loadData();
        } else {
            loadCartComputation();
        }
        super.onResume();
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (requestTag.equalsIgnoreCase(GET_CART_DETAILS) || requestTag.equalsIgnoreCase(RequestTags.GET_CART_COMPUTATION)) {
            showLoadingView();
            changeViewVisiblity(cartList, View.GONE);
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (zProgressDialog != null)
            zProgressDialog.dismiss();

        if (requestTag.equalsIgnoreCase(GET_CART_DETAILS+buyingChannel) || requestTag.equalsIgnoreCase(RequestTags.GET_CART_COMPUTATION+buyingChannel)) {
            if(((CartObject) obj).getCart().getDetail()!=null && ((CartObject) obj).getCart().getDetail().size()>0 ) {
                showView();
                changeViewVisiblity(cartList, View.VISIBLE);
                cartObject = (CartObject) obj;
                setAdapterData(cartObject);
                AllProducts.getInstance().setCartCount(getCartQuantity());
            }else{
                showNullCaseView("No Items in cart");
                changeViewVisiblity(cartList, View.GONE);
                AllProducts.getInstance().setCartCount(0);
            }

            if(requestTag.equalsIgnoreCase(GET_CART_DETAILS+buyingChannel)){
                GetRequestManager.Update(AppPreferences.getDeviceID(getActivity()), null, RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
            }

        } else if (requestTag != null && requestTag.equals(REMOVE_FROM_CART+buyingChannel)) {
            JSONObject jsonObject = (JSONObject) obj;
            try {
                String message = jsonObject.getString("success");

                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                // loadData();
                cartObject.getCart().setPrice(Float.parseFloat(cartObject.getCart().getPrice()) -
                        (Float.parseFloat(cartObject.getCart().getDetail().get(quantityUpdatePosition).getPrice())
                                * Float.parseFloat(cartObject.getCart().getDetail().get(quantityUpdatePosition).getQuantity())) + "");
                float shippingPrice = Float.parseFloat(cartObject.getCart().getTotal_shipping()) -
                        (cartObject.getCart().getDetail().get(quantityUpdatePosition).getShipping_charges())
                                * Integer.parseInt(cartObject.getCart().getDetail().get(quantityUpdatePosition).getQuantity());

                cartObject.getCart().setTotal_shipping(((int) shippingPrice != 0) ? shippingPrice + "" : "Free");

                cartObject.getCart().setTotal_price(Float.parseFloat(cartObject.getCart().getTotal_price()) -
                        (cartObject.getCart().getDetail().get(quantityUpdatePosition).getIndividualTotal_price())
                                * Integer.parseInt(cartObject.getCart().getDetail().get(quantityUpdatePosition).getQuantity()) + "");

                AllProducts.getInstance().removeCartItem(Integer.parseInt(cartObject.getCart().getDetail().get(quantityUpdatePosition).getProduct_id()));
                AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartObjs().size());
                if(cartObject.getCart().getDetail().size()>1) {
                    cartObject.getCart().getDetail().remove(quantityUpdatePosition);
                    setAdapterData(cartObject);
                } else {
                    cartObject.getCart().getDetail().remove(quantityUpdatePosition);
                    showNullCaseView("No items in cart");
                    changeViewVisiblity(cartList, View.GONE);
                }


                //AllProducts.getInstance().setCartCount(getCartQuantity());

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
    public void onRequestFailed(String requestTag, Object obj) {
        if (requestTag.equalsIgnoreCase(GET_CART_DETAILS+buyingChannel) || requestTag.equalsIgnoreCase(RequestTags.GET_CART_COMPUTATION+buyingChannel)) {
            showNullCaseView("No items");
        }
        if (zProgressDialog != null)
            zProgressDialog.dismiss();

    }

    @Override
    public void onDestroy() {
        GetRequestManager.getInstance().removeCallbacks(this);
        UploadManager.getInstance().removeCallback(this);
        super.onDestroy();

    }

    public void setAdapterData(CartObject obj) {
        MyCartAdapter adapter = new MyCartAdapter(getActivity(), obj);
        cartList.setAdapter(adapter);
        adapter.setCartEditListener(new MyCartAdapter.cartEditListener() {
            @Override
            public void checkOut(int position) {
                if (getActivity() != null && AppPreferences.isUserLogIn(getActivity())) {
                    ((ProductCheckoutActivity) getActivity()).setOrderSummaryFragmentWithBackstack(bundle);
                } else {
                    Intent intent = new Intent(getActivity(), BaseLoginSignupActivity.class);
                    intent.putExtra("inside", true);
                    getActivity().startActivity(intent);
                }
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
                UploadManager.getInstance().makeAyncRequest(url, QUANTITY_UPDATE+buyingChannel, cartObject.getCart().getDetail().get(position).getSlug(),
                        OBJECT_ADD_TO_CART, null, nameValuePair, null);

            }

            @Override
            public void itemDeleted(int position) {
                if (!AppPreferences.isUserLogIn(getActivity())) {
                    // cartObject.getCart().getDetail().remove(position);
                    //AllProducts.getInstance().setCartCount(getCartQuantity());
                    ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(getActivity()), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
                    if (objs != null) {
                        for (int i = 0; i < objs.size(); i++) {

                            if (objs.get(i).getProductId().equalsIgnoreCase(cartObject.getCart().getDetail().get(position).getProduct_id())) {
                                objs.remove(i);
                            }
                        }
                        if (objs.size() == 0) {
                            objs = null;

                        }
                        GetRequestManager.Update(AppPreferences.getDeviceID(getActivity()), objs, OBJECT_TYPE_NONLOGGED_IN_CART, GetRequestManager.CONSTANT);
                        AllProducts.getInstance().setCartCount(loadLocalCount());

                    }
                    loadCartComputation();
                    //cartObject.getCart().getDetail().remove(position);
                    // setAdapterData(cartObject);
                } else {
                    zProgressDialog = ProgressDialog.show(mActivity, null, "Removing Item. Please wait...");
                    String url = AppApplication.getInstance().getBaseUrl() +
                            "ecommerce/remove-cart/" + cartObject.getCart().getDetail().get(position).getCart_item_id() + "/";
                    quantityUpdatePosition = position;
                    GetRequestManager.getInstance().makeAyncRequest(url, REMOVE_FROM_CART+buyingChannel, OBJECT_TYPE_ITEM_REMOVED);
                }
            }

            @Override
            public void changeAddress() {

            }
        });
    }

    public int loadLocalCount(){
        int count=0;
        ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj>)GetRequestManager.Request(AppPreferences.getDeviceID(getActivity()),RequestTags.NON_LOGGED_IN_CART_CACHE,GetRequestManager.CONSTANT);
        if(objs!=null){
            return objs.size();
        }
        return 0;
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
    public void uploadFinished(int requestType, String objectId, Object data,
                               Object response, boolean status, int parserId) {

        if (zProgressDialog != null)
            zProgressDialog.dismiss();
        if (requestType == QUANTITY_UPDATE) {
            if (status) {
                JSONObject jsonObject = (JSONObject) response;
                try {
                    long price = jsonObject.getInt("price");
                    if (price != -1) {
                        /*cartObject.getCart().setPrice(Float.parseFloat(cartObject.getCart().getPrice()) + price -
                                (Float.parseFloat(cartObject.getCart().getDetail().get(quantityUpdatePosition).getPrice())
                                        * Float.parseFloat(cartObject.getCart().getDetail().get(quantityUpdatePosition).getQuantity())) + "");*/
                        cartObject.getCart().setPrice(Float.parseFloat(cartObject.getCart().getPrice()) + price -
                                (Float.parseFloat(cartObject.getCart().getDetail().get(quantityUpdatePosition).getPrice())
                                        * Float.parseFloat(cartObject.getCart().getDetail().get(quantityUpdatePosition).getQuantity())) + "");
                        cartObject.getCart().setTotal_shipping(Float.parseFloat(cartObject.getCart().getTotal_shipping()) +
                                (updatedQuantity - Integer.parseInt(cartObject.getCart().getDetail().get(quantityUpdatePosition).getQuantity())) *
                                        cartObject.getCart().getDetail().get(quantityUpdatePosition).getIndividualShipping_charge() + "");
                        cartObject.getCart().setTotal_price(Float.parseFloat(cartObject.getCart().getPrice()) +
                                Float.parseFloat(cartObject.getCart().getTotal_shipping()) + "");


                        cartObject.getCart().getDetail().get(quantityUpdatePosition).setQuantity(updatedQuantity + "");
                        setAdapterData(cartObject);
                        ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj>)GetRequestManager.Request(AppPreferences.getDeviceID(getActivity()),RequestTags.NON_LOGGED_IN_CART_CACHE,GetRequestManager.CONSTANT);
                        if(objs!=null){
                            for(int i=0;i<objs.size();i++){
                                if(objs.get(i).getProductId().equalsIgnoreCase(cartObject.getCart().getDetail().get(quantityUpdatePosition).getProduct_id())){
                                    objs.get(i).setQuantity(updatedQuantity);
                                }
                            }
                            GetRequestManager.Update(AppPreferences.getDeviceID(getActivity()), objs,OBJECT_TYPE_NONLOGGED_IN_CART, GetRequestManager.CONSTANT);
                        }
                        //  AllProducts.getInstance().setCartCount(getCartQuantity());
                        // quantityUpdatePosition = -1;
                        //updatedQuantity = -1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else
                Toast.makeText(mActivity, "An error ocurred.", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onDestroyView() {
        UploadManager.getInstance().removeCallback(this);
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroyView();
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {
        if (isAdded() && mActivity != null)
            zProgressDialog = ProgressDialog.show(mActivity, null, "Loading...");
    }
}