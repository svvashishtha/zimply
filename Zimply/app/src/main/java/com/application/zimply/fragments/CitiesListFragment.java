package com.application.zimply.fragments;
/**
 * Created by Saurabh on 05-10-2015.
 */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.application.zimply.R;
import com.application.zimply.adapters.CityListAdapter;
import com.application.zimply.application.AppApplication;
import com.application.zimply.baseobjects.CategoryObject;
import com.application.zimply.extras.AppConstants;
import com.application.zimply.extras.ObjectTypes;
import com.application.zimply.managers.GetRequestListener;
import com.application.zimply.managers.GetRequestManager;
import com.application.zimply.objects.AllCities;
import com.application.zimply.preferences.AppPreferences;
import com.application.zimply.receivers.AddressResultReceiver;
import com.application.zimply.serverapis.RequestTags;
import com.application.zimply.utils.JSONUtils;
import com.application.zimply.utils.location.ZLocationCallback;

import org.json.JSONObject;

import java.util.ArrayList;

public class CitiesListFragment extends BaseFragment implements GetRequestListener,
        RequestTags, ObjectTypes, View.OnClickListener, AppConstants, ZLocationCallback {

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

    public CitiesListFragment() {
        // Required empty public constructor
    }

    public static CitiesListFragment newInstance() {
        CitiesListFragment fragment = new CitiesListFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
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
        if(AllCities.getInsance().getCities().size()==0) {
            String url = AppApplication.getInstance().getBaseUrl() + "ecommerce/city-list/";
            GetRequestManager.getInstance().makeAyncRequest(url, RequestTags.GET_CITY_LIST, OBJECT_TYPE_REGIONLIST);
        }else{
            citiesObjectList =  AllCities.getInsance().getCities();
            setAdapterData();
            showView();
            changeViewVisiblity(cityList, View.VISIBLE);
        }

        startLocationCheck();
        return view;
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
                if (getActivity() != null ) {
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
                        JSONObject city= JSONUtils.getJSONObject(jsonObject,"city");
                        JSONObject locality = JSONUtils.getJSONObject(jsonObject, "locality");
                        AppPreferences.setIsLocationSaved(getActivity(), true);
                        AppPreferences.setSavedCityServe(getActivity(), city.getBoolean("serve"));
                        AppPreferences.setSavedCity(getActivity(), JSONUtils.getStringfromJSON(city, "name"));
                        AppPreferences.setSavedCityId(getActivity(), JSONUtils.getStringfromJSON(city, "id"));
                        AppPreferences.setSavedLocality(getActivity(), JSONUtils.getStringfromJSON(locality,"name"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mListener.onCityReceivedFromServer();
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
        if (requestTag.equalsIgnoreCase(RequestTags.GET_CITY_LIST)) {
            showNetworkErrorView();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_location:
                if (mLastLocation != null) {
                    makeCityRequest();
                    if (z_ProgressDialog != null)
                        z_ProgressDialog = ProgressDialog.show(getActivity(), null, "Fetching location, Please wait...");
                    locationRequested = true;
                } else {
                    forced = true;
                    AppApplication.getInstance().startLocationCheck();
                }
        }
    }

    private void makeCityRequest() {
        lat = mLastLocation.getLatitude();
        longitude = mLastLocation.getLongitude();
        String url = AppApplication.getInstance().getBaseUrl() + GET_CITY_LL + "?lat=" + lat + "&long=" + longitude;
        GetRequestManager.getInstance().makeAyncRequest(url, GET_CITY_FROM_LL, ObjectTypes.OBJECT_TYPE_CATEGORY_OBJECT);
    }

    @Override
    public void onCoordinatesIdentified(Location loc) {
        mLastLocation = loc;
        if(forced) {
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
        if(forced) {
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
        if(z_ProgressDialog != null) {
            z_ProgressDialog.dismiss();
        }
        super.onDestroy();
    }

    public interface FragmentInteractionListener {
        void onCitySelected(CategoryObject selectedCity);

        void onCityReceivedFromServer();
    }
}
