package com.application.zimplyshop.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.AddressObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.objects.AllUsers;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.UploadManager;
import com.application.zimplyshop.utils.UploadManagerCallback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EditAddressActivity extends BaseActivity implements RequestTags, UploadManagerCallback, GetRequestListener, View.OnClickListener {
    AddressObject addressObject;
    private EditText phone, emailEdittext, name, addLine1, addLine2, city, state, pinCode;
    Context context;
    Toolbar toolbar;
    private ProgressDialog zProgressDialog;
    private int editPosition = -1;
    private boolean isEditAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        context = EditAddressActivity.this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.z_text_color_light));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        phone = (EditText) findViewById(R.id.phone);
        phone.setText(AppPreferences.getUserPhoneNumber(context));
        emailEdittext = (EditText) findViewById(R.id.email);
        emailEdittext.setText(AppPreferences.getUserEmail(context));
        name = (EditText) findViewById(R.id.name);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                int length = text.length();

                if (!Pattern.matches("^[a-zA-Z\\s]*$", text) && length > 0) {
                    s.delete(length - 1, length);
                }
            }
        });
        name.setText(AppPreferences.getUserName(context));
        addLine1 = (EditText) findViewById(R.id.address_line1);
        addLine2 = (EditText) findViewById(R.id.address_line2);
        city = (EditText) findViewById(R.id.city);
        city.setText(AppPreferences.getSavedCity(context));
        city.setKeyListener(null);
        state = (EditText) findViewById(R.id.state);
        state.setKeyListener(null);
        pinCode = (EditText) findViewById(R.id.pincode);

        setLoadingVariables();
        retryLayout.setOnClickListener(this);

        addressObject = (AddressObject) getIntent().getSerializableExtra("addressObject");
        if (addressObject != null) {
            setData();
            editPosition = getIntent().getIntExtra("edit_position", -1);
            isEditAddress = true;
            getSupportActionBar().setTitle("Edit address");
            showView();
        } else {
            getSupportActionBar().setTitle("Add address");
            showView();
        }
        setListeners();
        UploadManager.getInstance().addCallback(this);
        GetRequestManager.getInstance().addCallbacks(this);
    }

    private boolean checkEmailFormat(CharSequence target) {

        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();

        }
    }

    public boolean checkValues() {
        if (((TextView) findViewById(R.id.name)).getText().toString().length() > 0) {
            if (((TextView) findViewById(R.id.email)).getText().toString().length() > 0) {
                if (checkEmailFormat(((TextView) findViewById(R.id.email)).getText().toString())) {
                    if (((TextView) findViewById(R.id.phone)).getText().toString().length() >= 10) {
                        if (((TextView) findViewById(R.id.address_line1)).getText().toString().length() > 0) {
                            if (((TextView) findViewById(R.id.state)).getText().toString().length() > 0) {
                                if (((TextView) findViewById(R.id.pincode)).getText().toString().length() > 0) {
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
                    showToast("Not a valid email address");
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

    private void setListeners() {
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValues()) {
                    zProgressDialog = ProgressDialog.show(context, null, "Loading. Please wait..");

                    String phone = ((TextView) findViewById(R.id.phone)).getText().toString();
                    String email = ((TextView) findViewById(R.id.email)).getText().toString();
                    String name = ((TextView) findViewById(R.id.name)).getText().toString();
                    String address_line1 = ((TextView) findViewById(R.id.address_line1)).getText().toString();
                    String address_line2 = ((TextView) findViewById(R.id.address_line2)).getText().toString();
                    String state = ((TextView) findViewById(R.id.state)).getText().toString();
                    String pincode = ((TextView) findViewById(R.id.pincode)).getText().toString();
                    //TODO: city listview, get the selectedCity

                    String url = AppApplication.getInstance().getBaseUrl() + AppConstants.SAVE_ADDRESS;

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//                nameValuePairs.add(new BasicNameValuePair("city", "New Delhi"));
                    nameValuePairs.add(new BasicNameValuePair("name", name));
                    nameValuePairs.add(new BasicNameValuePair("userid", AppPreferences.getUserID(context)));
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
                    checkPincodeAvailabilty(((EditText) findViewById(R.id.pincode)).getText().toString());
                }
            }
        };
        ((TextView) findViewById(R.id.pincode)).addTextChangedListener(textWatcher);
    }

    public void checkPincodeAvailabilty(String text) {
        if (CommonLib.isNetworkAvailable(context)) {
            String url = AppApplication.getInstance().getBaseUrl() +
                    AppConstants.GET_CITY_FROM_PINCODE + "?pincode=" + text;
            GetRequestManager.getInstance().makeAyncRequest(url, GET_CITY_FROM_PINCODE, ObjectTypes.OBJECT_TYPE_CITY_STATE);
            CommonLib.hideKeyBoard((Activity) context, findViewById(R.id.pincode));
            /*String url = AppApplication.getInstance().getBaseUrl() + AppConstants.CHECK_PINCODE + "?pincode=" + text;
            GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.CHECKPINCODEREQUESTTAG, ObjectTypes.OBJECT_TYPE_CHECK_PINCODE);
    */
        } else {
            Toast.makeText(context, "No network available", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void uploadFinished(int requestType, String objectId, Object data, Object response, boolean status, int parserId) {
        if (zProgressDialog != null && zProgressDialog.isShowing()) {
            zProgressDialog.dismiss();

        }
        if (requestType == RequestTags.SAVE_ADDRESS && status) {
            addressObject = (AddressObject) response;
            if (isEditAddress) {
                showToast("Address saved");
                AllUsers.getInstance().getObjs().set(editPosition, addressObject);
            } else {
                AllUsers.getInstance().getObjs().add(0, addressObject);
            }
        }
    }

    @Override
    public void uploadStarted(int requestType, String objectId, int parserId, Object data) {

    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (requestTag.equalsIgnoreCase(RequestTags.CHECKPINCODEREQUESTTAG)) {
            zProgressDialog = ProgressDialog.show(context, null, "Checking availability.Please Wait..");
        }
        if (requestTag.equalsIgnoreCase(RequestTags.GET_CITY_FROM_PINCODE)) {
            zProgressDialog = ProgressDialog.show(context, null, "Getting details.Please Wait..");
        }
        // findViewById(R.id.progress_container).setVisibility(View.VISIBLE);

    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (requestTag.equalsIgnoreCase(RequestTags.CHECKPINCODEREQUESTTAG)) {
            if (zProgressDialog != null) {
                zProgressDialog.dismiss();
                if (((boolean) obj)) {
                    findViewById(R.id.is_available_pincode).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.is_available_pincode)).setTextColor(getResources().getColor(R.color.green_text_color));
                    ((TextView) findViewById(R.id.is_available_pincode)).setText("Available at selected pincode");
                } else {
                    findViewById(R.id.is_available_pincode).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.is_available_pincode)).setTextColor(getResources().getColor(R.color.red_text_color));
                    ((TextView) findViewById(R.id.is_available_pincode)).setText("Not available at selected pincode");
                }
                CommonLib.hideKeyBoard((Activity) context, findViewById(R.id.pincode));
            }
        } else if (requestTag.equalsIgnoreCase(RequestTags.GET_CITY_FROM_PINCODE)) {
            if (zProgressDialog != null) {
                zProgressDialog.dismiss();
            }
            if (obj != null) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = (JSONObject) (new JSONObject((String) obj)).get("Address");
                    String cityString = (String) jsonObject.get("city");
                    String stateString = (String) jsonObject.get("state");
                    city.setText(cityString);
                    state.setText(stateString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (requestTag != null && requestTag.equals(RequestTags.PRODUCT_DETAIL_REQUEST_TAG)) {
            // findViewById(R.id.progress_container).setVisibility(View.GONE);
            showNetworkErrorView();
            if (CommonLib.isNetworkAvailable(context))
                Toast.makeText(context, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Internet not available. Please try again", Toast.LENGTH_SHORT).show();
        } else if (requestTag != null && requestTag.equals(RequestTags.GET_CITY_FROM_PINCODE)) {
            if (zProgressDialog != null) {
                zProgressDialog.dismiss();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.retry_layout:
                findViewById(R.id.save).performClick();
                break;
        }
    }
}
