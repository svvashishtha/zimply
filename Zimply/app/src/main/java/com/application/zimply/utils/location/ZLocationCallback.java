package com.application.zimply.utils.location;

import android.location.Location;

public interface ZLocationCallback {

    void onCoordinatesIdentified(Location loc);
    void onLocationIdentified();
    void onLocationNotIdentified();
    void onDifferentCityIdentified();
    void locationNotEnabled();
    void onLocationTimedOut();
    void onNetworkError();

}