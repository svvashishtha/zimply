package com.application.zimplyshop.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.application.zimply.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.HomePhotoObj;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UrlRouter extends Activity implements GetRequestListener, RequestTags, ObjectTypes {

    private final String TAG = "URLRouter";
    HomePhotoObj homePhotoObj;
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

                    if (firstSegment.contains("article")) {
                        try {
                            thirdSegment = segments.get(2);
                        } catch (IndexOutOfBoundsException ie) {
                            ie.printStackTrace();
                        }
                        navigateToArticles(secondSegment);
                    } else if (firstSegment.contains("home")) {
                        navigateToHome();
                    } else if (firstSegment.contains("e")) {
                        navigateToExperts();
                    } else if (firstSegment.equalsIgnoreCase("p")) {
                        navigateToPhotos(secondSegment);
                    } else if (firstSegment.contains("shop-product")) {
                        String id = new URL(intent.getDataString()).getQuery();
                        id = id.split("=")[1];
                        String slug = segments.get(1);
                        navigateToProduct(slug, id);
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

    private void navigateToProduct(String slug, String id) {
        Intent intent = new Intent(UrlRouter.this, ProductDetailsActivity.class);
        intent.putExtra("slug", slug);
        intent.putExtra("is_shared",true);
        long pId = Integer.parseInt(id);
        intent.putExtra("id", pId);
        this.finish();
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

    private void navigateToExperts() {
        intent = new Intent(UrlRouter.this, ExpertsListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Source", "Router");
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();
    }

    private void navigateToArticles(String slug) {
        intent = new Intent(UrlRouter.this, ArticleDescriptionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("slug", slug);
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();
    }

    private void navigateToPhotos(String slug) {
        String url = AppApplication.getInstance().getBaseUrl() + "content/new-photo/" + slug;
        GetRequestManager.getInstance().makeAyncRequest(url, GET_PHOTO_SINGLE, OBJECT_TYPE_HOME_PHOTO);

    }

    private void navigateToProfile() {
        intent = new Intent(UrlRouter.this, UserProfileActivity.class);
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
        {
            if (requestTag.equalsIgnoreCase(GET_PHOTO_SINGLE)) {
                homePhotoObj = (HomePhotoObj) obj;
                intent = new Intent(UrlRouter.this, PhotoDetailsActivity.class);
                ArrayList<HomePhotoObj> temp = new ArrayList<>();
                temp.add(homePhotoObj);
                intent.putExtra("photo_objs", temp);
                intent.putExtra("position", 0);
                startActivity(intent);
                this.finish();
            }
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {

    }

}
