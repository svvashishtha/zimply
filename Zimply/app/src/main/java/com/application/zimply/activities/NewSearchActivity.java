package com.application.zimply.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
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

import com.application.zimply.R;
import com.application.zimply.fragments.ExpertSearchFragment;
import com.application.zimply.fragments.ProductSearchFragment;
import com.application.zimply.managers.GetRequestManager;
import com.application.zimply.utils.CommonLib;
import com.application.zimply.utils.JSONUtils;
import com.application.zimply.utils.NoSwipeViewPager;
import com.application.zimply.widgets.ZPagerSlidingTabStrip;

import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.List;

public class NewSearchActivity extends BaseActivity implements ZPagerSlidingTabStrip.OnTabClickListener {

    private boolean destroyed = false;
    View actionBarView;

    public static final int FRAGMENT_PRODUCT_SEARCH = 0;
    public static final int FRAGMENT_EXPERT_SEARCH = 1;
    public static final int VIEWPAGER_INDEX_ME_FRAGMENT = 2;

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
                        ExpertSearchFragment srf = (ExpertSearchFragment) fragments.get(FRAGMENT_EXPERT_SEARCH).get();
                        if (srf != null && srf.isVisible()) {
                            srf.performSearch(input);
                        }
                    }

                } else {
                    actionBarView.findViewById(R.id.clear_text_view_category).setVisibility(View.GONE);
                    //send the api call to the fragment
                    if (fragments.get(FRAGMENT_PRODUCT_SEARCH) != null || fragments.get(FRAGMENT_EXPERT_SEARCH) != null) {
                        ProductSearchFragment hf = (ProductSearchFragment) fragments.get(FRAGMENT_PRODUCT_SEARCH).get();
                        if (hf != null && hf.isVisible()) {
                            hf.performSearch(input);
                        }
                        ExpertSearchFragment srf = (ExpertSearchFragment) fragments.get(FRAGMENT_EXPERT_SEARCH).get();
                        if (srf != null && srf.isVisible()) {
                            srf.performSearch(input);
                        }
                    }
                }

            }
        };

        ((TextView)actionBarView.findViewById(R.id.search_category)).addTextChangedListener(textWatcher);

        // Search tabs
        homePager = (NoSwipeViewPager) findViewById(R.id.home_pager);
        homePager.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));
        homePager.setOffscreenPageLimit(4);

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

    private void addToolbarView(Toolbar toolbar) {
        actionBarView = LayoutInflater.from(this).inflate(R.layout.common_toolbar_text_layout, toolbar, false);
        actionBarView.findViewById(R.id.title_textview).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.search_frame).setVisibility(View.VISIBLE);
        actionBarView.findViewById(R.id.barcode_icon).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.barcode_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                PackageManager packageMgr = getPackageManager();
                List<ResolveInfo> activities = packageMgr.queryIntentActivities(intent, 0);
                if (activities.size() > 0) {
                    startActivityForResult(intent, 0);
                } else {
                    showToast("Barcode scanner is not available in your device");
                }
            }
        });

        actionBarView.findViewById(R.id.clear_text_view_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.search_category)).setText("");
            }
        });

        ((EditText)actionBarView.findViewById(R.id.search_category)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //call fragment search
                    return true;
                }
                return false;
            }
        });

        toolbar.addView(actionBarView);
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
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
        /*tabs.setDividerColor(getResources().getColor(R.color.transparent1));
        tabs.setBackgroundColor(getResources().getColor(R.color.zimply_darker));
        tabs.setUnderlineColor(getResources().getColor(R.color.zhl_dark));
        tabs.setTypeface(CommonLib.getTypeface(getApplicationContext(), CommonLib.BOLD_FONT), 0);
        tabs.setIndicatorColor(getResources().getColor(R.color.zhl_dark));
        tabs.setIndicatorHeight((int) getResources().getDimension(R.dimen.height3));
        tabs.setTextSize((int) getResources().getDimension(R.dimen.size15));
        tabs.setUnderlineHeight(0);
        tabs.setTabPaddingLeftRight(12);
        tabs.setOnTabClickListener(this);
*/
        final int tabsUnselectedColor = R.color.zhl_dark;
        final int tabsSelectedColor = R.color.zhl_darker;

        final TextView homeSearchHeader = (TextView) ((LinearLayout) tabs.getChildAt(0))
                .getChildAt(FRAGMENT_PRODUCT_SEARCH);
        final TextView homeNearbyHeader = (TextView) ((LinearLayout) tabs.getChildAt(0))
                .getChildAt(FRAGMENT_EXPERT_SEARCH);

        homeSearchHeader.setTextColor(getResources().getColor(tabsSelectedColor));
        homeNearbyHeader.setTextColor(getResources().getColor(tabsUnselectedColor));

        setPageChangeListenerOnTabs(tabs, tabsUnselectedColor, tabsSelectedColor, homeSearchHeader, homeNearbyHeader);
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

                    case FRAGMENT_EXPERT_SEARCH:

                        // Search Scroll Top
                        if (fragments.get(FRAGMENT_EXPERT_SEARCH) != null) {
                            ExpertSearchFragment srf = (ExpertSearchFragment) fragments.get(FRAGMENT_EXPERT_SEARCH).get();
                            if (srf != null) {
                                srf.scrollSearchToTop();
                            }

                        } else {
                            HomePagerAdapter hAdapter = (HomePagerAdapter) homePager.getAdapter();
                            if (hAdapter != null) {
                                try {
                                    ExpertSearchFragment fragMent = (ExpertSearchFragment) hAdapter.instantiateItem(homePager,
                                            FRAGMENT_EXPERT_SEARCH);
                                    if (fragMent != null)
                                        fragMent.scrollSearchToTop();
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

    private void setPageChangeListenerOnTabs(ZPagerSlidingTabStrip tabs, final int tabsUnselectedColor,
                                             final int tabsSelectedColor, final TextView homeSearchHeader, final TextView homeNearbyHeader) {
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                currentPageSelected = arg0;

                // SearchFragment
                if (arg0 == FRAGMENT_EXPERT_SEARCH) {

                    if (fragments.get(FRAGMENT_EXPERT_SEARCH) != null) {

                        if (fragments.get(FRAGMENT_EXPERT_SEARCH).get() instanceof ExpertSearchFragment) {
                            ExpertSearchFragment srf = (ExpertSearchFragment) fragments.get(FRAGMENT_EXPERT_SEARCH).get();
                            if (srf != null) {

                                // if (!srf.searchCallsInitiatedFromHome)
                                // srf.initiateSearchCallFromHome();

                            }
                        }

                    } else {
                        HomePagerAdapter hAdapter = (HomePagerAdapter) homePager.getAdapter();
                        if (hAdapter != null) {
                            try {
                                ExpertSearchFragment fragMent = (ExpertSearchFragment) hAdapter.instantiateItem(homePager,
                                        FRAGMENT_EXPERT_SEARCH);
                                if (fragMent != null) {

                                    // if
                                    // (!fragMent.searchCallsInitiatedFromHome)
                                    // fragMent.initiateSearchCallFromHome();

                                }
                            } catch (Exception e) {
                                // Crashlytics.logException(e);
                            }
                        }
                    }

                    homeSearchHeader.setTextColor(getResources().getColor(tabsUnselectedColor));
                    homeNearbyHeader.setTextColor(getResources().getColor(tabsSelectedColor));

                } else if (arg0 == FRAGMENT_PRODUCT_SEARCH) {

                    homeSearchHeader.setTextColor(getResources().getColor(tabsSelectedColor));
                    homeNearbyHeader.setTextColor(getResources().getColor(tabsUnselectedColor));

                    try {
                        ((DrawerLayout) findViewById(R.id.drawer_layout))
                                .setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    } catch (Exception e) {
                    }

                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == 0) {

                    int alphaValueUnderline = (int) ((((positionOffset - 0) * (255 - 0)) / (1 - 0)) + 0);
                    ((ZPagerSlidingTabStrip) findViewById(R.id.tabs))
                            .setUnderlineColor(Color.argb(alphaValueUnderline, 228, 228, 228));

                } else if (position > 0) {
                    ((ZPagerSlidingTabStrip) findViewById(R.id.tabs)).setUnderlineColor(Color.argb(255, 228, 228, 228));
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
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

                case FRAGMENT_EXPERT_SEARCH:
                    ExpertSearchFragment details = new ExpertSearchFragment();
                    fragments.put(FRAGMENT_EXPERT_SEARCH, new SoftReference<Fragment>(details));
                    return details;

            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        private String[] ids = { getResources().getString(R.string.products_title),
                getResources().getString(R.string.experts_title) };

        public String getPageTitle(int pos) {
            return ids[pos];
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                //Toast.makeText(this , "CONTENT"+contents,Toast.LENGTH_SHORT).show();
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                //Toast.makeText(this , "FORMAT:"+format,Toast.LENGTH_SHORT).show();
                JSONObject obj = JSONUtils.getJSONObject(contents);
                Intent workintent = new Intent(this, ProductDetailsActivity.class);
                intent.putExtra("slug", JSONUtils.getIntegerfromJSON(obj, "slug"));
                workintent .putExtra("id", (long)JSONUtils.getIntegerfromJSON(obj, "id"));
                startActivity(workintent );
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }


}
