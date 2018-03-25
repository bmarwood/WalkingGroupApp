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

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Dashboard Activity for parents to check location of their monitorees (Children)
 * and the location of the leader
 *
 */

public class DashBoard extends AbstractMapActivity implements Observer {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        user = ModelFacade.getInstance().getCurrentUser();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
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
        populateMonitorsOnMap();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void update(Observable o, Object arg) {

    }


    public static Intent makeIntent(Context context){
        return new Intent(context, DashBoard.class);
    }


    //Methods for Placing people the user monitors on map
    private void populateMonitorsOnMap(){

        //List<User> monitorsUsers = this.user.getMonitorsUsers();

        List<User> monitorsUsers = new ArrayList<>();
        User user1 = new User();

        LatLng vancouver = new LatLng(49.282729, -123.120738);
        user1.setLastGPSLocation(vancouver);
        user1.setName("User1");

        User sfu = new User();
        LatLng sfuBurnaby = new LatLng(49.278502, -122.916372);
        sfu.setLastGPSLocation(sfuBurnaby);
        sfu.setName("SFU");

        monitorsUsers.add(user1);
        monitorsUsers.add(sfu);


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
        marker.showInfoWindow();
    }
    private void addMarker(User user){
        LatLng markerLocation = user.getLastGPSLocation();
        placeMonitoreesOnMap(markerLocation, user);
    }


    //Methods for Placing Leader




}