package com.payu.sdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.payu.custombrowser.Bank;
import com.payu.custombrowser.PayUWebChromeClient;
import org.apache.http.util.EncodingUtils;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class ProcessPaymentActivity extends FragmentActivity {

    WebView webView;
    ProgressDialog mProgressDialog;
    private BroadcastReceiver mReceiver = null;
    private  ProgressDialog progressDialog;

    private int checkUnavailable=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mProgressDialog = new ProgressDialog(this);
        setContentView(R.layout.activity_process_payment);


        webView = (WebView) findViewById(R.id.webview);

        try {
            Class.forName("com.payu.custombrowser.Bank");

            Bank bank = new Bank() {
                @Override
                public void registerBroadcast(BroadcastReceiver broadcastReceiver, IntentFilter filter) {
                    mReceiver = broadcastReceiver;
                    registerReceiver(broadcastReceiver, filter);
                }

                @Override
                public void unregisterBroadcast(BroadcastReceiver broadcastReceiver) {
                    if(mReceiver != null){
                        unregisterReceiver(mReceiver);
                        mReceiver = null;
                    }
                }

                @Override
                public void onHelpUnavailable() {
                    if(checkUnavailable>1)
                        progressBarVisibility(View.GONE);
                    findViewById(R.id.parent).setVisibility(View.GONE);
                    findViewById(R.id.trans_overlay).setVisibility(View.GONE);
                }

                @Override
                public void onBankError() {
                   progressBarVisibility(View.GONE);
                    findViewById(R.id.parent).setVisibility(View.GONE);
                    findViewById(R.id.trans_overlay).setVisibility(View.GONE);
                }

                @Override
                public void onHelpAvailable() {
                    findViewById(R.id.parent).setVisibility(View.VISIBLE);
                    int countchild=((FrameLayout)findViewById(R.id.parent)).getChildCount();
                    if(countchild==1)
                    {
                        if (findViewById(R.id.parent).getVisibility()==View.VISIBLE && ((FrameLayout) findViewById(R.id.parent)).getChildAt(countchild-1).getVisibility() == View.VISIBLE) {
                            progressBarVisibility(View.GONE);
                        }
                    }

                }
            };
            Bundle args = new Bundle();
            args.putInt("webView", R.id.webview);
            args.putInt("tranLayout",R.id.trans_overlay);
            String [] list =  getIntent().getExtras().getString("postData").split("&");
            HashMap<String , String> intentMap = new HashMap<String , String>();
            for (String item : list) {
                String [] list1 =  item.split("=");
                intentMap.put(list1[0], list1[1]);
            }
            if(getIntent().getExtras().containsKey("txnid")) {
                args.putString(Bank.TXN_ID, getIntent().getStringExtra("txnid"));
            } else {
                args.putString(Bank.TXN_ID, intentMap.get("txnid"));
            }
            //args.putString(Bank.TXN_ID, "" + System.currentTimeMillis());
            if(getIntent().getExtras().containsKey("showCustom")) {
                args.putBoolean("showCustom", getIntent().getBooleanExtra("showCustom", false));
            }
            args.putBoolean("showCustom", true);
            bank.setArguments(args);
            findViewById(R.id.parent).bringToFront();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in,R.anim.face_out).add(R.id.parent, bank).commit();

            webView.setWebChromeClient(new PayUWebChromeClient(bank) {
                public void onProgressChanged(WebView view, int newProgress) {
                    if(isActivityRunning) {
                        super.onProgressChanged(view, newProgress);

                        progressBarVisibilityPayuChrome(View.VISIBLE);

                        if (newProgress == 100) {
                            checkUnavailable++;

                        }
                    }

                }

            });


        } catch (ClassNotFoundException e) {
            webView.getSettings().setSupportMultipleWindows(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.addJavascriptInterface(new Object() {
                @JavascriptInterface
                public void onSuccess() {
                    onSuccess("");
                }

                @JavascriptInterface
                public void onSuccess(final String result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.putExtra("result", result);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
//                }
                    });
                }

                @JavascriptInterface
                public void onFailure() {
                    onFailure("");
                }

                @JavascriptInterface
                public void onFailure(final String result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.putExtra("result", result);
                            setResult(RESULT_CANCELED, intent);
                            finish();
                        }
//                }
                    });
                }
            }, "PayU");

            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onCreateWindow (WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                    return false;
                }

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    progressBarVisibility(View.VISIBLE);

                    int countchild=((FrameLayout)findViewById(R.id.parent)).getChildCount();
                    if(countchild==1)
                    {
                        if (findViewById(R.id.parent).getVisibility()==View.VISIBLE && ((FrameLayout) findViewById(R.id.parent)).getChildAt(countchild-1).getVisibility() == View.VISIBLE) {
                            Log.d("pakage", "visible" + ((FrameLayout) findViewById(R.id.parent)).getChildAt(countchild-1).getVisibility() + "");
                            progressBarVisibility(View.GONE);
                        }
                    }
                    if (newProgress == 100) {
                        progressBarVisibility(View.GONE);
                    }
                }
            });
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.postUrl(Constants.PAYMENT_URL, EncodingUtils.getBytes(getIntent().getExtras().getString("postData"), "base64"));
    }

    boolean isActivityRunning;

    @Override
    protected void onResume() {
        isActivityRunning = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isActivityRunning =false;

        super.onPause();
    }

    @Override
    public void onBackPressed(){
        boolean disableBack = false;
        try {
            Bundle bundle = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            disableBack = bundle.containsKey("payu_disable_back") && bundle.getBoolean("payu_disable_back");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(!disableBack) {
            Intent intent = new Intent();
            intent.putExtra("result", "");
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
        }
    }
    public void progressBarVisibility(int visibility)
    {
        if(visibility==View.GONE || visibility==View.INVISIBLE ) {
            if(progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
        else if (progressDialog==null || !progressDialog.isShowing())
        {
            progressDialog=showProgress(this);
        }
    }

    public void progressBarVisibilityPayuChrome(int visibility)
    {
        if(visibility==View.GONE || visibility==View.INVISIBLE ) {
            if(progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
        else if (progressDialog==null)
        {
            progressDialog=showProgress(this);
        }
    }

    public ProgressDialog showProgress(Context context) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        final Drawable[] drawables = {getResources().getDrawable(R.drawable.nopoint),
                getResources().getDrawable(R.drawable.onepoint),
                getResources().getDrawable(R.drawable.twopoint),
                getResources().getDrawable(R.drawable.threepoint)
        };

        View layout = mInflater.inflate(R.layout.prog_dialog, null);
        final ImageView imageView; imageView = (ImageView) layout.findViewById(R.id.imageView);
        ProgressDialog progDialog = new ProgressDialog(context, R.style.ProgressDialog);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = -1;
            @Override
            synchronized public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        i++;
                        if (i >= drawables.length) {
                            i = 0;
                        }
                        imageView.setImageBitmap(null);
                        imageView.destroyDrawingCache();
                        imageView.refreshDrawableState();
                        imageView.setImageDrawable(drawables[i]);
                    }
                });

            }
        }, 0, 500);

        progDialog.show();
        progDialog.setContentView(layout);
        progDialog.setCancelable(true);
        progDialog.setCanceledOnTouchOutside(false);
        return progDialog;
    }


}
