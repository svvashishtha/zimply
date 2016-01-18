package com.application.zimplyshop.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import java.net.URL;
import java.util.List;

public class UrlRouter extends Activity implements GetRequestListener, RequestTags, ObjectTypes {

    private final String TAG = "URLRouter";
    Intent intent;
    String firstSegment = "";
    String thirdSegment = "";
    String mType, mId;

    boolean mFromExternalSource = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.url_router);

        CommonLib.ZLog(TAG, "URLRouter");

        SharedPreferences prefs = getSharedPreferences(CommonLib.preferenceName, 0);
        GetRequestManager.getInstance().addCallbacks(this);
        // check if it came here from any image sharing source
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (getIntent().getAction() != null
                        && getIntent().getAction().equals(Intent.ACTION_SEND)) {
                    mFromExternalSource = true;
                }
            }
        }

        try {

            if (true || (prefs != null && prefs.getInt("uid", 0) > 0)) {
                urlNavigation();
            } else
                navigateToTour();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void urlNavigation() {

        try {
            intent = this.getIntent();
            final String action = intent.getAction();

            CommonLib.ZLog(TAG, action + " " + intent.getDataString());

            if (Intent.ACTION_VIEW.equals(action)) {
                final List<String> segments = intent.getData()
                        .getPathSegments();

                if (segments.size() > 0) {

                    firstSegment = segments.get(0);
                    String secondSegment = segments.get(1);

                    if (firstSegment.contains("home")) {
                        navigateToHome();
                    } else if (firstSegment.equalsIgnoreCase("p")) {
                        navigateToPhotos(secondSegment);
                    } else if (firstSegment.contains("shop-product")) {
                        String id = new URL(intent.getDataString()).getQuery();
                        id = id.split("=")[1];
                        String slug = segments.get(1);
                        navigateToProduct(slug, id);
                    } else if (firstSegment.contains("shop")) {
                        String shopId = segments.get(1);
                        String shopName = "";
                        if (segments.size() >= 3)
                            shopName = segments.get(2);
                        navigateToShop(shopId, shopName);
                    } else {
                        navigateToTour();
                    }

                } else {
                    navigateToTour();
                }

            } else {
                navigateToTour();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToShop(String shopId, String shopName) {
        if (shopId != null && shopId.length() > 0) {
            Intent listIntent = new Intent(this, ProductListingActivity.class);
            listIntent.putExtra("category_id", "0");
            listIntent.putExtra("hide_filter", false);
            if (shopName.length() > 0) {
                shopName = shopName.replaceAll("-", " ");
                listIntent.putExtra("category_name", shopName);
            }
            listIntent.putExtra("url", AppConstants.GET_PRODUCT_LIST);
            listIntent.putExtra("discount_id", Integer.parseInt(shopId)
            );
            UrlRouter.this.finish();
            startActivity(listIntent);
        }
    }

    private void navigateToProduct(String slug, String id) {
        Intent intent = new Intent(UrlRouter.this, NewProductDetailActivity.class);
        intent.putExtra("slug", slug);
        intent.putExtra("is_shared", true);
        int pId = Integer.parseInt(id);
        intent.putExtra("id", pId);
        this.finish();

        //        GA Ecommerce
        intent.putExtra("productActionListName", "Navigate To Product Through Slug");
        intent.putExtra("screenName", "URLRouter Activity");
        intent.putExtra("actionPerformed", ProductAction.ACTION_CLICK);

        startActivity(intent);

    }

    private void navigateToHome() {
        intent = new Intent(UrlRouter.this, HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Source", "Router");
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();
    }

    private void navigateToTour() {
        intent = new Intent(UrlRouter.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToPhotos(String slug) {
        String url = AppApplication.getInstance().getBaseUrl() + "content/new-photo/" + slug;
        GetRequestManager.getInstance().makeAyncRequest(url, GET_PHOTO_SINGLE, OBJECT_TYPE_HOME_PHOTO);

    }

    private void navigateToProfile() {
        intent = new Intent(UrlRouter.this, ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Source", "Router");
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (requestTag.equalsIgnoreCase(GET_PHOTO_SINGLE)) {
            CommonLib.ZLog("UrlRouter", "received:photo");
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {

    }

    @Override
    protected void onDestroy() {
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }
}
