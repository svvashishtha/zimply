package com.application.zimplyshop.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.application.zimplyshop.R;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.utils.location.ZLocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by apoorvarora on 25/11/15.
 */
public class MapPage extends BaseActivity implements ZLocationCallback{

    private SharedPreferences prefs;
    private int width;
    private LayoutInflater inflater;
    private AppApplication zapp;

    /** Map Object */
    private boolean mapRefreshed = false;
    private GoogleMap mMap;
    private MapView mMapView;
    private float defaultMapZoomLevel = 12.5f + 1.75f;
    private boolean mMapSearchAnimating = false;
    public boolean mapSearchVisible = false;
    private boolean mapOptionsVisible = true;
    private final float MIN_MAP_ZOOM = 13.0f;

    private double lat;
    private double lon;

    private boolean destroyed = false;
    private String name;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view_layout);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        addToolbarView(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        zapp = (AppApplication) getApplication();

        width = getWindowManager().getDefaultDisplay().getWidth();

        Drawable dr = getResources().getDrawable(R.drawable.mapmarker);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        int height = bitmap.getHeight();

        findViewById(R.id.markerImg).setPadding(0, 0, 0, height);

        if (getIntent() != null && getIntent().hasExtra("lat") && getIntent().hasExtra("lon")) {
            lat =  getIntent().getDoubleExtra("lat", 0.0);
            lon =  getIntent().getDoubleExtra("lon", 0.0);
            name =  getIntent().getStringExtra("name");
        }

        inflater = LayoutInflater.from(this);

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            // Crashlytics.logException(e);
        }

        mMapView = (MapView) findViewById(R.id.search_map);
        mMapView.onCreate(savedInstanceState);


        prefs = getSharedPreferences("application_settings", 0);

        zapp.zll.forced = true;
        zapp.zll.addCallback(this);
        zapp.startLocationCheck();

        refreshMap();
    }

    private void addToolbarView(Toolbar toolbar) {
        View view = LayoutInflater.from(this).inflate(
                R.layout.common_toolbar_text_layout, null);
        TextView titleText = (TextView) view.findViewById(R.id.title_textview);
        titleText.setText(name);
        toolbar.addView(view);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void refreshMap() {
        if (mMap == null)
            setUpMapIfNeeded();
    }

    private boolean displayed = false;

    @Override
    public void onResume() {

        super.onResume();
        displayed = true;
        if (mMapView != null) {
            mMapView.onResume();

            if (mMap == null && (lat != 0.0 || lon != 0.0))
                setUpMapIfNeeded();
        }

    }

    private void setUpMapIfNeeded() {
        if (mMap == null && mMapView != null)
            mMap = mMapView.getMap();
        if (mMap != null) {

            LatLng targetCoords = null;

            if (lat != 0.0 || lon != 0.0)
                targetCoords = new LatLng(lat, lon);
            else {
                // target the current city
            }
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.setBuildingsEnabled(true);
            // mMap.getUiSettings().setZoomControlsEnabled(false);
            // mMap.getUiSettings().setTiltGesturesEnabled(false);
            // mMap.getUiSettings().setCompassEnabled(false);

            CameraPosition cameraPosition;
            if (targetCoords != null) {
                cameraPosition = new CameraPosition.Builder().target(targetCoords) // Sets
                        // the
                        // center
                        // of
                        // the
                        // map
                        // to
                        // Mountain
                        // View
                        .zoom(defaultMapZoomLevel) // Sets the zoom
                        .build(); // Creates a CameraPosition from the builder

                try {
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } catch (Exception e) {
                    MapsInitializer.initialize(MapPage.this);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }

        }

    }

    @Override
    public void onDestroy() {
        if (mMapView != null)
            mMapView.onDestroy();
        destroyed = true;
        zapp.zll.removeCallback(this);
        zapp.cache.clear();
        super.onDestroy();
    }

    @Override
    public void onCoordinatesIdentified(Location loc) {
        if (loc != null) {
            LatLng targetCoords = null;
            if (lat == 0.0 && lon == 0.0) {
                lat = loc.getLatitude();
                lon = loc.getLongitude();
                targetCoords = new LatLng(lat, lon);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(targetCoords) // Sets
                        // the
                        // center
                        // of
                        // the
                        // map
                        // to
                        // Mountain
                        // View
                        .zoom(defaultMapZoomLevel) // Sets the zoom
                        .build(); // Creates a CameraPosition from the builder

                try {
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } catch (Exception e) {
                    MapsInitializer.initialize(MapPage.this);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }
    }

    @Override
    public void onLocationIdentified() {

    }

    @Override
    public void onLocationNotIdentified() {

    }

    @Override
    public void onDifferentCityIdentified() {

    }

    @Override
    public void locationNotEnabled() {

    }

    @Override
    public void onLocationTimedOut() {

    }

    @Override
    public void onNetworkError() {

    }

    public void showDirections(View view) {
        if(!(zapp.lat == 0 && zapp.lon == 0)) {

            try {
                Uri uri = Uri.parse("geo:" + zapp.lat + "," + zapp.lon +"?q=" + lat + "," + lon + "(" + name +")");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            } catch(ActivityNotFoundException e)  {
                try {
                    Uri uri = Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lon + "&saddr=" + zapp.lat + "," + zapp.lon);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch(Exception e1) {
                }
            }

        } else {
            try {
                Uri uri = Uri.parse("geo:0,0?q=" + lat + "," + lon+ "(" + name +")");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            } catch(ActivityNotFoundException e) {
                try {
                    Uri uri = Uri.parse("http://maps.google.com/maps?daddr="+ lat + "," + lon);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch(Exception e1) {
                }
            }
        }
    }




}
