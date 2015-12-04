package com.application.zimplyshop.activities;
/**
 * Created by Saurabh on 05-10-2015.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.fragments.CitiesListFragment;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.utils.CommonLib;

public class SelectCity extends BaseActivity {
    CitiesListFragment citiesListFragment;

    int currentFragmentCount;
    TextView toolbartext;
    EditText searchEditText;
    boolean isEdittextActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        addToolbarView();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle  = new Bundle();

        try {
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().containsKey("show_back")) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                }

                bundle.putBoolean("fetch_location",getIntent().getExtras().getBoolean("fetch_location"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        citiesListFragment = CitiesListFragment.newInstance(bundle);
        citiesListFragment.setFragmentInteractionListener(new CitiesListFragment.FragmentInteractionListener() {

            @Override
            public void onCitySelected(CategoryObject selectedCity) {
                //start new fragment here
                AppPreferences.setSavedCityId(SelectCity.this, selectedCity.getId());
                AppPreferences.setSavedCity(SelectCity.this, selectedCity.getName());
                AppPreferences.setSavedCityServe(SelectCity.this, selectedCity.isServe());
                AppPreferences.setIsLocationSaved(SelectCity.this, true);
                CommonLib.hideKeyBoard(SelectCity.this, searchEditText);
                startHomeActivity();
            }

            @Override
            public void onCityReceivedFromServer() {
                startHomeActivity();
            }
        });
        currentFragmentCount = 1;
        getSupportFragmentManager().beginTransaction().add(R.id.container, citiesListFragment).commit();
        setToolbartext("Search for City");
    }

    public void addToolbarView() {
        View view = LayoutInflater.from(this).inflate(R.layout.search_edittext_toolbar_layout, toolbar, false);
        toolbartext = (TextView) view.findViewById(R.id.title_textview);
        searchEditText = (EditText) view.findViewById(R.id.search_edittext);
        final ImageView searchImg = (ImageView) view.findViewById(R.id.search_icon);

        view.findViewById(R.id.title_textview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchImg.performClick();
            }
        });
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEdittextActive) {
                    isEdittextActive = true;
                    searchEditText.setVisibility(View.VISIBLE);
                    boolean hasFocus = searchEditText.requestFocus();
                    if (hasFocus) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                    toolbartext.setVisibility(View.GONE);
                    searchImg.setImageBitmap(CommonLib.getBitmap(SelectCity.this, R.drawable.ic_cross, getResources().getDimensionPixelSize(R.dimen.z_item_height_48), getResources().getDimensionPixelSize(R.dimen.z_item_height_48)));
                } else {
                    CommonLib.hideKeyBoard(SelectCity.this, searchEditText);
                    searchEditText.setVisibility(View.GONE);
                    toolbartext.setVisibility(View.VISIBLE);
                    isEdittextActive = false;
                    searchImg.setImageBitmap(CommonLib.getBitmap(SelectCity.this, R.drawable.ic_action_action_search_menu
                            , getResources().getDimensionPixelSize(R.dimen.z_item_height_48), getResources().getDimensionPixelSize(R.dimen.z_item_height_48)));
                    searchEditText.setText("");
                }

            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence arg0, int start, int before, int count) {
                if (citiesListFragment != null && citiesListFragment.getAdapter() != null) {
                    citiesListFragment.getAdapter().getFilter().filter(arg0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        toolbar.addView(view);
    }

    public void setToolbartext(String toolbartext) {
        this.toolbartext.setText(toolbartext);
    }

    public void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        this.finish();
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (currentFragmentCount == 2) {
            currentFragmentCount = 1;
            setToolbartext("Select City");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            currentFragmentCount = 0;
        }
        super.onBackPressed();
    }


 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_select_city, menu);
        return true;
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (citiesListFragment!=null && requestCode == CitiesListFragment.REQUEST_LOCATION){
                citiesListFragment.onActivityResult(requestCode, resultCode, data);
            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
}
