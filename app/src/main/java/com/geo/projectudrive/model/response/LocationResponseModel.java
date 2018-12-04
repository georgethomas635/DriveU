package com.geo.projectudrive.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by george
 * on 02/12/18.
 */
public class LocationResponseModel extends BaseResponse {

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("longitude")
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
