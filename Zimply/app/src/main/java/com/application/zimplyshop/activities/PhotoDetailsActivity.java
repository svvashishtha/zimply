package com.application.zimplyshop.activities;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.HomePhotoObj;
import com.application.zimplyshop.baseobjects.PhotoListObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.utils.ZTracker;
import com.application.zimplyshop.widgets.ParallaxPageTransformer;
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

public class PhotoDetailsActivity extends BaseActivity
        implements ImageLoaderManager.ImageLoaderCallback, OnClickListener, OnGlobalLayoutListener, UploadManagerCallback, RequestTags, GetRequestListener {

    int width, height;
    Boolean asyncStatus = false;

    LinearLayout overflowLayout;

    View dimView;

    int overflowLayoutHeight;

    boolean isOptionsLayoutShown;

    LinearLayout likeLayout, shareLayout/*, viewSourceLayout*/;
    ViewPager pager;
    PhotoPagerAdapter mAdapter;
    ArrayList<HomePhotoObj> photoObjects = new ArrayList<HomePhotoObj>();
    int position = 0;
    String nextUrl;
    GoogleApiClient mClient;
    String baseAppUri = "android-app://com.application.zimply/http/www.zimply.in/p/";
    String baseWebUri = "http://www.zimply.in/home/photo_index/";
    String slug = null;
    boolean isDestroyed;
    Uri WEB_URL;
    Uri APP_URI;
    ViewPager.OnPageChangeListener mListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int pos) {
//            if(position < this.position) {
//            }
            //call the load more if the current page is 3 pages behind the last page
            position = pos;
            if (photoObjects != null) {
                int totalObjects = photoObjects.size();
                if (pos + 3 > totalObjects) {
                    //call the load more
                    loadData();
                }
            }
            findViewById(R.id.like_icon).setSelected(photoObjects.get(position).getIs_favourite());
            setPageTitle(position);
            ZTracker.logGAEvent(PhotoDetailsActivity.this, "Photo", photoObjects.get(position).getSlug(), "");
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private boolean firstTime = true;
    private boolean destroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_details_activity);
        dimView = findViewById(R.id.dim_view);
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        // indexing
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // setting view pager
        onNewIntent(getIntent());
        pager = (ViewPager) findViewById(R.id.photo_view_pager);
        mAdapter = new PhotoPagerAdapter(photoObjects);
        try {
            setUpIndexingApi();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pager.setAdapter(mAdapter);
        pager.setOffscreenPageLimit(1);
        pager.setCurrentItem(position);
        pager.setOnPageChangeListener(mListener);
        pager.setPageTransformer(false, new ParallaxPageTransformer((float) .5, (float) .5, R.id.photo_imageview));

        fixSizes();
        setListeners();
        setOptionsLayout();

        overflowLayout.getViewTreeObserver().addOnGlobalLayoutListener(this);
        setListeners();
        GetRequestManager.getInstance().addCallbacks(this);
        UploadManager.getInstance().addCallback(this);

        if (photoObjects.get(position) != null)
            findViewById(R.id.like_icon).setSelected(photoObjects.get(position).getIs_favourite());
        if (firstTime) {
            showStuff();
            firstTime = false;
        }
        setPageTitle(position);
    }

    private void setUpIndexingApi() {
        // Construct the Action performed by the user
        mClient.connect();
        APP_URI = Uri.parse(baseAppUri + photoObjects.get(position).getSlug());
        WEB_URL = Uri.parse(baseWebUri + photoObjects.get(position).getSlug());
        Action viewAction = Action.newAction(Action.TYPE_VIEW, photoObjects.get(position).getImage2(), WEB_URL, APP_URI);

        // Call the App Indexing API start method after the view has
        // completely
        // rendered
        // Call the App Indexing API view method
        PendingResult<Status> result = AppIndex.AppIndexApi.start(mClient, viewAction);

        result.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    CommonLib.ZLog("PhotoDetailsActivity", "App Indexing API: Recorded photo " + photoObjects.get(position).getImage2() + " view successfully.");
                } else {
                    CommonLib.ZLog("PhotoDetailsActivity", "App Indexing API: There was an error recording the photo view." + status.toString());
                }
            }
        });
    }

    public void setPageTitle(int position) {
        ((TextView) findViewById(R.id.gallery_image_info)).setText(photoObjects.get(position).getCat() + "-" + photoObjects.get(position).getStyle());
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        UploadManager.getInstance().removeCallback(this);
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();

    }

    private void setOptionsLayout() {
        findViewById(R.id.more_info).setOnClickListener(this);
        overflowLayout = (LinearLayout) findViewById(R.id.overflow_layout);
        likeLayout = (LinearLayout) findViewById(R.id.like_layout);
        shareLayout = (LinearLayout) findViewById(R.id.share_layout);

        //   viewSourceLayout = (LinearLayout) findViewById(R.id.view_source_layout);
        // ((LinearLayout) findViewById(R.id.source_parent_layout)).setVisibility(View.VISIBLE);
        addClicksForOptions();
    }

    private void addClicksForOptions() {
        likeLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        // viewSourceLayout.setOnClickListener(this);

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
                dimView.setOnClickListener(PhotoDetailsActivity.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);*/
        menu.clear();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        setImage();
        if (isOptionsLayoutShown) {
            overflowLayout.setVisibility(View.VISIBLE);
        } else {
            overflowLayout.setVisibility(View.GONE);
        }
    }

    private void setImage() {
        if (photoObjects.get(position) != null) {
            ImageView imageView = (ImageView) findViewById(R.id.photo_imageview);

            new ImageLoaderManager(this).setImageFromUrl(photoObjects.get(position).getImage(), imageView, "users", width, height, false,
                    false);
            setPageTitle(position);
        }
    }

    private void fixSizes() {
        // findViewById(R.id.photo_header_container).setPadding(width / 20,
        // width / 20, width / 20, width / 20);

        // findViewById(R.id.photo_header_container_parent).getLayoutParams().height
        // = 3 * width / 20;
        // findViewById(R.id.photo_footer_container).getLayoutParams().height =
        // 3 * width / 20;
    }

    private void setListeners() {
        findViewById(R.id.back_key).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.get_look).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PhotoDetailsActivity.this, FilterActivity.class);
                intent.putExtra("photo_slug", photoObjects.get(position).getSlug());
                intent.putExtra("type", AppConstants.ITEM_TYPE_PHOTO);
                startActivity(intent);
                overridePendingTransition(R.anim.animate_bottom_in, R.anim.animate_bottom_out);
            }
        });
        findViewById(R.id.back_arrow_container).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (photoObjects.get(position) != null)
            ((TextView) findViewById(R.id.gallery_image_info)).setText(photoObjects.get(position).getCat() + "-" + photoObjects.get(position).getStyle());
        findViewById(R.id.gallery_image_info_container).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void hideStuff() {
        findViewById(R.id.photo_header_container_parent).setVisibility(View.INVISIBLE);
        findViewById(R.id.photo_footer_container).setVisibility(View.INVISIBLE);
    }

    private void showStuff() {
        findViewById(R.id.photo_header_container_parent).setVisibility(View.VISIBLE);
        findViewById(R.id.photo_footer_container).setVisibility(View.VISIBLE);
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				if(!destroyed)
//					hideStuff();
//			}
//		}, 500);
    }

    @Override
    public void loadingStarted() {
        if (!destroyed)
            showStuff();
    }

    @Override
    public void loadingFinished(Bitmap bitmap) {
        if (!destroyed)
            hideStuff();
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

                Intent shareIntent = new Intent(PhotoDetailsActivity.this, SharingOptionsActivity.class);
                shareIntent.putExtra("title", "Checkout this awesome photo");
                shareIntent.putExtra("slug", photoObjects.get(position).getSlug());
                shareIntent.putExtra("type_name", AppConstants.ITEM_TYPE_PHOTO);
                shareIntent.putExtra("item_name", "");
                shareIntent.putExtra("image_url", "/p/");
                try {
                    shareIntent.putExtra("short_url", "www.zimply.in/p/" + photoObjects.get(position).getSlug());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                shareIntent.putExtra("tags", "");
                startActivity(shareIntent);

                break;
            case R.id.like_layout:

                if (!AppPreferences.isUserLogIn(this)) {
                    Intent intent = new Intent(this, BaseLoginSignupActivity.class);
                    intent.putExtra("inside", true);
                    startActivity(intent);
                } else {
                    if (photoObjects.get(position).getIs_favourite()) {
                        makeUnLikeRequest();
                        findViewById(R.id.like_icon).setSelected(false);
                        photoObjects.get(position).setIs_favourite(false);
                        showToast("Successfully removed from favourites");

                    } else {
                        makeLikeRequest();
                        findViewById(R.id.like_icon).setSelected(true);
                        photoObjects.get(position).setIs_favourite(true);
                        showToast("Successfully added from favourites");

                    }
                    animateOptionsLayoutOut();
                }
                break;
            /*case R.id.view_source_layout:
                startExpertActivity();
                break;*/
        }
    }

    private void startExpertActivity() {
        Intent intent = new Intent(this, ExpertProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("expert_obj", photoObjects.get(position).getExpert());
        intent.putExtras(bundle);
        startActivity(intent);

    }

    private void makeUnLikeRequest() {

        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.MARK_UNFAVOURITE_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("favourite_item_id", photoObjects.get(position).getFavorite_item_id()));

        UploadManager.getInstance().makeAyncRequest(url, MARK_UN_FAVOURITE_REQUEST_TAG, photoObjects.get(position).getSlug(),
                ObjectTypes.OBJECT_TYPE_MARKED_UNFAV, photoObjects.get(position), list, null);
    }

    /**
     * Request for marking favourite
     */
    public void makeLikeRequest() {
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.MARK_FAVOURITE_URL;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("item_type", AppConstants.ITEM_TYPE_PHOTO + ""));
        list.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
        list.add(new BasicNameValuePair("item_id", photoObjects.get(position).getSlug()));
        UploadManager.getInstance().makeAyncRequest(url, MARK_FAVOURITE_REQUEST_TAG, photoObjects.get(position).getSlug(),
                ObjectTypes.OBJECT_TYPE_MARKED_FAV, photoObjects.get(position), list, null);
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
                showToast("Favourite ItemId" + data);

                photoObjects.get(position).setFavorite_item_id((String) data);
            }
        } else if (requestType == MARK_UN_FAVOURITE_REQUEST_TAG) {
            if (status) {
                showToast((String) data);
            }
        }
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object object) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (photoObjects.get(position) != null) {
            intent.putExtra("is_favourite", photoObjects.get(position).getIs_favourite());
            intent.putExtra("favourite_item_id", photoObjects.get(position).getFavorite_item_id());
        }
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    public void onNewIntent(Intent intent) {
        if (getIntent() != null && intent.getExtras() != null) {
            if (intent.getExtras().containsKey("photo_objs")
                    && intent.getExtras().get("photo_objs") instanceof ArrayList<?>) {
                photoObjects = (ArrayList<HomePhotoObj>) intent.getSerializableExtra("photo_objs");
                if (intent.getExtras().containsKey("position"))
                    position = intent.getExtras().getInt("position");
            } else if (intent.getExtras().containsKey("slug")) {

            }
            if (intent.getExtras().containsKey("nextUrl")) {
                nextUrl = String.valueOf(intent.getExtras().get("nextUrl"));
            }
        }

    }

    private void loadData() {

        String finalUrl;
        if (nextUrl == null) {
            finalUrl = AppApplication.getInstance().getBaseUrl() + AppConstants.PHOTOS_LISTING_URL + "?photo_width=300"
                    + "&filter=0"
                    + (AppPreferences.isUserLogIn(this) ? "&userid=" + AppPreferences.getUserID(this) : "");
        } else {
            finalUrl = AppApplication.getInstance().getBaseUrl() + nextUrl;
        }

        GetRequestManager.getInstance().makeAyncRequest(finalUrl, PHOTOS_LIST_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_PHOTO_LIST_OBJECT);
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (!destroyed && requestTag.equalsIgnoreCase(PHOTOS_LIST_REQUEST_TAG)) {
//            if (photosList.getAdapter() == null || photosList.getAdapter().getItemCount() == 0 || isRefreshData) {
//                if (isRefreshData && ((PhotosListRecyclerAdapter) photosList.getAdapter() != null)) {
//                    ((PhotosListRecyclerAdapter) photosList.getAdapter()).removePreviousData();
//                }
//            } else {
//
//            }
//            isLoading = true;
//        } else if (!destroyed && (requestTag.equalsIgnoreCase(COMPLETE_CATEGORY_LIST_REQUEST_TAG)
//                || requestTag.equalsIgnoreCase(ARTICLE_PHOTO_CAT_REQUEST_TAG))) {
//            showLoadingView();
//            changeViewVisiblity(photosList, View.GONE);
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (!destroyed && requestTag.equalsIgnoreCase(PHOTOS_LIST_REQUEST_TAG)) {
            if (((PhotoListObject) obj).getOutput().size() == 0) {
                if (mAdapter == null || mAdapter.getCount() == 1) {
//                    showNullCaseView("No Photos");

                } else {
//                    ((PhotosListRecyclerAdapter) photosList.getAdapter()).removeItem();
//                    Toast.makeText(this, "No more Photos", Toast.LENGTH_SHORT).show();
                }
//                isRequestAllowed = false;
            } else {
                photoObjects.addAll(((PhotoListObject) obj).getOutput());
                mAdapter.notifyDataSetChanged();
                nextUrl = ((PhotoListObject) obj).getNext_url();
//                showView();
//                changeViewVisiblity(photosList, View.VISIBLE);
//                if (((PhotoListObject) obj).getOutput().size() < 10) {
//                    isRequestAllowed = false;
//                    ((PhotosListRecyclerAdapter) photosList.getAdapter()).removeItem();
//                } else {
//                    isRequestAllowed = true;
//                }
            }
//            if (isRefreshData) {
//                isRefreshData = false;
//            }
//
//            isLoading = false;
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (!destroyed && requestTag.equalsIgnoreCase(PHOTOS_LIST_REQUEST_TAG)) {
            if (mAdapter == null || mAdapter.getCount() == 0) {
//                showNetworkErrorView();
//                changeViewVisiblity(pager, View.GONE);
//                isRefreshData = false;
            } else {
                if (((ErrorObject) obj).getErrorCode() == 500) {
                    showToast("Could not load more data");

                } else {
                    showToast(((ErrorObject) obj).getErrorMessage());
                }

//                ((PhotosListRecyclerAdapter) photosList.getAdapter()).removeItem();
//                isRequestAllowed = false;
            }
//            isLoading = false;
        }
    }

    @Override
    protected void onStop() {

        // Call end() and disconnect the client

      /* final Uri APP_URI = Uri.parse(baseAppUri + slug); */
        if (photoObjects.get(position).getImage2() != null) {
            Action viewAction = Action.newAction(Action.TYPE_VIEW, photoObjects.get(position).getImage2(), APP_URI);
            PendingResult<Status> result = AppIndex.AppIndexApi.end(mClient, viewAction);

            result.setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        CommonLib.ZLog(TAG, "App Indexing API: Recorded photo " + photoObjects.get(position).getImage2() + " view end successfully.");
                    } else {
                        Log.e(TAG, "App Indexing API: There was an error recording the photo view." + status.toString());
                    }

                }
            });
        }
        mClient.disconnect();
        super.onStop();
    }

    public class PhotoPagerAdapter extends PagerAdapter {

        ArrayList<HomePhotoObj> photoObjs;

        public PhotoPagerAdapter(ArrayList<HomePhotoObj> photoObjs) {
            this.photoObjs = photoObjs;
        }

        @Override
        public int getCount() {
            return photoObjs.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            final HomePhotoObj photoObject = photoObjs.get(position);
            LinearLayout photoLayout;
            if (container.findViewWithTag(position) == null) {
                photoLayout = (LinearLayout) (getLayoutInflater().inflate(R.layout.photo_list_item_snippet, null));
                photoLayout.setTag(position);
            } else {
                photoLayout = (LinearLayout) container.findViewWithTag(position);
            }
            final ImageView imageView = (ImageView) photoLayout.findViewById(R.id.photo_imageview);

            new ImageLoaderManager(PhotoDetailsActivity.this).setImageFromUrl(photoObject.getImage2(), imageView, "users", width / 20, height / 20, false,
                    false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!destroyed)
                        new ImageLoaderManager(PhotoDetailsActivity.this).setImageFromUrl(photoObject.getImage(), imageView, "photo_details", width, height, false,
                                false);
                }
            }, 200);

          /*  if(photoObject.getImage2()!=null)
                new ImageLoaderManager(PhotoDetailsActivity.this).setImageFromUrl(photoObject.getImage2(), imageView, "users", width, height, false,
                    false);
*/
            if (container.findViewWithTag(position) == null)
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

    }
}
