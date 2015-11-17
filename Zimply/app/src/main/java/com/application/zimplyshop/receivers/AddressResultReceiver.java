package com.application.zimplyshop.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.application.zimplyshop.extras.AppConstants;

/**
 * Created by Saurabh on 29-09-2015.
 */
public class AddressResultReceiver extends ResultReceiver {
    AddressFoundListener mListener;

    public AddressResultReceiver(Handler handler) {
        super(handler);

    }

    public void setAddressFoundListener(AddressFoundListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        // Display the address string
        // or an error message sent from the intent service.

        // Show a toast message if an address was found.
        if (resultCode == AppConstants.SUCCESS_RESULT) {
            mListener.getAddress(resultData.getString(AppConstants.RESULT_DATA_KEY));

        }
        else
            mListener.errorInAddress(resultData.getString(AppConstants.RESULT_DATA_KEY));

    }

    public interface AddressFoundListener {
        void getAddress(String address);
        void errorInAddress(String errorMessage);
    }

}