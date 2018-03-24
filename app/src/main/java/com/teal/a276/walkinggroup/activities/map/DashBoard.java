package com.teal.a276.walkinggroup.activities.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;

import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class DashBoard extends AbstractMapActivity implements Observer {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        user = ModelFacade.getInstance().getCurrentUser();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
        placeCurrentLocationMarker(false, R.mipmap.ic_user_location);
        if(updateLocation) {
            startLocationUpdates();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
    /*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
    */

    /*
    @Override
    protected void onStart(){
        super.onStart();
        googleApiClient.connect();
    }
    */


    public static Intent makeIntent(Context context){
        return new Intent(context, DashBoard.class);
    }




    //Methods for Placing monitorees on map

    private void populateUsersOnMap(){
        List<User> monitorsUsers = this.user.getMonitorsUsers();
        for(User user : monitorsUsers){
            addMarker(user);
        }
    }
    private void placeMonitoreesOnMap(LatLng markerLocation, User user){
        MarkerOptions markerOptions = new MarkerOptions().position(markerLocation);
        String title = user.getName();
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        Marker marker = map.addMarker(markerOptions);
    }
    private void addMarker(User user){

        //User still does not have location fields

        //LatLng markerLocation = new LatLng(user.getLatLng);
        //placeMonitoreesOnMap(markerLocation, user);
    }
}







//Below is the mapView method, works

/*
public class DashBoard extends AbstractMapActivity implements Observer {
    //public class DashBoard extends AbstractMapActivity implements Observer {

    LatLng currentLocation;
    private int LOCATION_PERMISSION_REQUEST_CODE=1;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey");
        }

        mapView = findViewById(R.id.dashMapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
        //placeCurrentLocationMarker(false, R.mipmap.ic_user_location);
        if(updateLocation) {
            startLocationUpdates();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
    @Override
    protected void onStart(){
        super.onStart();
        googleApiClient.connect();
        mapView.onStart();
    }

    protected void setUpMap(){
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation
                        .getLongitude());
                currentLocation = locationToLatLng();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
            }
        }
    }
    public static Intent makeIntent(Context context){
        return new Intent(context, DashBoard.class);
    }
}
*/