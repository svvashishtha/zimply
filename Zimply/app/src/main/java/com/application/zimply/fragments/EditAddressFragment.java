package com.application.zimply.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimply.R;
import com.application.zimply.activities.ProductCheckoutActivity;
import com.application.zimply.application.AppApplication;
import com.application.zimply.baseobjects.AddressObject;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.extras.ObjectTypes;
import com.application.zimply.managers.GetRequestListener;
import com.application.zimply.managers.GetRequestManager;
import com.application.zimply.preferences.AppPreferences;
import com.application.zimply.serverapis.RequestTags;
import com.application.zimply.utils.CommonLib;
import com.application.zimply.utils.UploadManager;
import com.application.zimply.utils.UploadManagerCallback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class EditAddressFragment extends ZFragment implements UploadManagerCallback, View.OnClickListener, GetRequestListener {

    private static EditAddressFragment fragment;
    Activity mActivity;
    int width, height;
    ProgressDialog zProgressDialog;
    EditText emailEdittext, phone, name, addLine1, addLine2, city, state, pinCode;
    // TextView cancel;
    AddressObject addressObject = null;
    boolean finishActivity;
    String titletext;
    private boolean destroyed = false;

    public static EditAddressFragment newInstance(Bundle bundle) {
        if (fragment == null) {
            fragment = new EditAddressFragment();
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CommonLib.ZLog("onCreateView", toString());

        view = inflater.inflate(R.layout.edit_address_fragment, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        titletext = ((ProductCheckoutActivity) getActivity()).getTitleText().getText().toString();
        return view;
    }

    @Override
    public void onDestroyView() {
        if (zProgressDialog != null) {
            zProgressDialog.dismiss();
        }
        destroyed = true;
        GetRequestManager.getInstance().removeCallbacks(this);
        UploadManager.getInstance().removeCallback(this);
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        width = mActivity.getWindowManager().getDefaultDisplay().getWidth();
        height = mActivity.getWindowManager().getDefaultDisplay().getHeight();


        phone = (EditText) view.findViewById(R.id.phone);
        emailEdittext = (EditText) view.findViewById(R.id.email);
        emailEdittext.setText(AppPreferences.getUserEmail(getActivity()));
        //cancel = (TextView) view.findViewById(R.id.cancel);
        // cancel.setOnClickListener(this);

        name = (EditText) view.findViewById(R.id.name);
        name.setText(AppPreferences.getUserName(getActivity()));
        addLine1 = (EditText) view.findViewById(R.id.address_line1);
        addLine2 = (EditText) view.findViewById(R.id.address_line2);
        city = (EditText) view.findViewById(R.id.city);
        city.setText(AppPreferences.getSavedCity(getActivity()));
        state = (EditText) view.findViewById(R.id.state);
        pinCode = (EditText) view.findViewById(R.id.pincode);

        setLoadingVariables();
        showView();

        if (getArguments() != null) {
            //get the arguments and set the value
            finishActivity = getArguments().getBoolean("stack");
            try {
                addressObject = (AddressObject) getArguments().getSerializable("addressObject");
                if (addressObject != null)
                    setData();
            } catch (Exception e) {
                e.printStackTrace();
                addressObject = null;
            }
        }
        //setValues();
        fixSizes();
        setListeners();
        UploadManager.getInstance().addCallback(this);
        GetRequestManager.getInstance().addCallbacks(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onBackPressed() {
        if (zProgressDialog != null) {
            zProgressDialog.dismiss();
        }
//        getActivity().getFragmentManager().popBackStack();
        return false;
    }

    @Override
    public boolean onFragmentResult(Bundle bundle) {
        return false;
    }

    private void fixSizes() {
    }

    public boolean checkValues() {
        if (((TextView) view.findViewById(R.id.name)).getText().toString().length() > 0) {
            if (((TextView) view.findViewById(R.id.email)).getText().toString().length() > 0) {
                if (((TextView) view.findViewById(R.id.phone)).getText().toString().length() >= 10) {
                    if (((TextView) view.findViewById(R.id.address_line1)).getText().toString().length() > 0) {
                        if (((TextView) view.findViewById(R.id.state)).getText().toString().length() > 0) {
                            if (((TextView) view.findViewById(R.id.pincode)).getText().toString().length() > 0) {
                                return true;
                            } else {
                                showToast("Please enter Pincode");
                                return false;
                            }
                        } else {
                            showToast("Please enter State");
                            return false;
                        }
                    } else {
                        showToast("Please enter Address Line 1");
                        return false;
                    }
                } else {
                    showToast("Invalid Phone Number ( Phone number should be 10 digits )");
                    return false;
                }

            } else {
                showToast("Please enter Email");
                return false;
            }
        } else {
            showToast("Please enter Name");
            return false;
        }
    }

    private void setListeners() {
        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValues()) {
                    zProgressDialog = ProgressDialog.show(mActivity, null, "Loading. Please wait..");

                    String phone = ((TextView) view.findViewById(R.id.phone)).getText().toString();
                    String email = ((TextView) view.findViewById(R.id.email)).getText().toString();
                    String name = ((TextView) view.findViewById(R.id.name)).getText().toString();
                    String address_line1 = ((TextView) view.findViewById(R.id.address_line1)).getText().toString();
                    String address_line2 = ((TextView) view.findViewById(R.id.address_line2)).getText().toString();
                    String state = ((TextView) view.findViewById(R.id.state)).getText().toString();
                    String pincode = ((TextView) view.findViewById(R.id.pincode)).getText().toString();
                    //TODO: city listview, get the selectedCity

                    String url = AppApplication.getInstance().getBaseUrl() + AppConstants.SAVE_ADDRESS;

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//                nameValuePairs.add(new BasicNameValuePair("city", "New Delhi"));
                    nameValuePairs.add(new BasicNameValuePair("name", name));
                    nameValuePairs.add(new BasicNameValuePair("userid", AppPreferences.getUserID(mActivity)));
                    nameValuePairs.add(new BasicNameValuePair("line2", address_line2));
                    nameValuePairs.add(new BasicNameValuePair("line1", address_line1));
                    nameValuePairs.add(new BasicNameValuePair("pincode", pincode));
                    nameValuePairs.add(new BasicNameValuePair("phone", phone));

//                nameValuePairs.add(new BasicNameValuePair("state", state));
                    if (addressObject != null) {
                        nameValuePairs.add(new BasicNameValuePair("id", addressObject.getId() + ""));
                    }
                    nameValuePairs.add(new BasicNameValuePair("email", email));

                    UploadManager.getInstance().makeAyncRequest(url, RequestTags.SAVE_ADDRESS, ObjectTypes.OBJECT_TYPE_SAVE_LIST + "", ObjectTypes.OBJECT_TYPE_SAVE_LIST, null, nameValuePairs, null);
                }
            }
        });

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
                if (input.length() == 6) {
                    checkPincodeAvailabilty(((EditText) view.findViewById(R.id.pincode)).getText().toString());
                }
            }
        };
        ((TextView) view.findViewById(R.id.pincode)).addTextChangedListener(textWatcher);
    }

    public void checkPincodeAvailabilty(String text) {
        if(CommonLib.isNetworkAvailable(getActivity())){
            String url = AppApplication.getInstance().getBaseUrl() + AppConstants.CHECK_PINCODE + "?pincode=" + text;
            GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.CHECKPINCODEREQUESTTAG, ObjectTypes.OBJECT_TYPE_CHECK_PINCODE);
        }else{
            Toast.makeText(getActivity(),"No network available",Toast.LENGTH_SHORT).show();
        }
    }

    private void setData() {
        name.setText(addressObject.getName());
        pinCode.setText(addressObject.getPincode());
        addLine2.setText(addressObject.getLine2());
        addLine1.setText(addressObject.getLine1());
        city.setText(addressObject.getCity());
        state.setText(addressObject.getState());
        phone.setText(addressObject.getPhone());
        emailEdittext.setText(addressObject.getEmail());
    }

    private void refreshView() {
        //Get the list of addresses
    }

    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if (zProgressDialog != null && zProgressDialog.isShowing()) {
            zProgressDialog.dismiss();

        }
        if (requestType == RequestTags.SAVE_ADDRESS && status && !destroyed) {
            addressObject = (AddressObject) response;
            Bundle bundle = new Bundle();
            bundle.putSerializable("address", addressObject);
            ((ProductCheckoutActivity) mActivity).setOrderSummaryFragment(bundle);
        } else {
            showToast("An error occurred.");
        }

    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {
        if (requestType == RequestTags.SAVE_ADDRESS && !destroyed) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

           /* case R.id.cancel:
                ((ProductCheckoutActivity) mActivity).onBackPressed();*/
        }
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (!destroyed && requestTag.equalsIgnoreCase(RequestTags.CHECKPINCODEREQUESTTAG)) {
            zProgressDialog = ProgressDialog.show(mActivity, null, "Checking availability.Please Wait..");
        }
        // findViewById(R.id.progress_container).setVisibility(View.VISIBLE);

    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (!destroyed && requestTag.equalsIgnoreCase(RequestTags.CHECKPINCODEREQUESTTAG)) {
            if (zProgressDialog != null) {
                zProgressDialog.dismiss();
                if (((boolean) obj)) {
                    view.findViewById(R.id.is_available_pincode).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.is_available_pincode)).setTextColor(getResources().getColor(R.color.green_text_color));
                    ((TextView) view.findViewById(R.id.is_available_pincode)).setText("Available at selected pincode");
                } else {
                    view.findViewById(R.id.is_available_pincode).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.is_available_pincode)).setTextColor(getResources().getColor(R.color.red_text_color));
                    ((TextView) view.findViewById(R.id.is_available_pincode)).setText("Not available at selected pincode");
                }
                CommonLib.hideKeyBoard(mActivity, view.findViewById(R.id.pincode));
            }
        }
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (requestTag != null && requestTag.equals(RequestTags.PRODUCT_DETAIL_REQUEST_TAG) && !destroyed) {
            // findViewById(R.id.progress_container).setVisibility(View.GONE);
            showNetworkErrorView();
            if (CommonLib.isNetworkAvailable(mActivity))
                Toast.makeText(mActivity, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mActivity, "Internet not available. Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        if (getActivity() != null && titletext != null) {
            ((ProductCheckoutActivity) getActivity()).setTitleText(titletext);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (getActivity() != null)
            hideKeyboard(getActivity());
        super.onPause();
    }
}