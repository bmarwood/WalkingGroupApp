package com.teal.a276.walkinggroup.activities.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.message.Messages;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.Message;
import com.teal.a276.walkinggroup.model.dataobjects.MessageUpdater;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.dataobjects.UserLocation;
import com.teal.a276.walkinggroup.model.serverproxy.MessageRequestConstant;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;

/**
 * Dashboard Activity for parents to check location of their children
 * and the location of the leader.
 *
 */

public class DashBoard extends AbstractMapActivity implements Observer{

    private User user;
    Timer timer = new Timer();
    String messageCount = "UNREAD MSG: ";

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
        setUpMsgButton();
        setUpMap();
        placeCurrentLocationMarker(false, R.mipmap.ic_user_location);
        if(updateLocation) {
            startLocationUpdates();
        }

        //First call to populate pins before timer starts
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<User>> call = proxy.getMonitors(user.getId(), 1L);
        ServerManager.serverRequest(call, this::monitorsResult, this::error);

        populateUsersOnMap();
        updateMessageCount();
    }

    private void updateMessageCount() {
        MessageUpdater messageUpdater = new MessageUpdater(user, this::error, 30000);
        messageUpdater.addObserver(this);
    }

    private void setUpMsgButton() {
        Button button = findViewById(R.id.dashMsgBtn);

        getServerMessageCount(button);

        button.setOnClickListener(v -> {
            startActivity(new Intent(this, Messages.class));
        });
    }

    private void getServerMessageCount(Button button){
        HashMap<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(MessageRequestConstant.STATUS, MessageRequestConstant.UNREAD);
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<Message>> call = proxy.getMessages(requestParameters);

        ServerManager.serverRequest(call, ans -> {
            String unreadCount = String.valueOf(ans.size());

            //unreadCount = "1";
            messageCount = "UNREAD MSG: " + unreadCount;
            button.setText(messageCount);
        }, this::error);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, DashBoard.class);
    }

    protected void error(String error){
        super.error(error);
    }

    private void populateUsersOnMap(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    map.clear();
                    //Toast.makeText(DashBoard.this, "Timer", Toast.LENGTH_SHORT).show();
                    ServerProxy proxy = ServerManager.getServerRequest();
                    Call<List<User>> call = proxy.getMonitors(user.getId(), 1L);
                    ServerManager.serverRequest(call, DashBoard.this::monitorsResult, DashBoard.this::error);
                });
            }
        },30000, 30000);
    }

    private void monitorsResult(List<User> users) {
        for(User user: users) {

            ServerProxy proxy = ServerManager.getServerRequest();
            Call<UserLocation> call = proxy.getLastGpsLocation(user.getId());
            ServerManager.serverRequest(call, result -> placeMonitorsOnMap(result, user.getName()), this::error);

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

    @Override
    public void update(Observable o, Object arg) {

    }
}