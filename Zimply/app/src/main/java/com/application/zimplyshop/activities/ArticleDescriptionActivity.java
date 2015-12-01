package com.application.zimplyshop.activities;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ArticleDetailObject;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.HomeArticleObj;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.utils.ZTracker;
import com.application.zimplyshop.widgets.CircularImageView;
import com.application.zimplyshop.widgets.CustomScrollView;
import com.application.zimplyshop.widgets.CustomScrollView.OnScrollParallexListener;
import com.application.zimplyshop.widgets.VideoEnabledWebView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class ArticleDescriptionActivity extends BaseActivity implements GetRequestListener, AppConstants, RequestTags,
        OnClickListener, OnGlobalLayoutListener, ObjectTypes, UploadManagerCallback {
    String baseAppUri = "android-app://com.application.zimply/http/www.zimply.in/article/";
    String baseWebUri = "http://www.zimply.in/article/";
    String slug = null;
    boolean isDestroyed;
    Uri WEB_URL;
    Uri APP_URI;
    CustomScrollView scrollView;

    LinearLayout overflowLayout;

    View dimView;

    int overflowLayoutHeight;

    boolean isOptionsLayoutShown;

    LinearLayout likeLayout, shareLayout;

    HomeArticleObj articleObj;

    GoogleApiClient mClient;
    ArticleDetailObject articleDetailObject;
    TextView titleText;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_description_layout);

        //add callbacks
        UploadManager.getInstance().addCallback(this);

        dimView = findViewById(R.id.dim_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        // indexing
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        findViewById(R.id.more_info).setOnClickListener(this);
        articleObj = getIntent().getExtras().getParcelable("article_obj");
        scrollView = (CustomScrollView) findViewById(R.id.parent_scroll);

        onNewIntent(getIntent());

        GetRequestManager.getInstance().addCallbacks(this);
        setStatusBarColor();
        setLoadingVariables();

        retryLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        setOptionsLayout();
        overflowLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);

        changeViewVisiblity(scrollView, View.VISIBLE);
        if (articleObj != null) {
            setArticleData(articleObj);
            setToolBarTitleText(articleObj.getTitle());
        }
        loadData();
    }

    private void loadData() {
        if (CommonLib.isNetworkAvailable(this)) {
            findViewById(R.id.parent_scroll).setVisibility(View.VISIBLE);
            showView();
        } else {
            findViewById(R.id.parent_scroll).setVisibility(View.GONE);
            showNetworkErrorView();
        }
        if (articleObj != null) {
            setArticleData(articleObj);
            setToolBarTitleText(articleObj.getTitle());
        }
    }

    public void setToolBarTitleText(String text) {
        //change the titile to text to set the name as title in toolbar
        titleText.setText("Article");
    }

    private void setOptionsLayout() {
        findViewById(R.id.more_info).setOnClickListener(this);
        overflowLayout = (LinearLayout) findViewById(R.id.overflow_layout);
        likeLayout = (LinearLayout) findViewById(R.id.like_layout);
        shareLayout = (LinearLayout) findViewById(R.id.share_layout);
        addClicksForOptions();
    }

    private void addClicksForOptions() {
        likeLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);

    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, null);
        titleText = (TextView) view.findViewById(R.id.title_textview);
        // titleText.setText(getString(R.string.article_text));
        toolbar.addView(view);
    }

    private void setArticleData(HomeArticleObj obj) {
        ZTracker.logGAEvent(this, "Article", articleObj.getTitle(), "");
        // int height = (3 * getDisplayMetrics().widthPixels) / 4;
        int height = (articleObj.getHeight() * getDisplayMetrics().widthPixels) / articleObj.getWidth();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        FrameLayout imageFrame = (FrameLayout) findViewById(R.id.image_frame);
        imageFrame.setLayoutParams(lp);
        final ImageView featuredImg = (ImageView) findViewById(R.id.featured_img);
        // featuredImg.setLayoutParams(lp);
        loadImage(obj.getImage(), featuredImg, lp.width, lp.height, false, false);
        TextView title = (TextView) findViewById(R.id.title);
        // this.title refers to the string containing name of article and
        // "title" is a textView
        this.title = obj.getTitle();
        title.setText(this.title);
        TextView description = (TextView) findViewById(R.id.description);
        description.setText(obj.getSubtitle());
        TextView publisherName = (TextView) findViewById(R.id.publisher_name);
        publisherName.setText(obj.getPublisher_name());
        CircularImageView publisherImg = (CircularImageView) findViewById(R.id.publisher_img);
        loadImage(obj.getPublisher_profile_photo(), publisherImg,
                getResources().getDimensionPixelSize(R.dimen.pro_image_size),
                getResources().getDimensionPixelSize(R.dimen.pro_image_size), false, false);
        VideoEnabledWebView contentContainer = (VideoEnabledWebView) findViewById(R.id.content_container);
        //contentContainer.loadUrl(AppApplication.getInstance().getBaseUrl() + obj.getContent_url());
        /*contentContainer.getSettings().setJavaScriptEnabled(true);
      WebSettings webSettings = contentContainer.getSettings();
      webSettings.setPluginState(WebSettings.PluginState.ON);
      webSettings.setJavaScriptEnabled(true);*/
        //webSettings.setUseWideViewPort(true);
        //webSettings.setLoadWithOverviewMode(true);
        //VideoEnabledWebChromeClient webChromClient = new VideoEnabledWebChromeClient();
        //webSettings.setDefaultFontSize((int) getResources().getDimension(R.dimen.font_medium));

        WebSettings webSettings = contentContainer.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT <= 18) {
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        }

        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        contentContainer.loadUrl(AppApplication.getInstance().getBaseUrl() + obj.getContent_url());
        //contentContainer.setWebViewClient(new HelloWebViewClient());
        scrollView.setOnScrollParallexListener(new OnScrollParallexListener() {

            @Override
            public void onScrollParallexListener(CustomScrollView scrollview, int dy) {
                featuredImg.setTranslationY((int) (dy / 1.3));
            }
        });
        findViewById(R.id.like_icon).setSelected(obj.getIs_favourite());
        setUpIndexingApi();
    }

    private void setArticleData1(ArticleDetailObject obj) {
        setToolBarTitleText(obj.getTitle());
        int height = (3 * getDisplayMetrics().widthPixels) / 4;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        FrameLayout imageFrame = (FrameLayout) findViewById(R.id.image_frame);
        imageFrame.setLayoutParams(lp);
        final ImageView featuredImg = (ImageView) findViewById(R.id.featured_img);
        loadImage(obj.getImage_featured(), featuredImg, lp.width, lp.height, false, false);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(obj.getTitle());
        TextView description = (TextView) findViewById(R.id.description);
        description.setText(obj.getSubtitle());
        TextView publisherName = (TextView) findViewById(R.id.publisher_name);
        publisherName.setText(obj.getPublisher_name());
        CircularImageView publisherImg = (CircularImageView) findViewById(R.id.publisher_img);
        loadImage(obj.getPublisher_profile_photo(), publisherImg,
                getResources().getDimensionPixelSize(R.dimen.pro_image_size),
                getResources().getDimensionPixelSize(R.dimen.pro_image_size), false, false);
        //WebView contentContainer = (WebView) findViewById(R.id.content_container);
        VideoEnabledWebView contentContainer = (VideoEnabledWebView) findViewById(R.id.content_container);
        contentContainer.loadUrl(AppApplication.getInstance().getBaseUrl() + obj.getContent_url());
        scrollView.setOnScrollParallexListener(new OnScrollParallexListener() {

            @Override
            public void onScrollParallexListener(CustomScrollView scrollview, int dy) {
                featuredImg.setTranslationY((int) (dy / 1.3));
            }
        });
        findViewById(R.id.like_icon).setSelected(obj.getIs_favourite());
        setUpIndexingApi();
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.filter).setVisible(false);

        return true;
    }*/

    @Override
    public void onRequestStarted(String requestTag) {
        if (requestTag.equalsIgnoreCase(ARTICLE_DETAIL_REQUEST_TAG)) {
            showLoadingView();
            changeViewVisiblity(scrollView, View.GONE);
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (requestTag.equalsIgnoreCase(ARTICLE_DETAIL_REQUEST_TAG)) {
            showView();
            changeViewVisiblity(scrollView, View.VISIBLE);
            // title to be shown in auto complete ui
            articleDetailObject = (ArticleDetailObject) obj;
            title = articleDetailObject.getTitle();
            setArticleData1(articleDetailObject);
        }
    }

    private void setUpIndexingApi() {
        // Construct the Action performed by the user
        mClient.connect();
        APP_URI = Uri.parse(baseAppUri + slug);
        WEB_URL = Uri.parse(baseWebUri + slug);
        Action viewAction = Action.newAction(Action.TYPE_VIEW, title, WEB_URL, APP_URI);

        // Call the App Indexing API start method after the view has
        // completely
        // rendered
        // Call the App Indexing API view method
        PendingResult<Status> result = AppIndex.AppIndexApi.start(mClient, viewAction);

        result.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    CommonLib.ZLog(TAG, "App Indexing API: Recorded article " + title + " view successfully.");
                } else {
                    Log.e(TAG, "App Indexing API: There was an error recording the article view." + status.toString());
                }
            }
        });
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (requestTag.equalsIgnoreCase(ARTICLE_DETAIL_REQUEST_TAG)) {
            if (((ErrorObject) obj).getErrorCode() == 500) {
                showToast("Could not load. Try again");
            } else {
                showToast(((ErrorObject) obj).getErrorMessage());
            }
            changeViewVisiblity(scrollView, View.GONE);
            showNetworkErrorView();

        }
    }

    @Override
    protected void onDestroy() {
        isDestroyed = true;
        UploadManager.getInstance().removeCallback(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_info:
                if (dimView.isShown()) {
                    animateOptionsLayoutOut();
                } else {
                    animateOptionsLayoutIn();
                }
                break;
            case R.id.dim_view:
                animateOptionsLayoutOut();
                break;
            case R.id.share_layout:

                Intent shareIntent = new Intent(ArticleDescriptionActivity.this, SharingOptionsActivity.class);
                shareIntent.putExtra("title", "Checkout this article ");
                shareIntent.putExtra("slug", slug);
                shareIntent.putExtra("type_name", AppConstants.ITEM_TYPE_ARTICLE);
                shareIntent.putExtra("item_name", title);
                shareIntent.putExtra("image_url", "/article/");
                shareIntent.putExtra("short_url", "www.zimply.in/article/" + slug);
                shareIntent.putExtra("tags", "");
                startActivity(shareIntent);

                break;
            case R.id.like_layout:
                if (!AppPreferences.isUserLogIn(this)) {
                    Intent intent = new Intent(this, BaseLoginSignupActivity.class);
                    intent.putExtra("inside", true);
                    startActivity(intent);
                } else {
                    if (articleObj.getIs_favourite()) {
                        makeUnLikeRequest();
                        findViewById(R.id.like_icon).setSelected(false);
                        articleObj.setIs_favourite(false);
                        showToast("Successfully removed from favourites");

                    } else {
                        makeLikeRequest();
                        findViewById(R.id.like_icon).setSelected(true);
                        articleObj.setIs_favourite(true);
                        showToast("Successfully added to favourites");

                    }
                    animateOptionsLayoutOut();
                }
                break;

        }
    }

    private void makeUnLikeRequest() {

        String url = AppApplication.getInstance().getBaseUrl() + MARK_UNFAVOURITE_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("favourite_item_id", articleObj.getFavorite_item_id()));
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        UploadManager.getInstance().makeAyncRequest(url, MARK_UN_FAVOURITE_REQUEST_TAG, articleObj.getSlug(),
                OBJECT_TYPE_MARKED_UNFAV, articleObj, list, null);
    }

    /**
     * Request for marking favourite
     */
    public void makeLikeRequest() {
        String url = AppApplication.getInstance().getBaseUrl() + MARK_FAVOURITE_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("item_type", ITEM_TYPE_ARTICLE + ""));
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        list.add(new BasicNameValuePair("item_id", articleObj.getSlug()));
        UploadManager.getInstance().makeAyncRequest(url, MARK_FAVOURITE_REQUEST_TAG, articleObj.getSlug(),
                OBJECT_TYPE_MARKED_FAV, articleObj, list, null);
    }

    private void animateOptionsLayoutIn() {

        overflowLayout.setVisibility(View.VISIBLE);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator anim = ObjectAnimator.ofFloat(overflowLayout, View.TRANSLATION_Y, overflowLayoutHeight, 0);
        anim.setDuration(200);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        dimView.setVisibility(View.VISIBLE);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(dimView, View.ALPHA, 0, 1);
        anim2.setDuration(200);
        anim2.setInterpolator(new AccelerateDecelerateInterpolator());
        anim2.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dimView.setOnClickListener(ArticleDescriptionActivity.this);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

        set.playTogether(anim, anim2);
        set.start();
        isOptionsLayoutShown = true;
    }

    private void animateOptionsLayoutOut() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator anim = ObjectAnimator.ofFloat(overflowLayout, View.TRANSLATION_Y, 0, overflowLayoutHeight);
        anim.setDuration(200);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                overflowLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(dimView, View.ALPHA, 1, 0);
        anim2.setDuration(200);
        anim2.setInterpolator(new AccelerateDecelerateInterpolator());
        anim2.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dimView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        set.playTogether(anim, anim2);
        set.start();
        isOptionsLayoutShown = false;
    }

    @SuppressLint("NewApi")
    @Override
    public void onGlobalLayout() {
        this.overflowLayoutHeight = overflowLayout.getHeight();
        if (android.os.Build.VERSION.SDK_INT == 16) {
            overflowLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            overflowLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }

        overflowLayout.setVisibility(View.GONE);
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object respose, boolean status, int parserId) {
        if (requestType == MARK_FAVOURITE_REQUEST_TAG) {
            if (status) {
                articleObj.setFavorite_item_id((String) data);
            }
        } else if (requestType == MARK_UN_FAVOURITE_REQUEST_TAG) {
            if (status) {

            }
        }
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object object) {

    }

    @Override
    protected void onStop() {

        // Call end() and disconnect the client

      /* final Uri APP_URI = Uri.parse(baseAppUri + slug); */

        Action viewAction = Action.newAction(Action.TYPE_VIEW, title, APP_URI);
        PendingResult<Status> result = AppIndex.AppIndexApi.end(mClient, viewAction);
        CommonLib.ZLog("ArticleDescriptionActivity", result.toString());
        result.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.d(TAG, "App Indexing API: Recorded article  view end successfully.");
                } else {
                    Log.e(TAG, "App Indexing API: There was an error recording the article view." + status.toString());
                }

            }
        });

        mClient.disconnect();
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (articleObj != null && articleObj.getFavorite_item_id() != null) {
            intent.putExtra("is_favourite", articleObj.getIs_favourite());
            intent.putExtra("favourite_item_id", articleObj.getFavorite_item_id());
        }
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onNewIntent(Intent intent) {
        HomeArticleObj articleObj = getIntent().getExtras().getParcelable("article_obj");
        slug = getIntent().getStringExtra("slug");
        if (articleObj != null) {
            slug = articleObj.getSlug();
            setArticleData(articleObj);
        } else if (slug != null) {
            String url = AppApplication.getInstance().getBaseUrl() + "content/article/" + slug;
            GetRequestManager.getInstance().makeAyncRequest(url, ARTICLE_DETAIL_REQUEST_TAG,
                    OBJECT_TYPE_ARTICLE_DETAIL);
        }
    }


    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView webview, String url) {
            webview.setWebChromeClient(new WebChromeClient() {

                private View mCustomView;

                @Override
                public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
                    // if a view already exists then immediately terminate the new one
                    if (mCustomView != null) {
                        callback.onCustomViewHidden();
                        return;
                    }

               /*// Add the custom view to its container.
               mCustomViewContainer.addView(view, COVER_SCREEN_GRAVITY_CENTER);
               mCustomView = view;
               mCustomViewCallback = callback;

               // hide main browser view
               mContentView.setVisibility(View.GONE);

               // Finally show the custom view container.
               mCustomViewContainer.setVisibility(View.VISIBLE);
               mCustomViewContainer.bringToFront();*/
                }

            });

            webview.loadUrl(url);

            return true;
        }
    }
}