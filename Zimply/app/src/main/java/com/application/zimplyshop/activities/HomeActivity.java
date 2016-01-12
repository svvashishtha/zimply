
package com.application.zimplyshop.activities;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.MenuAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.BannerObject;
import com.application.zimplyshop.baseobjects.BaseCartProdutQtyObj;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.baseobjects.HomeObject;
import com.application.zimplyshop.baseobjects.HomeProductObj;
import com.application.zimplyshop.baseobjects.NonLoggedInCartObj;
import com.application.zimplyshop.baseobjects.ShopCategoryObject;
import com.application.zimplyshop.baseobjects.SignupObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.fragments.BannerDialogFragment;
import com.application.zimplyshop.fragments.ProductsListFragment;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.objects.AllNotifications;
import com.application.zimplyshop.objects.AllProducts;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.JSONUtils;
import com.application.zimplyshop.utils.TimeUtils;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.utils.fab.FABControl;
import com.application.zimplyshop.utils.fab.FABUnit;
import com.application.zimplyshop.widgets.CircularImageView;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Home Activity for a user who opens the app
 *
 * @author Umesh
 */
public class HomeActivity extends BaseActivity implements OnClickListener,
        RequestTags, GetRequestListener, UploadManagerCallback, AppConstants, FABControl.OnFloatingActionsMenuUpdateListener {


    //OnPageChangeListener commented
    private static final int FRAGMENT_PRODUCT = 0;
    private final int EMAIL_FEEDBACK = 1500;
    DrawerLayout mDrawer;
    int width, height;
    ActionBarDrawerToggle mToggle;
    ScrollView parentScrollView;
    boolean isDestroyed = false;
    ImageView arrowRight;
    boolean isArrowHidden;
    ArrayList<HomeProductObj> deals = new ArrayList<HomeProductObj>();
    ArrayList<HomeProductObj> products = new ArrayList<HomeProductObj>();
    ArrayList<ShopCategoryObject> shopCategories = new ArrayList<ShopCategoryObject>();
    TextView noInterNetConnection;
    boolean noNetworkViewAdded = false;
    boolean isFreshDataLoaded;
    TextView toolbarTitle;
    MenuItem filterItem, searchItem, notificationsItem;
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
    ListView menuListView;
    View toolbarView;
    NavigationView navView;
    boolean isToLoginPage;
    // FAB Stuff
    private View mFABOverlay;
    private boolean mFABExpanded = false;
    private boolean mFABVisible = false;
    private boolean reloadUserInfo = false;

    private ProgressDialog zProgressDialog;

    private String mobile;

    private Fragment fragment;

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
        setUpDrawer();
        mToggle.syncState();
        setUpFAB();
        setNavigationList();

        onNewIntent(getIntent());

        getUserNotificationCount();

        if (AppPreferences.isUserLogIn(this))
            phoneVerification();

        //init fragements
        Bundle arguments = new Bundle();
        arguments.putString("title", getString(R.string.deals_of_day_text));
        fragment = ProductsListFragment.newInstance(arguments);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit();


        if (!AppPreferences.isBarcodeTutorialShown(this) && AppPreferences.isFirstBookingDone(this)) {
            AppPreferences.setIsBarcodeTutorialShown(this, true);
            findViewById(R.id.fab_tutorial).setVisibility(View.VISIBLE);
            findViewById(R.id.hint_fab).setVisibility(View.VISIBLE);
            findViewById(R.id.hint_fab).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeFabTutorial();

                }
            });
        } else {
            loadBanner();
        }
        boolean isNotification = getIntent().getBooleanExtra("is_notification", false);
        if (isNotification) {
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
    }

    @Override
    public void userDataReceived() {
        if (AllProducts.getInstance().getCartCount() > 0) {
            findViewById(R.id.cart_item_true).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.cart_item_true)).setText(AllProducts.getInstance().getCartCount() + "");
        } else {
            findViewById(R.id.cart_item_true).setVisibility(View.GONE);
        }
    }

    public void closeFabTutorial() {
        findViewById(R.id.fab_tutorial).setVisibility(View.GONE);
        findViewById(R.id.hint_fab).setVisibility(View.GONE);
    }

    public void getUserNotificationCount() {

        CommonLib.ZLog("Notification Date Time", TimeUtils.getFormatedDate(AppPreferences.getNotifModDateTime(this)));
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.NOTIFICATION_COUNT + "?created_on=" + TimeUtils.getFormatedDate(AppPreferences.getNotifModDateTime(this));
        GetRequestManager.getInstance().makeAyncRequest(url, LATEST_NOTIFICATION_COUNT_TAG, ObjectTypes.OBJECT_TYPE_NOTIFICATION_COUNT);
    }

    public void toggleFilterVisibility(boolean visibility) {
        filterItem.setVisible(visibility);
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
        ((FABUnit) findViewById(R.id.fab_post_request)).setIcon(R.drawable.ic_barcodescanner);
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
        closeFabTutorial();
        Intent intent = new Intent(HomeActivity.this, BarcodeScannerActivity.class);
        startActivityForResult(intent, AppConstants.REQUEST_TYPE_FROM_SEARCH);
        /*switch (view.getId()) {
            case R.id.fab_post_request:
                if(pager.getCurrentItem() == 0 || pager.getCurrentItem() == 2) {
                    Intent intent = new Intent(HomeActivity.this,BarcodeScannerActivity.class);
                    startActivityForResult(intent, AppConstants.REQUEST_TYPE_FROM_SEARCH);
                }else if(pager.getCurrentItem() == 3){
                    *//*Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    PackageManager packageMgr = getPackageManager();
                    List<ResolveInfo> activities = packageMgr.queryIntentActivities(intent, 0);
                    if (activities.size() > 0) {
                        startActivityForResult(intent, 0);
                    } else {
                        showToast("Barcode scanner is not available in your device");
                    }*//*

                }
                break;
        }*/
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
                        //intent.putExtra("show_back", true);
                        mDrawer.closeDrawers();
                        startActivity(intent);
                        break;
                    case 2:
                        if (AppPreferences.isUserLogIn(HomeActivity.this)) {
                            intent = new Intent(HomeActivity.this, BookedForReviewActivity.class);
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
                    case 3:
                        if (AppPreferences.isUserLogIn(HomeActivity.this)) {
                            intent = new Intent(HomeActivity.this, MyWishlist.class);
                            intent.putExtra("url", AppConstants.USER_WISHLIST);
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
                    case 1:
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
                        mDrawer.closeDrawers();
                        if (AppPreferences.isUserLogIn(HomeActivity.this)) {
                            Intent settingsIntent = new Intent(HomeActivity.this, SettingsPage.class);
                            startActivity(settingsIntent);
                        } else {
                            showToast("Please Login to continue");
                            intent = new Intent(HomeActivity.this, BaseLoginSignupActivity.class);
                            isToLoginPage = true;
                            intent.putExtra("inside", true);
                            intent.putExtra("inside", true);
                            startActivity(intent);
                        }
                        break;

                    case 6:
                        mDrawer.closeDrawers();
                        Intent shareIntent = new Intent(HomeActivity.this, SharingOptionsActivity.class);
                        shareIntent.putExtra("title", "Download Zimply App by clicking on link");
                        shareIntent.putExtra("type_name", AppConstants.ITEM_APP_SHARE);
                        shareIntent.putExtra("product_url", "/shop-product/");
                        shareIntent.putExtra("short_url", "https://play.google.com/store/apps/details?id=com.application.zimplyshop");
                        startActivity(shareIntent);
                        break;
                    case 7:
                        intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("application/octet-stream");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@zimply.in"});
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

                    case 8:
                        try {
                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Crashlytics.logException(e);
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                        }
                        break;
                    case 9:
                        intent = new Intent(HomeActivity.this, AboutUsPage.class);
                        mDrawer.closeDrawers();
                        startActivity(intent);

                        break;
                    /*case 10:
                        ZTracker.logGAEvent(HomeActivity.this, "Home", "Logout", "");
                        final AlertDialog logoutDialog;
                        logoutDialog = new AlertDialog.Builder(HomeActivity.this)
                                .setTitle(getResources().getString(R.string.logout))
                                .setMessage(getResources().getString(R.string.logout_confirm))
                                .setPositiveButton(getResources().getString(R.string.logout),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                BaseActivity.logOutUserFromApp(HomeActivity.this, BaseLoginSignupActivity.class);
                                            }
                                        }).setNegativeButton(getResources().getString(R.string.dialog_cancel),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                        logoutDialog.show();
                        break;*/
                }

            }


        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EMAIL_FEEDBACK) {
            deleteFile("log.txt");
        } else if (requestCode == REQUEST_TYPE_FROM_SEARCH) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");

                String format = data.getStringExtra("SCAN_RESULT_FORMAT");

                JSONObject obj = JSONUtils.getJSONObject(contents);

                productId = JSONUtils.getIntegerfromJSON(obj, "id");
                slug = JSONUtils.getStringfromJSON(obj, "slug");
                moveToProductDetail(productId, slug, "Scan Product Result");

            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    public void addScannedObjToCart(int id, String slug) {
        if (AppPreferences.isUserLogIn(this)) {
            if (AllProducts.getInstance().cartContains((int) id)) {
                Toast.makeText(this, "Already added to cart", Toast.LENGTH_SHORT).show();
                moveToProductDetail(id, slug, "Add Scanned Object To Cart");
            } else {

                String url = AppApplication.getInstance().getBaseUrl() + ADD_TO_CART_URL;
                List<NameValuePair> nameValuePair = new ArrayList<>();
                nameValuePair.add(new BasicNameValuePair("buying_channel", AppConstants.BUYING_CHANNEL_OFFLINE + ""));
                nameValuePair.add(new BasicNameValuePair("product_id", id + ""));
                nameValuePair.add(new BasicNameValuePair("quantity", "1"));
                nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));

                UploadManager.getInstance().addCallback(this);
                UploadManager.getInstance().makeAyncRequest(url, ADD_TO_CART, slug, ObjectTypes.OBJECT_ADD_TO_CART, null, nameValuePair, null);
            }
        } else {
            ArrayList<NonLoggedInCartObj> oldObj = ((ArrayList<NonLoggedInCartObj>) GetRequestManager.Request(AppPreferences.getDeviceID(this), RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT));
            if (oldObj == null) {
                oldObj = new ArrayList<NonLoggedInCartObj>();
            }
            NonLoggedInCartObj item = new NonLoggedInCartObj(id + "", 1, BUYING_CHANNEL_OFFLINE);
            if (oldObj.contains(item)) {
                Toast.makeText(this, "Already added to cart", Toast.LENGTH_SHORT).show();
            } else {
                AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartCount() + 1);
                // checkCartCount();
                oldObj.add(item);
                GetRequestManager.Update(AppPreferences.getDeviceID(this), oldObj, RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
                Toast.makeText(this, "Successfully added to cart", Toast.LENGTH_SHORT).show();
            }
            moveToProductDetail(id, slug, "Add Scanned Object To Cart");

        }
    }

    public void moveToProductDetail(int productId, String slug, String productActionListName) {
        Intent intent = new Intent(this, NewProductDetailActivity.class);
        intent.putExtra("slug", slug);
        intent.putExtra("id", productId);
        intent.putExtra("is_scanned", true);

//        GA Ecommerce
        intent.putExtra("productActionListName", productActionListName);
        intent.putExtra("screenName", "Home Activity");
        intent.putExtra("actionPerformed", ProductAction.ACTION_CLICK);
        startActivity(intent);
    }

    public void moveToCartActivity() {
        Intent intent = new Intent(this, ProductCheckoutActivity.class);
        intent.putExtra("OrderSummaryFragment", false);
        startActivity(intent);
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
                //intent.putExtra("show_back", true);
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
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Adding action bar drawer toggle
        mToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.app_name,
                R.string.app_name);
        mDrawer.setDrawerListener(mToggle);
        mDrawer.setDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerStateChanged(int arg0) {
            }

            @Override
            public void onDrawerSlide(View arg0, float arg1) {
                scaleFAB(arg1);
            }

            @Override
            public void onDrawerOpened(View arg0) {
            }

            @Override
            public void onDrawerClosed(View arg0) {
            }
        });

        navView = (NavigationView) findViewById(R.id.navigation_view);

        CircularImageView userImage = (CircularImageView) navView.findViewById(R.id.user_img);
        userImage.setOnClickListener(this);
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
            ((TextView) navView.findViewById(R.id.drawer_user_email)).setVisibility(View.VISIBLE);
            ((TextView) navView.findViewById(R.id.drawer_user_email)).setText(AppPreferences.getUserEmail(this));
            ((TextView) navView.findViewById(R.id.drawer_user_phone)).setVisibility(View.VISIBLE);
            ((TextView) navView.findViewById(R.id.drawer_user_phone)).setText(AppPreferences.getUserPhoneNumber(this));

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

        if (AppPreferences.isUserLogIn(this)) {
            if (((TextView) navView.findViewById(R.id.drawer_user_phone)).getText() != null && ((TextView) navView.findViewById(R.id.drawer_user_phone)).getText().length() == 0) {
                ((TextView) navView.findViewById(R.id.drawer_user_phone)).setText(AppPreferences.getUserPhoneNumber(this));
            }
        }

        if (!AppPreferences.isBarcodeTutorialShown(this) && AppPreferences.isFirstBookingDone(this)) {
            AppPreferences.setIsBarcodeTutorialShown(this, true);
            findViewById(R.id.fab_tutorial).setVisibility(View.VISIBLE);
            findViewById(R.id.hint_fab).setVisibility(View.VISIBLE);
            findViewById(R.id.hint_fab).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeFabTutorial();

                }
            });
        }

        super.onResume();
    }


    boolean isLoadingBanner;

    private void loadBanner() {
        if (!isLoadingBanner) {
            isLoadingBanner = true;
            String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_BANNER_REQUEST;
            GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.BANNER_REQUEST_TAG, ObjectTypes.OBJECT_TYPE_BANNER_OBJECT);
        }
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
        notificationsItem = menu.findItem(R.id.notifications);
        notificationsItem.setVisible(true);
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
            case R.id.null_case_image:
            case R.id.retry_layout:
                if (isRequestFailed)
                    //   loadData();
                    break;
            case R.id.drawer_user_container:
                if (!AppPreferences.isUserLogIn(this)) {
                    Intent loginIntent = new Intent(this, BaseLoginSignupActivity.class);
                    loginIntent.putExtra("inside", true);
                    isToLoginPage = true;
                    mDrawer.closeDrawers();
                    startActivity(loginIntent);
                }
            case R.id.user_img:
                /*if (!AppPreferences.isUserLogIn(this)) {
                    Intent loginIntent = new Intent(this, BaseLoginSignupActivity.class);
                    loginIntent.putExtra("inside", true);
                    isToLoginPage = true;
                    mDrawer.closeDrawers();
                    startActivity(loginIntent);
                }
                else
                {
                    Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
                    startActivity(intent);

                }*/
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                break;
            case R.id.search:
                Intent searchIntent = new Intent(this, NewSearchActivity.class);
                searchIntent.putExtra("position", FRAGMENT_PRODUCT);
                startActivity(searchIntent);
                break;
            case R.id.cart:
                Intent intent = new Intent(this, ProductCheckoutActivity.class);
                intent.putExtra("OrderSummaryFragment", false);
                intent.putExtra("buying_channel", BUYING_CHANNEL_ONLINE);
                startActivity(intent);
                break;
            case R.id.notifications:
                findViewById(R.id.notification_count).setVisibility(View.GONE);
                Intent notifIntent = new Intent(this, NotificationsActivity.class);
                startActivity(notifIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
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
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(BANNER_REQUEST_TAG)) {
            if (((BannerObject) obj).getType() != 0) {
                isLoadingBanner = false;
                showBanner((BannerObject) obj);
            }
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(RequestTags.PHONE_VERIFICATION)) {
            if (obj instanceof Object[]) {
                Object[] response = (Object[]) obj;

                String phoneNumber = (String) response[0];
                boolean isVerified = (Boolean) response[1];

                if (!isVerified) {
                    Intent intent = new Intent(this, CheckPhoneVerificationActivity.class);
                    startActivity(intent);
                }
            }
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(LATEST_NOTIFICATION_COUNT_TAG)) {
            if (AllNotifications.getsInstance().getNewNotificationCount() == 0) {
                findViewById(R.id.notification_count).setVisibility(View.GONE);
            } else {
                ((TextView) findViewById(R.id.notification_count)).setText(AllNotifications.getsInstance().getNewNotificationCount() + "");
                findViewById(R.id.notification_count).setVisibility(View.VISIBLE);
                if (AllNotifications.getsInstance().getNewNotificationCount() != AppPreferences.getPreviousNotificationCount(this)) {

                    AppPreferences.setPreviousNotificationCount(this, AllNotifications.getsInstance().getNewNotificationCount());
                    (findViewById(R.id.notif_tut)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.notif_tut)).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            (findViewById(R.id.notif_tut)).setVisibility(View.GONE);
                        }
                    });
                } else {
                    (findViewById(R.id.notif_tut)).setVisibility(View.GONE);
                }
            }
        }

    }

    public void showBanner(BannerObject obj) {
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
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(BANNER_REQUEST_TAG)) {
            isLoadingBanner = false;
        }
    }

    @Override
    protected void onDestroy() {
        GetRequestManager.getInstance().removeCallbacks(this);
        UploadManager.getInstance().removeCallback(this);
        isDestroyed = true;
        super.onDestroy();
    }

    ProgressDialog progressDialog;

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object object) {
        if (requestType == ADD_TO_CART && !isDestroyed) {
            progressDialog = ProgressDialog.show(this, null, "Adding to cart. Please wait");
            // isLoading = true;
        }
    }

    int productId;
    String slug;

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if (isDestroyed)
            return;
        if (requestType == ADD_TO_CART && status) {

            String message = "An error occurred. Please try again...";
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            try {

                JSONObject jsonObject = ((JSONObject) response);
                if (jsonObject.getString("success") != null && jsonObject.getString("success").length() > 0)
                    message = jsonObject.getString("success");
                if (message != null) {
                    AllProducts.getInstance().getCartObjs().add(new BaseCartProdutQtyObj((int) productId, 1));
                    AllProducts.getInstance().setCartCount(AllProducts.getInstance().getCartCount() + 1);
                    moveToProductDetail(productId, slug, "Add To Cart");
                } else if (jsonObject.getString("error") != null && jsonObject.getString("error").length() > 0) {

                    message = jsonObject.getString("error");
                }
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestType == SIGNUP_REQUEST_TAG_BASE) {
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
                    case NOTIFICATION_TYPE_WEBVIEW:
                        break;
                    case NOTIFICATION_TYPE_PHOTO_LISTING:
                        break;
                    case NOTIFICATION_TYPE_SHOP_LISTING:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void phoneVerification() {
        String finalUrl = AppApplication.getInstance().getBaseUrl() + AppConstants.PHONE_VERIFICATION
                + "?userid=" + AppPreferences.getUserID(this);
        GetRequestManager.getInstance().makeAyncRequest(finalUrl,
                RequestTags.PHONE_VERIFICATION,
                ObjectTypes.OBJECT_TYPE_PHONE_VERIFICATION);
    }

}
