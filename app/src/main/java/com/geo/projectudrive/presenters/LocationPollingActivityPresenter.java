package com.geo.projectudrive.presenters;

import android.os.Handler;

import com.geo.projectudrive.R;
import com.geo.projectudrive.app.DriveUConstants;
import com.geo.projectudrive.contracts.activities.LocationPollingActivityContract;
import com.geo.projectudrive.managers.GeoLocationManager;
import com.geo.projectudrive.model.response.LocationResponseModel;

public class LocationPollingActivityPresenter implements LocationPollingActivityContract.Presenter {

    private LocationPollingActivityContract.View mView;
    private GeoLocationManager geoLocationManager;
    private boolean actionPlay;

    public LocationPollingActivityPresenter(GeoLocationManager geoLocationManager) {
        this.geoLocationManager = geoLocationManager;
    }

    @Override
    public void attach(LocationPollingActivityContract.View view) {
        mView = view;
    }

    @Override
    public void detach() {
        mView = null;
    }

    @Override
    public void getGeoLocationRequestCompleted(LocationResponseModel response, String errorMessage) {
        if (response != null) {
            mView.markLocation(response.getLongitude(), response.getLatitude());
        } else {
            mView.showErrorMessage(R.string.something_went_wrong);
        }
    }

    @Override
    public void findCustomerLocation(final boolean isConnectedToInternet) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (actionPlay) {
                    findNearestCustomer(isConnectedToInternet);
                    handler.postDelayed(this, DriveUConstants.DELAY_IN_MILLIS);
                }
            }
        }, DriveUConstants.ZERO);

    }

    @Override
    public void setActionPlay(boolean actionPlay) {
        this.actionPlay = actionPlay;
    }


    private void findNearestCustomer(boolean isConnectedToInternet) {
        if (isConnectedToInternet) {
            geoLocationManager.getGeoLocation(this);
        } else {
            mView.showErrorMessage(R.string.action_no_network);
        }
    }
}
