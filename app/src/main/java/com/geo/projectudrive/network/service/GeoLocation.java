package com.geo.projectudrive.network.service;


import com.geo.projectudrive.model.response.LocationResponseModel;
import com.geo.projectudrive.network.ApiConstants;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GeoLocation {

    @GET(ApiConstants.RANDOM_LOCATION_URL)
    Call<LocationResponseModel> getGeoLocation();
}
