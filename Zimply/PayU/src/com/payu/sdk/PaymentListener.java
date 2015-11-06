package com.payu.sdk;

/**
 * Created by franklin.michael on 27-06-2014.
 */
public interface PaymentListener {
    void onPaymentOptionSelected(PayU.PaymentMode paymentMode);

    void onGetResponse(String responseMessage);
}
