package com.application.zimplyshop.activities;

import android.support.v7.app.ActionBarActivity;

public class ProductDetailsActivity extends ActionBarActivity {

    /*public int IMAGE_WIDTH = 0;
    public int IMAGE_HEIGHT = 0;
    // Views
    int width, height;
    boolean isDestroyed = false;
    String productSlug;
    long productId;
    ViewPager pager;
    //PhotoPagerAdapter mAdapter;
    TextView addToCart;
    LinearLayout retryLayout;
    ProgressBar progress;
    TextView nullcaseText;
    TextView quoteText;
    boolean isRequestFailed;
    View restPageContent;
    ProgressDialog progressDialog;
    GoogleApiClient mClient;
    String baseAppUri = "android-app://com.application.zimplyshop/http/www.zimply.in/shop-product/";
    String baseWebUri = "http://www.zimply.in/home/shop-product/";
    Uri WEB_URL;
    Uri APP_URI;
    TextView cartCount;
    FadingActionBarHelper helper;
    View mActionBarCustomView;
    // Product objects
    private HomeProductObj product;
    private LayoutInflater inflater;
    private boolean destroyed = false;
    private int STATE = 0;
    private int DROPDOWN_VISIBLE = 911;
    private int DROPDOWN_INVISIBLE = 912;
    private Activity mContext;
    private boolean isLoading, userLoading;
    private boolean isOfflinePurchase;

    private boolean isScannedProduct;

    Toast toast;

    CustomTextView bookAVisitBtn;
    CustomTextView emptyBtn;

    int BOOK_BTN_CLICK=1;
    int PROGRESS_TIME_COMPLETE = 2;
    int PROGRESS_LOADING_COMPLETE=3;
    int BOOK_PROCESS_COMPLETE = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GetRequestManager.getInstance().addCallbacks(this);
        UploadManager.getInstance().addCallback(this);
        //inflater to inflate the view
        inflater = LayoutInflater.from(this);
        mContext = this;
        //compute the values
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();

        //set image height
        IMAGE_HEIGHT = width / 3;
        IMAGE_WIDTH = width;
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //Add loading layout

        //init fading action bar
        helper = new FadingActionBarHelper();
        helper.actionBarBackground(R.color.white);
        helper.headerLayout(R.layout.product_tab_info_top);//ViewPager
        helper.contentLayout(R.layout.product_tab_info);

        final View mainContent = helper.createView(this);

        restPageContent = inflater.inflate(R.layout.product_details_activity, null);
        if (AppPreferences.isPincodeSaved(this)) {
            String pincode = AppPreferences.getSavedPincode(this);
           ((EditText) mainContent.findViewById(R.id.pincode)).setText(pincode);
            checkPincodeAvailabilty(false, pincode);
        }
        if (restPageContent != null) {
            ((ViewGroup) restPageContent.findViewById(R.id.product_page_content)).addView(mainContent, 0);
            mainContent.findViewById(R.id.pincode_check).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((EditText) mainContent.findViewById(R.id.pincode)).getText().toString().length() == 6) {
                        checkPincodeAvailabilty(true, ((EditText) findViewById(R.id.pincode)).getText().toString());
                    } else {
                        showToast("Please enter a valid pincode");
                        //  Toast.makeText(ProductDetailsActivity.this, "Please enter a valid pincode", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            setContentView(restPageContent);
            setLoadingVariables();
        } else {
            Crashlytics.logException(new Throwable("Product page not rendered. Finishing activity."));
            finish();
            return;
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (input.length() == 6) {
                    checkPincodeAvailabilty(true, ((EditText) findViewById(R.id.pincode)).getText().toString());
                }
            }
        };
        ((LinearLayout) findViewById(R.id.base_parent_layout)).setBackgroundColor(getResources().getColor(R.color.white));
        ((TextView) mainContent.findViewById(R.id.pincode)).addTextChangedListener(textWatcher);
        // Order not to be changed
        Toolbar toolbar = (Toolbar) findViewById(R.id.prod_toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();
        helper.initActionBar(this);

        getWindow().setBackgroundDrawable(null);

        // getting intent
        String[] type = {""};
        if (getIntent().getExtras().containsKey("slug"))
            productSlug = String.valueOf(getIntent().getExtras().get("slug"));
        if (getIntent().getExtras().containsKey("id") && getIntent().getExtras().get("id") instanceof Long)
            productId = getIntent().getExtras().getLong("id");
        if(getIntent().getExtras().getBoolean("is_scanned")){
            isScannedProduct = getIntent().getExtras().getBoolean("is_scanned");
        }

        if (getIntent().getExtras().containsKey("is_shared") && AppPreferences.isUserLogIn(ProductDetailsActivity.this)) {
            loadUserData();
        } else
            loadData();
        ImageView productImg = (ImageView) findViewById(R.id.product_img);
        productImg.getLayoutParams().width = width;
        productImg.getLayoutParams().height = (height )/ 2;
        *//*pager = (ViewPager) findViewById(R.id.photos_viewpager);
        pager.getLayoutParams().width = width;
        pager.getLayoutParams().height = height / 2;
        *//*
        fixSizes();

        // handling touch on the overlay when the dropdown is visible
        STATE = DROPDOWN_INVISIBLE;
        findViewById(R.id.product_overlay).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return STATE == DROPDOWN_VISIBLE;
            }
        });
        addToCart = (TextView) findViewById(R.id.add_to_cart);
        bookAVisitBtn = (CustomTextView)findViewById(R.id.book_store_visit);


        emptyBtn = (CustomTextView)findViewById(R.id.empty_btn);
        bookAVisitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppPreferences.isUserLogIn(ProductDetailsActivity.this)) {
                   // bookAVisitBtn.setVisibility(View.GONE);
                    //emptyBtn.setVisibility(View.VISIBLE);
                    // scaleView(emptyBtn, 1f, 0.15f, true);
                    slideViewsRightToLeft(bookAVisitBtn,findViewById(R.id.loading_layout),BOOK_BTN_CLICK);
                } else {
                    Intent intent = new Intent(ProductDetailsActivity.this, BaseLoginSignupActivity.class);
                    intent.putExtra("inside", true);
                    startActivity(intent);
                }
            }
        });
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoading) {
                    showToast("Please wait while loading..");
                    // Toast.makeText(ProductDetailsActivity.this, "Please wait while loading..", Toast.LENGTH_SHORT).show();
                } else {
                    if (CommonLib.isNetworkAvailable(ProductDetailsActivity.this)) {

                        String userId = AppPreferences.getUserID(mContext);
                        if (AppPreferences.isUserLogIn(ProductDetailsActivity.this)) {
                            if (!(addToCart.getText().toString().equalsIgnoreCase("Go To Cart"))) {
                                isOfflinePurchase = false;
                                String url = AppApplication.getInstance().getBaseUrl() + ADD_TO_CART_URL;
                                List<NameValuePair> nameValuePair = new ArrayList<>();
                                nameValuePair.add(new BasicNameValuePair("buying_channel", AppConstants.BUYING_CHANNEL_ONLINE+""));
                                nameValuePair.add(new BasicNameValuePair("product_id", productId + ""));
                                nameValuePair.add(new BasicNameValuePair("quantity", "1"));
                                nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(mContext)));

                       //         UploadManager.getInstance().makeAyncRequest(url, ADD_TO_CART_PRODUCT_DETAIL, product.getSlug(), OBJECT_ADD_TO_CART, null, nameValuePair, null);

                            } else {
                                Intent intent = new Intent(ProductDetailsActivity.this, ProductCheckoutActivity.class);
                                intent.putExtra("OrderSummaryFragment", false);
                                intent.putExtra("buying_channel",BUYING_CHANNEL_ONLINE);
                                startActivity(intent);
                            }

                        } else {

                            if (!(addToCart.getText().toString().equalsIgnoreCase("Go To Cart"))) {
                                ArrayList<NonLoggedInCartObj> oldObj = ((ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(ProductDetailsActivity.this), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT));
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
                                    GetRequestManager.Update(AppPreferences.getDeviceID(mContext), oldObj, RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
                                    showToast("Successfully added to cart");
                                    //  Toast.makeText(mContext, "Successfully added to cart", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Intent intent = new Intent(ProductDetailsActivity.this, ProductCheckoutActivity.class);
                                intent.putExtra("OrderSummaryFragment", false);
                                intent.putExtra("buying_channel",BUYING_CHANNEL_ONLINE);
                                startActivity(intent);
                            }
                        }

                    } else {
                        showToast("No network available");
                        // Toast.makeText(ProductDetailsActivity.this, "No network available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        findViewById(R.id.buy_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoading) {
                    showToast("Please wait while loading..");
                    // Toast.makeText(ProductDetailsActivity.this, "Please wait while loading..", Toast.LENGTH_SHORT).show();
                } else {

                    if (CommonLib.isNetworkAvailable(ProductDetailsActivity.this)) {

                        if(AppPreferences.isUserLogIn(ProductDetailsActivity.this)){
                            Intent intent =new Intent(ProductDetailsActivity.this,ProductCheckoutActivity.class);
                            intent.putExtra("OrderSummaryFragment", true);
                            intent.putExtra("productids",product.getId()+"");
                            intent.putExtra("quantity","1");
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(ProductDetailsActivity.this, BaseLoginSignupActivity.class);
                            intent.putExtra("inside", true);
                            startActivity(intent);
                        }

                    } else {
                        showToast("No network available");
                        //Toast.makeText(ProductDetailsActivity.this, "No network available", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
       *//* findViewById(R.id.buy_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoading) {
                    Toast.makeText(ProductDetailsActivity.this, "Please wait while loading..", Toast.LENGTH_SHORT).show();
                } else {
                    if (CommonLib.isNetworkAvailable(ProductDetailsActivity.this)) {
                        String userId = AppPreferences.getUserID(mContext);
                        if (AppPreferences.isUserLogIn(mContext)) {
                            if (AllProducts.getInstance().cartContains((int) product.getId())) {
                                Intent intent = new Intent(ProductDetailsActivity.this, ProductCheckoutActivity.class);
                                intent.putExtra("OrderSummaryFragment", true);
                                startActivity(intent);
                            } else {
                                String url = AppApplication.getInstance().getBaseUrl() + ADD_TO_CART_PRODUCT_DETAIL_URL;
                                List<NameValuePair> nameValuePair = new ArrayList<>();
                                nameValuePair.add(new BasicNameValuePair("product_id", productId + ""));
                                nameValuePair.add(new BasicNameValuePair("quantity", "1"));
                                nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(mContext)));
                                UploadManager.getInstance().makeAyncRequest(url, BUY_NOW, product.getSlug(), OBJECT_ADD_TO_CART_PRODUCT_DETAIL, null, nameValuePair, null);
                            }

                        } else {
                            Intent intent = new Intent(ProductDetailsActivity.this, BaseLoginSignupActivity.class);
                            intent.putExtra("inside", true);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(ProductDetailsActivity.this, "No network available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });*//*

        toggleButtonsState(true);
        findViewById(R.id.relativeParent).setBackgroundColor(getResources().getColor(R.color.white));
    }

    public void slideViewsRightToLeft(final View v1,View v2,final int checkCase){

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(v1,View.TRANSLATION_X,0,-width);
        anim1.setDuration(300);
        v2.setVisibility(View.VISIBLE);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(v2,View.TRANSLATION_X,width,0);
        anim2.setDuration(300);
        set.playTogether(anim1, anim2);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v1.setVisibility(View.GONE);
                if(checkCase==BOOK_BTN_CLICK){
                    startProgressBarLayout();
                }else if(checkCase == PROGRESS_TIME_COMPLETE){
                    makeProductPreviewRequest();
                }else if(checkCase == PROGRESS_LOADING_COMPLETE){
                    moveBookingCompleteCardIn();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }
    public void slideViewsLeftToRight(final View v1,final View v2,int checkCase){

        AnimatorSet set = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(v1,View.TRANSLATION_X,0,width);
        anim1.setDuration(300);
        v2.setVisibility(View.VISIBLE);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(v2,View.TRANSLATION_X,-width,0);
        anim2.setDuration(300);
        set.playTogether(anim1, anim2);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v1.setVisibility(View.GONE);


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    public void scaleView(final View v, float startScale, float endScale,final  boolean isAnimateIn) {
        Animation anim = new ScaleAnimation(
                startScale, endScale, // Start and end values for the X axis scaling
                1f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isAnimateIn) {
                    startProgressBarLayout();
                } else {
                    // v.setVisibility(View.GONE);
                    showBookBtnNormal();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        anim.setDuration(300);
        v.startAnimation(anim);
    }
    public void showBookBtnNormal(){
        emptyBtn.clearAnimation();
        bookAVisitBtn.setVisibility(View.VISIBLE);
        emptyBtn.setVisibility(View.INVISIBLE);
    }
    int seconds = 30;

    public void startProgressBarLayout(){
        if(!isDestroyed) {

            final ProgressBar bookVisitProgress = (ProgressBar) findViewById(R.id.progressBar);

            final TextView crossImg = (TextView) findViewById(R.id.cross_img);

            final Timer timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!destroyed) {
                                seconds -= 1;
                                if (seconds <= 0) {
                                    seconds = 30;
                                    timer.cancel();

                                    // emptyBtn.setVisibility(View.GONE);
                                    slideViewsRightToLeft(findViewById(R.id.loading_layout), findViewById(R.id.book_progress),PROGRESS_TIME_COMPLETE);

                                    //makeProductPreviewRequest();

                                } else {
                                    bookVisitProgress.setProgress(100 - ((seconds*100)/30));
                                }

                            }
                        }
                    });

                }
            }, 0, 50);

            crossImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timer.cancel();
                    seconds = 30;
                    bookVisitProgress.setProgress(0);
                    slideViewsLeftToRight(findViewById(R.id.loading_layout),bookAVisitBtn,BOOK_BTN_CLICK);
                    //scaleView(emptyBtn, 0.15f, 1f, false);
                }
            });
        }
    }

    public void makeProductPreviewRequest(){
        String url = AppApplication.getInstance().getBaseUrl() + MARK_PRODUCT_REVIEW_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("product_id", product.getId()+""));
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        UploadManager.getInstance().makeAyncRequest(url, MARK_PRODUCT_REVIEW_TAG, product.getId() + "",
                OBJECT_TYPE_MARK_PRODUCT_REVIEW, product, list, null);
    }

    public void checkPincodeAvailabilty(boolean showProgress, String text) {
        if (CommonLib.isNetworkAvailable(this)) {
            if(showProgress)
                progressDialog = ProgressDialog.show(this, null, "Checking availability.Please Wait..");
            AppPreferences.setSavedPincode(this, text);
            AppPreferences.setIsPincodeSaved(this, true);
            String url = AppApplication.getInstance().getBaseUrl() + AppConstants.CHECK_PINCODE + "?pincode=" + text;
            GetRequestManager.getInstance().makeAyncRequest(url, CHECKPINCODEREQUESTTAG, ObjectTypes.OBJECT_TYPE_CHECK_PINCODE);
        } else {
            showToast( "No network available");
            //  Toast.makeText(this, "No network available", Toast.LENGTH_SHORT).show();
        }
    }

    *//**
     * Show Loading for all activities
     *//*
    public void setLoadingVariables() {
        progress = (ProgressBar) findViewById(R.id.progress);
        nullcaseText = (TextView) findViewById(R.id.nullcase_text);
        retryLayout = (LinearLayout) findViewById(R.id.retry_layout);
        retryLayout.setOnClickListener(this);
        quoteText = (TextView) findViewById(R.id.quote);
    }

    private void fixSizes() {
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();

        *//*final int buttonHeight = 3 * width / 20;
        findViewById(R.id.bottom_action_container).getLayoutParams().height = buttonHeight;
*//*
        //  ((LinearLayout.LayoutParams) findViewById(R.id.return_value).getLayoutParams()).setMargins(0, 0, 0, buttonHeight);
        // ((LinearLayout.LayoutParams) findViewById(R.id.return_container).getLayoutParams()).setMargins(0, 0, 0, buttonHeight + width / 20);

        findViewById(R.id.return_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog logoutDialog;
                logoutDialog = new AlertDialog.Builder(ProductDetailsActivity.this).setTitle(getResources().getString(R.string.return_policy))
                        .setMessage(product.getReturnPolicy())
                        .setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                logoutDialog.show();

            }
        });

        findViewById(R.id.description_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog logoutDialog;
                logoutDialog = new AlertDialog.Builder(ProductDetailsActivity.this).setTitle(getResources().getString(R.string.description))
                        .setMessage(product.getDescription())
                        .setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                logoutDialog.show();

            }
        });

    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        UploadManager.getInstance().removeCallback(this);
        getSupportActionBar().invalidateOptionsMenu();
        super.onDestroy();
        setContentView(new View(getApplicationContext()));
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("is_shared", false)||getIntent().getBooleanExtra("is_notification", false)) {
            Intent intent = new Intent(this, HomeActivity.class);
            this.finish();
            startActivity(intent);
        } else {
            super.onBackPressed();
        }

    }

    private void loadData() {
        String url = AppApplication.getInstance().getBaseUrl() + PRODUCT_DESCRIPTION_REQUETS_URL + "?id=" + productId
                + "&width=" + width / 2 + "&thumb=100" + (AppPreferences.isUserLogIn(this) ? "&userid=" + AppPreferences.getUserID(this) : "");
        GetRequestManager.getInstance().makeAyncRequest(url, PRODUCT_DETAIL_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_PRODUCT_DETAIL);
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (requestTag != null && requestTag.equals(RequestTags.GET_USER_DATA) && !isDestroyed) {
            userLoading = true;
            restPageContent.findViewById(R.id.product_page_content).setVisibility(View.GONE);
            restPageContent.findViewById(R.id.product_overlay).setVisibility(View.GONE);
            restPageContent.findViewById(R.id.dropdown_container).setVisibility(View.GONE);
            showLoadingView();
        } else if (requestTag != null && requestTag.equals(PRODUCT_DETAIL_REQUEST_TAG) && !isDestroyed) {
            restPageContent.findViewById(R.id.product_page_content).setVisibility(View.GONE);
            restPageContent.findViewById(R.id.product_overlay).setVisibility(View.GONE);
            restPageContent.findViewById(R.id.dropdown_container).setVisibility(View.GONE);
            restPageContent.findViewById(R.id.bottom_action_container).setVisibility(View.INVISIBLE);

            isLoading = true;
            showLoadingView();
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(CHECKPINCODEREQUESTTAG) && !isDestroyed) {
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {

        isRequestFailed = false;
        if (requestTag != null && requestTag.equals(RequestTags.GET_USER_DATA) && !isDestroyed) {

            userLoading = false;
            loadData();
            checkCartCount();
            isLoading = false;
        }
        if (requestTag != null && requestTag.equals(PRODUCT_DETAIL_REQUEST_TAG) && !isDestroyed) {
            //set the product views
            if (obj instanceof HomeProductObj) {
                product = (HomeProductObj) obj;
                refreshViews();
                new AddProductToCache().execute();

                restPageContent.findViewById(R.id.product_overlay).setVisibility(View.VISIBLE);
                restPageContent.findViewById(R.id.dropdown_container).setVisibility(View.VISIBLE);
                restPageContent.findViewById(R.id.product_page_content).setVisibility(View.VISIBLE);

                showView();
                setUpIndexingApi();
            } else {
                showNullCaseView("No Info Available");
            }
            isLoading = false;
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(CHECKPINCODEREQUESTTAG) ) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (((boolean) obj)) {
                findViewById(R.id.is_available_pincode).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.is_available_pincode)).setTextColor(getResources().getColor(R.color.green_text_color));
                ((TextView) findViewById(R.id.is_available_pincode)).setText("Available at selected pincode");
            } else {
                findViewById(R.id.is_available_pincode).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.is_available_pincode)).setTextColor(getResources().getColor(R.color.red_text_color));
                ((TextView) findViewById(R.id.is_available_pincode)).setText("Not available at selected pincode");
            }
            CommonLib.hideKeyBoard(mContext, findViewById(R.id.pincode));

            isLoading = false;
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {

        if (requestTag != null && requestTag.equals(RequestTags.GET_USER_DATA) && !isDestroyed) {
            showNetworkErrorView();
            if (CommonLib.isNetworkAvailable(ProductDetailsActivity.this)) {
                // Toast.makeText(ProductDetailsActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                showToast("Something went wrong. Please try again");
            }else {
                // Toast.makeText(ProductDetailsActivity.this, "Internet not available. Please try again", Toast.LENGTH_SHORT).show();
                showToast("Something went wrong. Please try again");
            }
            isLoading = false;
        }
        if (requestTag != null && requestTag.equals(PRODUCT_DETAIL_REQUEST_TAG) && !isDestroyed) {
            // findViewById(R.id.progress_container).setVisibility(View.GONE);
            showNetworkErrorView();
            if (CommonLib.isNetworkAvailable(ProductDetailsActivity.this)) {
                showToast("Something went wrong. Please try again");
                //Toast.makeText(ProductDetailsActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
            }
            else {
                showToast("Internet not available. Please try again");
                //  Toast.makeText(ProductDetailsActivity.this, "Internet not available. Please try again", Toast.LENGTH_SHORT).show();
            }
            isLoading = false;
        }
        if (requestTag != null && requestTag.equals(CHECKPINCODEREQUESTTAG) && !isDestroyed) {
            if (CommonLib.isNetworkAvailable(ProductDetailsActivity.this)) {
                showToast("Something went wrong. Please try again");
                //   Toast.makeText(ProductDetailsActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
            }
            else {
                showToast("Internet not available. Please try again");
                //   Toast.makeText(ProductDetailsActivity.this, "Internet not available. Please try again", Toast.LENGTH_SHORT).show();
            }
            isLoading = false;
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

    }

    private void setupActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);

        mActionBarCustomView = inflater.inflate(R.layout.product_custom_action_bar, null);
        int toolbarHeight = (int) getResources().getDimension(R.dimen.toolbar_height);

        //  View actionbarRestContainer = mActionBarCustomView.findViewById(R.id.actionbar_product_container);
        //  actionbarRestContainer.setPadding(toolbarHeight, 0, toolbarHeight, 0);
        actionBar.setCustomView(mActionBarCustomView);
       // mActionBarCustomView.findViewById(R.id.back_icon).setPadding(width / 20, 0, width / 20, 0);
        // mActionBarCustomView.findViewById(R.id.back_icon).setOnClickListener(this);
        // mActionBarCustomView.findViewById(R.id.actionbar_call_icon).setPadding(width / 20, 0, width / 20, 0);
        mActionBarCustomView.findViewById(R.id.share_product).setOnClickListener(this);
        // mActionBarCustomView.findViewById(R.id.fav_product).setOnClickListener(this);
        mActionBarCustomView.findViewById(R.id.cart_icon).setOnClickListener(this);
        cartCount = (TextView) mActionBarCustomView.findViewById(R.id.cart_item_true);
        cartCount.setText(AllProducts.getInstance().getCartCount() + "");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        return super.onCreateOptionsMenu(menu);
    }

    *//*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
*//*


    public void actionBarSelected(View view) {

        switch (view.getId()) {

           *//* case R.id.back_icon:
                onBackPressed();
                break;*//*

            case android.R.id.home:
                onBackPressed();
                break;

        }
    }

    private void refreshViews() {

        if (product == null)
            return;
//        findViewById(R.id.obp_gradient).setVisibility(View.GONE);

       *//* mAdapter = new PhotoPagerAdapter(product.getImageUrls());
        pager.setAdapter(mAdapter);
        pager.getParent().requestDisallowInterceptTouchEvent(true);
        pager.setOffscreenPageLimit(1);
        pager.setCurrentItem(0);
        pager.setPageTransformer(false, new ParallaxPageTransformer((float) .5, (float) .5, R.id.photo_imageview));
*//*

        //Umesh
        ((LinearLayout)findViewById(R.id.more_from_seller)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.product_fav)).setSelected(product.is_favourite());
        RecyclerView thumbList = (RecyclerView) findViewById(R.id.product_thumb_icons);
        if(product.is_o2o()){
            ((TextView)findViewById(R.id.coc_avaiable)).setText("Available");
        }else{
            ((TextView)findViewById(R.id.coc_avaiable)).setText("Not Available");
        }
        ((TextView)findViewById(R.id.online_payment_available)).setText("Available");
        thumbList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        thumbList.addItemDecoration(new ProductThumbListItemDecorator(getResources().getDimensionPixelSize(R.dimen.margin_small)));
        final ProductThumbAdapters adapter = new ProductThumbAdapters(this, product.getThumbs(), getResources().getDimensionPixelSize(R.dimen.pro_image_size), getResources().getDimensionPixelSize(R.dimen.pro_image_size));
        thumbList.setAdapter(adapter);
        if (AllProducts.getInstance().getCartObjs() != null && AllProducts.getInstance().cartContains((int) product.getId()))
            ((TextView) findViewById(R.id.add_to_cart)).setText("Go To Cart");
        else
            ((TextView) findViewById(R.id.add_to_cart)).setText("Add To Cart");
        adapter.setOnItemClickListener(new ProductThumbAdapters.OnItemClickListener() {
            @Override
            public void onItemClick(final int pos) {
                if (adapter.getSelectedPos() != pos) {
                    adapter.setSelectedPos(pos);
                    new ImageLoaderManager(mContext).setImageFromUrl(product.getThumbs().get(pos), ((ImageView) findViewById(R.id.product_img)), "users", width / 2, height / 20, false,
                            false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!destroyed)
                                new ImageLoaderManager(ProductDetailsActivity.this).setImageFromUrl(product.getImageUrls().get(pos), ((ImageView) findViewById(R.id.product_img)), "photo_details", width / 2, height / 20, false,
                                        false);

                        }
                    }, 200);
                }
            }
        });
        ((ImageView) findViewById(R.id.product_fav)).setOnClickListener(this);

        ((View) findViewById(R.id.parallax_content_top_margin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsActivity.this, ProductPhotoZoomActivity.class);
                intent.putExtra("product_obj", product);
                intent.putExtra("position", adapter.getSelectedPos());
                startActivity(intent);
            }
        });
        new ImageLoaderManager(mContext).setImageFromUrl(product.getThumbs().get(0), ((ImageView) findViewById(R.id.product_img)), "users", width / 2, height / 20, false,
                false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!destroyed)
                    new ImageLoaderManager(ProductDetailsActivity.this).setImageFromUrl(product.getImageUrls().get(0), ((ImageView) findViewById(R.id.product_img)), "photo_details", width / 2, height / 20, false,
                            false);

            }
        }, 200);

        ((TextView) findViewById(R.id.product_title)).setText(product.getName());
        ((TextView) findViewById(R.id.product_price)).setText(getString(R.string.rs_text) + " " + Math.round(product.getPrice()) + "");
        ((TextView) findViewById(R.id.delivery)).setText(product.getMinShippingDays() + "-" + product.getMaxShippingDays() + " Days");
        ((TextView) findViewById(R.id.shipping_charges)).setText((product.getShippingCharges()==0)?"Free":getString(R.string.rs_text) + " " + Math.round(product.getShippingCharges()));
        ((TextView) findViewById(R.id.sold_by)).setText(product.getVendor().getCompany_name());

        ((TextView) mActionBarCustomView.findViewById(R.id.title)).setText(product.getName());

        ((TextView) findViewById(R.id.specification_title)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog logoutDialog;
                LinearLayout linLayout = new LinearLayout((ProductDetailsActivity.this));
                linLayout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                linLayout.setPadding(getResources().getDimensionPixelSize(R.dimen.margin_large), getResources().getDimensionPixelSize(R.dimen.margin_small), getResources().getDimensionPixelSize(R.dimen.margin_large), getResources().getDimensionPixelSize(R.dimen.margin_small));
                for (ProductAttribute attribute : product.getAttributes()) {

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout layout = new LinearLayout(ProductDetailsActivity.this);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    layout.setLayoutParams(params);
                    TextView unit = new TextView(ProductDetailsActivity.this);
                    unit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    ((LinearLayout.LayoutParams) unit.getLayoutParams()).weight = 1;
                    unit.setTextColor(getResources().getColor(R.color.text_color1));
                    unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.font_medium));
                    unit.setText(Html.fromHtml("<font color=#535353>" + attribute.getKey() + "</font>" + " : " + attribute.getValue() + " " + attribute.getUnit()));
                    layout.addView(unit);
                    linLayout.addView(layout);
                }
                linLayout.setLayoutParams(lp);
                logoutDialog = new AlertDialog.Builder(ProductDetailsActivity.this).setTitle(getResources().getString(R.string.specifications))
                        .setView(linLayout)
                        .setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                logoutDialog.show();
            }
        });
        *//*LinearLayout specificationsLayout = (LinearLayout) findViewById(R.id.specifications);
        for (ProductAttribute attribute : product.getAttributes()) {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setLayoutParams(params);
            TextView unit = new TextView(this);
            unit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ((LinearLayout.LayoutParams) unit.getLayoutParams()).weight = 1;
            unit.setTextColor(getResources().getColor(R.color.zhl_darkest));
            unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.font_medium));
            unit.setText(attribute.getKey() + " : " + attribute.getValue() + " " + attribute.getUnit());
            layout.addView(unit);
            specificationsLayout.addView(layout);
        }*//*
        toggleButtonsState(true);
        if(AllProducts.getInstance().vendorIdsContains(product.getVendor().getVendor_id())){
            bookAVisitBtn.setVisibility(View.GONE);
            showVisitBookedInitialCard(product.getVendor());
        } else {
            if(product.is_o2o()){
                bookAVisitBtn.setVisibility(View.VISIBLE);
            }else {
                bookAVisitBtn.setVisibility(View.GONE);
            }
            ;
        }
        restPageContent.findViewById(R.id.bottom_action_container).setVisibility(View.VISIBLE);

    }


    @Override
    public void uploadFinished(int requestType, String objectId, Object data,
                               Object response, boolean status, int parserId) {
        if (progressDialog != null)
            progressDialog.dismiss();

        if (requestType == ADD_TO_CART_PRODUCT_DETAIL && status && !isDestroyed) {
            isLoading = false;
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
                    if(isOfflinePurchase ){
                        AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj((int)scannedProductId, 1));
                        AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartCount() + 1);
                        cartCount.setVisibility(View.VISIBLE);
                        cartCount.setText(AllProducts.getInstance().getCartCount() + "");
                        moveToCartActivity();
                        isOfflinePurchase = false;
                    }else {
                        AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj((int) product.getId(), 1));
                        AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartCount() + 1);
                        cartCount.setVisibility(View.VISIBLE);
                        cartCount.setText(AllProducts.getInstance().getCartCount() + "");

                    }
                } else if (jsonObject.getString("error") != null && jsonObject.getString("error").length() > 0) {
                    message = jsonObject.getString("error");
                }

                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestType == BUY_NOW && status && !isDestroyed) {
            isLoading = false;
            if (progressDialog != null)
                progressDialog.dismiss();
            if (status) {
                AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj((int) product.getId(), 1));
                AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartCount() + 1);
                cartCount.setText(AllProducts.getInstance().getCartCount() + "");
                Intent intent = new Intent(ProductDetailsActivity.this, ProductCheckoutActivity.class);
                intent.putExtra("OrderSummaryFragment", true);
                startActivity(intent);
            } else {
                Toast.makeText(mContext, "Could not process buy now.", Toast.LENGTH_SHORT).show();
            }
        } else if ((requestType == MARK_UN_FAVOURITE_REQUEST_TAG || requestType == MARK_FAVOURITE_REQUEST_TAG) && !isDestroyed) {
            if (requestType == MARK_FAVOURITE_REQUEST_TAG) {
                if (status) {
                    product.setIs_favourite(false);
                    product.setFavourite_item_id((String) data);
                }
            } else if (requestType == MARK_UN_FAVOURITE_REQUEST_TAG) {
                if (status) {
                    product.setIs_favourite(true);
                }
            }
        }else if(requestType == MARK_PRODUCT_REVIEW_TAG){
            //findViewById(R.id.book_progress).setVisibility(View.GONE);
            //bookAVisitBtn.setVisibility(View.GONE);
            if(status) {

               *//* AllProducts.getInstance().getBookedObjs().add(new BaseCartProdutQtyObj((int)product.getId(),1));
                Intent intent = new Intent(this, ProductDemoActivity.class);
                intent.putExtra("product_vendor_time", ((ProductVendorTimeObj) response));
                startActivity(intent);*//*
                AllProducts.getInstance().getVendorIds().add(((ProductVendorTimeObj) response).getVendor_id());
                showVisitBookedCard(((ProductVendorTimeObj) response));
                if(!AppPreferences.isFirstBookingDone(this)){
                    AppPreferences.setIsFirstBookingDone(this,true);
                }
            }else{
                Toast.makeText(this,((ErrorObject)response).getErrorMessage(),Toast.LENGTH_SHORT).show();
            }
        }else if(!isDestroyed && requestType == CANCEL_PRODUCT_REVIEW_TAG){
            if (progressDialog != null)
                progressDialog.dismiss();
            if(status) {
                AllProducts.getInstance().getVendorIds().remove((Integer) product.getVendor().getVendor_id());
                //findViewById(R.id.booking_confirm_card).setVisibility(View.GONE);
               // bookAVisitBtn.setVisibility(View.VISIBLE);
                slideViewsLeftToRight(findViewById(R.id.booking_confirm_card),bookAVisitBtn,BOOK_BTN_CLICK);
                showToast("Successfully cancelled");
            }else{
                showToast("Cannot cancel request. Try again");
            }
        }
        isRequestFailed = !status;
    }

    public void cancelBooking(ProductVendorTimeObj obj){
        String url = AppApplication.getInstance().getBaseUrl() + REMOVE_PRODUCT_REVIEW_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        list.add(new BasicNameValuePair("book_product_id", obj.getBook_product_id() + ""));
        UploadManager.getInstance().makeAyncRequest(url, RequestTags.CANCEL_PRODUCT_REVIEW_TAG, obj.getBook_product_id() + "",
                ObjectTypes.OBJECT_TYPE_REMOVE_PRODUCT_REVIEW, obj, list, null);
    }

    public void cancelBookingId(VendorObj obj){
        String url = AppApplication.getInstance().getBaseUrl() + REMOVE_PRODUCT_REVIEW_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        list.add(new BasicNameValuePair("book_product_id", obj.getBook_product_id() + ""));
        UploadManager.getInstance().makeAyncRequest(url, RequestTags.CANCEL_PRODUCT_REVIEW_TAG, obj.getBook_product_id() + "",
                ObjectTypes.OBJECT_TYPE_REMOVE_PRODUCT_REVIEW, obj, list, null);
    }
    public void showVisitBookedCard(final ProductVendorTimeObj obj){
        View view = findViewById(R.id.booking_confirm_card);
        //view.setVisibility(View.VISIBLE);
        ((CustomTextView)view.findViewById(R.id.address)).setText(obj.getLine1() + "\n" + obj.getCity());
        ((LinearLayout)view.findViewById(R.id.call_customer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + product.getVendor().getReg_add().getPhone()));
                mContext.startActivity(callIntent);
            }
        });

        ((LinearLayout)view.findViewById(R.id.get_direction_customer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("geo:" + AppApplication.getInstance().lat + "," + AppApplication.getInstance().lon +"?q=" + product.getVendor().getReg_add().getLocation().getLatitude()+ "," + product.getVendor().getReg_add().getLocation().getLongitude() + "(" + product.getVendor().getCompany_name()+")");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                *//*Intent intent = new Intent(mContext, MapPage.class);
                intent.putExtra("lat", product.getVendor().getReg_add().getLocation().getLatitude());
                intent.putExtra("lon", product.getVendor().getReg_add().getLocation().getLongitude());
                intent.putExtra("name", product.getVendor().getReg_add().getLocation().getName());
                mContext.startActivity(intent);*//*
            }
        });

        ((ImageView)view.findViewById(R.id.close_card)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog logoutDialog;
                logoutDialog = new AlertDialog.Builder(ProductDetailsActivity.this)
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
        });

        slideViewsRightToLeft(findViewById(R.id.book_progress), findViewById(R.id.congrats_layout), PROGRESS_LOADING_COMPLETE);

    }

    public void moveBookingCompleteCardIn(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isDestroyed)
                    slideViewsRightToLeft(findViewById(R.id.congrats_layout), findViewById(R.id.booking_confirm_card), BOOK_PROCESS_COMPLETE);
            }
        },1000);
    }
    public void showVisitBookedInitialCard(final VendorObj obj){
        View view = findViewById(R.id.booking_confirm_card);
        view.setVisibility(View.VISIBLE);
        ((CustomTextView)view.findViewById(R.id.address)).setText(obj.getReg_add().getLine1() + "\n" + obj.getReg_add().getLocation().getName());
        ((LinearLayout)view.findViewById(R.id.call_customer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" +product.getVendor().getReg_add().getPhone()));
                mContext.startActivity(callIntent);
            }
        });

        ((LinearLayout)view.findViewById(R.id.get_direction_customer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               *//* Intent intent = new Intent(mContext, MapPage.class);
                intent.putExtra("lat",product.getVendor().getReg_add().getLocation().getLatitude());
                intent.putExtra("lon", product.getVendor().getReg_add().getLocation().getLongitude());
                intent.putExtra("name", product.getVendor().getReg_add().getLocation().getName());
                mContext.startActivity(intent);*//*
                Uri uri = Uri.parse("geo:" + AppApplication.getInstance().lat + "," + AppApplication.getInstance().lon +"?q=" + product.getVendor().getReg_add().getLocation().getLatitude()+ "," + product.getVendor().getReg_add().getLocation().getLongitude() + "(" + product.getVendor().getCompany_name()+")");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);
            }
        });

        ((ImageView)view.findViewById(R.id.close_card)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog logoutDialog;
                logoutDialog = new AlertDialog.Builder(ProductDetailsActivity.this)
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
                                        cancelBookingId(obj);
                                    }
                                }).create();
                logoutDialog.show();

            }
        });
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {

        if ((requestType == ADD_TO_CART_PRODUCT_DETAIL || requestType == BUY_NOW) && !isDestroyed) {
            progressDialog = ProgressDialog.show(this, null, "Adding to cart. Please wait");
            // isLoading = true;
        } else if ((requestType == MARK_UN_FAVOURITE_REQUEST_TAG || requestType == MARK_FAVOURITE_REQUEST_TAG) && !isDestroyed) {
            // progressDialog = ProgressDialog.show(this, null, "Loading. Please wait");
        }else if(requestType == MARK_PRODUCT_REVIEW_TAG){
            //progressDialog = ProgressDialog.show(this, null, "Loading. Please wait");
//            ((ProgressBar)findViewById(R.id.progress_book_visit)).setIndeterminate(true);
        }else if(requestType == CANCEL_PRODUCT_REVIEW_TAG){
            progressDialog = ProgressDialog.show(this, null, "Cancelling booking. Please wait");
        }
    }

    *//**
     * Method shows the listview as soon as the data is successfully received
     * from the server and loaded into the adapter
     *//*
    public void showView() {
        progress.setVisibility(View.GONE);
        nullcaseText.setVisibility(View.GONE);
        retryLayout.setVisibility(View.GONE);
        quoteText.setVisibility(View.GONE);

    }

    *//**
     * Method shows loading view when a server request is generated
     *//*
    public void showLoadingView() {
        progress.setVisibility(View.VISIBLE);
        nullcaseText.setVisibility(View.GONE);
        retryLayout.setVisibility(View.GONE);
        quoteText.setVisibility(View.VISIBLE);
        quoteText.setText(UiUtils.getTextFromRes(this));
        retryLayout.setVisibility(View.GONE);
    }

    *//**
     * Method shows the Error view when the server request could not be
     * completed
     *//*
    public void showNetworkErrorView() {
        isRequestFailed = true;
        nullcaseText.setVisibility(View.VISIBLE);
        nullcaseText.setText(getString(R.string.try_again_text));
        changeLeftDrawable(R.drawable.ic_navigation_refresh);
        quoteText.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        retryLayout.setVisibility(View.VISIBLE);

    }

    public void showNullCaseView(String text) {
        nullcaseText.setVisibility(View.VISIBLE);
        nullcaseText.setText(text);
        changeLeftDrawable(0);
        quoteText.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        retryLayout.setVisibility(View.VISIBLE);
    }

    *//**
     * Method to change the Left drawbale of the nullcase text view
     *
     * @param drawable
     *//*
    public void changeLeftDrawable(int drawable) {
        if (drawable != 0) {
            Drawable drawableTop = getResources().getDrawable(drawable);
            drawableTop.setBounds(0, 0, drawableTop.getIntrinsicWidth(), drawableTop.getIntrinsicHeight());

            nullcaseText.setCompoundDrawables(null, drawableTop, null, null);
        } else {
            nullcaseText.setCompoundDrawables(null, null, null, null);
        }

    }

    private void makeUnLikeRequest() {

        String url = AppApplication.getInstance().getBaseUrl() + MARK_UNFAVOURITE_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("favourite_item_id", product.getFavourite_item_id()));
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        UploadManager.getInstance().makeAyncRequest(url, MARK_UN_FAVOURITE_REQUEST_TAG, product.getId() + "",
                OBJECT_TYPE_MARKED_UNFAV, product, list, null);
    }

    *//**
     * Request for marking favourite
     *//*
    public void makeLikeRequest() {
        String url = AppApplication.getInstance().getBaseUrl() + MARK_FAVOURITE_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("item_type", ITEM_TYPE_PRODUCT + ""));
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        list.add(new BasicNameValuePair("item_id", product.getId() + ""));
        UploadManager.getInstance().makeAyncRequest(url, MARK_FAVOURITE_REQUEST_TAG, product.getId() + "",
                OBJECT_TYPE_MARKED_FAV, product, list, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_from_seller:
                Intent bookIntent = new Intent(mContext, BookingStoreProductListingActivity.class);
                bookIntent.putExtra("hide_filter",true);
                bookIntent.putExtra("vendor_id", product.getVendor().getVendor_id());
                bookIntent.putExtra("url", AppConstants.GET_PRODUCT_LIST);
                bookIntent.putExtra("vendor_name", product.getVendor().getCompany_name());
                startActivity(bookIntent);
                break;
            case R.id.product_fav:
                if (!isLoading) {
                    if (AppPreferences.isUserLogIn(this)) {
                        if (product.is_favourite()) {
                            product.setIs_favourite(false);
                            ((ImageView) findViewById(R.id.product_fav)).setSelected(false);
                            makeUnLikeRequest();
                            Toast.makeText(this, "Successfully removed from wishlist", Toast.LENGTH_SHORT).show();
                        } else {
                            product.setIs_favourite(true);
                            ((ImageView) findViewById(R.id.product_fav)).setSelected(true);
                            makeLikeRequest();
                            Toast.makeText(this, "Successfully added to wishlist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Please login to continue", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, BaseLoginSignupActivity.class);
                        intent.putExtra("inside", true);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Please wait while loading...", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.share_product:
                if (!isLoading) {
                    Intent shareIntent = new Intent(ProductDetailsActivity.this, SharingOptionsActivity.class);
                    shareIntent.putExtra("title", "Checkout this awesome product ");
                    shareIntent.putExtra("slug", productSlug);
                    shareIntent.putExtra("type_name", AppConstants.ITEM_TYPE_PRODUCT);
                    shareIntent.putExtra("item_name", product.getName());
                    shareIntent.putExtra("product_url", "/shop-product/");
                    shareIntent.putExtra("short_url", "www.zimply.in/shop-product/" + productSlug + "?pid=" + productId);
                    startActivity(shareIntent);
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Please wait while loading...", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cart_icon:
                Intent intent = new Intent(this, ProductCheckoutActivity.class);
                intent.putExtra("OrderSummaryFragment", false);
                intent.putExtra("buying_channel",BUYING_CHANNEL_ONLINE);
                startActivity(intent);
                break;
          *//*  case R.id.back_icon:
                onBackPressed();
                break;*//*
            case R.id.retry_layout:
                if (isRequestFailed)
                    if (userLoading)
                        loadUserData();
                    else loadData();
                break;
        }
    }

    private void toggleButtonsState(boolean clickable) {
        findViewById(R.id.add_to_cart).setClickable(clickable);
        findViewById(R.id.buy_now).setClickable(clickable);
    }

    private void setUpIndexingApi() {
        // Construct the Action performed by the user
        mClient.connect();
        APP_URI = Uri.parse(baseAppUri + productSlug + "/?pid=" + productId);
        WEB_URL = Uri.parse(baseWebUri + productSlug + "/?pid=" + productId);
        Action viewAction = Action.newAction(Action.TYPE_VIEW, product.getName(), WEB_URL, APP_URI);

        // Call the App Indexing API start method after the view has
        // completely
        // rendered
        // Call the App Indexing API view method
        PendingResult<Status> result = AppIndex.AppIndexApi.start(mClient, viewAction);

        result.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    CommonLib.ZLog("ProductDetailsActivity", "App Indexing API: Recorded product" + product.getName() + " view successfully.");
                } else {
                    CommonLib.ZLog("ProductDetailsActivity", "App Indexing API: There was an error recording the product view." + status.toString());
                }
            }
        });
    }

    @Override
    protected void onStop() {

        // Call end() and disconnect the client

      *//* final Uri APP_URI = Uri.parse(baseAppUri + slug); *//*
        try {
            if (product != null) {
                Action viewAction = Action.newAction(Action.TYPE_VIEW, product.getName(), APP_URI);
                PendingResult<Status> result = AppIndex.AppIndexApi.end(mClient, viewAction);

                result.setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            CommonLib.ZLog("ProductDetailsActivity", "App Indexing API: Recorded product " + product.getName() + " view end successfully.");
                        } else {
                            CommonLib.ZLog("ProductDetailsActivity", "App Indexing API: There was an error recording the product view." + status.toString());
                        }
                    }
                });
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(mClient != null)
            mClient.disconnect();
        super.onStop();
    }

    public void checkCartCount() {
        if (AllProducts.getInstance().getCartCount() > 0) {
            cartCount.setVisibility(View.VISIBLE);
            cartCount.setText(AllProducts.getInstance().getCartCount() + "");

        } else {
            cartCount.setVisibility(View.GONE);
        }
    }

    public void loadUserData() {
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_USER_DATA + "?userid=" + AppPreferences.getUserID(this);
        GetRequestManager.getInstance().requestHTTPThenCache(url, RequestTags.GET_USER_DATA, ObjectTypes.OBJECT_USER_DETAILS, GetRequestManager.THREE_DAYS);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AppPreferences.isUserLogIn(this)) {
            if (product != null && AllProducts.getInstance().getCartObjs() != null && AllProducts.getInstance().cartContains((int) product.getId()))
                ((TextView) findViewById(R.id.add_to_cart)).setText("Go To Cart");
            else
                ((TextView) findViewById(R.id.add_to_cart)).setText("Add To Cart");
        } else {
            ArrayList<NonLoggedInCartObj> objs = (ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(this), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
            if (objs != null) {
                int count = 0;
                for (NonLoggedInCartObj cObj : objs) {
                    AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj(Integer.parseInt(cObj.getProductId()), cObj.getQuantity()));
                }
                AllProducts.getInstance().setCartCount(objs.size());
            }
            if (product != null && AllProducts.getInstance().getCartObjs() != null && AllProducts.getInstance().cartContains((int) product.getId()))
                ((TextView) findViewById(R.id.add_to_cart)).setText("Go To Cart");
            else
                ((TextView) findViewById(R.id.add_to_cart)).setText("Add To Cart");
        }
        checkCartCount();
    }

    *//*public class PhotoPagerAdapter extends BaseAdapter {

        ArrayList<String> photoUrls;

        public PhotoPagerAdapter(ArrayList<String> photoUrls) {
            this.photoUrls = photoUrls;
        }

        @Override
        public int getCount() {
            if (photoUrls == null)
                return 0;
            else
                return photoUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            final String photoObject = photoUrls.get(position);
            RelativeLayout photoLayout;

            if (container.findViewWithTag(position) == null) {
                photoLayout = (RelativeLayout) (getLayoutInflater().inflate(R.layout.pager_image_view, null));
                photoLayout.setTag(position);
            } else {
                photoLayout = (RelativeLayout) container.findViewWithTag(position);
            }

            final ImageView imageView = (ImageView) photoLayout.findViewById(R.id.featured_image);

            new ImageLoaderManager(mContext).setImageFromUrl(photoObject, imageView, "users", width / 2, height / 20, false,
                    false);

            // if (container.findViewWithTag(position) == null)
            container.addView(photoLayout);

            return photoLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(View collection, int position, Object o) {
            View view = (View) o;
            ((ViewPager) collection).removeView(view);
            view = null;
        }

    }*//*

    public class AddProductToCache extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... params) {
            int result = -1;
            try {
                int userId = Integer.parseInt(AppPreferences.getUserID(mContext));
                result = RecentProductsDBWrapper.addProduct(product, userId, System.currentTimeMillis());
            } catch (NumberFormatException e) {
                try {
                    result = RecentProductsDBWrapper.addProduct(product, -1, System.currentTimeMillis());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_TYPE_FROM_SEARCH) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                //Toast.makeText(this , "CONTENT"+contents,Toast.LENGTH_SHORT).show();
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                //Toast.makeText(this , "FORMAT:"+format,Toast.LENGTH_SHORT).show();
                JSONObject obj = JSONUtils.getJSONObject(contents);
                // Intent workintent = new Intent(this, ProductDetailsActivity.class);
                scannedProductId=(long) JSONUtils.getIntegerfromJSON(obj, "id");

                addScannedObjToCart((long) JSONUtils.getIntegerfromJSON(obj, "id"),JSONUtils.getStringfromJSON(obj, "slug"));
                // intent.putExtra("slug", JSONUtils.getIntegerfromJSON(obj, "slug"));
                // workintent .putExtra("id", (long)JSONUtils.getIntegerfromJSON(obj, "id"));
                // startActivity(workintent );
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    long scannedProductId;

    public void addScannedObjToCart(long id,String slug) {
        if (AppPreferences.isUserLogIn(this)) {
            if (AllProducts.getInstance().cartContains((int) id)) {
                Toast.makeText(this, "Already added to cart", Toast.LENGTH_SHORT).show();
                moveToCartActivity();
            } else {
                isOfflinePurchase = true;
                String url = AppApplication.getInstance().getBaseUrl() + ADD_TO_CART_URL;
                List<NameValuePair> nameValuePair = new ArrayList<>();
                nameValuePair.add(new BasicNameValuePair("product_id", id + ""));
                nameValuePair.add(new BasicNameValuePair("quantity", "1"));
                nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
                UploadManager.getInstance().addCallback(this);
                UploadManager.getInstance().makeAyncRequest(url, ADD_TO_CART_PRODUCT_DETAIL, slug, ObjectTypes.OBJECT_ADD_TO_CART, null, nameValuePair, null);
            }
        } else {
            ArrayList<NonLoggedInCartObj> oldObj = ((ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(this), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT));
            if (oldObj == null) {
                oldObj = new ArrayList<NonLoggedInCartObj>();
            }
            NonLoggedInCartObj item = new NonLoggedInCartObj(id + "", 1,BUYING_CHANNEL_OFFLINE);
            if (oldObj.contains(item)) {
                Toast.makeText(this, "Already added to cart", Toast.LENGTH_SHORT).show();
            } else {
                AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartCount() + 1);
                // checkCartCount();
                ((CustomTextView)findViewById(R.id.add_to_cart)).setText("Go To Cart");
                oldObj.add(item);
                GetRequestManager.Update(AppPreferences.getDeviceID(this), oldObj, RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
                Toast.makeText(this, "Successfully added to cart", Toast.LENGTH_SHORT).show();
            }
            moveToCartActivity();

        }
    }

    public void moveToCartActivity(){
        Intent intent = new Intent(this, ProductCheckoutActivity.class);
        intent.putExtra("OrderSummaryFragment", false);
        startActivity(intent);
    }

    public void showToast(String message) {
        if (toast == null) {
            toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        toast.setText(message);
        //toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
*/}
