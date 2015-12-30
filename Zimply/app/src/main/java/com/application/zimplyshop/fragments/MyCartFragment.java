package com.application.zimplyshop.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.activities.BaseLoginSignupActivity;
import com.application.zimplyshop.activities.ProductCheckoutActivity;
import com.application.zimplyshop.adapters.MyCartAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.CartObject;
import com.application.zimplyshop.baseobjects.NonLoggedInCartObj;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.objects.AllProducts;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.widgets.CustomTextViewBold;
import com.application.zimplyshop.widgets.SpaceItemDecoration;

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
    double requestTime;
    LinearLayout buyLayout;

    boolean isMoveToWishlist;

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
        buyLayout = (LinearLayout) view.findViewById(R.id.payment_layout);
        setLoadingVariables();

        FRAGMENT_PAGE_TYPE = AppConstants.FRAGMENT_TYPE_MY_CART;
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

        UploadManager.getInstance().addCallback(this);
        GetRequestManager.getInstance().addCallbacks(this);

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


    public String getProductBuyingChannels(ArrayList<NonLoggedInCartObj> objs) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < objs.size(); i++) {
            s.append(objs.get(i).getSellingChannel() + "");
            if (i != objs.size() - 1) {
                s.append(".");
            }
        }
        return s.toString();
    }

    public void loadCartComputation() {
        ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(getActivity()), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
        if (objs != null && objs.size() > 0) {
            String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_CART_COMPUTATION + "?src=mob" + "&ids=" + getProductIdStrings(objs) + "&quantity=" + getProductQuantityString(objs) + "&buying_channels=" + getProductBuyingChannels(objs);
            GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.GET_CART_COMPUTATION, OBJECT_TYPE_CART);
        } else {
            showNullCaseView("No items in cart");
            changeViewVisiblity(cartList, View.GONE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public void loadData() {
        requestTime = System.currentTimeMillis();
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_CART_URL + "?src=mob&userid=" + AppPreferences.getUserID(getActivity());
        ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(getActivity()), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
        if (objs != null) {
            url += "&ids=" + getProductIdStrings(objs) + "&quantity=" + getProductQuantityString(objs) + "&buying_channels=" + getProductBuyingChannels(objs);
        }
        GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.GET_CART_DETAILS, OBJECT_TYPE_CART);

    }

    @Override
    public void onResume() {
        if (getActivity() != null) {
            ((ProductCheckoutActivity) getActivity()).setTitleText("My Cart");
        }

        if (AppPreferences.isUserLogIn(getActivity())) {
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
            changeViewVisiblity(buyLayout, View.GONE);
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (zProgressDialog != null)
            zProgressDialog.dismiss();

        if (requestTag.equalsIgnoreCase(GET_CART_DETAILS) || requestTag.equalsIgnoreCase(RequestTags.GET_CART_COMPUTATION)) {
            if (((CartObject) obj).getCart().getDetail() != null && ((CartObject) obj).getCart().getDetail().size() > 0) {
                CommonLib.ZLog("Request Time", "Cart Detail Page Request :" + (System.currentTimeMillis() - requestTime) + " mS");
                CommonLib.writeRequestData("Cart Detail Page Request :" +   (System.currentTimeMillis() - requestTime) + " mS");

                showView();
                changeViewVisiblity(buyLayout, View.VISIBLE);
                changeViewVisiblity(cartList, View.VISIBLE);
                cartObject = (CartObject) obj;
                setAdapterData(cartObject);
                AllProducts.getInstance().setCartCount(getCartQuantity());
            } else {
                showNullCaseView("No Items in cart");
                changeViewVisiblity(cartList, View.GONE);
                changeViewVisiblity(buyLayout, View.GONE);
                AllProducts.getInstance().setCartCount(0);
            }

            if (requestTag.equalsIgnoreCase(GET_CART_DETAILS)) {
                GetRequestManager.Update(AppPreferences.getDeviceID(getActivity()), null, RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
            }

        } else if (requestTag != null && requestTag.equals(REMOVE_FROM_CART)) {
            JSONObject jsonObject = (JSONObject) obj;
            try {
                String message;
                if (isMoveToWishlist) {
                    message = "Successfully added to wishlist";
                } else {
                    message = jsonObject.getString("success");
                }

                Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                // loadData();
                cartObject.getCart().setPrice(cartObject.getCart().getPrice() -
                        (cartObject.getCart().getDetail().get(quantityUpdatePosition).getProduct().getPrice()
                                * cartObject.getCart().getDetail().get(quantityUpdatePosition).getQty()));
                int shippingPrice = cartObject.getCart().getTotal_shipping() -
                        (cartObject.getCart().getDetail().get(quantityUpdatePosition).getShipping_charge()
                                * cartObject.getCart().getDetail().get(quantityUpdatePosition).getQty());

                cartObject.getCart().setTotal_shipping(shippingPrice);

                cartObject.getCart().setTotal_price(cartObject.getCart().getPrice() + shippingPrice);

                AllProducts.getInstance().removeCartItem(cartObject.getCart().getDetail().get(quantityUpdatePosition).getProduct().getId());
                AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartObjs().size());
                if (cartObject.getCart().getDetail().size() > 1) {
                    cartObject.getCart().getDetail().remove(quantityUpdatePosition);
                    setAdapterData(cartObject);

                } else {
                    cartObject.getCart().getDetail().remove(quantityUpdatePosition);
                    showNullCaseView("No items in cart");
                    changeViewVisiblity(cartList, View.GONE);
                    changeViewVisiblity(buyLayout, View.GONE);
                    AllProducts.getInstance().setCartCount(0);
                }


                //AllProducts.getInstance().setCartCount(getCartQuantity());

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(mActivity, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Request for marking favourite
     */
    public void makeLikeRequest(int id) {
        String url = AppApplication.getInstance().getBaseUrl() + MARK_FAVOURITE_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("item_type", ITEM_TYPE_PRODUCT + ""));
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(getActivity())));
        list.add(new BasicNameValuePair("item_id", id + ""));
        UploadManager.getInstance().makeAyncRequest(url, MARK_FAVOURITE_REQUEST_TAG, id + "",
                OBJECT_TYPE_MARKED_FAV, null, list, null);
    }

    public int getCartQuantity() {
        if (cartObject != null && cartObject.getCart() != null && cartObject.getCart().getDetail() != null)
            return cartObject.getCart().getDetail().size();
        return 0;
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (requestTag.equalsIgnoreCase(GET_CART_DETAILS) || requestTag.equalsIgnoreCase(RequestTags.GET_CART_COMPUTATION)) {
            showNullCaseView("No items");
            changeViewVisiblity(buyLayout, View.GONE);
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

    public String getProductIdStringsFromCart() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < cartObject.getCart().getDetail().size(); i++) {
            s.append(cartObject.getCart().getDetail().get(i).getProduct().getId() + "");
            if (i != cartObject.getCart().getDetail().size() - 1) {
                s.append(".");
            }
        }
        return s.toString();
    }

    public String getProductQuantityStringsFromCart() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < cartObject.getCart().getDetail().size(); i++) {
            s.append(cartObject.getCart().getDetail().get(i).getQty() + "");
            if (i != cartObject.getCart().getDetail().size() - 1) {
                s.append(".");
            }
        }
        return s.toString();
    }

    public void setAdapterData(CartObject obj) {
        MyCartAdapter adapter = new MyCartAdapter(getActivity(), obj);
        cartList.setAdapter(adapter);
        adapter.setCartEditListener(new MyCartAdapter.cartEditListener() {
            @Override
            public void checkOut(int position) {
                if (getActivity() != null && AppPreferences.isUserLogIn(getActivity())) {

                    bundle.putString("productids", getProductIdStringsFromCart());
                    bundle.putString("quantity", getProductQuantityStringsFromCart());
                    ((ProductCheckoutActivity) getActivity()).newOrderSummaryFragmentWithBackStack(bundle);
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
                nameValuePair.add(new BasicNameValuePair("product_id", cartObject.getCart().getDetail().get(position).getProduct().getId() + ""));
                nameValuePair.add(new BasicNameValuePair("quantity", quantity + ""));
                nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(mActivity)));
                quantityUpdatePosition = position;
                updatedQuantity = quantity;
                UploadManager.getInstance().makeAyncRequest(url, QUANTITY_UPDATE, cartObject.getCart().getDetail().get(position).getProduct().getSlug(),
                        OBJECT_ADD_TO_CART, null, nameValuePair, null);

            }

            @Override
            public void itemDeleted(final int position) {
                isMoveToWishlist = false;
                final AlertDialog logoutDialog;
                logoutDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Remove Item")
                        .setMessage("Do you wish to remove item from the cart?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeFromCart(position);
                                    }
                                }).setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();
                logoutDialog.show();

            }

            @Override
            public void changeAddress() {

            }

            @Override
            public void itemMovedToWishlist(int position) {
                if (!AppPreferences.isUserLogIn(getActivity())) {

                    Intent loginIntent = new Intent(getActivity(), BaseLoginSignupActivity.class);
                    loginIntent.putExtra("inside", true);
                    startActivity(loginIntent);
                } else {
                    isMoveToWishlist = true;
                    zProgressDialog = ProgressDialog.show(mActivity, null, "Adding to wishlist. Please wait...");
                    String url = AppApplication.getInstance().getBaseUrl() +
                            "ecommerce/remove-cart/" + cartObject.getCart().getDetail().get(position).getCart_item_id() + "/";
                    quantityUpdatePosition = position;
                    GetRequestManager.getInstance().makeAyncRequest(url, REMOVE_FROM_CART, OBJECT_TYPE_ITEM_REMOVED);
                    makeLikeRequest(cartObject.getCart().getDetail().get(position).getProduct().getId());
                }
            }
        });

        ((CustomTextViewBold) view.findViewById(R.id.total_amount)).setText(Html.fromHtml("Total : " + "<font color=#0093b8>" + getResources().getString(R.string.rs_text) + " " + obj.getCart().getTotal_price() + "</font>"));
        ((CustomTextViewBold) view.findViewById(R.id.buy_btn)).setText("Checkout");
        ((CustomTextViewBold) view.findViewById(R.id.buy_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null && AppPreferences.isUserLogIn(getActivity())) {
                    bundle.putString("productids", getProductIdStringsFromCart());
                    bundle.putString("quantity", getProductQuantityStringsFromCart());
                    ((ProductCheckoutActivity) getActivity()).setOrderSummaryFragmentWithBackstack(bundle);
                } else {
                    Intent intent = new Intent(getActivity(), BaseLoginSignupActivity.class);
                    intent.putExtra("inside", true);
                    getActivity().startActivity(intent);
                }
            }
        });
    }

    public void removeFromCart(int position) {
        if (!AppPreferences.isUserLogIn(getActivity())) {
            // cartObject.getCart().getDetail().remove(position);
            //AllProducts.getInstance().setCartCount(getCartQuantity());
            ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(getActivity()), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
            if (objs != null) {
                for (int i = 0; i < objs.size(); i++) {

                    if (Integer.parseInt(objs.get(i).getProductId()) == cartObject.getCart().getDetail().get(position).getProduct().getId()) {
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
            GetRequestManager.getInstance().makeAyncRequest(url, REMOVE_FROM_CART, OBJECT_TYPE_ITEM_REMOVED);
        }
    }

    public int loadLocalCount() {
        int count = 0;
        ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(getActivity()), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
        if (objs != null) {
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
                    int price = jsonObject.getInt("price");
                    if (price != -1) {
                        /*cartObject.getCart().setPrice(Float.parseFloat(cartObject.getCart().getPrice()) + price -
                                (Float.parseFloat(cartObject.getCart().getDetail().get(quantityUpdatePosition).getPrice())
                                        * Float.parseFloat(cartObject.getCart().getDetail().get(quantityUpdatePosition).getQuantity())) + "");*/
                        cartObject.getCart().setPrice(cartObject.getCart().getPrice() + price -
                                (cartObject.getCart().getDetail().get(quantityUpdatePosition).getProduct().getPrice()
                                        * cartObject.getCart().getDetail().get(quantityUpdatePosition).getQty()));
                        cartObject.getCart().setTotal_shipping(cartObject.getCart().getTotal_shipping() +
                                (updatedQuantity - cartObject.getCart().getDetail().get(quantityUpdatePosition).getQty()) *
                                        cartObject.getCart().getDetail().get(quantityUpdatePosition).getShipping_charge());
                        cartObject.getCart().setTotal_price(cartObject.getCart().getPrice() +
                                cartObject.getCart().getTotal_shipping());


                        cartObject.getCart().getDetail().get(quantityUpdatePosition).setQty(updatedQuantity);
                        setAdapterData(cartObject);
                        ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(getActivity()), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
                        if (objs != null) {
                            for (int i = 0; i < objs.size(); i++) {
                                if (Integer.parseInt(objs.get(i).getProductId()) == cartObject.getCart().getDetail().get(quantityUpdatePosition).getProduct().getId()) {
                                    objs.get(i).setQuantity(updatedQuantity);
                                }
                            }
                            GetRequestManager.Update(AppPreferences.getDeviceID(getActivity()), objs, OBJECT_TYPE_NONLOGGED_IN_CART, GetRequestManager.CONSTANT);
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
        if (isAdded() && mActivity != null && requestType == QUANTITY_UPDATE) {
            zProgressDialog = ProgressDialog.show(mActivity, null, "Loading...");
        }
    }
}