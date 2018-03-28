package com.teal.a276.walkinggroup.activities.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import com.teal.a276.walkinggroup.model.dataobjects.UserLocation;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverproxy.ServerResult;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import retrofit2.Call;

/**
 * Dashboard Activity for parents to check location of their children
 * and the location of the leader.
 *
 */

public class DashBoard extends AbstractMapActivity {

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
        createLocationRequest(0L, 0L);

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

            ServerProxy proxy = ServerManager.getServerRequest();
            Call<UserLocation> call = proxy.getLastGpsLocation(user.getId());
            ServerManager.serverRequest(call, result -> {
                DashBoard.this.addMonitorName(user.getName(), result.getTimestamp(), user.getId());
                DashBoard.this.placeMonitorsOnMap(result, user.getName());
            }, this::error);


            List<Group> groups = user.getMemberOfGroups();
            for(Group group : groups){
                ServerProxy proxyForGroup = ServerManager.getServerRequest();
                Call<User> callForGroup = proxyForGroup.getUserById(group.getLeader().getId());
                ServerManager.serverRequest(callForGroup, this::addLeadersMarker, this::error);
            }
        }
    }

    private void placeMonitorsOnMap(UserLocation location, String name){
        if(!(location.getLat() == null)) {
            LatLng markerLocation = new LatLng(location.getLat(), location.getLng());
            MarkerOptions markerOptions = new MarkerOptions().position(markerLocation);
            markerOptions.title(name);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            map.addMarker(markerOptions);
        }
    }

    private void addLeadersMarker(User user){
        addLeaderName(user);
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<UserLocation> call = proxy.getLastGpsLocation(user.getId());
        ServerManager.serverRequest(call, result -> placeLeadersOnMap(result, user.getName()), this::error);
    }

    private void placeLeadersOnMap(UserLocation location, String name) {
        if(!(location.getLat() == null)) {
            LatLng markerLocation = new LatLng(location.getLat(), location.getLng());
            MarkerOptions markerOptions = new MarkerOptions().position(markerLocation);
            markerOptions.title(name);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
            map.addMarker(markerOptions);
        }
    }

    private void addLeaderName(User user) {

//        LinearLayout scrollDash = findViewById(R.id.dashboardBottomLeaders);
//            TextView leader = new TextView(this);
//            leader.setText(user.getName());
//            leader.setId(user.getId().intValue());
//            scrollDash.addView(leader);

    }

    private void addMonitorName(String name, String timestamp, Long id) {
        TableLayout scrollDash = findViewById(R.id.monitorsTable);

        TableRow row = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(layoutParams);


        TextView leader = new TextView(this);
        leader.setText("Sally");
        //leader.setId(id.intValue());
        row.addView(leader);


        TextView gps = new TextView(this);
        leader.setText(timestamp);
        //leader.setId(user.getId().intValue()+1);
        row.addView(gps);
        scrollDash.addView(row);
    }


}