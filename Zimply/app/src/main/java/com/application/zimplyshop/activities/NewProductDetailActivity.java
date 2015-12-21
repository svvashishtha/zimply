package com.application.zimplyshop.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.NewProductDetailAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.BaseCartProdutQtyObj;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.baseobjects.NonLoggedInCartObj;
import com.application.zimplyshop.baseobjects.ProductVendorTimeObj;
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
import com.application.zimplyshop.widgets.CustomTextView;
import com.application.zimplyshop.widgets.ScrollCustomizedLayoutManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Umesh Lohani on 12/11/2015.
 */
public class NewProductDetailActivity extends BaseActivity implements AppConstants,RequestTags,GetRequestListener,ObjectTypes,UploadManagerCallback,View.OnClickListener{

    RecyclerView productDetailList;

    String productSlug;

    long productId;

    boolean isScannedProduct;

    int width,height;

    int spaceHeight;
    TextView addToCart,buyNow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_product_detail_activity);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spaceHeight = (getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material)
                +getResources().getDimensionPixelSize(R.dimen.action_bar_offset)-(2*getStatusBarHeight()));
        String[] type = {""};
        if (getIntent().getExtras().containsKey("slug"))
            productSlug = String.valueOf(getIntent().getExtras().get("slug"));
        if (getIntent().getExtras().containsKey("id") )
            productId = getIntent().getExtras().getInt("id");
        if(getIntent().getExtras().getBoolean("is_scanned")){
            isScannedProduct = getIntent().getExtras().getBoolean("is_scanned");
        }
        width = getDisplayMetrics().widthPixels;
        height = getDisplayMetrics().heightPixels;
        productDetailList = (RecyclerView)findViewById(R.id.categories_list);
        productDetailList.setLayoutManager(new ScrollCustomizedLayoutManager(this));
        productDetailList.setBackgroundColor(getResources().getColor(R.color.pager_bg));

        addToCart = (TextView)findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(this);
        buyNow = (TextView)findViewById(R.id.buy_now);
        buyNow.setOnClickListener(this);
        setLoadingVariables();

        GetRequestManager.getInstance().addCallbacks(this);
        UploadManager.getInstance().addCallback(this);
        loadData();
    }
    TextView toolbarTitle;

    TextView cartCount;

    @Override
    protected void onResume() {
        super.onResume();
        if(AllProducts.getInstance().getCartCount() == 0){
            cartCount.setVisibility(View.GONE);
        }else{
            cartCount.setVisibility(View.VISIBLE);
            cartCount.setText(AllProducts.getInstance().getCartCount()+"");
        }

        if(adapter!=null && adapter.getObj()!=null && AllProducts.getInstance().cartContains((int)adapter.getObj().getProduct().getId())){
            addToCart.setText("Go to cart");
        }else{
            addToCart.setText("Add to cart");
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public void addToolbarView(Toolbar toolbar){
        View view = LayoutInflater.from(this).inflate(R.layout.product_custom_action_bar, toolbar, false);
        toolbarTitle = (TextView) view.findViewById(R.id.title);
        if(getIntent()!=null && getIntent().getStringExtra("title")!=null){
            toolbarTitle.setText(getIntent().getStringExtra("title"));
        }
        ((ImageView)view.findViewById(R.id.cart_icon)).setOnClickListener(this);
        ((ImageView)view.findViewById(R.id.share_product)).setOnClickListener(this);
        cartCount = (TextView)view.findViewById(R.id.cart_item_true);
        toolbar.addView(view);
    }

    public void loadData(){
        String url = AppApplication.getInstance().getBaseUrl() + PRODUCT_DESCRIPTION_REQUETS_URL + "?id=" + productId
                + "&width=" + width / 2 + "&thumb=60" + (AppPreferences.isUserLogIn(this) ? "&userid=" + AppPreferences.getUserID(this) : "");
        GetRequestManager.getInstance().makeAyncRequest(url, PRODUCT_DETAIL_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_PRODUCT_DETAIL);
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if(!isDestroyed && requestTag.equalsIgnoreCase(PRODUCT_DETAIL_REQUEST_TAG)){
            showLoadingView();
            (findViewById(R.id.bottom_action_container)).setVisibility(View.GONE);
            changeViewVisiblity(productDetailList, View.GONE);
        }

    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if(!isDestroyed && requestTag.equalsIgnoreCase(PRODUCT_DETAIL_REQUEST_TAG)){
            if (obj instanceof HomeProductObj) {
                HomeProductObj  product = (HomeProductObj) obj;
                addAdapterData(product);
                showView();
                changeViewVisiblity(productDetailList, View.VISIBLE);
            } else {
                showNullCaseView("No Info Available");
            }
        }else if (!isDestroyed && requestTag.equalsIgnoreCase(CHECKPINCODEREQUESTTAG) ) {
            if (progressDialog != null ) {
                progressDialog.dismiss();
            }
            if (((boolean) obj)) {
                if(adapter!=null){
                    adapter.setIsAvailableAtPincode(true);
                }
            } else {
                adapter.setIsAvailableAtPincode(false);
                /*findViewById(R.id.is_available_pincode).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.is_available_pincode)).setTextColor(getResources().getColor(R.color.red_text_color));
                ((TextView) findViewById(R.id.is_available_pincode)).setText("Not available at selected pincode");*/
            }
            CommonLib.hideKeyBoard(this, findViewById(R.id.pincode));
        }
    }
    NewProductDetailAdapter adapter;
    public void addAdapterData(HomeProductObj obj){
        toolbarTitle.setText(obj.getProduct().getName());
        adapter =new NewProductDetailAdapter(this,width,height,obj);
        productDetailList.setAdapter(adapter);
        adapter.setOnViewsClickedListener(new NewProductDetailAdapter.OnViewsClickedListener() {
            @Override
            public void onCheckPincodeClicked(boolean isShowProgress, String pincode) {
                checkPincodeAvailability(isShowProgress, pincode);
            }

            @Override
            public void markReviewRequest(NewProductDetailAdapter.ProductInfoHolder2 holder) {
                NewProductDetailActivity.this.holder = holder;
                makeProductPreviewRequest();
            }

            @Override
            public void onCancelBookingRequest(NewProductDetailAdapter.ProductInfoHolder2 holder) {
               /* final AlertDialog logoutDialog;
                logoutDialog = new AlertDialog.Builder(NewProductDetailActivity.this)
                        .setTitle("Confirm?")
                        .setCancelable(false)
                        .setMessage("Are you sure you want to cancel?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        cancelBooking(adapter.getObj().getVendor().getBook_product_id());
                                    }
                                }).create();
                logoutDialog.show();*/
            }

            @Override
            public void onMarkFavorite() {
                makeLikeRequest();
            }

            @Override
            public void onMarkUnFavorite() {
                makeUnLikeRequest();
            }
        });
        ((ScrollCustomizedLayoutManager)productDetailList.getLayoutManager()).setScrollEnabled(true);
        productDetailList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
               /* NewProductDetailAdapter.ProductInfoHolder2 holder = ( NewProductDetailAdapter.ProductInfoHolder2 )recyclerView.findViewHolderForItemId(adapter.getItemId(2));
                if(holder!=null && holder.mapParent!=null &&holder.mapParent.getTop()==getResources().getDimension(R.dimen.padding_top_66)){
                    recyclerView.canScrollVertically(0);
                    Toast.makeText(NewProductDetailActivity.this,"Header reached",Toast.LENGTH_SHORT).show();
                }*/
                if (adapter.getRefernceHolder() != null) {

                    System.out.println("Scroll Position::" + adapter.getRefernceHolder().mapParent.getTop());
                    if (!AppPreferences.isBookTutorialShown(NewProductDetailActivity.this) && adapter.getRefernceHolder().mapParent.getTop() < spaceHeight && !isViewShown) {
                        ((ScrollCustomizedLayoutManager) productDetailList.getLayoutManager()).setScrollEnabled(false);
                        AppPreferences.setIsBookTutorialShown(NewProductDetailActivity.this,true);
                        showTransparentView();
                    } else {
                        super.onScrolled(recyclerView, dx, dy);
                    }


                } else {
                    super.onScrolled(recyclerView, dx, dy);
                }

            }
        });

        (findViewById(R.id.bottom_action_container)).setVisibility(View.VISIBLE);
        if(AllProducts.getInstance().cartContains((int)adapter.getObj().getProduct().getId())){
            addToCart.setText("Go to cart");
        }else{
            addToCart.setText("Add to cart");
        }
        if(AllProducts.getInstance().vendorIdsContains(adapter.getObj().getVendor().getId())){
            adapter.setIsCancelBookingShown(true);
        }else{
            adapter.setIsCancelBookingShown(false);
        }

        if (AppPreferences.isPincodeSaved(this)) {
            String pincode = AppPreferences.getSavedPincode(this);
            checkPincodeAvailability(false, pincode);
        }


    }

    boolean isViewShown;

    public void showTransparentView(){
        if(adapter!=null && adapter.getRefernceHolder()!=null) {
            isViewShown = true;
            final LinearLayout layout = (LinearLayout)findViewById(R.id.dim_layout);
            layout.setVisibility(View.VISIBLE);
            View view = findViewById(R.id.transparentView);
            NewProductDetailAdapter.ProductInfoHolder2 holder = adapter.getRefernceHolder();

            int viewHeight =holder.mapParent.getHeight();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, viewHeight);
            view.setLayoutParams(lp);
            ((TextView)findViewById(R.id.got_it)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateLayoutViewOut(layout);
                }
            });
            animateLayoutViewIn(layout);
        }
    }

    public void animateLayoutViewIn(View view){
        ObjectAnimator anim = ObjectAnimator.ofFloat(view,View.ALPHA,0,1);
        anim.setDuration(200);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }
    public void animateLayoutViewOut(final View view){
        ObjectAnimator anim = ObjectAnimator.ofFloat(view,View.ALPHA,1,0);
        anim.setDuration(200);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                ((ScrollCustomizedLayoutManager)productDetailList.getLayoutManager()).setScrollEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    NewProductDetailAdapter.ProductInfoHolder2 holder;

    public void makeProductPreviewRequest(){
        String url = AppApplication.getInstance().getBaseUrl() + MARK_PRODUCT_REVIEW_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("product_id", adapter.getObj().getProduct().getId()+""));
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        UploadManager.getInstance().makeAyncRequest(url, MARK_PRODUCT_REVIEW_TAG, adapter.getObj().getProduct().getId() + "",
                OBJECT_TYPE_MARK_PRODUCT_REVIEW, adapter.getObj(), list, null);
    }


    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if(!isDestroyed && requestTag.equalsIgnoreCase(PRODUCT_DETAIL_REQUEST_TAG)){
            showNetworkErrorView();
            showToast("Something went wrong. Please try again");
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(CHECKPINCODEREQUESTTAG) ) {
            if (CommonLib.isNetworkAvailable(NewProductDetailActivity.this)) {
                showToast("Something went wrong. Please try again");
            }
            else {
                showToast("Internet not available. Please try again");
            }

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }
    ProgressDialog  progressDialog;
    public void checkPincodeAvailability(boolean showProgress, String text) {
        if (CommonLib.isNetworkAvailable(this)) {
            if(showProgress)
                progressDialog = ProgressDialog.show(this, null, "Checking availability.Please Wait..");
            AppPreferences.setSavedPincode(this, text);
            AppPreferences.setIsPincodeSaved(this, true);
            String url = AppApplication.getInstance().getBaseUrl() + AppConstants.CHECK_PINCODE + "?pincode=" + text;
            GetRequestManager.getInstance().makeAyncRequest(url, CHECKPINCODEREQUESTTAG, ObjectTypes.OBJECT_TYPE_CHECK_PINCODE);
        } else {
            showToast("No network available");

        }
    }


    boolean isDestroyed;
    @Override
    protected void onDestroy() {
        isDestroyed = true;
        UploadManager.getInstance().removeCallback(this);
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if ( !isDestroyed && requestType == MARK_PRODUCT_REVIEW_TAG) {
            if (status) {

                AllProducts.getInstance().getVendorIds().add(((ProductVendorTimeObj) response).getVendor_id());
                //adapter.getObj().getVendor().setBook_product_id(((ProductVendorTimeObj) response).getBook_product_id());
                newVisitBookedCard();
            } else {
                Toast.makeText(this, ((ErrorObject) response).getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (!isDestroyed && requestType == CANCEL_PRODUCT_REVIEW_TAG) {
            if (progressDialog != null)
                progressDialog.dismiss();
            if (status) {
                AllProducts.getInstance().getVendorIds().remove((Integer) adapter.getObj().getVendor().getId());

                adapter.onBookCancelledSuccessfully(holder);
                // slideViewsLeftToRight(findViewById(R.id.booking_confirm_card),bookAVisitBtn,BOOK_BTN_CLICK);
                showToast("Successfully cancelled");
            } else {
                showToast("Cannot cancel request. Try again");
            }
        }
        if (requestType == ADD_TO_CART_PRODUCT_DETAIL && status && !isDestroyed) {

            String message = "An error occurred. Please try again...";
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            try {

                JSONObject jsonObject = ((JSONObject) response);
                if (jsonObject.getString("success") != null && jsonObject.getString("success").length() > 0)
                    message = jsonObject.getString("success");
                if (message != null) {
                    ((CustomTextView)findViewById(R.id.add_to_cart)).setText("Go To Cart");
                    AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj((int) adapter.getObj().getProduct().getId(), 1));
                    AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartCount() + 1);
                    cartCount.setVisibility(View.VISIBLE);
                    cartCount.setText(AllProducts.getInstance().getCartCount() + "");
                } else if (jsonObject.getString("error") != null && jsonObject.getString("error").length() > 0) {
                    message = jsonObject.getString("error");
                }

                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if ((requestType == MARK_UN_FAVOURITE_REQUEST_TAG || requestType == MARK_FAVOURITE_REQUEST_TAG) && !isDestroyed) {
            /*if (requestType == MARK_FAVOURITE_REQUEST_TAG) {
                if (status) {
                    adapter.getObj().setIs_favourite(true);
                    adapter.getObj().setFavourite_item_id((String) data);
                }
            } else if (requestType == MARK_UN_FAVOURITE_REQUEST_TAG) {
                if (status) {
                    adapter.getObj().setIs_favourite(false);
                }
            }*/
        }
    }

    private void makeUnLikeRequest() {

        String url = AppApplication.getInstance().getBaseUrl() + MARK_UNFAVOURITE_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("favourite_item_id", adapter.getObj().getProduct().getFavourite_item_id()+""));
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        UploadManager.getInstance().makeAyncRequest(url, MARK_UN_FAVOURITE_REQUEST_TAG, adapter.getObj().getProduct().getId() + "",
                OBJECT_TYPE_MARKED_UNFAV, adapter.getObj(), list, null);
    }

    /**
     * Request for marking favourite
     */
    public void makeLikeRequest() {
        String url = AppApplication.getInstance().getBaseUrl() + MARK_FAVOURITE_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("item_type", ITEM_TYPE_PRODUCT + ""));
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        list.add(new BasicNameValuePair("item_id", adapter.getObj().getProduct().getId() + ""));
        UploadManager.getInstance().makeAyncRequest(url, MARK_FAVOURITE_REQUEST_TAG, adapter.getObj().getProduct().getId() + "",
                OBJECT_TYPE_MARKED_FAV, adapter.getObj(), list, null);
    }

    private void newVisitBookedCard() {
        adapter.setIsCancelBookingShown(true);
        adapter.onBookComplete(holder);
        if(!AppPreferences.isFirstBookingDone(this)){
            addBookingConfirmTutorial();
            AppPreferences.setIsFirstBookingDone(this,true);
        }


    }
    public void addBookingConfirmTutorial(){
        findViewById(R.id.booking_tut).setVisibility(View.VISIBLE);
        findViewById(R.id.booking_tut).setClickable(true);
        final ViewPager pager = (ViewPager)findViewById(R.id.booking_confirm_tut);

        // pager.setPageMargin(-(getResources().getDimensionPixelOffset(R.dimen.margin_large)));
        final MyPagerAdapter adapter = new MyPagerAdapter();
        pager.setAdapter(adapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0 || position == 1) {
                    ((TextView) findViewById(R.id.next_title)).setText("NEXT");
                } else {
                    ((TextView) findViewById(R.id.next_title)).setText("GOT IT");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ((TextView) findViewById(R.id.next_title)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TextView) findViewById(R.id.next_title)).getText().toString().equalsIgnoreCase("NEXT")) {
                    if (pager.getCurrentItem() == 0) {
                        pager.setCurrentItem(1);
                    } else if (pager.getCurrentItem() == 1) {
                        pager.setCurrentItem(2);
                    }
                } else {
                    animateLayoutViewOut(findViewById(R.id.booking_tut));
                }
            }
        });
        animateLayoutViewIn(findViewById(R.id.booking_tut));
    }
    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {
        if (!isDestroyed && requestType == CANCEL_PRODUCT_REVIEW_TAG) {
            progressDialog = ProgressDialog.show(this,null,"Booking. Please wait...");
        }else if ((requestType == ADD_TO_CART_PRODUCT_DETAIL || requestType == BUY_NOW) && !isDestroyed) {
            progressDialog = ProgressDialog.show(this, null, "Adding to cart. Please wait");
            // isLoading = true;
        }
    }

    public void showVisitBookedCard(final ProductVendorTimeObj obj){
        View view = findViewById(R.id.booking_confirm_card);
        //view.setVisibility(View.VISIBLE);
        ((CustomTextView)view.findViewById(R.id.address)).setText(obj.getLine1() + "\n" + obj.getCity());
        ((LinearLayout)view.findViewById(R.id.call_customer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + adapter.getObj().getVendor().getAddress().getPhone()));
                startActivity(callIntent);
            }
        });

        ((LinearLayout)view.findViewById(R.id.get_direction_customer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("geo:" + AppApplication.getInstance().lat + "," + AppApplication.getInstance().lon + "?q=" + adapter.getObj().getVendor().getAddress().getLatitude() + "," + adapter.getObj().getVendor().getAddress().getLongitude() + "(" + adapter.getObj().getVendor().getName() + ")");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                /*Intent intent = new Intent(mContext, MapPage.class);
                intent.putExtra("lat", product.getVendor().getReg_add().getLocation().getLatitude());
                intent.putExtra("lon", product.getVendor().getReg_add().getLocation().getLongitude());
                intent.putExtra("name", product.getVendor().getReg_add().getLocation().getName());
                mContext.startActivity(intent);*/
            }
        });
