package com.application.zimplyshop.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.BaseCartProdutQtyObj;
import com.application.zimplyshop.baseobjects.NonLoggedInCartObj;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.fragments.ProductSearchFragment;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.objects.AllProducts;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.JSONUtils;
import com.application.zimplyshop.utils.NoSwipeViewPager;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;
import com.application.zimplyshop.widgets.ZPagerSlidingTabStrip;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class NewSearchActivity extends BaseActivity implements ZPagerSlidingTabStrip.OnTabClickListener ,AppConstants,RequestTags,UploadManagerCallback{

    private boolean destroyed = false;
    View actionBarView;

    public static final int FRAGMENT_PRODUCT_SEARCH = 0;
    public static final int FRAGMENT_EXPERT_SEARCH = 1;

    private NoSwipeViewPager homePager;
    private SparseArray<SoftReference<Fragment>> fragments = new SparseArray<SoftReference<Fragment>>();
    int currentPageSelected = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_search_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        GetRequestManager.getInstance().addCallbacks(this);
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
                if(input.length() >= 2) {
                    actionBarView.findViewById(R.id.clear_text_view_category).setVisibility(View.VISIBLE);
                    //send the api call to the fragment
                    if (fragments.get(FRAGMENT_PRODUCT_SEARCH) != null || fragments.get(FRAGMENT_EXPERT_SEARCH) != null) {
                        ProductSearchFragment hf = (ProductSearchFragment) fragments.get(FRAGMENT_PRODUCT_SEARCH).get();
                        if (hf != null && hf.isVisible()) {
                            hf.performSearch(input);
                        }
                        /*ExpertSearchFragment srf = (ExpertSearchFragment) fragments.get(FRAGMENT_EXPERT_SEARCH).get();
                        if (srf != null && srf.isVisible()) {
                            srf.performSearch(input);
                        }*/
                    }

                } else {
                    actionBarView.findViewById(R.id.clear_text_view_category).setVisibility(View.GONE);
//                        //send the api call to the fragment
                    if (fragments.get(FRAGMENT_PRODUCT_SEARCH) != null || fragments.get(FRAGMENT_EXPERT_SEARCH) != null) {
                        ProductSearchFragment hf = (ProductSearchFragment) fragments.get(FRAGMENT_PRODUCT_SEARCH).get();
                        if (hf != null && hf.isVisible()) {
                            hf.performSearch(input);
                        }
                    }
                }
            }
        };

        ((TextView)actionBarView.findViewById(R.id.search_category)).addTextChangedListener(textWatcher);

        // Search tabs
        homePager = (NoSwipeViewPager) findViewById(R.id.home_pager);
        homePager.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));
        homePager.setOffscreenPageLimit(1);

        homePager.setSwipeable(true);
        if(getIntent()!=null){
            int position = getIntent().getIntExtra("position",0);
            if(position == 2){
                homePager.setCurrentItem(FRAGMENT_EXPERT_SEARCH);
            }else{
                homePager.setCurrentItem(FRAGMENT_PRODUCT_SEARCH);
            }
        }
        // hide the thin line below tabs on androidL
        if (CommonLib.isAndroidL())
            findViewById(R.id.tab_thin_line).setVisibility(View.GONE);

        setUpTabs();


    }

    @Override
    public void onBackPressed() {

        if( actionBarView != null )
            CommonLib.hideKeyBoard(this, actionBarView.findViewById(R.id.search_category));
        super.onBackPressed();
    }

    private void addToolbarView(Toolbar toolbar) {
        actionBarView = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, toolbar, false);
        actionBarView.findViewById(R.id.title_textview).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.search_frame).setVisibility(View.VISIBLE);
        //actionBarView.findViewById(R.id.barcode_icon).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.barcode_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                PackageManager packageMgr = getPackageManager();
                List<ResolveInfo> activities = packageMgr.queryIntentActivities(intent, 0);
                if (activities.size() > 0) {
                    startActivityForResult(intent, 0);
                } else {
                    showToast("Barcode scanner is not available in your device");
                }*/
                Intent intent = new Intent(NewSearchActivity.this,BarcodeScannerActivity.class);
                startActivityForResult(intent, AppConstants.REQUEST_TYPE_FROM_SEARCH);
            }
        });

        actionBarView.findViewById(R.id.clear_text_view_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.search_category)).setText("");
                //stop the progress
                actionBarView.findViewById(R.id.clear_text_view_category).setVisibility(View.GONE);
