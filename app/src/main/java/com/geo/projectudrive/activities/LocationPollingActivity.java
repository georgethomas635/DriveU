package com.geo.projectudrive.activities;

import android.Manifest;
import android.app.Service;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.geo.projectudrive.R;
import com.geo.projectudrive.app.DriveUConstants;
import com.geo.projectudrive.contracts.activities.LocationPollingActivityContract;
import com.geo.projectudrive.managers.GeoLocationManager;
import com.geo.projectudrive.presenters.LocationPollingActivityPresenter;
import com.geo.projectudrive.utils.AppUtilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationPollingActivity extends AppCompatActivity implements
        LocationPollingActivityContract.View, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int CAMERA_PADDING = 150;
    public static final float CAMERA_ZOOM_LEVEL = 14.0f;

    @BindView(R.id.fab)
    FloatingActionButton fabPlay;
    LocationManager locationManager;
    LatLng currentLocation;
    private LocationPollingActivityPresenter presenter;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private boolean actionPlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_polling);
        ButterKnife.bind(this);
        initMapView();
        initPresenter();
    }

    private void initMapView() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
    }

    private void initPresenter() {
        GeoLocationManager geoLocationManager = GeoLocationManager.getInstance(this);
        presenter = new LocationPollingActivityPresenter(geoLocationManager);
        presenter.attach(this);
    }

    @Override
    public void showErrorMessage(int errorMessageId) {

    }

    @Override
    public void showProgressDialog(boolean showDialog) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        //Not Required
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Not Required
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (AppUtilities.checkPermission(this)) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
                Location loc = AppUtilities.getLastKnownLocation(
                        getApplicationContext(), AppUtilities.checkPermission(this));
                currentLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
                setPosition(currentLocation);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void checkLocationPermission() {
        if (AppUtilities.checkPermission(this)) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.location_permission_title))
                        .setMessage(getResources().getString(R.string.location_permission_popup_message))
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LatLng defaltPosition = new LatLng(DriveUConstants.DEFAULT_LAT, DriveUConstants.DEFAULT_LONG);
                                setPosition(defaltPosition);
                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(LocationPollingActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void markLocation(double longitude, double latitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        mGoogleMap.clear();
        if (AppUtilities.checkPermission(this)) {
            mGoogleMap.setMyLocationEnabled(true);
        }
        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
        LatLng location = new LatLng(latitude, longitude);
        adjustCameraZoom(location);
    }

    private void adjustCameraZoom(LatLng location) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(location);
        Location loc = AppUtilities.getLastKnownLocation(getApplicationContext(), AppUtilities.checkPermission(this));
        if (loc != null) {
            currentLocation = new LatLng(loc.getLatitude(), loc.getLongitude());
            builder.include(currentLocation);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, CAMERA_PADDING);
        mGoogleMap.animateCamera(cu);
    }

    private void setPosition(LatLng currentLatLong) {
        CameraUpdate point = CameraUpdateFactory.newLatLngZoom(currentLatLong, CAMERA_ZOOM_LEVEL);
        moveCameraPoint(point);
    }


    @Override
    public void moveCameraPoint(CameraUpdate point) {
        mGoogleMap.moveCamera(point);
        mGoogleMap.animateCamera(point);
    }

    @OnClick(R.id.fab)
    public void clickPlayButton() {
        actionPlay = !actionPlay;

        Animation mAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (actionPlay) {
                    fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                } else {
                    fabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow));
                }
                getGeoLocationAPI(actionPlay);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fabPlay.startAnimation(mAnimation);
    }

    private void getGeoLocationAPI(boolean actionPlay) {
        presenter.setActionPlay(actionPlay);
        presenter.findCustomerLocation(AppUtilities.isConnectedToInternet(this));
    }
}
