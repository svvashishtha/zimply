package com.application.zimplyshop.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.ErrorObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;

public class ForgotPassword extends BaseActivity implements GetRequestListener, OnClickListener {

	private boolean destroyed = false;
	boolean isDestroyed = false;
	private Activity mContext;
	private ProgressDialog zProgressDialog;
    EditText email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password_activity);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		View view = LayoutInflater.from(this).inflate(R.layout.login_signup_toolbar_layout, toolbar, false);
		view.findViewById(R.id.cancel_action).setOnClickListener(this);
		((TextView) view.findViewById(R.id.signup_layout_text)).setText("Forgot Password?");
        email = (EditText) findViewById(R.id.email_et);
		toolbar.addView(view);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		mContext = this;
		fixSizes();
		setListeners();
		GetRequestManager.getInstance().addCallbacks(this);
	}

	private void fixSizes() {

	}

	private void setListeners() {
		findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {

                String email = String.valueOf(((EditText) mContext.findViewById(R.id.email_et)).getText());
                if (CommonLib.isNetworkAvailable(ForgotPassword.this)) {
                    if (checkEmail()) {
                        String url = AppApplication.getInstance().getBaseUrl() + AppConstants.FORGOT_PASSWORD + "?email="
                                + email;
                        GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.FORGOT_PASSWORD_REQUEST_TAG,
                                ObjectTypes.OBJECT_TYPE_FORGOT_PASSWORD);
                    }
                }else{
                    showToast("Failed to load. Check your internet connection");
                }
            }
		});
	}

    public boolean checkEmail(){
        if(email.getText().toString().trim().length()>0){
            if(checkEmailFormat(email.getText().toString().trim())){
                return true;
            }else {
                showToast("Please enter a valid email address");

                return false;
            }
        }else{
            showToast("Please enter an email address");
            return false;
        }
    }

    private boolean checkEmailFormat(CharSequence target) {

        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();

        }
    }
	@Override
	public void onDestroy() {
		destroyed = true;
		GetRequestManager.getInstance().removeCallbacks(this);
		if (zProgressDialog != null)
			zProgressDialog.dismiss();
		super.onDestroy();
	}

	@Override
	public void onRequestStarted(String requestTag) {
		if (requestTag.equals(RequestTags.FORGOT_PASSWORD_REQUEST_TAG)) {
			zProgressDialog = ProgressDialog.show(this, null, "Loading. Please wait..");
		}
	}

	@Override
	public void onRequestCompleted(String requestTag, Object obj) {
		if (requestTag.equals(RequestTags.FORGOT_PASSWORD_REQUEST_TAG)) {
			if (!destroyed) {
				if (zProgressDialog != null)
                    zProgressDialog.dismiss();
                showToast((String) obj);

				mContext.finish();
			}
		}
	}

	@Override
	public void onRequestFailed(String requestTag, Object obj) {
		if (requestTag.equals(RequestTags.FORGOT_PASSWORD_REQUEST_TAG)) {
			if (!destroyed) {
				if (zProgressDialog != null)
					zProgressDialog.dismiss();
                showToast(((ErrorObject)obj).getErrorMessage());
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel_action:
			onBackPressed();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_right_exit);
		super.onBackPressed();
	}
}