//                        //send the api call to the fragment
                if (fragments.get(FRAGMENT_PRODUCT_SEARCH) != null || fragments.get(FRAGMENT_EXPERT_SEARCH) != null) {
                    ProductSearchFragment hf = (ProductSearchFragment) fragments.get(FRAGMENT_PRODUCT_SEARCH).get();
                    if (hf != null && hf.isVisible()) {
                        hf.performSearch("");
                    }
                }
                //cancel the api call
                //call the fragment on perform search
            }
        });

        ((EditText)actionBarView.findViewById(R.id.search_category)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        ((TextView)actionBarView.findViewById(R.id.search_category)).setHint(getResources().getString(R.string.search_products_hint));
        toolbar.addView(actionBarView);
    }

    private void performSearch(String query) {
        Intent intent = new Intent(this, SearchResultsActivity.class);

        if(query == null || query.length() < 1)
            return;
        String[] params = query.split(" ");
        String builder = "";
        for ( String pam:params ) {
            builder += (pam + "+");
        }
        builder = builder.substring(0, builder.length() - 1);

        intent.putExtra("query", builder);
        intent.putExtra("url", AppConstants.GET_SEARCH_RESULTS);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        UploadManager.getInstance().removeCallback(this);
        super.onDestroy();
    }

    private void setUpTabs() {
        // tabs
        ZPagerSlidingTabStrip tabs = (ZPagerSlidingTabStrip) findViewById(R.id.tabs);
//        tabs.setmZimplyHome(true);
        tabs.setTabBackground(R.drawable.white_card_bg);
        tabs.setIndicatorHeight(5);

        tabs.setActivateTextColor(getResources()
                .getColor(R.color.z_blue_color));
        tabs.setIndicatorColor(getResources().getColor(
                R.color.z_blue_color));
        tabs.setShouldExpand(true);
        tabs.setDeactivateTextColor(getResources().getColor(
                R.color.z_grey_color));
        tabs.setAllCaps(true);
        tabs.setForegroundGravity(Gravity.LEFT);
        tabs.setShouldExpand(true);
        tabs.setViewPager(homePager);
        final int tabsUnselectedColor = R.color.zhl_dark;
        final int tabsSelectedColor = R.color.zhl_darker;

        final TextView homeSearchHeader = (TextView) ((LinearLayout) tabs.getChildAt(0))
                .getChildAt(FRAGMENT_PRODUCT_SEARCH);
        homeSearchHeader.setTextColor(getResources().getColor(tabsSelectedColor));

    }

    public View getActionBarView() {
        return actionBarView;
    }

    @Override
    public void onTabClick(int position) {
        if (currentPageSelected == position) {

            try {
                switch (position) {

                    case FRAGMENT_PRODUCT_SEARCH:

                        // Home Scroll Top
                        if (fragments.get(FRAGMENT_PRODUCT_SEARCH) != null) {
                            ProductSearchFragment hf = (ProductSearchFragment) fragments.get(FRAGMENT_PRODUCT_SEARCH).get();
                            if (hf != null) {
                                hf.scrollHomeToTop();
                            }
                        } else {
                            HomePagerAdapter hAdapter = (HomePagerAdapter) homePager.getAdapter();
                            if (hAdapter != null) {
                                try {
                                    ProductSearchFragment fragMent = (ProductSearchFragment) hAdapter.instantiateItem(homePager,
                                            FRAGMENT_PRODUCT_SEARCH);
                                    if (fragMent != null)
                                        fragMent.scrollHomeToTop();
                                } catch (Exception e) {
                                }
                            }
                        }
                        break;

                }
            } catch (Exception e) {

            }

        }

    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if (requestType == ADD_TO_CART_SEARCH && status && !destroyed) {

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
                   // moveToCartActivity();
                    moveToProductDetail(productId,slug);
                } else if (jsonObject.getString("error") != null && jsonObject.getString("error").length() > 0) {

                    message = jsonObject.getString("error");
                }
                if (progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void moveToCartActivity(){
        Intent intent = new Intent(this, ProductCheckoutActivity.class);
        intent.putExtra("OrderSummaryFragment", false);
        startActivity(intent);
    }

    public void moveToProductDetail(int productId , String slug){
        Intent intent = new Intent(this, NewProductDetailActivity.class);
        intent.putExtra("slug", slug);
        intent.putExtra("id", productId);
        intent.putExtra("is_scanned",true);
        startActivity(intent);
    }
    ProgressDialog progressDialog;

    int productId;

    String slug;

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {
        if (requestType == ADD_TO_CART_SEARCH && !destroyed) {
            progressDialog = ProgressDialog.show(this, null, "Adding to cart. Please wait");
            // isLoading = true;
        }
    }

    private class HomePagerAdapter extends FragmentStatePagerAdapter {

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            fragments.put(position, null);
            super.destroyItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case FRAGMENT_PRODUCT_SEARCH:
                    CommonLib.ZLog("HomePage", "Creating new home page fragment");
                    ProductSearchFragment home = new ProductSearchFragment();
                    fragments.put(FRAGMENT_PRODUCT_SEARCH, new SoftReference<Fragment>(home));
                    return home;

            }
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }

        private String[] ids = { getResources().getString(R.string.products_title),
                getResources().getString(R.string.experts_title) };

        public String getPageTitle(int pos) {
            return ids[pos];
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TYPE_FROM_SEARCH) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");

                String format = data.getStringExtra("SCAN_RESULT_FORMAT");

                JSONObject obj = JSONUtils.getJSONObject(contents);

                productId = JSONUtils.getIntegerfromJSON(obj, "id");
                slug = JSONUtils.getStringfromJSON(obj, "slug");
                moveToProductDetail(productId, slug);

            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    public void addScannedObjToCart(int id,String slug){
        if (AppPreferences.isUserLogIn(this)) {
            if(AllProducts.getInstance().cartContains((int)id)){
                Toast.makeText(this, "Already added to cart", Toast.LENGTH_SHORT).show();
                //moveToCartActivity();
                moveToProductDetail(id,slug);
            }else {
                String url = AppApplication.getInstance().getBaseUrl() + ADD_TO_CART_URL;
                List<NameValuePair> nameValuePair = new ArrayList<>();

                nameValuePair.add(new BasicNameValuePair("buying_channel", AppConstants.BUYING_CHANNEL_OFFLINE+""));
                nameValuePair.add(new BasicNameValuePair("product_id", id + ""));
                nameValuePair.add(new BasicNameValuePair("quantity", "1"));
                nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(this)));
                UploadManager.getInstance().addCallback(this);
                UploadManager.getInstance().makeAyncRequest(url, ADD_TO_CART_SEARCH, slug, ObjectTypes.OBJECT_ADD_TO_CART, null, nameValuePair, null);
            }
        }else{
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
                oldObj.add(item);
                GetRequestManager.Update(AppPreferences.getDeviceID(this), oldObj, RequestTags.NON_LOGGED_IN_CART_CACHE, GetRequestManager.CONSTANT);
                Toast.makeText(this, "Successfully added to cart", Toast.LENGTH_SHORT).show();
            }
           // moveToCartActivity();
            moveToProductDetail(id,slug);
        }
    }



}
