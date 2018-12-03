package com.geo.projectudrive.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class AppUtilities {

    /**
     * This method checks Internet connectivity and return true if it is.
     *
     * @param context: Context
     * @return true if connected to internet
     */
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }

    public static Location getLastKnownLocation(Context context, boolean isPermissionGranded) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        assert locationManager != null;
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        if (isPermissionGranded) {
            for (String provider : providers) {
                @SuppressLint("MissingPermission") Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }
}
