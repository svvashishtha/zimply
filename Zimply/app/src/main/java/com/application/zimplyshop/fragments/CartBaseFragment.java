package com.application.zimplyshop.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.zimplyshop.R;
import com.application.zimplyshop.extras.AppConstants;

/**
 * Created by Umesh Lohani on 11/14/2015.
 */
public class CartBaseFragment extends ZFragment{

    public static CartBaseFragment newInstance(Bundle bundle){
     CartBaseFragment fragment = new CartBaseFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
    Bundle  bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.base_cart_layout,container,false);
         pager = (ViewPager) view.findViewById(R.id.viewpager);
        MainFragmentsAdapter adapter = new MainFragmentsAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(1);
        bundle = getArguments();
        TabLayout mTabs = (TabLayout)view.findViewById(R.id.indicator);

        //   mTabs.setTabTextColors(R.color.heading_text_color,R.color.pager_tab_selected_color);
        mTabs.setupWithViewPager(pager);


        return view;
    }

    ViewPager pager;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        int buyingChannel = getArguments().getInt("buying_channel");
        if(buyingChannel == AppConstants.BUYING_CHANNEL_ONLINE){
            pager.setCurrentItem(0);
        }else{
            pager.setCurrentItem(1);
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean onFragmentResult(Bundle bundle) {
        return false;
    }

    public class MainFragmentsAdapter extends FragmentStatePagerAdapter{

        String []titles = {"Online", "Offline"};

        public MainFragmentsAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            Bundle newBundle = new Bundle();
            newBundle .putAll(bundle);

            if(position == 0){
                newBundle.putInt("buying_channel", AppConstants.BUYING_CHANNEL_ONLINE);
            }else{
                newBundle.putInt("buying_channel", AppConstants.BUYING_CHANNEL_OFFLINE);
            }
            ZFragment fragment=MyCartFragment.newInstance(newBundle);

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
