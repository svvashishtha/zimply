package com.application.zimplyshop.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.NewProductDetailAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.BaseCartProdutQtyObj;
import com.application.zimplyshop.baseobjects.BaseProductListObject;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.baseobjects.NonLoggedInCartObj;
import com.application.zimplyshop.baseobjects.ProductVendorTimeObj;
import com.application.zimplyshop.baseobjects.SimilarProductsListObject;
import com.application.zimplyshop.db.RecentProductsDBWrapper;
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
import com.application.zimplyshop.utils.ZTracker;
import com.application.zimplyshop.widgets.CustomTextView;
import com.application.zimplyshop.widgets.ScrollCustomizedLayoutManager;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Umesh Lohani on 12/11/2015.
 */
public class NewProductDetailActivity extends BaseActivity implements AppConstants, RequestTags, GetRequestListener, ObjectTypes, UploadManagerCallback, View.OnClickListener {

    RecyclerView productDetailList;

    String productSlug;

    public long productId;

    boolean isScannedProduct;

    int width, height, widthSimilarProducts;
    double requestTime;
    int spaceHeight;
    TextView addToCart, buyNow;

    boolean isShared;
    private int picncodePosition;

    boolean isSimilarProductsLoaded;

    //    Google analytics ecommerce
    String productActionListName, screenName, actionPerformed;
    int position;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_product_detail_activity);

        isShared = getIntent().getBooleanExtra("is_shared", false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        PAGE_TYPE = AppConstants.PAGE_TYPE_NETWORK_NO_WIFI;
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spaceHeight = (getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material)
                + getResources().getDimensionPixelSize(R.dimen.action_bar_offset) - (2 * getStatusBarHeight()));
        String[] type = {""};
        if (getIntent().getExtras().containsKey("slug"))
            productSlug = String.valueOf(getIntent().getExtras().get("slug"));
        if (getIntent().getExtras().containsKey("id"))
            productId = getIntent().getExtras().getInt("id");
        if (getIntent().getExtras().getBoolean("is_scanned")) {
            isScannedProduct = getIntent().getExtras().getBoolean("is_scanned");
        }
        width = getDisplayMetrics().widthPixels;
        widthSimilarProducts = (getDisplayMetrics().widthPixels - (int) (2 * getResources()
                .getDimension(R.dimen.font_small))) / 2;
        height = getDisplayMetrics().heightPixels;
        productDetailList = (RecyclerView) findViewById(R.id.categories_list);
        productDetailList.setLayoutManager(new ScrollCustomizedLayoutManager(this));
        productDetailList.setBackgroundColor(getResources().getColor(R.color.pager_bg));

        //        GA Ecommerce
        if (getIntent().hasExtra("productActionListName"))
            productActionListName = getIntent().getExtras().getString("productActionListName");
        if (getIntent().hasExtra("screenName"))
            screenName = getIntent().getExtras().getString("screenName");
        if (getIntent().hasExtra("actionPerformed"))
            actionPerformed = getIntent().getExtras().getString("actionPerformed");
        position = getIntent().getExtras().getInt("position", -1);

        addToCart = (TextView) findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(this);
        buyNow = (TextView) findViewById(R.id.buy_now);
        buyNow.setOnClickListener(this);
        setLoadingVariables();
        retryLayout.setOnClickListener(this);
        findViewById(R.id.null_case_image).setOnClickListener(this);
        GetRequestManager.getInstance().addCallbacks(this);
        UploadManager.getInstance().addCallback(this);
        loadData();
    }


    TextView toolbarTitle;

    TextView cartCount;

    @Override
    protected void onResume() {
        super.onResume();
        if (AllProducts.getInstance().getCartCount() == 0) {
            cartCount.setVisibility(View.GONE);
        } else {
            cartCount.setVisibility(View.VISIBLE);
            cartCount.setText(AllProducts.getInstance().getCartCount() + "");
        }

        if (adapter != null && adapter.getObj() != null && AllProducts.getInstance().cartContains((int) adapter.getObj().getProduct().getId())) {
            addToCart.setText("Go to cart");
        } else {
            addToCart.setText("Add to cart");
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (adapter != null) {
                    adapter.notifyItemChanged(1);
                }
            }
        }, 500);
        if (adapter != null && !AppPreferences.isUserLogIn(this)) {
            new GetDataFromCache().execute();
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

    public void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(R.layout.product_custom_action_bar, toolbar, false);
        toolbarTitle = (TextView) view.findViewById(R.id.title);
        if (getIntent() != null && getIntent().getStringExtra("title") != null) {
            toolbarTitle.setText(getIntent().getStringExtra("title"));
        }
        ((ImageView) view.findViewById(R.id.cart_icon)).setOnClickListener(this);
        ((ImageView) view.findViewById(R.id.share_product)).setOnClickListener(this);
        cartCount = (TextView) view.findViewById(R.id.cart_item_true);
        toolbar.addView(view);
    }

    public void loadData() {
        requestTime = System.currentTimeMillis();
        String url = AppApplication.getInstance().getBaseUrl() + PRODUCT_DESCRIPTION_REQUETS_URL + "?id=" + productId
                + "&width=" + (width / 2) + "&thumb=60" + (AppPreferences.isUserLogIn(this) ? "&userid=" + AppPreferences.getUserID(this) : "");
        GetRequestManager.getInstance().makeAyncRequest(url, PRODUCT_DETAIL_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_PRODUCT_DETAIL);
    }

    public void loadSimilarProductsRequest() {
        String url = AppApplication.getInstance().getBaseUrl() + PRODUCT_DESCRIPTION_SIMILAR_PRODUCTS_URL + "?id=" + productId
                + "&width=" + widthSimilarProducts + "&size=10&page=1";
        GetRequestManager.getInstance().makeAyncRequest(url, PRODUCT_DETAIL_SIMILAR_PRODUCTS_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_PRODUCT_DETAIL_SIMILAR_PRODUCTS);
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(PRODUCT_DETAIL_REQUEST_TAG)) {
            showLoadingView();
            (findViewById(R.id.bottom_action_container)).setVisibility(View.GONE);
            changeViewVisiblity(productDetailList, View.GONE);
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(PRODUCT_DETAIL_SIMILAR_PRODUCTS_REQUEST_TAG)) {
            isLoading = true;
        }

    }

    boolean isCartChecked;

    @Override
    public void userDataReceived() {
        isCartChecked = true;
        if (AllProducts.getInstance().getCartCount() == 0) {
            cartCount.setVisibility(View.GONE);
        } else {
            cartCount.setVisibility(View.VISIBLE);
            cartCount.setText(AllProducts.getInstance().getCartCount() + "");
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(PRODUCT_DETAIL_REQUEST_TAG)) {
            CommonLib.ZLog("Request Time", "Product Detail Page Request :" + (System.currentTimeMillis() - requestTime) + " mS");
            CommonLib.writeRequestData("Product Detail Page Request :" + (System.currentTimeMillis() - requestTime) + " mS");
            if (obj instanceof HomeProductObj) {
                HomeProductObj product = (HomeProductObj) obj;
                addAdapterData(product);
                showView();
                changeViewVisiblity(productDetailList, View.VISIBLE);

                //log GA event of Product View

                ZTracker.logGaCustomEvent(NewProductDetailActivity.this, "Product Description", product.getProduct().getName(), product.getProduct().getCategory(), product.getProduct().getSku());

                //   ZTracker.logGaProductEvent(NewProductDetailActivity.this, product.getProduct().getName(), product.getProduct().getCategory(), product.getProduct().getSku());

//                GA Ecommerce
                ZTracker.logGAEcommerceProductClickAction(this, screenName, product.getProduct().getId() + "", product.getProduct().getName(), product.getProduct().getCategory(), product.getProduct().getSlug(), null, position, product.getProduct().getPrice(), productActionListName, actionPerformed);

                // loadSimilarProductsRequest();

            } else {
                showNullCaseView("No Info Available");
            }
            if (isCartChecked && isShared) {
                if (AppPreferences.isUserLogIn(this)) {
                    loadUserData();
                } else {
                    ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(this), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
                    if (objs != null) {
                        int count = 0;
                        for (NonLoggedInCartObj cObj : objs) {
                            AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj(Integer.parseInt(cObj.getProductId()), cObj.getQuantity()));
                        }
                        AllProducts.getInstance().setCartCount(objs.size());

                    }
                }
            }
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(CHECKPINCODEREQUESTTAG)) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (((boolean) obj)) {
                if (adapter != null) {
                    adapter.setIsAvailableAtPincode(true, picncodePosition);
                }
            } else {
                adapter.setIsAvailableAtPincode(false, picncodePosition);
                /*findViewById(R.id.is_available_pincode).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.is_available_pincode)).setTextColor(getResources().getColor(R.color.red_text_color));
                ((TextView) findViewById(R.id.is_available_pincode)).setText("Not available at selected pincode");*/
            }
            CommonLib.hideKeyBoard(this, findViewById(R.id.pincode));
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(PRODUCT_DETAIL_SIMILAR_PRODUCTS_REQUEST_TAG)) {
            isSimilarProductsLoaded = true;
            if (adapter != null) {
                adapter.addSimilarProducts(((SimilarProductsListObject) obj).getProducts());
            }
            if (AppPreferences.isUserLogIn(this)) {
                loadRecentlyViewed();
            } else {
                new GetDataFromCache().execute();
            }
            isLoading = false;
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(RECENT_PRODUCT_REQUEST)) {

            if (adapter != null) {
                adapter.addRecentlyViewed(((SimilarProductsListObject) obj).getProducts());
                adapter.setIsFooterRemoved(true);
            }
        }
    }

    NewProductDetailAdapter adapter;

    boolean isLoading, isRequestAllowed;

    public void addAdapterData(HomeProductObj obj) {
        toolbarTitle.setText(obj.getProduct().getName());
        adapter = new NewProductDetailAdapter(this, width, height, obj);
        productDetailList.setAdapter(adapter);
        adapter.setOnViewsClickedListener(new NewProductDetailAdapter.OnViewsClickedListener() {
            @Override
            public void onCheckPincodeClicked(boolean isShowProgress, String pincode, int position) {
                checkPincodeAvailability(isShowProgress, pincode);
                picncodePosition = position;
            }

            @Override
            public void markReviewRequest(NewProductDetailAdapter.ProductInfoHolder2 holder) {
                NewProductDetailActivity.this.holder = holder;
                makeProductPreviewRequest();
            }

            @Override
            public void onCancelBookingRequest(NewProductDetailAdapter.ProductInfoHolder2 holder) {

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
        ((ScrollCustomizedLayoutManager) productDetailList.getLayoutManager()).setScrollEnabled(true);
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

                visibleItemCount = productDetailList.getLayoutManager()
                        .getChildCount();
                totalItemCount = productDetailList.getLayoutManager()
                        .getItemCount();
                pastVisiblesItems = ((LinearLayoutManager) productDetailList
                        .getLayoutManager())
                        .findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount
                        && !isLoading) {
                    if (!isSimilarProductsLoaded) {
                        loadSimilarProductsRequest();
                    }
                }

                if (adapter.getRefernceHolder() != null) {

                    System.out.println("Scroll Position::" + adapter.getRefernceHolder().mapParent.getTop());
                    if (!AppPreferences.isBookTutorialShown(NewProductDetailActivity.this) && adapter.getRefernceHolder().mapParent.getTop() < spaceHeight && !isViewShown) {
                        ((ScrollCustomizedLayoutManager) productDetailList.getLayoutManager()).setScrollEnabled(false);
                        AppPreferences.setIsBookTutorialShown(NewProductDetailActivity.this, true);
                        showTransparentView();
                    } else {
                        super.onScrolled(recyclerView, dx, dy);
                    }
                } else {
                    super.onScrolled(recyclerView, dx, dy);
                }

            }
        });
        if ((int) adapter.getObj().getProduct().getAvailable_quantity() == 0) {
            (findViewById(R.id.out_of_stock_layout)).setVisibility(View.VISIBLE);
            (findViewById(R.id.bottom_action_container)).setVisibility(View.GONE);
            ((EditText) findViewById(R.id.search_text)).setText(AppPreferences.getUserEmail(this));
        } else {
            (findViewById(R.id.out_of_stock_layout)).setVisibility(View.GONE);
            (findViewById(R.id.bottom_action_container)).setVisibility(View.VISIBLE);
        }

        (findViewById(R.id.notify_me_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((EditText) findViewById(R.id.search_text)).getText().toString().trim().length() > 0) {
                    if (checkEmailFormat(((EditText) findViewById(R.id.search_text)).getText().toString())) {
                        notifyMeAbout(((EditText) findViewById(R.id.search_text)).getText().toString());
                    } else {
                        Toast.makeText(NewProductDetailActivity.this, "Pease enter a valid email address", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NewProductDetailActivity.this, "Please enter an email address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (AllProducts.getInstance().cartContains((int) adapter.getObj().getProduct().getId())) {
            addToCart.setText("Go to cart");
        } else {
            addToCart.setText(getResources().getString(R.string.add_to_cart));
        }
        if (AllProducts.getInstance().vendorIdsContains(adapter.getObj().getVendor().getId())) {
            adapter.setIsCancelBookingShown(true);
        } else {
            adapter.setIsCancelBookingShown(false);
        }

        if (AppPreferences.isPincodeSaved(this)) {
            String pincode = AppPreferences.getSavedPincode(this);
            checkPincodeAvailability(false, pincode);
        }


    }


    public void loadRecentlyViewed() {
        int width = (getDisplayMetrics().widthPixels - (3 * getResources().getDimensionPixelSize(R.dimen.margin_small))) / 3;
        String finalUrl = AppApplication.getInstance().getBaseUrl() + PRODUCT_DESCRIPTION_RECENT_PRODUCTS_URL + "?userid=" + AppPreferences.getUserID(this)
                + "&width=" + width + "&size=5&page=1";
        GetRequestManager.getInstance().makeAyncRequest(finalUrl, RECENT_PRODUCT_REQUEST,
                ObjectTypes.OBJECT_TYPE_PRODUCT_DETAIL_SIMILAR_PRODUCTS);
    }


    private boolean checkEmailFormat(CharSequence target) {

        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();

        }
    }

    public void notifyMeAbout(String emailId) {
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.NOTIFY_ME_URL;

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("product_id", adapter.getObj().getProduct().getId() + ""));
        list.add(new BasicNameValuePair("email", emailId));
        UploadManager.getInstance().makeAyncRequest(url, NOTIFY_ME_TAG, adapter.getObj().getProduct().getId() + "",
                OBJECT_TYPE_NOTIFY_ME, adapter.getObj(), list, null);
    }

    boolean isViewShown;

    public void showTransparentView() {
        if (adapter != null && adapter.getRefernceHolder() != null) {
            isViewShown = true;
            final LinearLayout layout = (LinearLayout) findViewById(R.id.dim_layout);
            layout.setVisibility(View.VISIBLE);
            View view = findViewById(R.id.transparentView);
            NewProductDetailAdapter.ProductInfoHolder2 holder = adapter.getRefernceHolder();

            int viewHeight = holder.mapParent.getHeight();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, viewHeight);
            view.setLayoutParams(lp);
            ((TextView) findViewById(R.id.got_it)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateLayoutViewOut(layout);
                }
            });
            animateLayoutViewIn(layout);
        }
    }

    public void animateLayoutViewIn(View view) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, View.ALPHA, 0, 1);
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

    public void animateLayoutViewOut(final View view) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, View.ALPHA, 1, 0);
        anim.setDuration(200);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                ((ScrollCustomizedLayoutManager) productDetailList.getLayoutManager()).setScrollEnabled(true);
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

    public void makeProductPreviewRequest() {
        ZTracker.logGaCustomEvent(NewProductDetailActivity.this, "Book-Store-Visit", adapter.getObj().getProduct().getName(), adapter.getObj().getProduct().getCategory(), adapter.getObj().getProduct().getSku());
        String url = AppApplication.getInstance().getBaseUrl() + MARK_PRODUCT_REVIEW_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("product_id", adapter.getObj().getProduct().getId() + ""));
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        UploadManager.getInstance().makeAyncRequest(url, MARK_PRODUCT_REVIEW_TAG, adapter.getObj().getProduct().getId() + "",
                OBJECT_TYPE_MARK_PRODUCT_REVIEW, adapter.getObj(), list, null);
    }


    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(PRODUCT_DETAIL_REQUEST_TAG)) {
            showNetworkErrorView();
            isRequestFailed = true;
            showToast("Something went wrong. Please try again");
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(CHECKPINCODEREQUESTTAG)) {
            if (CommonLib.isNetworkAvailable(NewProductDetailActivity.this)) {
                showToast("Something went wrong. Please try again");
            } else {
                showToast("Internet not available. Please try again");
            }

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(PRODUCT_DETAIL_SIMILAR_PRODUCTS_REQUEST_TAG)) {
            if (AppPreferences.isUserLogIn(this)) {
                loadRecentlyViewed();
            } else {
                new GetDataFromCache().execute();
            }
            isLoading = false;
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(RECENT_PRODUCT_REQUEST)) {

            if (adapter != null) {
                adapter.setIsFooterRemoved(true);
            }
        }
    }

    ProgressDialog progressDialog;

    public void checkPincodeAvailability(boolean showProgress, String text) {
        if (CommonLib.isNetworkAvailable(this)) {
            if (showProgress)
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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if (!isDestroyed && requestType == MARK_PRODUCT_REVIEW_TAG) {
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
        } else if (requestType == ADD_TO_CART_PRODUCT_DETAIL && status && !isDestroyed) {

            String message = "An error occurred. Please try again...";
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            try {

                JSONObject jsonObject = ((JSONObject) response);
                if (jsonObject.getString("success") != null && jsonObject.getString("success").length() > 0)
                    message = jsonObject.getString("success");
                if (message != null) {
                    ((CustomTextView) findViewById(R.id.add_to_cart)).setText("Go To Cart");
                    AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj((int) adapter.getObj().getProduct().getId(), 1));
                    AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartCount() + 1);
                    cartCount.setVisibility(View.VISIBLE);
                    cartCount.setText(AllProducts.getInstance().getCartCount() + "");

                    if (CommonLib.isNetworkAvailable(NewProductDetailActivity.this)) {
                        Product product = new Product()
                                .setId(adapter.getObj().getProduct().getId() + "")
                                .setName(adapter.getObj().getProduct().getName())
                                .setCategory(adapter.getObj().getProduct().getCategory())
                                .setBrand(adapter.getObj().getVendor().getName())
                                .setPrice(adapter.getObj().getProduct().getPrice())
                                .setQuantity(1);
// Add the step number and additional info about the checkout to the action.
                        ProductAction productAction = new ProductAction(ProductAction.ACTION_ADD)
                                .setCheckoutStep(1)
                                .setCheckoutOptions("Add to cart");
                        ZTracker.checkOutGaEvents(productAction, product, getApplicationContext());
                    }
                } else if (jsonObject.getString("error") != null && jsonObject.getString("error").length() > 0) {
                    message = jsonObject.getString("error");
                }

                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        } else if (requestType == ADD_TO_CART_PRODUCT_DETAIL_COVERT && status && !isDestroyed) {
            try {
                JSONObject jsonObject = ((JSONObject) response);
                String message = null;
                if (jsonObject.getString("success") != null && jsonObject.getString("success").length() > 0)
                    message = jsonObject.getString("success");
                if (message != null) {
                    ((CustomTextView) findViewById(R.id.add_to_cart)).setText("Go To Cart");
                    AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj((int) adapter.getObj().getProduct().getId(), 1));
                    AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartCount() + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if ((requestType == MARK_UN_FAVOURITE_REQUEST_TAG || requestType == MARK_FAVOURITE_REQUEST_TAG) && !isDestroyed) {
            if (requestType == MARK_FAVOURITE_REQUEST_TAG) {
                if (status) {
                    adapter.getObj().getProduct().setIs_favourite(true);
                    adapter.getObj().getProduct().setFavourite_item_id((int) data);
                }
            } else if (requestType == MARK_UN_FAVOURITE_REQUEST_TAG) {
                if (status) {
                    adapter.getObj().getProduct().setIs_favourite(false);
                }
            }
        } else if (requestType == NOTIFY_ME_TAG && !isDestroyed) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (status) {
                Toast.makeText(this, "You will be notified once the item is available.",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Could not process request. Please try again.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makeUnLikeRequest() {

        String url = AppApplication.getInstance().getBaseUrl() + MARK_UNFAVOURITE_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("favourite_item_id", adapter.getObj().getProduct().getFavourite_item_id() + ""));
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        UploadManager.getInstance().makeAyncRequest(url, MARK_UN_FAVOURITE_REQUEST_TAG, adapter.getObj().getProduct().getId() + "",
                OBJECT_TYPE_MARKED_UNFAV, adapter.getObj(), list, null);
    }

    /**
     * Request for marking favourite
     */
    public void makeLikeRequest() {
        ZTracker.logGaCustomEvent(NewProductDetailActivity.this, "Add-To-Wishlist", adapter.getObj().getProduct().getName(), adapter.getObj().getProduct().getCategory(), adapter.getObj().getProduct().getSku());
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
        if (!AppPreferences.isFirstBookingDone(this)) {
            addBookingConfirmTutorial();
            AppPreferences.setIsFirstBookingDone(this, true);
        }


    }

    public void addBookingConfirmTutorial() {
        findViewById(R.id.booking_tut).setVisibility(View.VISIBLE);
        findViewById(R.id.booking_tut).setClickable(true);
        final ViewPager pager = (ViewPager) findViewById(R.id.booking_confirm_tut);

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
            progressDialog = ProgressDialog.show(this, null, "Booking. Please wait...");
        } else if ((requestType == ADD_TO_CART_PRODUCT_DETAIL || requestType == BUY_NOW) && !isDestroyed) {
            progressDialog = ProgressDialog.show(this, null, "Adding to cart. Please wait");
            // isLoading = true;
        } else if (!isDestroyed && requestType == NOTIFY_ME_TAG) {
            progressDialog = ProgressDialog.show(this, null, "Loading. Please wait...");
        }
    }

    public void showVisitBookedCard(final ProductVendorTimeObj obj) {
        View view = findViewById(R.id.booking_confirm_card);
        //view.setVisibility(View.VISIBLE);
        ((CustomTextView) view.findViewById(R.id.address)).setText(obj.getLine1() + "\n" + obj.getCity());
        ((LinearLayout) view.findViewById(R.id.call_customer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + adapter.getObj().getVendor().getAddress().getPhone()));
                startActivity(callIntent);
            }
        });

        ((LinearLayout) view.findViewById(R.id.get_direction_customer)).setOnClickListener(new View.OnClickListener() {
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

    public void cancelBooking(int bookingID) {
        String url = AppApplication.getInstance().getBaseUrl() + REMOVE_PRODUCT_REVIEW_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        list.add(new BasicNameValuePair("book_product_id", bookingID + ""));
        UploadManager.getInstance().makeAyncRequest(url, RequestTags.CANCEL_PRODUCT_REVIEW_TAG, bookingID + "",
                ObjectTypes.OBJECT_TYPE_REMOVE_PRODUCT_REVIEW, adapter.getObj(), list, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.null_case_image:
            case R.id.retry_layout:
                if (isRequestFailed) {
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
                            nameValuePair.add(new BasicNameValuePair("buying_channel", AppConstants.BUYING_CHANNEL_ONLINE + ""));
                            nameValuePair.add(new BasicNameValuePair("product_id", productId + ""));
                            nameValuePair.add(new BasicNameValuePair("quantity", "1"));
                            nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));

                            UploadManager.getInstance().makeAyncRequest(url, ADD_TO_CART_PRODUCT_DETAIL, adapter.getObj().getProduct().getSlug(), OBJECT_ADD_TO_CART, null, nameValuePair, null);

                        } else {
                            Intent intent = new Intent(NewProductDetailActivity.this, ProductCheckoutActivity.class);
                            intent.putExtra("OrderSummaryFragment", false);
                            intent.putExtra("buying_channel", BUYING_CHANNEL_ONLINE);
                            startActivity(intent);

                        }

                    } else {

                        if (!(addToCart.getText().toString().equalsIgnoreCase("Go To Cart"))) {
                            ArrayList<NonLoggedInCartObj> oldObj = ((ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(NewProductDetailActivity.this), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT));
                            if (oldObj == null) {
                                oldObj = new ArrayList<NonLoggedInCartObj>();
                            }
                            NonLoggedInCartObj item = new NonLoggedInCartObj(productId + "", 1, BUYING_CHANNEL_ONLINE);
                            if (oldObj.contains(item)) {
                                showToast("Already added to cart");
                                //Toast.makeText(mContext, "Already added to cart", Toast.LENGTH_SHORT).show();

                            } else {
                                AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartCount() + 1);
                                checkCartCount();
                                ((CustomTextView) findViewById(R.id.add_to_cart)).setText("Go To Cart");
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
                try {
                    ZTracker.logGaCustomEvent(NewProductDetailActivity.this, "Add-To-Cart", adapter.getObj().getProduct().getName(), adapter.getObj().getProduct().getCategory(), adapter.getObj().getProduct().getSku());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.buy_now:

                if (CommonLib.isNetworkAvailable(NewProductDetailActivity.this)) {
                    if (!AllProducts.getInstance().cartContains(productId)) {
                        String url = AppApplication.getInstance().getBaseUrl() + ADD_TO_CART_URL;
                        List<NameValuePair> nameValuePair = new ArrayList<>();
                        nameValuePair.add(new BasicNameValuePair("buying_channel", AppConstants.BUYING_CHANNEL_ONLINE + ""));
                        nameValuePair.add(new BasicNameValuePair("product_id", productId + ""));
                        nameValuePair.add(new BasicNameValuePair("quantity", "1"));
                        nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));

                        UploadManager.getInstance().makeAyncRequest(url, ADD_TO_CART_PRODUCT_DETAIL_COVERT, adapter.getObj().getProduct().getSlug(), OBJECT_ADD_TO_CART, null, nameValuePair, null);
                    }
// Add the step number and additional info about the checkout to the action.


                    Product product = new Product()
                            .setId(adapter.getObj().getProduct().getId() + "")
                            .setName(adapter.getObj().getProduct().getName())
                            .setCategory(adapter.getObj().getProduct().getCategory())
                            .setBrand(adapter.getObj().getVendor().getName())
                            .setPrice(adapter.getObj().getProduct().getPrice())
                            .setQuantity(1);
// Add the step number and additional info about the checkout to the action.
                    ProductAction productAction = new ProductAction(ProductAction.ACTION_ADD)
                            .setCheckoutStep(2)
                            .setCheckoutOptions("Buy Now");
                    ZTracker.checkOutGaEvents(productAction, product, getApplicationContext());
                    if (AppPreferences.isUserLogIn(NewProductDetailActivity.this)) {
                        Intent intent = new Intent(NewProductDetailActivity.this, ProductCheckoutActivity.class);
                        intent.putExtra("OrderSummaryFragment", true);
                        intent.putExtra("productids", adapter.getObj().getProduct().getId() + "");
                        intent.putExtra("quantity", "1");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(NewProductDetailActivity.this, BaseLoginSignupActivity.class);
                        intent.putExtra("inside", true);
                        startActivity(intent);
                    }
                } else {
                    showToast("No network available");
                    //Toast.makeText(ProductDetailsActivity.this, "No network available", Toast.LENGTH_SHORT).show();
                }
                ZTracker.logGaCustomEvent(NewProductDetailActivity.this, "Buy-Now", adapter.getObj().getProduct().getName(), adapter.getObj().getProduct().getCategory(), adapter.getObj().getProduct().getSku());
                break;
            case R.id.share_product:
                if (adapter != null && adapter.getObj() != null) {
                    Intent shareIntent = new Intent(this, SharingOptionsActivity.class);
                    shareIntent.putExtra("title", "Checkout this awesome product ");
                    shareIntent.putExtra("slug", productSlug);
                    shareIntent.putExtra("type_name", AppConstants.ITEM_TYPE_PRODUCT);
                    shareIntent.putExtra("item_name", adapter.getObj().getProduct().getName());
                    shareIntent.putExtra("product_url", "/shop-product/");
                    shareIntent.putExtra("short_url", "www.zimply.in/shop-product/" + productSlug + "?pid=" + productId);
                    ZTracker.logGaCustomEvent(NewProductDetailActivity.this, "Share-Product", adapter.getObj().getProduct().getName(), adapter.getObj().getProduct().getCategory(), adapter.getObj().getProduct().getSku());
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

    public void openSimilarProductsActivity() {
        Intent i = new Intent(this, SimilarProductsActivity.class);
        i.putExtra("productid", productId);
        startActivity(i);
    }

    public void openRecentlyViewed() {
        Intent i = new Intent(this, RecentProductsActivity.class);

        startActivity(i);
    }

    public class MyPagerAdapter extends PagerAdapter {

        int[] resId = {R.drawable.book_tut2, R.drawable.book_tut1, R.drawable.book_tut3};

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


    @Override
    public void onBackPressed() {
        if (isShared) {
            Intent intent = new Intent(this, HomeActivity.class);
            this.finish();
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }

    public class GetDataFromCache extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... params) {
            ArrayList<BaseProductListObject> result = null;
            try {
                int userId = 1;
                result = RecentProductsDBWrapper.getProducts(userId);
            } catch (NumberFormatException e) {
                result = RecentProductsDBWrapper.getProducts(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (!isDestroyed && result != null && result instanceof ArrayList<?> && ((ArrayList<?>) result).size() > 0) {
                adapter.addRecentlyViewed((ArrayList<BaseProductListObject>) result);

            }
            if (adapter != null)
                adapter.setIsFooterRemoved(true);
        }

    }

}
