package com.application.zimplyshop.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.fragments.BaseFragment;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by apoorvarora on 19/11/15.
 */
public class CheckPhoneVerificationFragment extends BaseFragment implements UploadManagerCallback {

    private View getView;
    private boolean destroyed = false;
    private Bundle mBundle;
    private String mobile;
    Timer timer;
    private int width;
    EditText mVerificationCodeEditText;
    String mVerificationCode = "", enteredVerficationCode = "";


    public static CheckPhoneVerificationFragment newInstance(Bundle bundle) {
        CheckPhoneVerificationFragment fragment = new CheckPhoneVerificationFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.check_phone_verification_fragment, null);
        destroyed = false;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int type = 0;
        getView = getView();
        destroyed = false;
        mBundle = getArguments();
        width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        mobile = mBundle.getString("mobile");

        //register receviers
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mNotificationReceived, new IntentFilter(CommonLib.LOCAL_SMS_BROADCAST));

        UploadManager.getInstance().addCallback(this);
        fillInfo();
        setListeners();
    }

    private void setListeners() {
    }

    private void fillInfo() {
        ((TextView) getView.findViewById(R.id.verify_phone_text)).setText(getActivity().getResources().getString(R.string.verification_code_message, mobile));
        mVerificationCodeEditText = (EditText) getView.findViewById(R.id.verification_code);
        mVerificationCodeEditText.getLayoutParams().height = 3 * width / 20;
        ((RelativeLayout.LayoutParams) mVerificationCodeEditText.getLayoutParams()).setMargins(0, width / 20, 0, width / 20);

        mVerificationCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 6) {
                    try {
                        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mVerificationCodeEditText.getRootView().getWindowToken(), 0);
                    } catch (Exception e) {
                    }
                    CommonLib.hideKeyBoard(getActivity(), getView.findViewById(R.id.verification_code));
                    String url = AppApplication.getInstance().getBaseUrl() + AppConstants.PHONE_VERIFICATION;
                    List<NameValuePair> nameValuePair = new ArrayList<>();
                    nameValuePair.add(new BasicNameValuePair("otp", s.toString().trim()));
                    nameValuePair.add(new BasicNameValuePair("mobile", mobile));
                    nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(getActivity())));
                    UploadManager.getInstance().makeAyncRequest(url, RequestTags.PHONE_VERIFICATION_OTP, "", ObjectTypes.OBJECT_TYPE_PHONE_VERIFICATION_OTP, null, nameValuePair, null);
                }
            }
        });

        View resendButton = getView.findViewById(R.id.resend_code);
        ((RelativeLayout.LayoutParams) resendButton.getLayoutParams()).setMargins(0, width / 20, width / 30, width / 20);
        resendButton.getLayoutParams().height = width / 10;
        resendButton.getLayoutParams().width = 9 * width / 20 - width / 60 - width / 10;
        resendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mobile == null || mobile.length() < 10 || mobile.length() > 10) {
                    showToast("Invalid phone number");
                    return;
                }

                String url = AppApplication.getInstance().getBaseUrl() + AppConstants.PHONE_VERIFICATION;
                List<NameValuePair> nameValuePair = new ArrayList<>();
                nameValuePair.add(new BasicNameValuePair("mobile", mobile));
                nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(getActivity())));
                UploadManager.getInstance().makeAyncRequest(url, RequestTags.PHONE_VERIFICATION_INPUT_NUMBER, "", ObjectTypes.OBJECT_TYPE_PHONE_VERIFICATION_INPUT, null, nameValuePair, null);
                startTimer();

            }
        });

        View editNumberButton = getView.findViewById(R.id.edit_number);
        ((RelativeLayout.LayoutParams) editNumberButton.getLayoutParams()).setMargins(width / 30, width / 20, 0, width / 20);
        editNumberButton.getLayoutParams().height = width / 10;
        editNumberButton.getLayoutParams().width = 9 * width / 20 - width / 60 - width / 10;
        editNumberButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((CheckPhoneVerificationActivity)getActivity()).setPreviousFragment(mBundle);
            }
        });

        startTimer();
        mVerificationCodeEditText.requestFocus();
        try {
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(mVerificationCodeEditText, InputMethodManager.SHOW_FORCED);
                }
            }, 400);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if(requestType == RequestTags.PHONE_VERIFICATION_OTP) {

            if(!destroyed) {
                if(status) {
                    showToast("Verified");
                    CommonLib.hideKeyBoard(getActivity(), getView.findViewById(R.id.verification_code));
                    getActivity().finish();
                } else {
                    showToast("Something went wrong in the phone verification. Please try after some time.");
                }
            }

        }
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {

    }

    @Override
    public void onDestroyView() {
        destroyed = true;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        UploadManager.getInstance().removeCallback(this);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mNotificationReceived);
        super.onDestroy();
    }

    int seconds = 60;

    private void startTimer() {
        if (getActivity() == null || getView == null || getView.findViewById(R.id.resend_code) == null)
            return;

        getView.findViewById(R.id.resend_code).setBackgroundResource(R.drawable.round_corner_borders_zhl);
        getView.findViewById(R.id.resend_code).setClickable(false);
        ((TextView) getView.findViewById(R.id.resend_code)).setTextColor(getActivity().getResources().getColor(R.color.zhl_darker));
        ((TextView) getView.findViewById(R.id.resend_code)).setText(getActivity().getResources().getString(R.string.retry_in, seconds));
        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!destroyed) {
                                seconds -= 1;
                                if (seconds <= 0) {
                                    seconds = 60;
                                    ((TextView) getView.findViewById(R.id.resend_code)).setText(getActivity().getResources().getString(R.string.resend_code));
                                    getView.findViewById(R.id.resend_code).setBackgroundResource(R.drawable.round_corner_borders_zdark);
                                    getView.findViewById(R.id.resend_code).setClickable(true);
                                    ((TextView) getView.findViewById(R.id.resend_code)).setTextColor(getActivity().getResources().getColor(R.color.zdark));
                                    timer.cancel();
                                } else {
                                    ((TextView) getView.findViewById(R.id.resend_code)).setText(getActivity().getResources().getString(R.string.retry_in, seconds));
                                }

                            }
                        }
                    });
                } else {
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }

    private BroadcastReceiver mNotificationReceived = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String verificationMessage = intent.getExtras().getString("verification_message");

                if (verificationMessage != null && !"".equals(verificationMessage)) {

                    StringTokenizer tokens = new StringTokenizer(verificationMessage, " ");
                    String otp = "";
                    boolean otpFound = false;
                    if(tokens.countTokens() > 0) {

                        if(tokens.hasMoreTokens()) {
                            otp = tokens.nextToken();
                            if (otp != null && otp.length() == 6) {
                                otpFound = true;
                            }
                        }
                    }

                    if(!otpFound)
                        return;

                    ((TextView)getView.findViewById(R.id.verification_code)).setText(otp+"");
                    CommonLib.hideKeyBoard(getActivity(), getView.findViewById(R.id.verification_code));
                    String url = AppApplication.getInstance().getBaseUrl() + AppConstants.PHONE_VERIFICATION;
                    List<NameValuePair> nameValuePair = new ArrayList<>();
                    nameValuePair.add(new BasicNameValuePair("otp", otp));
                    nameValuePair.add(new BasicNameValuePair("mobile", mobile));
                    nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(getActivity())));
                    UploadManager.getInstance().makeAyncRequest(url, RequestTags.PHONE_VERIFICATION_OTP, "", ObjectTypes.OBJECT_TYPE_PHONE_VERIFICATION_OTP, null, nameValuePair, null);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
