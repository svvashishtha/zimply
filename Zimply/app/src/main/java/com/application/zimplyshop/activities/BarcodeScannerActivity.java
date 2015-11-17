package com.application.zimplyshop.activities;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.application.zimply.R;
import com.application.zimplyshop.utils.JSONUtils;
import com.application.zimplyshop.widgets.CustomTextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Umesh Lohani on 11/9/2015.
 */
public class BarcodeScannerActivity  extends BaseActivity implements ZXingScannerView.ResultHandler{


    ZXingScannerView scannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_scanner_layout);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        scannerView = (ZXingScannerView)findViewById(R.id.barcode_scanner_view);
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.QR_CODE);
        scannerView.setFormats(formats);
        scannerView.setAutoFocus(true);
        scannerView.setSoundEffectsEnabled(true);


    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(
                R.layout.common_toolbar_text_layout, null);
        CustomTextView titleText = (CustomTextView) view.findViewById(R.id.title_textview);
        titleText.setText("Scan Product");
        toolbar.addView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
        //  new Handler().postDelayed(r,1000);

    }
    Runnable r = new Runnable() {
        @Override
        public void run() {
            scannerView.startCamera();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleResult(Result result) {
        Intent intent = new Intent();
        if(result!=null && result.getText()!=null && result.getText().length()>0){
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

            try {
                if (JSONUtils.getJSONObject(result.getText()).has("id") && JSONUtils.getJSONObject(result.getText()).has("slug")) {
                    intent.putExtra("SCAN_RESULT", result.getText());
                    setResult(RESULT_OK, intent);
                    this.finish();
                } else {
                    setResult(RESULT_CANCELED, intent);
                    this.finish();
                }
            }catch(Exception e){
                setResult(RESULT_CANCELED, intent);
                this.finish();
            }
        }else{
            setResult(RESULT_CANCELED, intent);
            this.finish();
        }
    }


}
