package com.application.zimply.activities;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimply.adapters.MenuAdapter;
import com.application.zimply.application.AppApplication;
import com.application.zimply.baseobjects.BannerObject;
import com.application.zimply.baseobjects.ErrorObject;
import com.application.zimply.baseobjects.HomeArticleObj;
import com.application.zimply.baseobjects.HomeExpertObj;
import com.application.zimply.baseobjects.HomeObject;
import com.application.zimply.baseobjects.HomePhotoObj;
import com.application.zimply.baseobjects.HomeProductObj;
import com.application.zimply.baseobjects.ShopCategoryObject;
import com.application.zimply.baseobjects.SignupObject;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.extras.ObjectTypes;
import com.application.zimply.firstRunOverlay.OnShowcaseEventListener;
import com.application.zimply.firstRunOverlay.ShowcaseView;
import com.application.zimply.firstRunOverlay.ViewTarget;
import com.application.zimply.fragments.ArticleListingFragment;
import com.application.zimply.fragments.BannerDialogFragment;
import com.application.zimply.fragments.ExpertCategoryFragment;
import com.application.zimply.fragments.PhotosListingFragment;
import com.application.zimply.fragments.PhotosListingFragmentWebView;
import com.application.zimply.fragments.ProductsListFragment;
import com.application.zimply.managers.GetRequestListener;
import com.application.zimply.managers.GetRequestManager;
import com.application.zimply.objects.AllProducts;
import com.application.zimply.preferences.AppPreferences;
import com.application.zimply.serverapis.RequestTags;
import com.application.zimply.utils.CommonLib;
import com.application.zimply.utils.UploadManager;
import com.application.zimply.utils.UploadManagerCallback;
import com.application.zimply.utils.ZTracker;
import com.application.zimply.utils.fab.FABControl;
import com.application.zimply.utils.fab.FABUnit;
import com.application.zimply.widgets.CircularImageView;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Home Activity for a user who opens the app
 *
 * @author Umesh
 */
