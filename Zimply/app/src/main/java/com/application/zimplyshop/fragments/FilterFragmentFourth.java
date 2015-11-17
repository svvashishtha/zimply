package com.application.zimplyshop.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.application.zimply.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class FilterFragmentFourth extends BaseFragment implements OnClickListener, UploadManagerCallback {

	private View rootView;
	private Activity mActivity;
	int width;
	ProgressDialog zProgressDialog;
	String queryId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.photo_filter_final, null);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = getActivity();
		width = mActivity.getWindowManager().getDefaultDisplay().getWidth();
		UploadManager.getInstance().addCallback(this);
		fixSizes();
		setListeners();
		queryId = getArguments().getString("query_id");
		//String url = AppApplication.getInstance().getBaseUrl() + AppConstants.GET_CITY_LIST;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*case R.id.top_img_1:
			getActivity().onBackPressed();
			//((FilterActivity) mActivity).getSupportFragmentManager().popBackStack();
			break;
		case R.id.top_img_2:
			break;*/
		}
	}

	@Override
	public void uploadFinished(int requestType, String objectId, Object data, Object respose, boolean status, int parserId) {
	if(requestType == CommonLib.SAVE_QUERUY_DETAILS){
		zProgressDialog.dismiss();
		AppPreferences.setIsStartRating(getActivity(),true);
		showToast("Form submitted successfully.");
		getActivity().finish();
	}
	}

	@Override
	public void uploadStarted(int requestType, String objectId, int parserId, Object object) {
	if(requestType == CommonLib.SAVE_QUERUY_DETAILS){
		zProgressDialog = ProgressDialog.show(getActivity() , null,"Saving. Please wait..");
	}
	}

	private void fixSizes() {

		//rootView.findViewById(R.id.thank_you_message).setPadding(width / 20, width / 20, width / 20, width / 20);
		//rootView.findViewById(R.id.additional_label).setPadding(width / 20, width / 20, width / 20, width / 20);
		//rootView.findViewById(R.id.additional_details_et).setPadding(width / 40, width / 40, width / 40, width / 40);
		/*((RelativeLayout.LayoutParams) rootView.findViewById(R.id.additional_details_et).getLayoutParams())
				.setMargins(width / 20, width / 20, width / 20, width / 20);*/
	}

	private void setListeners() {
		rootView.findViewById(R.id.finish_bt);
		rootView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(((EditText)rootView.findViewById(R.id.additional_details_et)).getText().toString().length()>0){
					postAdditionalDetail();
				}else{
					showToast("Form submitted successfully.");
					mActivity.finish();
				}

			}
		});
	}
	public void postAdditionalDetail(){
		String url = AppApplication.getInstance().getBaseUrl()+AppConstants.SAVE_FILTER	;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("query_id",queryId ));
		nameValuePairs.add(new BasicNameValuePair("detail",((EditText)rootView.findViewById(R.id.additional_details_et)).getText().toString() ));
		UploadManager.getInstance().makeAyncRequest(url, CommonLib.SAVE_QUERUY_DETAILS, "",
				ObjectTypes.OBJECT_TYPE_RESPONSE_FILTER, null, nameValuePairs, null);
	}

	@Override
	public void onDestroy() {
		UploadManager.getInstance().removeCallback(this);
		super.onDestroy();
	}
}

