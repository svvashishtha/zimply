package com.application.zimplyshop.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.BookedHistoryAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.BaseCartProdutQtyObj;
import com.application.zimplyshop.baseobjects.BookedProductHistoryObject;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.LatestBookingObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.objects.AllProducts;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.widgets.SpaceItemDecoration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Umesh Lohani on 11/14/2015.
 */
public class BookedForReviewActivity extends BaseActivity implements GetRequestListener, UploadManagerCallback, View.OnClickListener, AppConstants, RequestTags {

    RecyclerView orderList;


    boolean isDestroyed;

    int addToCartId;

    int pastVisiblesItems, visibleItemCount, totalItemCount;

    boolean isLoading, isRequestAllowed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_toolbar_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        orderList = (RecyclerView) findViewById(R.id.categories_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));
        orderList.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_small)));
        GetRequestManager.getInstance().addCallbacks(this);
        UploadManager.getInstance().addCallback(this);
        setLoadingVariables();
        retryLayout.setOnClickListener(this);

        loadData();
    }

    public void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, toolbar, false);
        ((TextView) view.findViewById(R.id.title_textview)).setText("My Bookings");
        toolbar.addView(view);
    }

    String nextUrl;

    public void loadData() {
        String finalUrl;
        if (nextUrl == null) {
            finalUrl = AppApplication.getInstance().getBaseUrl() + AppConstants.BOOKED_PRODUCTS_URL + "?userid=" + AppPreferences.getUserID(this);

        } else {
            finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
        }
        GetRequestManager.getInstance().makeAyncRequest(finalUrl, RequestTags.BOOKED_HISTORY_REQUEST_TAG, ObjectTypes.OBJECT_TYPE_ALL_BOOKED_PRODUCTS);
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if (!isDestroyed && requestType == RequestTags.CANCEL_PRODUCT_REVIEW_TAG) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            if (status) {
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
                                        AllProducts.getInstance().getVendorIds().remove((Integer) ((LatestBookingObject) adapter.getItem(clickedPos)).getVendor().getId());
                                        adapter.removePos(clickedPos);

                                    }
                                }).create();
                logoutDialog.show();
            }
        } else if (!isDestroyed && requestType == ADD_TO_CART_PRODUCT_DETAIL) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (status) {
                showToast("Successfully added to cart");
                AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj(addToCartId, 1));
                AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartCount() + 1);
                adapter.changeAddBtnText(addToCartId);
            } else {
                showToast("Could not add into cart. Try again");
            }
        }
    }

    ProgressDialog progressDialog;

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {
        if (!isDestroyed && requestType == RequestTags.CANCEL_PRODUCT_REVIEW_TAG) {
            progressDialog = ProgressDialog.show(this, null, "Loading.Please Wait..");
        } else if (!isDestroyed && requestType == RequestTags.ADD_TO_CART_PRODUCT_DETAIL) {
            progressDialog = ProgressDialog.show(this, null, "Adding to cart.Please Wait..");
        }
    }


    @Override
    public void onRequestStarted(String requestTag) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(RequestTags.BOOKED_HISTORY_REQUEST_TAG)) {

            if (orderList.getAdapter() == null
                    || orderList.getAdapter().getItemCount() == 0) {
                showLoadingView();
                changeViewVisiblity(orderList, View.GONE);

            } else {

            }
            isLoading = true;

        }

    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(RequestTags.BOOKED_HISTORY_REQUEST_TAG)) {

            if (((BookedProductHistoryObject) obj).getBooks().size() == 0) {
                if (orderList.getAdapter() == null
                        || orderList.getAdapter().getItemCount() == 1) {
                    showNullCaseView("No Bookings");

                } else {
                    showToast("No more Bookings");
                    ((BookedHistoryAdapter) orderList.getAdapter())
                            .removeItem();
                }
                isRequestAllowed = false;
            } else {
                setAdapterData(((BookedProductHistoryObject) obj).getBooks());

                nextUrl = ((BookedProductHistoryObject) obj).getNext_url();

                showView();
                changeViewVisiblity(orderList, View.VISIBLE);
                if (((BookedProductHistoryObject) obj).getBooks().size() < 10) {
                    isRequestAllowed = false;
                    ((BookedHistoryAdapter) orderList.getAdapter())
                            .removeItem();
                } else {
                    isRequestAllowed = true;
                }
            }

            isLoading = false;
        }

    }

    BookedHistoryAdapter adapter;

    int clickedPos;

    public void setAdapterData(ArrayList<LatestBookingObject> objs) {
        if (adapter == null) {
            adapter = new BookedHistoryAdapter(this);
            orderList.setAdapter(adapter);
            adapter.setOnItemClickListener(new BookedHistoryAdapter.OnItemClickListener() {
                @Override
                public void onCancelClick(final int pos, final int bookProductId) {
                    final AlertDialog cancelBooking;
                    cancelBooking = new AlertDialog.Builder(BookedForReviewActivity.this)
                            .setTitle("Success")
                            .setCancelable(false)
                            .setMessage("Booking cancelled successfully")
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            clickedPos = pos;
                                            removeBookedItem(bookProductId);
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    cancelBooking.show();

                }

                @Override
                public void addToCartClick(int pos, int id) {
                    addToCartId = id;
                    addToCart(id);
                }

                @Override
                public void moveToCartActivity() {
                    Intent intent = new Intent(BookedForReviewActivity.this, ProductCheckoutActivity.class);
                    intent.putExtra("buying_channel", BUYING_CHANNEL_OFFLINE);
                    intent.putExtra("OrderSummaryFragment", false);
                    startActivity(intent);
                }
            });
            orderList
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {

                            visibleItemCount = orderList.getLayoutManager()
                                    .getChildCount();
                            totalItemCount = orderList.getLayoutManager()
                                    .getItemCount();
                            pastVisiblesItems = ((LinearLayoutManager) orderList
                                    .getLayoutManager())
                                    .findFirstVisibleItemPosition();

                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount
                                    && !isLoading && isRequestAllowed) {
                                loadData();
                            }
                            //scrollToolbarAndHeaderBy(-dy);

                        }

                        @Override
                        public void onScrollStateChanged(
                                RecyclerView recyclerView, int newState) {
                            new ImageLoaderManager(BookedForReviewActivity.this)
                                    .setScrollState(newState);
                            super.onScrollStateChanged(recyclerView, newState);
                        }
                    });

        }
        adapter.addData(objs);
    }

    public void addToCart(int id) {
        String url = AppApplication.getInstance().getBaseUrl() + ADD_TO_CART_URL;
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair("buying_channel", AppConstants.BUYING_CHANNEL_OFFLINE + ""));
        nameValuePair.add(new BasicNameValuePair("product_id", id + ""));
        nameValuePair.add(new BasicNameValuePair("quantity", "1"));
        nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));

        UploadManager.getInstance().makeAyncRequest(url, ADD_TO_CART_PRODUCT_DETAIL, id + "", ObjectTypes.OBJECT_ADD_TO_CART, null, nameValuePair, null);

    }

    public void removeBookedItem(int productId) {
        String url = AppApplication.getInstance().getBaseUrl() + REMOVE_PRODUCT_REVIEW_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        list.add(new BasicNameValuePair("book_product_id", productId + ""));
        UploadManager.getInstance().makeAyncRequest(url, RequestTags.CANCEL_PRODUCT_REVIEW_TAG, productId + "",
                ObjectTypes.OBJECT_TYPE_REMOVE_PRODUCT_REVIEW, productId, list, null);
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(RequestTags.BOOKED_HISTORY_REQUEST_TAG)) {

            if (orderList.getAdapter() == null
                    || orderList.getAdapter().getItemCount() == 1) {
                showNetworkErrorView();
                changeViewVisiblity(orderList, View.GONE);

            } else {
                if (((ErrorObject) obj).getErrorCode() == 500) {
                    showToast("Could not load more data");

                } else {
                    showToast(((ErrorObject) obj).getErrorMessage());

                }

                ((BookedHistoryAdapter) orderList.getAdapter()).removeItem();
                isRequestAllowed = false;
            }
            isLoading = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_layout:
                if (isRequestFailed) {
                    loadData();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        UploadManager.getInstance().removeCallback(this);
        super.onDestroy();

    }
}
