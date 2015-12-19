package com.application.zimplyshop.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.SettingAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apoorvarora on 23/11/15.
 */
public class SettingsPage extends BaseActivity implements UploadManagerCallback {

    private boolean destroyed = false;
    RecyclerView settingList;
    LinearLayoutManager linearLayoutManager;
    SettingAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        settingList = (RecyclerView) findViewById(R.id.setting_list);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        settingList.setLayoutManager(linearLayoutManager);
        mAdapter = new SettingAdapter(this);
        mAdapter.setCounter(1);
        settingList.setAdapter(mAdapter);
        UploadManager.getInstance().addCallback(this);
        mAdapter.setClickListener(new SettingAdapter.ClickListeners() {
            @Override
            public void onPhoneNumberVerify(String number) {


                if (number == null || number.length() < 10 || number.length() > 10) {
                    showToast("Invalid phone number");
                    return;
                }

                String url = AppApplication.getInstance().getBaseUrl() + AppConstants.PHONE_VERIFICATION;
                List<NameValuePair> nameValuePair = new ArrayList<>();
                nameValuePair.add(new BasicNameValuePair("mobile", number));
                nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(SettingsPage.this)));
               // UploadManager.getInstance().makeAyncRequest(url, RequestTags.PHONE_VERIFICATION_INPUT_NUMBER, "", ObjectTypes.OBJECT_TYPE_PHONE_VERIFICATION_INPUT, null, nameValuePair, null);
                mAdapter.setCounter(3);
                mAdapter.notifyDataSetChanged();
                //show loader till the response is completed
                /*getView.findViewById(R.id.proceed_button_progress).setVisibility(View.VISIBLE);
                getView.findViewById(R.id.proceed_button_text).setVisibility(View.GONE);*/
                //CommonLib.hideKeyBoard(getActivity(), getView.findViewById(R.id.phone_number));
            }

            @Override
            public void onPhoneNumberCancel() {
                mAdapter.setCounter(1);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onOtpVerifyCancel() {
                mAdapter.setCounter(1);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void editNumber() {
                mAdapter.setCounter(2);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void resendOtp() {

            }

            @Override
            public void sendPasswordRequest() {

            }
        });
      /*  findViewById(R.id.change_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppPreferences.isUserLogIn(SettingsPage.this)) {
                    Intent intent = new Intent(SettingsPage.this, CheckPhoneVerificationActivity.class);
                    intent.putExtra("finish_on_touch_outside", true);
                    startActivity(intent);
                } else {
                    showToast("Please Login to continue");
                    Intent intent = new Intent(SettingsPage.this, BaseLoginSignupActivity.class);
                    intent.putExtra("inside", true);
                    startActivity(intent);
                }
            }
        });*/
    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(
                R.layout.common_toolbar_text_layout, null);
        TextView titleText = (TextView) view.findViewById(R.id.title_textview);
        titleText.setText("Settings");
        toolbar.addView(view);
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        super.onDestroy();
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if (requestType == RequestTags.PHONE_VERIFICATION_INPUT_NUMBER) {
            mAdapter.setCounter(3);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {

    }
}