/*
        ((ImageView)view.findViewById(R.id.close_card)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog logoutDialog;
                logoutDialog = new AlertDialog.Builder(NewProductDetailActivity.this)
                        .setTitle("Confirm?")
                        .setCancelable(false)
                        .setMessage("Are you sure you want to cancel?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        cancelBooking(obj);
                                    }
                                }).create();
                logoutDialog.show();

            }
        });*/
        adapter.onBookComplete(holder);


    }
    public void cancelBooking(int bookingID){
        String url = AppApplication.getInstance().getBaseUrl() + REMOVE_PRODUCT_REVIEW_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        list.add(new BasicNameValuePair("book_product_id", bookingID + ""));
        UploadManager.getInstance().makeAyncRequest(url, RequestTags.CANCEL_PRODUCT_REVIEW_TAG, bookingID + "",
                ObjectTypes.OBJECT_TYPE_REMOVE_PRODUCT_REVIEW, adapter.getObj(), list, null);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.retry_layout:
                if(isRequestFailed){
                    loadData();
                }
                break;
            case R.id.add_to_cart:
                if (CommonLib.isNetworkAvailable(NewProductDetailActivity.this)) {

                    String userId = AppPreferences.getUserID(this);
                    if (AppPreferences.isUserLogIn(NewProductDetailActivity.this)) {
                        if (!(addToCart.getText().toString().equalsIgnoreCase("Go To Cart"))) {
                            String url = AppApplication.getInstance().getBaseUrl() + ADD_TO_CART_URL;
                            List<NameValuePair> nameValuePair = new ArrayList<>();
                            nameValuePair.add(new BasicNameValuePair("buying_channel", AppConstants.BUYING_CHANNEL_ONLINE+""));
                            nameValuePair.add(new BasicNameValuePair("product_id", productId + ""));
                            nameValuePair.add(new BasicNameValuePair("quantity", "1"));
                            nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));

                            UploadManager.getInstance().makeAyncRequest(url, ADD_TO_CART_PRODUCT_DETAIL, adapter.getObj().getProduct().getSlug(), OBJECT_ADD_TO_CART, null, nameValuePair, null);

                        } else {
                            Intent intent = new Intent(NewProductDetailActivity.this, ProductCheckoutActivity.class);
                            intent.putExtra("OrderSummaryFragment", false);
                            intent.putExtra("buying_channel",BUYING_CHANNEL_ONLINE);
                            startActivity(intent);
                        }

                    } else {

                        if (!(addToCart.getText().toString().equalsIgnoreCase("Go To Cart"))) {
                            ArrayList<NonLoggedInCartObj> oldObj = ((ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(NewProductDetailActivity.this), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT));
                            if (oldObj == null) {
                                oldObj = new ArrayList<NonLoggedInCartObj>();
                            }
                            NonLoggedInCartObj item = new NonLoggedInCartObj(productId + "", 1,BUYING_CHANNEL_ONLINE);
                            if (oldObj.contains(item)) {
                                showToast("Already added to cart");
                                //Toast.makeText(mContext, "Already added to cart", Toast.LENGTH_SHORT).show();

                            } else {
                                AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartCount() + 1);
                                checkCartCount();
                                ((CustomTextView)findViewById(R.id.add_to_cart)).setText("Go To Cart");
                                oldObj.add(item);
                                GetRequestManager.Update(AppPreferences.getDeviceID(this), oldObj, RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
                                showToast("Successfully added to cart");
                                //  Toast.makeText(mContext, "Successfully added to cart", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Intent intent = new Intent(NewProductDetailActivity.this, ProductCheckoutActivity.class);
                            intent.putExtra("OrderSummaryFragment", false);
                            startActivity(intent);
                        }
                    }

                } else {
                    showToast("No network available");
                    // Toast.makeText(ProductDetailsActivity.this, "No network available", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buy_now:

                if (CommonLib.isNetworkAvailable(NewProductDetailActivity.this)) {

                    if(AppPreferences.isUserLogIn(NewProductDetailActivity.this)){
                        Intent intent =new Intent(NewProductDetailActivity.this,ProductCheckoutActivity.class);
                        intent.putExtra("OrderSummaryFragment", true);
                        intent.putExtra("productids",adapter.getObj().getProduct().getId()+"");
                        intent.putExtra("quantity","1");
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(NewProductDetailActivity.this, BaseLoginSignupActivity.class);
                        intent.putExtra("inside", true);
                        startActivity(intent);
                    }

                } else {
                    showToast("No network available");
                    //Toast.makeText(ProductDetailsActivity.this, "No network available", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.share_product:
                if (adapter!=null && adapter.getObj()!=null) {
                    Intent shareIntent = new Intent(this, SharingOptionsActivity.class);
                    shareIntent.putExtra("title", "Checkout this awesome product ");
                    shareIntent.putExtra("slug", productSlug);
                    shareIntent.putExtra("type_name", AppConstants.ITEM_TYPE_PRODUCT);
                    shareIntent.putExtra("item_name", adapter.getObj().getProduct().getName());
                    shareIntent.putExtra("product_url", "/shop-product/");
                    shareIntent.putExtra("short_url", "www.zimply.in/shop-product/" + productSlug + "?pid=" + productId);
                    startActivity(shareIntent);
                } else {
                    Toast.makeText(this, "Please wait while loading...", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cart_icon:
                Intent intent = new Intent(this, ProductCheckoutActivity.class);
                intent.putExtra("OrderSummaryFragment", false);
                startActivity(intent);
                break;
        }
    }

    public void checkCartCount() {
        if (AllProducts.getInstance().getCartCount() > 0) {
            cartCount.setVisibility(View.VISIBLE);
            cartCount.setText(AllProducts.getInstance().getCartCount() + "");

        } else {
            cartCount.setVisibility(View.GONE);
        }
    }
    public class MyPagerAdapter extends PagerAdapter {

        int[] resId = {R.drawable.book_tut1,R.drawable.book_tut2, R.drawable.book_tut3};

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = LayoutInflater.from(NewProductDetailActivity.this).inflate(R.layout.booking_pager_layout, container,
                    false);
            ImageView img = (ImageView) v.findViewById(R.id.img);
            img.setImageBitmap(
                    CommonLib.getBitmap(NewProductDetailActivity.this, resId[position], width, (height - 3 * getResources().getDimensionPixelSize(R.dimen.z_item_height_48))));
            /*TextView text = (TextView) v.findViewById(R.id.text1);
            text.setVisibility(View.GONE);*/
            //text.setText(texts[position]);
            container.addView(v, 0);
            return v;
        }

       /* @Override
        public float getPageWidth(int position) {
            return 0.9f;
        }*/

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

}