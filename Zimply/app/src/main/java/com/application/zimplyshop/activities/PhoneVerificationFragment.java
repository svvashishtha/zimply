package com.application.zimplyshop.activities;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ErrorObject;
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

/**
 * Created by apoorvarora on 19/11/15.
 */
public class PhoneVerificationFragment extends BaseFragment implements UploadManagerCallback {

    private View getView;
    private boolean destroyed = false;
    private int width;

    public static PhoneVerificationFragment newInstance(Bundle bundle) {
        PhoneVerificationFragment fragment = new PhoneVerificationFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.phone_verification_fragment, null);
        destroyed = false;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int type = 0;
        getView = getView();
        destroyed = false;
        width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        ((EditText) getView.findViewById(R.id.phone_number)).setInputType(InputType.TYPE_CLASS_PHONE);
        UploadManager.getInstance().addCallback(this);

        View verifyButton = getView.findViewById(R.id.proceed_button);
        ((LinearLayout.LayoutParams) verifyButton.getLayoutParams()).setMargins(0, width / 20, 0, width / 20);
        verifyButton.getLayoutParams().height = 3 * width / 20;

        setListeners();
    }

    private void setListeners() {
        getView.findViewById(R.id.proceed_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = ((TextView) getView.findViewById(R.id.phone_number)).getText().toString();

                if (mobile == null || mobile.length() < 10 || mobile.length() > 10) {
                    showToast("Invalid phone number");
                    return;
                }

                String url = AppApplication.getInstance().getBaseUrl() + AppConstants.PHONE_VERIFICATION;
                List<NameValuePair> nameValuePair = new ArrayList<>();
                nameValuePair.add(new BasicNameValuePair("mobile", mobile));
                nameValuePair.add(new BasicNameValuePair("userid", AppPreferences.getUserID(getActivity())));
                UploadManager.getInstance().makeAyncRequest(url, RequestTags.PHONE_VERIFICATION_INPUT_NUMBER, "", ObjectTypes.OBJECT_TYPE_PHONE_VERIFICATION_INPUT, null, nameValuePair, null);

                //show loader till the response is completed
                getView.findViewById(R.id.proceed_button_progress).setVisibility(View.VISIBLE);
                getView.findViewById(R.id.proceed_button_text).setVisibility(View.GONE);

                CommonLib.hideKeyBoard(getActivity(), getView.findViewById(R.id.phone_number));
            }
        });

        getView.findViewById(R.id.notyoulogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.logOutUserFromApp(getActivity(), BaseLoginSignupActivity.class);
            }
        });
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if (requestType == RequestTags.PHONE_VERIFICATION_INPUT_NUMBER) {
            if (!destroyed) {
                getView.findViewById(R.id.proceed_button_progress).setVisibility(View.GONE);
                getView.findViewById(R.id.proceed_button_text).setVisibility(View.VISIBLE);
                if (status) {
                    ((CheckPhoneVerificationActivity) getActivity()).setSubCategoryFragment(((TextView) getView.findViewById(R.id.phone_number)).getText().toString());
                } else {
                    showToast(((ErrorObject) response).getErrorMessage());
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
        super.onDestroy();
    }
}
