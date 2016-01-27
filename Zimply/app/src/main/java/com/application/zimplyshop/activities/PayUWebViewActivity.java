package com.application.zimplyshop.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

//        mWebView.addJavascriptInterface(new Object() {
//
//            @android.webkit.JavascriptInterface
//            public void onSuccess() {
//                onSuccess("");
//            }
//
//            @android.webkit.JavascriptInterface
//            public void onSuccess(final String result) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent();
//                        intent.putExtra("result", result);
//                        setResult(RESULT_OK, intent);
//                        finish();
//                    }
////                }
//                });
//            }
//
//            @android.webkit.JavascriptInterface
//            public void onFailure() {
//                onFailure("");
//            }
//
//            @android.webkit.JavascriptInterface
//            public void onFailure(final String result) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent();
//                        intent.putExtra("result", result);
//                        setResult(RESULT_CANCELED, intent);
//                        finish();
//                    }
//                });
//            }
//        }, "PayU");
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Do you really want to cancel the transaction?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("result", "Transaction canceled due to back pressed!");
                setResult(RESULT_CANCELED, intent);
                PayUWebViewActivity.this.finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
