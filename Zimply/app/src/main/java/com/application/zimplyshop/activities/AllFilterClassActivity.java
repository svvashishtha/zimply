package com.application.zimplyshop.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.zimplyshop.R;
import com.application.zimplyshop.fragments.ArticleCategoryDialogFragment;
import com.application.zimplyshop.fragments.BaseDialogFragment;
import com.application.zimplyshop.fragments.BaseFragment;
import com.application.zimplyshop.fragments.FiltersFragmentPhoto;
import com.application.zimplyshop.fragments.ProductFilterFragment;
import com.application.zimplyshop.fragments.ProductPriceFilterFragment;
import com.application.zimplyshop.fragments.SortDialogFragment;

import java.util.HashMap;

/**
 * Created by Umesh Lohani on 10/5/2015.
 */
public class AllFilterClassActivity extends BaseDialogFragment implements View.OnClickListener {

    MainFragmentsAdapter adapter;

    Bundle bundle;

    OnApplyClickListener mListener;

    HashMap<Integer, Fragment> fragments;

    TabLayout mTabs;
    ViewPager pager;

    public static AllFilterClassActivity newInstance(Bundle bundle) {
        AllFilterClassActivity fragment = new AllFilterClassActivity();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_tab_layout, container, false);
        if (getDialog() != null) {
            getDialog().getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.white));

        }

        bundle = getArguments();
        pager = (ViewPager) view.findViewById(R.id.viewpager);
        view.findViewById(R.id.apply_btn).setOnClickListener(this);
        view.findViewById(R.id.reset_btn).setOnClickListener(this);

        view.findViewById(R.id.cancel_text).setOnClickListener(this);

        adapter = new MainFragmentsAdapter(getChildFragmentManager());
        pager.setOffscreenPageLimit(2);

        mTabs = (TabLayout) view.findViewById(R.id.indicator);
        mTabs.setBackgroundColor(getResources().getColor(R.color.white));


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pager.setAdapter(adapter);
        mTabs.setupWithViewPager(pager);
        pager.setCurrentItem(1);
    }

    /*@NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = new Dialog(getActivity(), R.style.HJCustomDialogTheme);
            bundle = getArguments();

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            DisplayMetrics metrics = new DisplayMetrics();

            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(view);

            dialog.getWindow().setLayout(
                    metrics.widthPixels - 2 * getResources().getDimensionPixelSize(R.dimen.margin_large),
                    ((4 * metrics.heightPixels) / 5));

            return dialog;
        }
    */
    @Override
    public void onResume() {
        super.onResume();



    }

    public void setOnApplyClickListener(OnApplyClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apply_btn:
                Bundle bundle = new Bundle();

                if (this.bundle.getBoolean("is_photos")) {
                    bundle.putInt("selected_pos", ((ArticleCategoryDialogFragment) fragments.get(0)).getSelectedPos());
                    bundle.putInt("size_id", ((FiltersFragmentPhoto) fragments.get(1)).getSizeId());
                    bundle.putInt("style_id", ((FiltersFragmentPhoto) fragments.get(1)).getStyleId());
                    bundle.putInt("budget_id", ((FiltersFragmentPhoto) fragments.get(1)).getBudgetId());
                } else if (this.bundle.getBoolean("is_articles")) {
                    bundle.putInt("selected_pos", ((ArticleCategoryDialogFragment) fragments.get(0)).getSelectedPos() == -1 ? 0 : ((ArticleCategoryDialogFragment) fragments.get(0)).getSelectedPos());
                    bundle.putInt("sort_id", ((SortDialogFragment) fragments.get(1)).getSortId());
                } else if (this.bundle.getBoolean("is_products")) {
                    bundle.putInt("selected_pos", ((ArticleCategoryDialogFragment) fragments.get(0)).getSelectedPos());
                    bundle.putInt("sort_id", ((ProductPriceFilterFragment) fragments.get(1)).getSortById());
                    if (((ProductPriceFilterFragment) fragments.get(1)).getSelectedMinValue().length() > 0) {
                        bundle.putInt("from_price", Integer.parseInt(((ProductPriceFilterFragment) fragments.get(1)).getSelectedMinValue()));
                    }
                    if (((ProductPriceFilterFragment) fragments.get(1)).getSelectedMaxValue().length() > 0) {
                        bundle.putInt("to_price", Integer.parseInt(((ProductPriceFilterFragment) fragments.get(1)).getSelectedMaxValue()));
                    }
                } else {
                    bundle.putInt("selected_pos", ((ProductFilterFragment) fragments.get(0)).getSelectedCategoryId());
                    bundle.putInt("expand_group", ((ProductFilterFragment) fragments.get(0)).getExpandGroup());
                    bundle.putInt("parent_category_id", ((ProductFilterFragment) fragments.get(0)).getSelectedParent_categoryId());
                    bundle.putInt("city_id", ((ArticleCategoryDialogFragment) fragments.get(1)).getSelectedPos());
                    bundle.putString("selected_category", ((ProductFilterFragment) fragments.get(0)).getSelectedCategoryName());
                }
                mListener.onApplyClick(bundle);
                dismiss();
                break;
            case R.id.reset_btn:
                if (this.bundle.getBoolean("is_photos")) {
                    ((ArticleCategoryDialogFragment) fragments.get(0)).setSelectedPos(0);
                    ((FiltersFragmentPhoto) fragments.get(1)).setSizeId(-1);
                    ((FiltersFragmentPhoto) fragments.get(1)).setBudgetId(-1);
                    ((FiltersFragmentPhoto) fragments.get(1)).setStyleId(-1);
                    ((FiltersFragmentPhoto) fragments.get(1)).resetAllValues();
                } else if (this.bundle.getBoolean("is_articles")) {
                    ((ArticleCategoryDialogFragment) fragments.get(0)).setSelectedPos(0);
                    ((SortDialogFragment) fragments.get(1)).setSortId(1);
                    ((SortDialogFragment) fragments.get(1)).changeSelectedValues();
                } else if (this.bundle.getBoolean("is_products")) {
                    ((ArticleCategoryDialogFragment) fragments.get(0)).setSelectedPos(0);
                    ((ProductPriceFilterFragment) fragments.get(1)).changeSelectedValues();
                } else {

                    ((ArticleCategoryDialogFragment) fragments.get(1)).setSelectedPos(0);
                    pager.setCurrentItem(1);
                }
                break;
            case R.id.cancel_text:
                dismiss();
                break;
        }

    }

    public interface OnApplyClickListener {
        void onApplyClick(Bundle bundle);
    }

    public class MainFragmentsAdapter extends FragmentPagerAdapter {


        public MainFragmentsAdapter(FragmentManager fm) {
            super(fm);
            fragments = new HashMap<>();
        }

        @Override
        public Fragment getItem(int position) {

            BaseFragment fragment;

            if (position == 0) {
                if (bundle.getBoolean("is_expert")) {
                    fragment = ProductFilterFragment.newInstance(bundle);
                } else {
                    fragment = ArticleCategoryDialogFragment.newInstance(bundle);
                }

            } else {
                if (bundle.getBoolean("is_photos")) {
                    fragment = FiltersFragmentPhoto.newInstance(bundle);
                } else if (bundle.getBoolean("is_location")) {
                    bundle.putBoolean("is_second", true);
                    fragment = ArticleCategoryDialogFragment.newInstance(bundle);
                    ((ArticleCategoryDialogFragment) fragment).setIsLoc(true);
                } else if (bundle.getBoolean("is_products")) {
                    fragment = ProductPriceFilterFragment.newInstance(bundle);
                } else {
                    fragment = SortDialogFragment.newInstance(bundle);
                }
            }
            fragments.put(position, fragment);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title;
            if (position == 0) {
                title = "Categories";
            } else {
                if (bundle.getBoolean("is_photos")) {
                    title = "Filters";
                } else if (bundle.getBoolean("is_articles")) {
                    title = "Sort By";
                } else if (bundle.getBoolean("is_products")) {
                    title = "Filter";
                } else {
                    title = "Location";
                }
            }
            return title;
        }
    }
}
