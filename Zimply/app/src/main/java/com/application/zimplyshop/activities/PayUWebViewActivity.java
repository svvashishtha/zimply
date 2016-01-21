package com.application.zimplyshop.activities;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.application.zimplyshop.R;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Payu.PayuConstants;

import org.apache.http.util.EncodingUtils;


/**
 * Created by Ashish Goel on 1/20/2016.
 */
public class PayUWebViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payu_webview_activity_layout);
        // initialize
        Bundle bundle = getIntent().getExtras();
        PayuConfig payuConfig = bundle.getParcelable(PayuConstants.PAYU_CONFIG);
        WebView mWebView = (WebView) findViewById(R.id.webview);
        String url = payuConfig.getEnvironment() == PayuConstants.PRODUCTION_ENV ? PayuConstants.PRODUCTION_PAYMENT_URL : PayuConstants.MOBILE_TEST_PAYMENT_URL;
        byte[] encodedData = EncodingUtils.getBytes(payuConfig.getData(), "base64");
        mWebView.postUrl(url, encodedData);
        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
        });
        mWebView.setWebViewClient(new WebViewClient() {
        });
    }
}
