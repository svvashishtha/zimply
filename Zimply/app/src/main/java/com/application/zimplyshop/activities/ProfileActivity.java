package com.application.zimplyshop.activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.managers.ImageLoaderManager;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.widgets.CircularImageView;
import com.application.zimplyshop.widgets.CustomTextView;
import com.application.zimplyshop.widgets.CustomTextViewBold;

import java.util.ArrayList;

public class ProfileActivity extends BaseActivity implements GetRequestListener, View.OnClickListener {

    ImageView backgroundImage;
    CircularImageView propic;
    CustomTextView emailUser, pNumber, nameAddress, addressLine1, addressLine2, numberAddress, viewAll;
    CustomTextViewBold nameOfUser;
    ArrayList<AddressObject> addressObjectArrayList;
    View mainView, addressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        GetRequestManager.getInstance().addCallbacks(this);
        viewAll = (CustomTextView) findViewById(R.id.view_all);
        viewAll.setOnClickListener(this);
        addressView = findViewById(R.id.address_view);
        mainView = findViewById(R.id.mainView);
        backgroundImage = (ImageView) findViewById(R.id.background_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_back_white));
        //getSupportActionBar().setDisplayShowTitleEnabled(false);*/
        setLoadingVariables();

        propic = (CircularImageView) findViewById(R.id.profile_pic);
        nameOfUser = (CustomTextViewBold) findViewById(R.id.name_user);
        emailUser = (CustomTextView) findViewById(R.id.email_user);
        pNumber = (CustomTextView) findViewById(R.id.number_user);
        nameOfUser.setText(AppPreferences.getUserName(ProfileActivity.this));
        emailUser.setText(AppPreferences.getUserEmail(ProfileActivity.this));
        pNumber.setText(AppPreferences.getUserPhoneNumber(ProfileActivity.this));
        nameAddress = (CustomTextView) findViewById(R.id.name_address);
        addressLine1 = (CustomTextView) findViewById(R.id.address_line1);
        addressLine2 = (CustomTextView) findViewById(R.id.address_line2);
        numberAddress = (CustomTextView) findViewById(R.id.phone_number);

        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());
        float density = getResources().getDisplayMetrics().density;
        float dp = 100 / density;
        new ImageLoaderManager(this).setImageFromUrl(AppPreferences.getUserPhoto(this), propic, "users", (int) dp, (int) dp, false, false);
        new ImageLoaderManager(this).setImageFromUrl(AppPreferences.getUserPhoto(this), backgroundImage, "users", (int) px * 8, (int) px * 4, true, false);
        loadData();
    }

    void fillAddressView(AddressObject addressObject) {
        nameAddress.setText(addressObject.getName());
        addressLine1.setText(addressObject.getLine1());
        addressLine2.setText(addressObject.getLine2());
        numberAddress.setText(addressObject.getPhone());
    }

    private void loadData() {
        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_ADDRESSES +
                "?src=mob&userid=" + AppPreferences.getUserID(ProfileActivity.this);
        GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.GET_ADDRESS_REQUEST_TAG, ObjectTypes.OBJECT_TYPE_GET_ADDRESSES);
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (requestTag.equalsIgnoreCase(RequestTags.GET_ADDRESS_REQUEST_TAG)) {
            showLoadingView();
            mainView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (requestTag.equalsIgnoreCase(RequestTags.GET_ADDRESS_REQUEST_TAG)) {

            showView();
            mainView.setVisibility(View.VISIBLE);
            if (addressObjectArrayList == null)
                addressObjectArrayList = new ArrayList<>();
            addressObjectArrayList = (ArrayList<AddressObject>) obj;
            if (addressObjectArrayList.size() > 0)
                fillAddressView(addressObjectArrayList.get(0));
            else addressView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (requestTag.equalsIgnoreCase(RequestTags.GET_ADDRESS_REQUEST_TAG)) {
            showView();
            mainView.setVisibility(View.VISIBLE);
            addressView.setVisibility(View.GONE);
            showToast("An error occurred. Please try again...");
        }
    }

    @Override
    protected void onDestroy() {
        GetRequestManager.getInstance().removeCallbacks(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_all:

                break;
        }
    }
}
