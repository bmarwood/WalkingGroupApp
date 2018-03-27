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
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import retrofit2.Call;

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
        populateUsersOnMap();
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

    private void populateUsersOnMap(){
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<User>> call = proxy.getMonitors(user.getId(), 1L);
        ServerManager.serverRequest(call, this::monitorsResult, this::error);
    }

    private void monitorsResult(List<User> users) {
        for(User user: users) {

            LatLng vancouver = new LatLng(52.282729, -123.120738);
            user.setLastGPSLocation(vancouver);
            addUsersMarker(user);
            List<Group> groups = user.getMemberOfGroups();
            for(Group group : groups){
                ServerProxy proxy = ServerManager.getServerRequest();
                Call<User> call = proxy.getUserById(group.getLeader().getId());
                ServerManager.serverRequest(call, this::addLeadersMarker, this::error);
            }
        }
    }

    private void addUsersMarker(User user){
        LatLng markerLocation = user.getLastGPSLocation();
        placeMonitorsOnMap(markerLocation, user);
    }

    private void placeMonitorsOnMap(LatLng markerLocation, User user){
        MarkerOptions markerOptions = new MarkerOptions().position(markerLocation);
        String title = user.getName();
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        map.addMarker(markerOptions);
    }

    private void addLeadersMarker(User user){

        //TODO: delete later
        LatLng vancouver = new LatLng(49.282729, -123.120738);
        user.setLastGPSLocation(vancouver);

        LatLng markerLocation = user.getLastGPSLocation();
        placeLeadersOnMap(markerLocation, user);
    }

    private void placeLeadersOnMap(LatLng markerLocation, User user){
        MarkerOptions markerOptions = new MarkerOptions().position(markerLocation);
        String title = user.getName();
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        map.addMarker(markerOptions);
    }
}