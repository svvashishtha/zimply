package com.application.zimplyshop.fragments;
/**
 * Created by Saurabh on 05-10-2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.zimplyshop.R;
import com.application.zimplyshop.adapters.CityListAdapter;
import com.application.zimplyshop.application.AppApplication;
import com.application.zimplyshop.baseobjects.CategoryObject;
import com.application.zimplyshop.extras.AppConstants;
import com.application.zimplyshop.extras.ObjectTypes;
import com.application.zimplyshop.managers.GetRequestListener;
import com.application.zimplyshop.managers.GetRequestManager;
import com.application.zimplyshop.objects.AllCities;
import com.application.zimplyshop.preferences.AppPreferences;
import com.application.zimplyshop.receivers.AddressResultReceiver;
import com.application.zimplyshop.serverapis.RequestTags;
import com.application.zimplyshop.utils.CommonLib;
import com.application.zimplyshop.utils.JSONUtils;
import com.application.zimplyshop.utils.location.ZLocationCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONObject;

import java.util.ArrayList;

public class CitiesListFragment extends BaseFragment implements GetRequestListener,
        RequestTags, ObjectTypes, View.OnClickListener, AppConstants, ZLocationCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    RecyclerView cityList;
    LinearLayoutManager linearLayoutManager;
    ArrayList<CategoryObject> citiesObjectList;
    FragmentInteractionListener mListener;
    TextView useMyLocation;
    EditText locationText;
    AddressResultReceiver mResultReceiver;
    ProgressDialog z_ProgressDialog;
    double lat = 0, longitude = 0;
    AlertDialog.Builder dialog;
    CityListAdapter adapter;
    private String mAddressOutput;
    private boolean cityFound = false, locationRequested = false;
    private Location mLastLocation;
    private boolean isDestroyed = false;
    private boolean forced = false;
    double requestTime = 0;
    private boolean locationButtonClicked;


    public CitiesListFragment() {
        // Required empty public constructor
    }

    public static CitiesListFragment newInstance(Bundle bundle) {
        CitiesListFragment fragment = new CitiesListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setFragmentInteractionListener(FragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        locationButtonClicked = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startLocationCheck1();
            }
        }, 2000);


    }


    @Override
    public void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cities_list, container, false);
        cityList = (RecyclerView) view.findViewById(R.id.city_list);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        cityList.setLayoutManager(linearLayoutManager);
        useMyLocation = (TextView) view.findViewById(R.id.my_location);
        useMyLocation.setOnClickListener(this);
        setLoadingVariables();
        GetRequestManager.getInstance().addCallbacks(this);
        if (AllCities.getInsance().getCities().size() == 0) {
            String url = AppApplication.getInstance().getBaseUrl() + "ecommerce/city-list/";
            GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.GET_CITY_LIST, OBJECT_TYPE_REGIONLIST);
        } else {
            citiesObjectList = AllCities.getInsance().getCities();
            setAdapterData();
            showView();
            changeViewVisiblity(cityList, View.VISIBLE);
        }
        if (getArguments().getBoolean("fetch_location")) {
            forced = true;
        }

        // startLocationCheck();
        // startLocationCheck1();
        return view;
    }

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    public static final int REQUEST_LOCATION = 199;

    public void startLocationCheck1() {
        PackageManager pm = AppApplication.getInstance().getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_LOCATION)) {
            if (getActivity() != null && mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();


            }
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
            if (mGoogleApiClient != null)
                mGoogleApiClient.connect();
        }
        requestTime = System.currentTimeMillis();
    }


    public void startLocationCheck() {
        AppApplication.getInstance().zll.forced = true;
        AppApplication.getInstance().zll.addCallback(this);
        AppApplication.getInstance().startLocationCheck();
    }

    public CityListAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onRequestStarted(String requestTag) {
        if (!isDestroyed) {
            if (requestTag.equalsIgnoreCase(RequestTags.GET_CITY_LIST)) {
                showLoadingView();
                changeViewVisiblity(cityList, View.GONE);
            } else if (requestTag.equalsIgnoreCase(RequestTags.GET_CITY_FROM_LL)) {
                if (getActivity() != null) {
                    z_ProgressDialog = ProgressDialog.show(getActivity(), null, "Fetching location, Please wait...");
                }
            }
        }
    }

    @Override
    public void onRequestCompleted(String requestTag, Object obj) {
        if (!isDestroyed) {
            if (requestTag.equalsIgnoreCase(RequestTags.GET_CITY_LIST)) {
                citiesObjectList = ((ArrayList<CategoryObject>) obj);
                setAdapterData();
                showView();
                changeViewVisiblity(cityList, View.VISIBLE);
            } else if (requestTag.equalsIgnoreCase(GET_CITY_FROM_LL)) {
                JSONObject jsonObject = (JSONObject) obj;
                if (jsonObject != null) {
                    try {
                        if (z_ProgressDialog != null) {
                            z_ProgressDialog.dismiss();
                        }
                        JSONObject city = JSONUtils.getJSONObject(jsonObject, "city");
                        JSONObject locality = JSONUtils.getJSONObject(jsonObject, "locality");
                        if (city.getBoolean("serve")) {
                            AppPreferences.setIsLocationSaved(getActivity(), true);
                            AppPreferences.setSavedCityServe(getActivity(), city.getBoolean("serve"));
                            AppPreferences.setSavedCity(getActivity(), JSONUtils.getStringfromJSON(city, "name"));
                            AppPreferences.setSavedCityId(getActivity(), JSONUtils.getStringfromJSON(city, "id"));
                            AppPreferences.setSavedLocality(getActivity(), JSONUtils.getStringfromJSON(locality, "name"));
                            mListener.onCityReceivedFromServer(requestTime);
                        } else {
                            final NoDeliveryDialog dialog = NoDeliveryDialog
                                    .newInstance(null);
                            dialog.setStyle(DialogFragment.STYLE_NORMAL,
                                    R.style.HJCustomDialogTheme);

                            dialog.show(getActivity().getSupportFragmentManager(), "BannerTag");
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
        }
    }

    private void setAdapterData() {
        adapter = new CityListAdapter(getActivity(), citiesObjectList);
        adapter.setSelectedId(AppPreferences.getSavedCityId(getActivity()));
        adapter.setOnItemClickListener(new CityListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(CategoryObject obj) {
                adapter.setSelectedId(obj.getId());

                mListener.onCitySelected(obj);
            }
        });
        cityList.setAdapter(adapter);
    }

    @Override
    public void onRequestFailed(String requestTag, Object obj) {
        if (!isDestroyed && requestTag.equalsIgnoreCase(RequestTags.GET_CITY_LIST)) {

            showNetworkErrorView();
        } else if (!isDestroyed && requestTag.equalsIgnoreCase(RequestTags.GET_CITY_FROM_LL)) {
            if (z_ProgressDialog != null)
                z_ProgressDialog.dismiss();
            showToast("Please check your network connection");

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_location:
                if (!locationButtonClicked) {
                    locationButtonClicked = true;
                    if (mLastLocation != null) {
                        makeCityRequest1();
                    /*if (z_ProgressDialog != null)
                        z_ProgressDialog = ProgressDialog.show(getActivity(), null, "Fetching location, Please wait...");*/
                        locationRequested = true;
                    } else {
                        forced = true;
                        // AppApplication.getInstance().startLocationCheck();
                        startLocationCheck1();
                    }
                }
        }
    }

    private void makeCityRequest() {

        lat = mLastLocation.getLatitude();

        longitude = mLastLocation.getLongitude();
        if (CommonLib.isNetworkAvailable(getActivity())) {
            String url = AppApplication.getInstance().getBaseUrl() + GET_CITY_LL + "?lat=" + lat + "&long=" + longitude;
            GetRequestManager.getInstance().makeAyncRequest(url, GET_CITY_FROM_LL, ObjectTypes.OBJECT_TYPE_CATEGORY_OBJECT);
        } else {
            showToast("Please check your internet connection");
        }
    }

    public Location getLocation() {
        z_ProgressDialog = ProgressDialog.show(getActivity(), null, "Finding Location..");
        if (LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient) == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getLocation();
        } else {
            return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    private void makeCityRequest1() {
       /* mLastLocation = getLocation();
        if(z_ProgressDialog!=null){
            z_ProgressDialog.dismiss();
        }*/
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            if (CommonLib.isNetworkAvailable(getActivity())) {
                String url = AppApplication.getInstance().getBaseUrl() + GET_CITY_LL + "?lat=" + lat + "&long=" + longitude;
                GetRequestManager.getInstance().makeAyncRequest(url, GET_CITY_FROM_LL, ObjectTypes.OBJECT_TYPE_CATEGORY_OBJECT);
            } else {
                showToast("Please check your internet connection");
            }
        } else {
            showToast("Unable to fetch location");
        }
    }

    @Override
    public void onCoordinatesIdentified(Location loc) {
        mLastLocation = loc;
        if (forced) {
            makeCityRequest();
            locationRequested = true;
            forced = false;
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
        if (forced) {
            dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onLocationTimedOut() {

    }

    @Override
    public void onNetworkError() {

    }

    @Override
    public void onDestroy() {
        AppApplication.getInstance().zll.removeCallback(this);

        isDestroyed = true;
        if (z_ProgressDialog != null) {
            z_ProgressDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkLocationConnection();
    }

    public void checkLocationConnection() {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        if (forced) {
                            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                            makeCityRequest1();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            if (forced && getActivity() != null) {
                                status.startResolutionForResult(
                                        getActivity(),
                                        REQUEST_LOCATION);
                            }
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        requestTime = System.currentTimeMillis();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made

                        Toast.makeText(getActivity(), "Location enabled by user!", Toast.LENGTH_LONG).show();
                        /*if (mGoogleApiClient == null){
                            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                                    .addConnectionCallbacks(this)
                                    .addOnConnectionFailedListener(this)
                                    .addApi(LocationServices.API)
                                    .build();
                        }*/
                        // startLocationCheck1();
                        // checkLocationConnection();
                        forced = true;
                        if (mGoogleApiClient.isConnected()) {
                            showToast("Google Api Client connected");
                        }
                        //mGoogleApiClient.connect();
                        //  onConnected(null);
                        //   makeCityRequest1();
                       /* mGoogleApiClient.disconnect();
                        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(LocationServices.API)
                                .build();
                        mGoogleApiClient.connect();*/

                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        forced = false;
                        Toast.makeText(getActivity(), "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();

                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }


    public interface FragmentInteractionListener {
        void onCitySelected(CategoryObject selectedCity);

        void onCityReceivedFromServer(double requestTime);
    }
}
