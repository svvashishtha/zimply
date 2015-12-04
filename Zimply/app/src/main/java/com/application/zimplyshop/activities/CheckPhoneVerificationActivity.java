package com.application.zimplyshop.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Window;

import com.application.zimplyshop.R;
import com.application.zimplyshop.extras.AppConstants;

/**
 * Created by apoorvarora on 19/11/15.
 */
public class CheckPhoneVerificationActivity extends BaseActivity {


    private ActionBarActivity mActivity;
    private Bundle mBundle;
    SharedPreferences prefs;
    LayoutInflater vi;
    int width;
    private boolean destroyed = false;
    int fragmentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_phone_verification_activity);
        setFinishOnTouchOutside(false);
        setCategoryFragment();
    }
    Fragment fragment;

    public void setCategoryFragment() {
        // FrameLayout container = (FrameLayout) findViewById(R.id.container);
        Bundle bundle = new Bundle();
        fragmentType = AppConstants.TYPE_CATEGORY;
        bundle.putInt("type", AppConstants.TYPE_CATEGORY);
        fragment = PhoneVerificationFragment.newInstance(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .setCustomAnimations(R.anim.fragment_slide_right_enter,
                        R.anim.fragment_slide_left_exit,
                        R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_right_exit).commit();
    }

    public void setSubCategoryFragment(String mobile) {
        Bundle bundle = new Bundle();
        fragmentType = AppConstants.TYPE_SUB_CATEGORY;
        fragment = CheckPhoneVerificationFragment.newInstance(bundle);
        bundle.putString("mobile", mobile);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .setCustomAnimations(R.anim.fragment_slide_right_enter,
                        R.anim.fragment_slide_left_exit,
                        R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_right_exit).addToBackStack(null)
                .commit();
    }

    public void setPreviousFragment(Bundle mobile) {
        Bundle bundle = mobile;
        fragmentType = AppConstants.TYPE_SUB_CATEGORY;
        fragment = PhoneVerificationFragment.newInstance(bundle);
        getSupportFragmentManager().popBackStackImmediate();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .setCustomAnimations(R.anim.fragment_slide_right_enter,
                        R.anim.fragment_slide_left_exit,
                        R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_right_exit).addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {

    }
}