public class HomeActivity extends BaseActivity implements OnClickListener,
        RequestTags, GetRequestListener, UploadManagerCallback, AppConstants, FABControl.OnFloatingActionsMenuUpdateListener {

    private static final int FRAGMENT_PHOTO = 0;
    private static final int FRAGMENT_ARTICLE = 1;
    private static final int FRAGMENT_EXPERT = 2;
    private static final int FRAGMENT_PRODUCT = 3;
    private final int EMAIL_FEEDBACK = 1500;
    DrawerLayout mDrawer;
    int width, height;
    ActionBarDrawerToggle mToggle;
    ScrollView parentScrollView;
    boolean isDestroyed = false;
    ImageView arrowRight;
    boolean isArrowHidden;
    ArrayList<HomePhotoObj> photos = new ArrayList<HomePhotoObj>();
    ArrayList<HomeArticleObj> articles = new ArrayList<HomeArticleObj>();
    ArrayList<HomeExpertObj> experts = new ArrayList<HomeExpertObj>();
    ArrayList<HomeProductObj> deals = new ArrayList<HomeProductObj>();
    ArrayList<HomeProductObj> products = new ArrayList<HomeProductObj>();
    ArrayList<ShopCategoryObject> shopCategories = new ArrayList<ShopCategoryObject>();
    TextView noInterNetConnection;
    boolean noNetworkViewAdded = false;
    boolean isFreshDataLoaded;
    MainFragmentsAdapter adapter;
    ViewPager pager;
    TextView toolbarTitle;
    MenuItem filterItem, searchItem;
    HashMap<Integer, Fragment> fragments;
    Runnable r2 = new Runnable() {

        @Override
        public void run() {
            if (!isDestroyed) {
                animateArrowForward();
            }
            // fadeOutView((LinearLayout) findViewById(R.id.username_layout));
        }

    };
    TabLayout mTabs;
    ListView menuListView;
    View toolbarView;
    NavigationView navView;
    boolean isToLoginPage;
    // FAB Stuff
    private View mFABOverlay;
    private boolean mFABExpanded = false;
    private boolean mFABVisible = false;
    private boolean reloadUserInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity_layout);

        //add callbacks
        GetRequestManager.getInstance().addCallbacks(this);
        UploadManager.getInstance().addCallback(this);

        getWindow().setBackgroundDrawable(null);
        noInterNetConnection = new TextView(this);
        noInterNetConnection.setText("No Internet Connection");
        //this is where the no network error view will be added

        noInterNetConnection.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_signup_btn_bg));
        noInterNetConnection.setTextColor(getResources().getColor(R.color.white));
        noInterNetConnection.setGravity(Gravity.CENTER);
        noInterNetConnection.setHeight((int) getResources().getDimension(R.dimen.header_item_size));
        noInterNetConnection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();

        // parentScrollView = (ScrollView) findViewById(R.id.parent_scrollview);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // getSupportActionBar().setIcon(R.drawable.ic_app_title_logo);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Adding action bar drawer toggle
        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.app_name,
                R.string.app_name);
        mDrawer.setDrawerListener(mToggle);
        mToggle.syncState();
        setUpDrawer();
        setUpFAB();
        setNavigationList();
        pager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new MainFragmentsAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);

        mTabs = (TabLayout) findViewById(R.id.indicator);

        //   mTabs.setTabTextColors(R.color.heading_text_color,R.color.pager_tab_selected_color);

        mTabs.setupWithViewPager(pager);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case FRAGMENT_PHOTO:
                        showFAB(false);
                        searchItem.setVisible(false);
                        break;
                    case FRAGMENT_EXPERT:
                        showFAB(false);
                        searchItem.setVisible(true);
                        break;
                    case FRAGMENT_PRODUCT:
                        searchItem.setVisible(true);
                        if (isDestroyed)
                            return;
                        break;
                    default:
                        hideFAB();
                        searchItem.setVisible(false);
                        break;
                }
                if (position == 2 || position == 3) {
                    filterItem.setVisible(false);
                    findViewById(R.id.filter_applied).setVisibility(View.GONE);
                } else {
                    filterItem.setVisible(true);
                }
                if (position == 0) {
                    if (((PhotosListingFragmentWebView) fragments.get(0)).isFilterApplied()) {
                        findViewById(R.id.filter_applied).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.filter_applied).setVisibility(View.GONE);
                    }
                }
                if (position == 1) {
                    if (((ArticleListingFragment) fragments.get(1)).isFilterApplied()) {
                        findViewById(R.id.filter_applied).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.filter_applied).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        onNewIntent(getIntent());
        checkForUpdate();
        loadBanner();
        //Show case view lib - FAB
        prefs = getSharedPreferences("preference_name", 0);
        if (prefs.getBoolean("first_run_photos", true)) {
            ViewTarget target = new ViewTarget(R.id.photos_view_pager, this);
            ShowcaseView collectionFirstRun =
                    new ShowcaseView.Builder(this, true, ShowcaseView.TYPE_CIRCLE, target, ShowcaseView.CUSTOMMEASURE_CIRCLE_LARGE)
                            .setTarget(target)
                            .setContentTitle("See home design ideas")
                            .setStyle(R.style.ShowcaseView)
                            .setShowcaseEventListener(new OnShowcaseEventListener() {
                                @Override
                                public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                    if (prefs.getBoolean("first_run_fab", true)) {
                                        ViewTarget target = new ViewTarget(R.id.fab_post_request, HomeActivity.this);
                                        ShowcaseView collectionFirstRun =
                                                new ShowcaseView.Builder(HomeActivity.this, true, ShowcaseView.TYPE_CIRCLE, target, ShowcaseView.CUSTOMMEASURE_CIRCLE)
                                                        .setTarget(target)
                                                        .setContentTitle("Hire home design experts.")
                                                        .setStyle(R.style.ShowcaseView)
                                                        .setShowcaseEventListener(new OnShowcaseEventListener() {
                                                            @Override
                                                            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                                                if (prefs.getBoolean("first_run_product", true)) {
                                                                    ViewTarget target = new ViewTarget(R.id.shop_view_pager, HomeActivity.this);
                                                                    ShowcaseView collectionFirstRun =
                                                                            new ShowcaseView.Builder(HomeActivity.this, true, ShowcaseView.TYPE_CIRCLE, target, ShowcaseView.CUSTOMMEASURE_CIRCLE_SMALL)
                                                                                    .setTarget(target)
                                                                                    .setContentTitle("Shop exclusive products for your home")
                                                                                    .setStyle(R.style.ShowcaseView)
                                                                                    .build();

                                                                    // 'OK' button
                                                                    RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                                    lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                                                    lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                                                    lps.setMargins(0, 0, width / 3 - width / 10, height / 3);
                                                                    collectionFirstRun.setButtonPosition(lps);

                                                                    collectionFirstRun.show();
                                                                    prefs.edit().putBoolean("first_run_product", false).commit();
                                                                }
                                                            }

                                                            @Override
                                                            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                                            }

                                                            @Override
                                                            public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                                            }
                                                        })
                                                        .build();

                                        // 'OK' button
                                        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                        lps.setMargins(0, 0, width / 3 - width / 10, height / 3);
                                        collectionFirstRun.setButtonPosition(lps);

                                        collectionFirstRun.show();
                                        prefs.edit().putBoolean("first_run_fab", false).commit();
                                    }
                                }

                                @Override
                                public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                }

                                @Override
                                public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                }
                            })
                            .build();

            // 'OK' button
            RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lps.setMargins(0, 0, width / 3 - width / 10, height / 3);
            collectionFirstRun.setButtonPosition(lps);

            collectionFirstRun.show();
            prefs.edit().putBoolean("first_run_photos", false).commit();
        }

    }


    public void loadBanner(){
        String url = AppApplication.getInstance().getBaseUrl()+AppConstants.BANNER_URL;
        GetRequestManager.getInstance().makeAyncRequest(url,RequestTags.BANNER_REQUEST_TAG,ObjectTypes.OBJECT_TYPE_BANNER_OBJECT);
    }

    public void toggleFilterVisibility(boolean visibility) {
        filterItem.setVisible(visibility);

    }

    public void checkForUpdate() {

    }

    // makes FAB gone.
    public void hideFAB() {

        if (mFABVisible) {
            mFABVisible = false;

            ViewPropertyAnimator animator = findViewById(R.id.fab_post_request)
                    .animate()
                    .scaleX(0)
                    .scaleY(0)
                    .setDuration(50)
                    .setStartDelay(0)
                    .setInterpolator(new AccelerateInterpolator());

            animator.setListener(new AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    findViewById(R.id.fab_post_request).setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }
            });
        }
    }

    private void setUpFAB() {

//        ((FABControl) findViewById(R.id.multiple_actions)).setOnFloatingActionsMenuUpdateListener(this);
        // overlay behind FAB
        mFABOverlay = findViewById(R.id.fab_overlay);
        mFABOverlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                toggleFAB();
            }
        });
        mFABOverlay.setClickable(false);

        showFAB(true);
    }

    private void toggleFAB() {

//        ((FABControl) findViewById(R.id.multiple_actions)).toggle();
    }

    // makes FAB visible.
    public void showFAB(boolean delayed) {

        if (!mFABVisible) {
            mFABVisible = true;

            findViewById(R.id.fab_post_request).setVisibility(View.VISIBLE);
            ((FABUnit) findViewById(R.id.fab_post_request)).setSize(FABUnit.SIZE_MINI);

            if (delayed) {
                ViewPropertyAnimator animator = findViewById(R.id.fab_post_request).animate().scaleX(1).scaleY(1)
                        .setDuration(250).setInterpolator(new AccelerateInterpolator()).setStartDelay(700);
                // required | dont remove
                animator.setListener(new AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        try {
                            // showCashlessInFab();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });

            } else {

                ViewPropertyAnimator animator = findViewById(R.id.fab_post_request).animate().scaleX(1).scaleY(1)
                        .setDuration(200).setInterpolator(new AccelerateInterpolator());
                // required | dont remove
                animator.setListener(new AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        try {
                            // showCashlessInFab();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }
                });
            }
        }
    }

    public void fabSelected(View view) {
        switch (view.getId()) {
            case R.id.fab_post_request:
                Intent intent = new Intent(this, FilterActivity.class);
                intent.putExtra("pro_slug", "");
                intent.putExtra("type", AppConstants.ITEM_TYPE_PHOTO);
                startActivity(intent);
                break;
        }
    }

    // Controls visibility/scale of FAB on drawer open/close
    private void scaleFAB(float input) {
        if (input < .7f) {

            if (findViewById(R.id.fab_post_request).getVisibility() != View.VISIBLE)
                findViewById(R.id.fab_post_request).setVisibility(View.VISIBLE);

            findViewById(R.id.fab_post_request).setScaleX(1 - input);
            findViewById(R.id.fab_post_request).setScaleY(1 - input);

        } else {

            if (findViewById(R.id.fab_post_request).getScaleX() != 0)
                findViewById(R.id.fab_post_request).setScaleX(0);

            if (findViewById(R.id.fab_post_request).getScaleY() != 0)
                findViewById(R.id.fab_post_request).setScaleY(0);

            if (findViewById(R.id.fab_post_request).getVisibility() != View.GONE)
                findViewById(R.id.fab_post_request).setVisibility(View.GONE);
        }

    }

    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }

    private void setNavigationList() {
        menuListView = (ListView) findViewById(R.id.menu_list);
        final MenuAdapter adapter = new MenuAdapter(this);
        menuListView.setAdapter(adapter);
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(HomeActivity.this, SelectCity.class);
                        mDrawer.closeDrawers();
                        startActivity(intent);
                        break;
                    case 1:
                        if (AppPreferences.isUserLogIn(HomeActivity.this)) {
                            intent = new Intent(HomeActivity.this, UserFavouritesActivity.class);
                            mDrawer.closeDrawers();
                            startActivity(intent);
                        } else {
                            showToast("Please Login to continue");
                            intent = new Intent(HomeActivity.this, BaseLoginSignupActivity.class);
                            intent.putExtra("inside", true);
                            isToLoginPage = true;
                            mDrawer.closeDrawers();
                            startActivity(intent);
                        }
                        break;
                    case 2:
                        if (AppPreferences.isUserLogIn(HomeActivity.this)) {
                            intent = new Intent(HomeActivity.this, MyWishlist.class);
                            intent.putExtra("url", AppConstants.USER_WISHLIST );
                            mDrawer.closeDrawers();
                            startActivity(intent);
                        }else{
                            showToast("Please Login to continue");
                            intent = new Intent(HomeActivity.this, BaseLoginSignupActivity.class);
                            isToLoginPage = true;
                            intent.putExtra("inside", true);
                            mDrawer.closeDrawers();
                            startActivity(intent);
                        }
                        break;
                    case 3:
                        if (AppPreferences.isUserLogIn(HomeActivity.this)) {
                            intent = new Intent(HomeActivity.this, PurchaseListActivity.class);
                            intent.putExtra("fromHome", true);
                            mDrawer.closeDrawers();
                            startActivity(intent);
                        } else {
                            showToast("Please Login to continue");
                            intent = new Intent(HomeActivity.this, BaseLoginSignupActivity.class);
                            isToLoginPage = true;
                            intent.putExtra("inside", true);
                            mDrawer.closeDrawers();
                            startActivity(intent);
                        }

                        break;
                    case 4:

                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("application/octet-stream");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"feedback@zimply.in"});
                        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback_email_subject));
                        mDrawer.closeDrawers();
                        try {
                            prefs = getSharedPreferences(CommonLib.preferenceName, 0);
                            final String LogString = new String("App Version  : " + CommonLib.VERSION_STRING + "\n"
                                    + "Connection   : " + CommonLib.getNetworkState(HomeActivity.this) + "_"
                                    + CommonLib.getNetworkType(HomeActivity.this) + "\n" + "Identifier   : "
                                    + prefs.getString("app_id", "") + "\n" + "User Id     	: " + prefs.getInt("uid", 0)
                                    + "\n" + "&device=" + android.os.Build.DEVICE);

                            FileOutputStream fOut = openFileOutput("log.txt", MODE_WORLD_READABLE);
                            File file = getFileStreamPath("log.txt");
                            Uri uri = Uri.fromFile(file);
                            OutputStreamWriter osw = new OutputStreamWriter(fOut);
                            osw.write(LogString);
                            osw.flush();
                            osw.close();
                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            startActivityForResult(
                                    Intent.createChooser(intent, getResources().getString(R.string.send_mail)),
                                    EMAIL_FEEDBACK);
                        } catch (android.content.ActivityNotFoundException ex) {
                            showToast(getResources().getString(R.string.no_email_clients));

                        }
                        break;

                    case 5:
                        try {
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Crashlytics.logException(e);
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                        }
                        break;
                    case 6:/*
                        AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).
                                setTitle("Support").
                                setMessage("Talk to us..").setPositiveButton("Call", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + AppApplication.getContactNumber()));
                                startActivity(intent);
                            }
                        }).setNegativeButton("Cancel", null).create();
                        alertDialog.show();
                        break;*/

                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("application/octet-stream");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"care@zimply.in"});
                        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.support_email_subject));
                        mDrawer.closeDrawers();
                        try {
                            prefs = getSharedPreferences(CommonLib.preferenceName, 0);
                            final String LogString = new String("App Version  : " + CommonLib.VERSION_STRING + "\n"
                                    + "Connection   : " + CommonLib.getNetworkState(HomeActivity.this) + "_"
                                    + CommonLib.getNetworkType(HomeActivity.this) + "\n" + "Identifier   : "
                                    + prefs.getString("app_id", "") + "\n" + "User Id     	: " + prefs.getInt("uid", 0)
                                    + "\n" + "&device=" + android.os.Build.DEVICE);

                            FileOutputStream fOut = openFileOutput("log.txt", MODE_WORLD_READABLE);
                            File file = getFileStreamPath("log.txt");
                            Uri uri = Uri.fromFile(file);
                            OutputStreamWriter osw = new OutputStreamWriter(fOut);
                            osw.write(LogString);
                            osw.flush();
                            osw.close();
                            intent.putExtra(Intent.EXTRA_STREAM, uri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            startActivityForResult(
                                    Intent.createChooser(intent, getResources().getString(R.string.send_mail)),
                                    EMAIL_FEEDBACK);
                        } catch (android.content.ActivityNotFoundException ex) {
                            showToast(getResources().getString(R.string.no_email_clients));

                        }
                        break;
                    case 7:
                        intent = new Intent(HomeActivity.this, AboutUsPage.class);
                        mDrawer.closeDrawers();
                        startActivity(intent);

                        break;
                    case 8:
                        ZTracker.logGAEvent(HomeActivity.this, "Home", "Logout", "");
                        final AlertDialog logoutDialog;
                        logoutDialog = new AlertDialog.Builder(HomeActivity.this)
                                .setTitle(getResources().getString(R.string.logout))
                                .setMessage(getResources().getString(R.string.logout_confirm))
                                .setPositiveButton(getResources().getString(R.string.logout),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                AppPreferences.setIsUserLogin(HomeActivity.this, false);
                                                AppPreferences.setUserID(HomeActivity.this, "");
                                                AllProducts.getInstance().setCartCount(0);
                                                AllProducts.getInstance().setCartObjs(null);
                                                Intent loginIntent = new Intent(HomeActivity.this, BaseLoginSignupActivity.class);
                                                loginIntent.putExtra("is_logout", true);
                                                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                //loginIntent.putExtra("inside", true);
                                                startActivity(loginIntent);
                                            }
                                        }).setNegativeButton(getResources().getString(R.string.dialog_cancel),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                        logoutDialog.show();
                        break;
                }

            }


        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EMAIL_FEEDBACK) {
            deleteFile("log.txt");
        } else if (requestCode == AppConstants.REQUEST_PHOTO_FILTER_ACTIVITY) {
            if (adapter != null && adapter.getCount() > 0) {
                Fragment fragment = adapter.getItem(0);
                if (fragment != null && fragment instanceof PhotosListingFragmentWebView) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    public void setRateUsLayout() {
        if (!AppPreferences.isAppRated(this)) {
            if (AppPreferences.getRateOpenCount(this) == AppPreferences.getOpenRateCount(this)) {
                showAlertDialogForRating();
            } else {
                AppPreferences.setRateOpenCount(this, AppPreferences.getRateOpenCount(this) + 1);
            }
        }
    }

    private void addToolbarView(Toolbar toolbar) {
        toolbarView = LayoutInflater.from(this).inflate(R.layout.home_toolbar_view_layout, toolbar, false);
        toolbarTitle = (TextView) toolbarView.findViewById(R.id.title_textview);
        toolbarView.findViewById(R.id.city_toolbar_layout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SelectCity.class);
                startActivity(intent);
            }
        });
        setToolbarTitle();

        toolbar.addView(toolbarView);
    }

    public void setToolbarTitle() {
        if (toolbarTitle != null) {
            toolbarTitle.setText(AppPreferences.getSavedCity(this));
            toolbarTitle.invalidate();
        }
    }

    private void setUpDrawer() {
        navView = (NavigationView) findViewById(R.id.navigation_view);

        mDrawer.setDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerStateChanged(int arg0) {
            }

            @Override
            public void onDrawerSlide(View arg0, float arg1) {

                switch (pager.getCurrentItem()) {
                    case FRAGMENT_PHOTO:
                        //showFAB(false);
                        scaleFAB(arg1);
                        break;
                    default:
                        //do nothing
                        break;
                }


            }

            @Override
            public void onDrawerOpened(View arg0) {
            }

            @Override
            public void onDrawerClosed(View arg0) {
            }
        });

        CircularImageView userImage = (CircularImageView) navView.findViewById(R.id.user_img);
        TextView userName = (TextView) navView.findViewById(R.id.drawer_user_name);

        ImageView backgroundImg = (ImageView) navView.findViewById(R.id.drawer_user_info_background_image_blurr);
        if (AppPreferences.isUserLogIn(this)) {
            if (!AppPreferences.getUserPhoto(this).equalsIgnoreCase("")) {
                userImage.setImageBitmap(CommonLib.getBitmap(this, R.drawable.ic_user, getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material), getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material)));

                loadImage(AppPreferences.getUserPhoto(this), userImage,
                        getResources().getDimensionPixelSize(R.dimen.pro_image_size),
                        getResources().getDimensionPixelSize(R.dimen.pro_image_size), false, false);
                loadImage(AppPreferences.getUserPhoto(this), backgroundImg, width, width, false, true);
            } else {
                userImage.setImageBitmap(CommonLib.getBitmap(this, R.drawable.ic_user, getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material), getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material)));
                backgroundImg.setImageBitmap(CommonLib.getBitmap(this, R.drawable.ic_user_profile_bg, width, width));
            }
            userName.setText(AppPreferences.getUserName(this));
        } else {
            SpannableString spannablecontent = new SpannableString("Hello! Login/Signup ?");
            userImage.setImageBitmap(CommonLib.getBitmap(this, R.drawable.ic_user, getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material), getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material)));
            spannablecontent.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 15, spannablecontent.length(), 0);
            userName.setText(spannablecontent);
            backgroundImg.setImageBitmap(CommonLib.getBitmap(this, R.drawable.ic_user_profile_bg, width, width));
            findViewById(R.id.drawer_user_container).setOnClickListener(this);
        }

    }

    @Override
    protected void onResume() {
//        toolbarView.requestLayout();
        if (toolbar != null) {
            toolbar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        }
        if (mTabs != null) {
            mTabs.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        }
        if (mDrawer != null) {
            mDrawer.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        }
        if (menuListView != null) {
            menuListView.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        }
        if (navView != null) {
            navView.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        }

        if (isToLoginPage && AppPreferences.isUserLogIn(this)) {
            //  loadData();
            isToLoginPage = false;
        }


        if (AllProducts.getInstance().getCartCount() > 0) {
            findViewById(R.id.cart_item_true).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.cart_item_true)).setText(AllProducts.getInstance().getCartCount() + "");
        } else {
            findViewById(R.id.cart_item_true).setVisibility(View.GONE);
        }

        if (AppPreferences.isStartRating(this)) {
            setRateUsLayout();
        }
        setToolbarTitle();

        if (reloadUserInfo) {
            setUpDrawer();
            setNavigationList();
            reloadUserInfo = false;
        }

        super.onResume();
    }

    private void loadData() {
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.HOMEPAGE_URL + "?product_width="
                + getResources().getDimensionPixelSize(R.dimen.product_img_size) + "&product_height="
                + getResources().getDimensionPixelSize(R.dimen.product_img_size) + "&size=3"
                + (AppPreferences.isUserLogIn(this) ? "&userid=" + AppPreferences.getUserID(this) : "");
        GetRequestManager.getInstance().requestHTTPThenCache(url, HOME_PAGE_REQUEST_TAG,
                ObjectTypes.OBJECT_TYPE_HOME_OBJECT, GetRequestManager.TEMP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        filterItem = menu.findItem(R.id.filter);
        searchItem = menu.findItem(R.id.search);
        return true;
    }

    private void animateArrowForward() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(arrowRight, View.TRANSLATION_X, 0, 30f);
        anim.setDuration(500);
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
                animateArrowBackward();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        anim.start();
    }

    private void animateArrowBackward() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(arrowRight, View.TRANSLATION_X, 30, 0);
        anim.setDuration(500);
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
                animateArrowForward();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        anim.start();
    }

    @Override
    public void onMenuExpanded() {
        mFABExpanded = true;
        mFABOverlay.animate().alpha(1).setDuration(100);
        mFABOverlay.setClickable(true);
    }

    // CALLBACK from FABControl
    @Override
    public void onMenuCollapsed() {
        mFABExpanded = false;
        mFABOverlay.animate().alpha(0).setDuration(100);
        mFABOverlay.setClickable(false);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_layout:
                if (isRequestFailed)
                    //   loadData();
                    break;
            case R.id.drawer_user_container:
                Intent loginIntent = new Intent(this, BaseLoginSignupActivity.class);
                loginIntent.putExtra("inside", true);
                isToLoginPage = true;
                startActivity(loginIntent);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                showFilterLayout();
                break;
            case R.id.search:
                Intent searchIntent = new Intent(this, NewSearchActivity.class);
                searchIntent.putExtra("position",pager.getCurrentItem());
                startActivity(searchIntent);
                break;
            case R.id.cart:
                Intent intent = new Intent(this, ProductCheckoutActivity.class);
                intent.putExtra("OrderSummaryFragment", false);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showFilterLayout() {
        if (pager.getCurrentItem() == 0) {
            ((PhotosListingFragmentWebView) fragments.get(0)).showFilterLayout();
        } else if (pager.getCurrentItem() == 1) {
            ((ArticleListingFragment) fragments.get(1)).showFilterLayout();
        }
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(HOME_PAGE_REQUEST_TAG)) {
            if (!noNetworkViewAdded) {
                showLoadingView();
                changeViewVisiblity(parentScrollView, View.GONE);

            }
            noInterNetConnection.setText("Retrying...");
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(HOME_PAGE_REQUEST_TAG)) {
            showView();

            this.photos = ((HomeObject) obj).getPhotos();
            this.articles = ((HomeObject) obj).getArticles();
            this.experts = ((HomeObject) obj).getExperts();
            this.deals = ((HomeObject) obj).getDeals();
            this.products = ((HomeObject) obj).getProducts();
          /*  setViewPagerContent(((HomeObject) obj).getPhotos(), ((HomeObject) obj).getArticles(),
                    ((HomeObject) obj).getExperts(), ((HomeObject) obj).getDeals(), ((HomeObject) obj).getProducts());*/
            //setCategoriesContent(((HomeObject) obj).getCategory());
            if (CommonLib.isNetworkAvailable(this)) {
                try {
                    isFreshDataLoaded = true;
                    noNetworkViewAdded = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                isRequestFailed = true;
                if (!noNetworkViewAdded) {
                    noNetworkViewAdded = true;
                }
                noInterNetConnection.setText("No Internet Connection");
            }
        }else if(!isDestroyed && requestTag.equalsIgnoreCase(BANNER_REQUEST_TAG)){
            if (((BannerObject) obj).getType() != 0) {
                showBanner((BannerObject) obj);
            }
        }
    }
public void showBanner(BannerObject obj){
    Bundle bundle = new Bundle();

    bundle.putSerializable("banner_obj", obj);

    final BannerDialogFragment dialog = BannerDialogFragment
            .newInstance(bundle);
    dialog.setStyle(DialogFragment.STYLE_NORMAL,
            R.style.HJCustomDialogTheme);

    dialog.show(getSupportFragmentManager(), "BannerTag");
}
    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        CommonLib.ZLog("err", "request failed");
        CommonLib.ZLog("err", "request " + requestTag);
        CommonLib.ZLog("err", "request failed" + obj);
        if (!isDestroyed && requestTag.equalsIgnoreCase(HOME_PAGE_REQUEST_TAG)) {
            showNetworkErrorView();
            changeViewVisiblity(parentScrollView, View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        GetRequestManager.getInstance().removeCallbacks(this);
        UploadManager.getInstance().removeCallback(this);
        isDestroyed = true;
        super.onDestroy();
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object object) {
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if (isDestroyed)
            return;
//        if (requestType == MARK_FAVOURITE_REQUEST_TAG) {
//            if (status) {
//                if (status && data != null && response != null) {
//                    if (data instanceof HomePhotoObj) {
//                        for (HomePhotoObj photo : photos) {
//                            if (photo.getSlug().equals(((HomePhotoObj) data).getSlug())) {
//                                photo.setFavorite_item_id(String.valueOf(response));
//                                photo.setIs_favourite(true);
//                            }
//                        }
//                    } else if (data instanceof HomeArticleObj) {
//                        for (HomeArticleObj article : articles) {
//                            if (article.getSlug().equals(((HomeArticleObj) data).getSlug())) {
//                                article.setFavorite_item_id(String.valueOf(response));
//                                article.setIs_favourite(true);
//                            }
//                        }
//                    }
//                }
//            }
//        } else if (requestType == MARK_UN_FAVOURITE_REQUEST_TAG) {
//            if (status && data != null && response != null) {
//                if (data instanceof HomePhotoObj) {
//                    for (HomePhotoObj photo : photos) {
//                        if (photo.getSlug().equals(((HomePhotoObj) data).getSlug())) {
//                            photo.setFavorite_item_id(String.valueOf(response));
//                            photo.setIs_favourite(false);
//                        }
//                    }
//                } else if (data instanceof HomeArticleObj) {
//                    for (HomeArticleObj article : articles) {
//                        if (article.getSlug().equals(((HomeArticleObj) data).getSlug())) {
//                            article.setFavorite_item_id(String.valueOf(response));
//                            article.setIs_favourite(false);
//                        }
//                    }
//                }
//            }
//        } else
        if (requestType == SIGNUP_REQUEST_TAG_BASE) {
            if (status) {
                reloadUserInfo = true;
                AppPreferences.setIsUserLogin(this, true);
                AppPreferences.setUserID(this, ((SignupObject) response).getId());
                AppPreferences.setUserToken(this, ((SignupObject) response).getToken());
                AppPreferences.setUserEmail(this, ((SignupObject) response).getEmail());
                AppPreferences.setUserName(this, ((SignupObject) response).getName());
                AppPreferences.setUserPhoto(this, ((SignupObject) response).getPhoto());
            } else {
                if (response != null) {
                    showToast(((ErrorObject) response).getErrorMessage());
                } else {
                    showToast("Falied. Try again");
                }
            }
        } else if (requestType == SIGNUP_REQUEST_TAG_LOGIN) {
            if (status) {
                reloadUserInfo = true;
                AppPreferences.setIsUserLogin(this, true);
                AppPreferences.setUserID(this, ((SignupObject) response).getId());
                AppPreferences.setUserToken(this, ((SignupObject) response).getToken());
                AppPreferences.setUserEmail(this, ((SignupObject) response).getEmail());
                AppPreferences.setUserName(this, ((SignupObject) response).getName());
                AppPreferences.setUserPhoto(this, ((SignupObject) response).getPhoto());

            } else {
                if (response != null) {
                    showToast(((ErrorObject) response).getErrorMessage());
                } else {
                    showToast("Falied. Try again");
                }
            }
        } else if (requestType == SIGNUP_REQUEST_TAG_SIGNUP) {
            if (status) {
                reloadUserInfo = true;
                AppPreferences.setIsUserLogin(this, true);
                AppPreferences.setUserID(this, ((SignupObject) response).getId());
                AppPreferences.setUserToken(this, ((SignupObject) response).getToken());
                AppPreferences.setUserEmail(this, ((SignupObject) response).getEmail());
                AppPreferences.setUserName(this, ((SignupObject) response).getName());
                AppPreferences.setUserPhoto(this, ((SignupObject) response).getPhoto());

            } else {
                if (response != null) {
                    showToast(((ErrorObject) response).getErrorMessage());
                } else {
                    showToast("Falied. Try again");

                }
            }
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        try {
            if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey("nType")) {

                int type = intent.getExtras().getInt("nType");
                switch (type) {
                    case NOTIFICATION_TYPE_ARTICLE_LISTING:
                        pager.setCurrentItem(0);
                        break;
                    case NOTIFICATION_TYPE_PHOTO_LISTING:
                        pager.setCurrentItem(1);
                        break;
                    case NOTIFICATION_TYPE_EXPERT_LISTING:
                        pager.setCurrentItem(2);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (pager != null && pager.getCurrentItem() != 0) {
            pager.setCurrentItem(0);
        } else {
            super.onBackPressed();
        }

    }

    public class MainFragmentsAdapter extends FragmentPagerAdapter {

        ArrayList<String> pageTitles;

        public MainFragmentsAdapter(FragmentManager fm) {
            super(fm);
            pageTitles = new ArrayList<String>();
            pageTitles.add("Photos");
            pageTitles.add("Stories");
            pageTitles.add("Experts");
            pageTitles.add("Shop");
            fragments = new HashMap<>();
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case FRAGMENT_PHOTO:
                    if (CommonLib.showPhotosWebView)
                        fragment = PhotosListingFragmentWebView.newInstance(null);
                    else
                        fragment = PhotosListingFragment.newInstance(null);
                    break;

                case FRAGMENT_ARTICLE:
                    fragment = ArticleListingFragment.newInstance(null);
                    break;

                case FRAGMENT_EXPERT:
                    fragment = ExpertCategoryFragment.newInstance(null);

                    break;
                case FRAGMENT_PRODUCT:
                    Bundle arguments = new Bundle();
                    arguments.putString("title", getString(R.string.deals_of_day_text));
                    fragment = ProductsListFragment.newInstance(arguments);
                    break;

            }
            fragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return pageTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles.get(position);
        }
    }
}
