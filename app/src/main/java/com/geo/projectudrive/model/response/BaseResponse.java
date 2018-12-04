package com.geo.projectudrive.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.geo.projectudrive.app.DriveUConstants;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse implements Serializable {

    @JsonProperty("status")
    private String status;

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return status != null && status.equalsIgnoreCase(DriveUConstants.STATUS_SUCCESS);
    }

}
