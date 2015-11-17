package com.application.zimplyshop.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.application.zimply.R;
import com.application.zimplyshop.extras.AppConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ZFetchAddressIntentService extends IntentService {

    private String TAG = "ZFetchAddressIntentService";
    protected ResultReceiver mReceiver;


    public ZFetchAddressIntentService() {
        super("ZFetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String errorMessage = "";

            // Get the location passed to this service through an extra.
            Location location = intent.getParcelableExtra(
                    AppConstants.LOCATION_DATA_EXTRA);
            mReceiver = intent.getParcelableExtra(AppConstants.RECEIVER);


            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        //we only need 1 address
                        1);
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                errorMessage = getString(R.string.service_not_available);
                Log.e(TAG, errorMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                // errorMessage = getString(R.string.invalid_lat_long_used);
                Log.e(TAG, errorMessage + ". " +
                        "Latitude = " + location.getLatitude() +
                        ", Longitude = " +
                        location.getLongitude(), illegalArgumentException);
            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size() == 0) {
                if (errorMessage.isEmpty()) {
                    //errorMessage = getString(R.string.no_address_found);
                    Log.e(TAG, errorMessage);
                }
                deliverResultToReceiver(AppConstants.FAILURE_RESULT, errorMessage);
            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                //Log.i(TAG, getString(R.string.address_found));
                deliverResultToReceiver(AppConstants.SUCCESS_RESULT,
                        /*TextUtils.join(System.getProperty("line.separator"),
                                addressFragments)*/address.getAdminArea());
            }
        }
    }


    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }


}
