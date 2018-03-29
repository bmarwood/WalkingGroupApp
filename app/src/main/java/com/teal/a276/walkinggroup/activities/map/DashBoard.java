package com.teal.a276.walkinggroup.activities.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.Monitor;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.dataobjects.UserLocation;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverproxy.ServerResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;

/**
 * Dashboard Activity for parents to check location of their children
 * and the location of the leader.
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
        if (updateLocation) {
            startLocationUpdates();
        }
        populateUsersOnMap();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, DashBoard.class);
    }

    private void populateUsersOnMap() {
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<User>> call = proxy.getMonitors(user.getId(), 1L);
        ServerManager.serverRequest(call, this::monitorsResult, this::error);
    }

    private void monitorsResult(List<User> users) {
        for (User user : users) {
            ServerProxy proxy = ServerManager.getServerRequest();
            Call<UserLocation> call = proxy.getLastGpsLocation(user.getId());
            ServerManager.serverRequest(call, result -> DashBoard.this.placeMonitorsOnMap(result, user.getName()), this::error);

            List<Group> groups = user.getMemberOfGroups();
            for (Group group : groups) {
                ServerProxy proxyForGroup = ServerManager.getServerRequest();
                Call<User> callForGroup = proxyForGroup.getUserById(group.getLeader().getId());
                ServerManager.serverRequest(callForGroup, this::addLeadersMarker, this::error);
            }
        }
    }

    private void placeMonitorsOnMap(UserLocation location, String name) {
        if (!(location.getLat() == null)) {
            LatLng markerLocation = new LatLng(location.getLat(), location.getLng());
            MarkerOptions markerOptions = new MarkerOptions().position(markerLocation);
            String timeStamp = "";
            try {
                timeStamp = generateTimeCode(location.getTimestamp());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            markerOptions.title(name + " " + timeStamp);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            map.addMarker(markerOptions);
        }
    }

    private String generateTimeCode(String timestamp) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        Date date = format.parse(timestamp);
        Long timeSince = System.currentTimeMillis() - date.getTime();
        return String.format(Locale.getDefault(), "%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(timeSince),
                TimeUnit.MILLISECONDS.toSeconds(timeSince) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeSince))
        );
    }

    private void addLeadersMarker(User user) {
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<UserLocation> call = proxy.getLastGpsLocation(user.getId());
        ServerManager.serverRequest(call, result -> placeLeadersOnMap(result, user.getName()), this::error);
    }

    private void placeLeadersOnMap(UserLocation location, String name) {
        if (!(location.getLat() == null)) {
            LatLng markerLocation = new LatLng(location.getLat(), location.getLng());
            MarkerOptions markerOptions = new MarkerOptions().position(markerLocation);
            markerOptions.title(name);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
            map.addMarker(markerOptions);
        }
    }

}