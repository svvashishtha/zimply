package com.application.zimply.utils;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimply.activities.BaseActivity;

public class ZWebView extends BaseActivity {

    private int width;

    private String mUrl;
    private String mTitle;

    boolean loadingFinished = true;
    boolean redirect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.get("title") != null)
                mTitle = bundle.getString("title");
            if (bundle.get("url") != null)
                mUrl = bundle.getString("url");
        }
        
		addToolbarView(toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		setStatusBarColor();
		setLoadingVariables();
		setFilterVariables();
		
        if(mUrl != null)
            mUrl = mUrl + "?src=mob";

        
        findViewById(R.id.loader).setVisibility(View.VISIBLE);
        findViewById(R.id.webView).setVisibility(View.GONE);

        WebView webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT <= 18) {
            webSettings.setRenderPriority(RenderPriority.HIGH);
        }

        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        CommonLib.ZLog("DBug", "webview url " + mUrl);
        webView.loadUrl(mUrl);

        //webView.postUrl(mUrl, EncodingUtils.getBytes(postData, "utf-8"));
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loadingFinished = false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                loadingFinished = false;

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished) {
                    findViewById(R.id.loader).setVisibility(View.GONE);
                    findViewById(R.id.webView).setVisibility(View.VISIBLE);

                } else {
                    redirect = false;
                }
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view,
                                                  HttpAuthHandler handler, String host, String realm) {
                //	handler.proceed("staging", "phaughoXii5ayu");
            }
        });
    }
    
    private void addToolbarView(Toolbar toolbar) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.common_toolbar_text_layout, null);
		TextView titleText = (TextView) view.findViewById(R.id.title_textview);
		titleText.setText(mTitle);
		toolbar.addView(view);
	}

    public void goBack(View view) {
        onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}