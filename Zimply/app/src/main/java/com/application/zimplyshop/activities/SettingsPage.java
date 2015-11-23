package com.application.zimplyshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.preferences.AppPreferences;

/**
 * Created by apoorvarora on 23/11/15.
 */
public class SettingsPage extends BaseActivity {

    private boolean destroyed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        findViewById(R.id.change_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppPreferences.isUserLogIn(SettingsPage.this)) {
                    Intent intent = new Intent(SettingsPage.this, CheckPhoneVerificationActivity.class);
                    startActivity(intent);
                } else {
                    showToast("Please Login to continue");
                    Intent intent = new Intent(SettingsPage.this, BaseLoginSignupActivity.class);
                    intent.putExtra("inside", true);
                    startActivity(intent);
                }
            }
        });
    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(
                R.layout.common_toolbar_text_layout, null);
        TextView titleText = (TextView) view.findViewById(R.id.title_textview);
        titleText.setText("Settings");
        toolbar.addView(view);
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        super.onDestroy();
    }

}
