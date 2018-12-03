package com.geo.projectudrive.contracts.activities;


import com.geo.projectudrive.contracts.BasePresenter;
import com.geo.projectudrive.contracts.BaseView;
import com.geo.projectudrive.model.responce.LocationResponceModel;
import com.google.android.gms.maps.CameraUpdate;

public interface LocationPollingActivityContract {

    interface Presenter extends BasePresenter<View> {
        void getGeoLocationRequestCompleted(LocationResponceModel response, String errorMessage);

        void findCustomerLocation(boolean isConnectedToInternet);

        void setActionPlay(boolean actionPlay);

    }

    interface View extends BaseView {

        void markLocation(double longitude, double latitude);

        boolean checkPermission();

        void moveCameraPoint(CameraUpdate point);
    }
}
