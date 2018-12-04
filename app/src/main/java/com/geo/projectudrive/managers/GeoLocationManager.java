package com.geo.projectudrive.managers;

import android.content.Context;

import com.geo.projectudrive.contracts.activities.LocationPollingActivityContract;
import com.geo.projectudrive.model.response.LocationResponseModel;
import com.geo.projectudrive.network.APICallBack;
import com.geo.projectudrive.network.NetworkModule;
import com.geo.projectudrive.network.service.GeoLocation;

import retrofit2.Call;

public class GeoLocationManager {

    private static GeoLocationManager instance;
    private final GeoLocation geoLocationService;

    private GeoLocationManager(Context context) {
        geoLocationService = NetworkModule.getInstance(context).getApiImplementer().create(GeoLocation.class);

    }

    public static GeoLocationManager getInstance(Context context) {
        if (instance == null) {
            instance = new GeoLocationManager(context);
        }
        return instance;
    }

    public void getGeoLocation(final LocationPollingActivityContract.Presenter listener) {
        Call<LocationResponseModel> call = geoLocationService.getGeoLocation();
        call.enqueue(new APICallBack<LocationResponseModel>() {
            @Override
            protected void onSuccessResponse(Call<LocationResponseModel> call, LocationResponseModel response) {
                listener.getGeoLocationRequestCompleted(response, null);
            }
            @Override
            protected void onFailureResponse(Call<LocationResponseModel> call, String errorCode) {
                listener.getGeoLocationRequestCompleted(null, errorCode);
            }
        });
    }
}
