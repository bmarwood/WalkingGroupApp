package com.teal.a276.walkinggroup.activities.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.GroupMembersInfo;
import com.teal.a276.walkinggroup.activities.Monitor;
import com.teal.a276.walkinggroup.activities.MyGroups;
import com.teal.a276.walkinggroup.activities.auth.Login;
import com.teal.a276.walkinggroup.activities.message.Messages;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.GroupManager;
import com.teal.a276.walkinggroup.model.dataobjects.Message;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.dataobjects.UserLocation;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;

import static com.teal.a276.walkinggroup.activities.auth.AuthenticationActivity.sharePrefLogger;
import static com.teal.a276.walkinggroup.activities.auth.AuthenticationActivity.sharePrefUser;

/**
 * Displays Google maps interface for user to interact with
 */

public class MapsActivity extends AbstractMapActivity implements Observer {
    private final HashMap<Marker, Group> markerGroupHashMap = new HashMap<>();
    GroupManager groupManager;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private boolean walkInProgress = false;
    private User currentUser;
    private Circle circle;
    private Button endButton;
    private Button startButton;
    private Button msgButton;
    Group groupSelected = new Group();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = ModelFacade.getInstance().getCurrentUser();
        groupManager = ModelFacade.getInstance().getGroupManager();

        setGroups();
        setUsers();
        setStartEndButton();
        setMsgButton();
    }

    private void setGroups() {
        ServerProxy getGroupsProxy = ServerManager.getServerRequest();
        Call<List<Group>> getGroupsCall = getGroupsProxy.getGroups();
        ServerManager.serverRequest(getGroupsCall, this::groupsResult, this::error);
    }

    private void setUsers() {
        ServerProxy getUsersProxy = ServerManager.getServerRequest();
        Call<List<User>> getUsersCall = getUsersProxy.getUsers();
        ServerManager.serverRequest(getUsersCall, this::getUsers, this::error);
    }

    private void setStartEndButton() {
        endButton = findViewById(R.id.endBtn);
        endButton.setOnClickListener(v -> {
            map.clear();
            populateGroupsOnMap();
            placeCurrentLocationMarker(false, R.mipmap.ic_user_location);
            walkInProgress = false;
            setButtonVisibility();
        });

        startButton = findViewById(R.id.startBtn);
        startButton.setOnClickListener((View v) -> {

            List<String> groupNames = new ArrayList<>();
            List<Group> memberAndLeaderGroups = new ArrayList<>();
            memberAndLeaderGroups.addAll(currentUser.getMemberOfGroups());
            memberAndLeaderGroups.addAll(currentUser.getLeadsGroups());
            for (int i = 0; i < memberAndLeaderGroups.size(); i++) {
                groupNames.add(memberAndLeaderGroups.get(i).getGroupDescription());
            }

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.select_group_alertdialog, null);

            Spinner spinner = dialogView.findViewById(R.id.selectGroupSpinner);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, groupNames);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    groupSelected = memberAndLeaderGroups.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder.setTitle("Choose group to walk with");
            alertDialogBuilder.setPositiveButton("Start", (dialog, which) -> {
                map.clear();
                addStartEndMarkers(groupSelected);
                walkInProgress = true;
                setButtonVisibility();
            });
            alertDialogBuilder.setNegativeButton("Cancel", null);
            alertDialogBuilder.show();

            // Set map refresh for 30 seconds
            createLocationRequest(30000L, 5000L);
        });
    }

    private void setMsgButton() {
        msgButton = findViewById(R.id.msgBtn);
        msgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.message_alert_dialog, null);
                alertDialogBuilder.setView(dialogView);
                alertDialogBuilder.setTitle("Send a message");

                alertDialogBuilder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Message message = getMessage(dialogView);
                        // Message members and monitors me
                        ServerProxy proxyGroup = ServerManager.getServerRequest();
                        Call<Message> callGroup = proxyGroup.sendMessageToGroup(groupSelected.getId(), message);
                        ServerManager.serverRequest(callGroup, MapsActivity.this::sendMessage, this::error);

                        ServerProxy proxyMonitors = ServerManager.getServerRequest();
                        Call<Message> callMonitors = proxyMonitors.sendMessageToMonitors(currentUser.getId(), message);
                        ServerManager.serverRequest(callMonitors, MapsActivity.this::sendMessage, this::error);
                    }

                    private void error(String s) {
                        Log.e("MapsActivity", s);
                    }
                });
                alertDialogBuilder.setNegativeButton("Panic", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Message message = getMessage(dialogView);
                        // Message leader and monitors me
                        ServerProxy proxy = ServerManager.getServerRequest();
                        Call<Message> call = proxy.sendMessageToMonitors(currentUser.getId(), message);
                        ServerManager.serverRequest(call, MapsActivity.this::sendMessage, this::error);
                    }

                    private void error(String s) {
                        Log.e("MapsActivity", s);
                    }
                });
                alertDialogBuilder.setNeutralButton("Cancel", null);
                alertDialogBuilder.show();
            }
        });
    }

    @NonNull
    private Message getMessage(View dialogView) {
        // Extract data from UI:
        EditText msg = dialogView.findViewById(R.id.msg);
        String messageString = msg.getText().toString();
        Message message = new Message();
        message.setText(messageString);
        return message;
    }

    private <T> void sendMessage(T t) {

    }

    private void addStartEndMarkers(Group group) {
        List<Double> routeLatArray = group.getRouteLatArray();
        List<Double> routeLngArray = group.getRouteLngArray();

        if (routeLatArray.size() != routeLngArray.size()) {
            Log.e("Array lengths error ", String.format("Expected matching lengths. " +
                            "Got latArrayLength %d, lngArrayLength %d",
                    routeLatArray.size(), routeLngArray.size()));
            return;
        }

        //after dest has been implemented, there will be 2 elements in the array
        if (!routeLatArray.isEmpty()) {
            LatLng startMarkerLocation = new LatLng(routeLatArray.get(0), routeLngArray.get(0));
            LatLng endMarkerLocation = new LatLng(routeLatArray.get(routeLngArray.size() - 1), routeLngArray.get(routeLngArray.size() - 1));

            circle = map.addCircle(new CircleOptions()
                    .center(endMarkerLocation)
                    .radius(100)
                    .strokeColor(Color.GRAY)
                    .fillColor(Color.GRAY));


            MarkerOptions markerOptions = new MarkerOptions().position(startMarkerLocation);
            String titleStr = "Start";
            markerOptions.title(titleStr);
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            map.addMarker(markerOptions);

            MarkerOptions secondMarkerOptions = new MarkerOptions().position(endMarkerLocation);
            String title = "Finish";
            markerOptions.title(title);
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
            map.addMarker(secondMarkerOptions);
        }
    }

    private void populateGroupsOnMap() {
        List<Group> activeGroups = groupManager.getGroups();
        for (Group group : activeGroups) {
            addMarker(group);
        }
    }

    private void addMarker(Group group) {
        List<Double> routeLatArray = group.getRouteLatArray();
        List<Double> routeLngArray = group.getRouteLngArray();

        if (routeLatArray.size() != routeLngArray.size()) {
            Log.e("Array lengths error ", String.format("Expected matching lengths. " +
                            "Got latArrayLength %d, lngArrayLength %d",
                    routeLatArray.size(), routeLngArray.size()));
            return;
        }

        //after dest has been implemented, there will be 2 elements in the array
        if (!routeLatArray.isEmpty()) {
            LatLng markerLocation = new LatLng(routeLatArray.get(0), routeLngArray.get(0));
            placeGroupMarker(markerLocation, group);
        }
    }

    private void placeGroupMarker(LatLng markerLocation, Group group) {
        MarkerOptions markerOptions = new MarkerOptions().position(markerLocation);
        String titleStr = group.getGroupDescription();
        markerOptions.title(titleStr);
        markerOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        Marker mapMarker = map.addMarker(markerOptions);
        markerGroupHashMap.put(mapMarker, group);
    }

    private void groupsResult(List<Group> groups) {
        groupManager.setGroups(groups);
        if (googleApiClient.isConnected() && map != null) {
            populateGroupsOnMap();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.monitorItem:
                startActivity(Monitor.makeIntent(this));
                break;
            case R.id.addNewGroup:
                startActivity(EmbeddedCreateGroup.makeIntent(this));
                break;
            case R.id.myGroups:
                startActivity(MyGroups.makeIntent(this));
                break;
            case R.id.messages:
                startActivity(new Intent(this, Messages.class));
                break;
            case R.id.logoutItem:
                logout();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void logout() {
        logoutPrefs();
        ServerManager.logout();
        ModelFacade.getInstance().setCurrentUser(null);
        ModelFacade.getInstance().setAppResources(null);

        startActivity(Login.makeIntent(this));
        finish();
    }

    private void logoutPrefs() {
        SharedPreferences prefs = getSharedPreferences(sharePrefLogger, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(sharePrefUser, null);
        editor.apply();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, MapsActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {
            updateLocation = true;
            startLocationUpdates();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        groupManager.addObserver(this);
        setButtonVisibility();
    }

    @Override
    public void onPause() {
        super.onPause();
        groupManager.deleteObserver(this);
        setButtonVisibility();
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        if (walkInProgress) {
            ServerProxy proxy = ServerManager.getServerRequest();
            Call<UserLocation> call = proxy.setLastLocation(currentUser.getId(), new UserLocation(location));
            ServerManager.serverRequest(call, this::result, this::error);


            // Source: https://stackoverflow.com/questions/30170271/android-google-map-how-to-check-if-the-gps-location-is-inside-the-circle
            float[] distance = new float[2];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                    circle.getCenter().latitude, circle.getCenter().longitude, distance);

            if (distance[0] > circle.getRadius()) {
                Log.d("MapsActivity", "Outside, distance from center: " + distance[0] + " radius: " + circle.getRadius());
            } else {
                Toast.makeText(MapsActivity.this, "You have arrived at your location", Toast.LENGTH_LONG).show();
                Log.d("MapsActivity", "Inside, distance from center: " + distance[0] + " radius: " + circle.getRadius());

                // Start timer 10 minutes
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            walkInProgress = false;
                            map.clear();
                            populateGroupsOnMap();
                            placeCurrentLocationMarker(false, R.mipmap.ic_user_location);
                            setButtonVisibility();
                        });
                    }
                }, 600000);
            }
        }
    }

    private void setButtonVisibility() {
        if (walkInProgress){
            endButton.setVisibility(View.VISIBLE);
            startButton.setVisibility(View.INVISIBLE);
            msgButton.setVisibility(View.VISIBLE);

        } else {
            endButton.setVisibility(View.INVISIBLE);
            startButton.setVisibility(View.VISIBLE);
            msgButton.setVisibility(View.INVISIBLE);
        }
    }

    private void result(UserLocation location) {
        // do nothing
        Log.d("MapsActivity", "Location sent:" + location.toString());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();

        if (!walkInProgress) {
            refreshGroupMarkers();
            placeCurrentLocationMarker(false, R.mipmap.ic_user_location);
        }

        if (updateLocation) {
            startLocationUpdates();
        }
    }

    private void refreshGroupMarkers() {
        map.clear();
        populateGroupsOnMap();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Group group = markerGroupHashMap.get(marker);
        if (group == null) {
            return false;
        }

        List<User> currentUsers = new ArrayList<>(currentUser.getMonitoredByUsers().size() + 1);
        currentUsers.addAll(currentUser.getMonitorsUsers());
        currentUsers.add(currentUser);

        List<String> userNames = getUserNames();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.join_leave_alertdialog, null);
        final User selectedUser = new User();

        initializeAlertDialogSpinner(dialogView, userNames, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUser.copyUser(currentUsers.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        setupDialogInfo(group, alertDialogBuilder, dialogView);

        alertDialogBuilder.setView(dialogView);
        initializeAlertDialog(alertDialogBuilder, group, selectedUser);
        return false;
    }

    private List<String> getUserNames() {
        User user = ModelFacade.getInstance().getCurrentUser();
        List<User> currentUsers = new ArrayList<>(user.getMonitoredByUsers().size() + 1);
        currentUsers.addAll(user.getMonitorsUsers());
        currentUsers.add(user);

        List<String> userNames = new ArrayList<>();
        for (int j = 0; j < currentUsers.size(); j++) {
            userNames.add(currentUsers.get(j).getName());
        }

        return userNames;
    }

    private void initializeAlertDialogSpinner(View dialog, List<String> userNames, AdapterView.OnItemSelectedListener listener) {
        Spinner spinner = dialog.findViewById(R.id.groupsSpinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, userNames);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(listener);
    }

    private void setupDialogInfo(Group group, AlertDialog.Builder alertDialogBuilder, View dialogView) {
        String title = getString(R.string.add_remove_user, group.getGroupDescription());
        alertDialogBuilder.setTitle(title);

        // Get leaders info
        List<User> users = ModelFacade.getInstance().getUsers();
        User leader = new User();
        for (int i = 0; i < users.size(); i++) {
            if (group.getLeader().getId().equals(users.get(i).getId())) {
                leader = users.get(i);
            }
        }

        TextView leaderName = dialogView.findViewById(R.id.leadersNameTxt);
        String leadersName = getString(R.string.leaders_name, leader.getName());
        leaderName.setText(leadersName);
        TextView leaderEmail = dialogView.findViewById(R.id.leadersEmailTxt);
        String leadersEmail = getString(R.string.leaders_email, leader.getEmail());
        leaderEmail.setText(leadersEmail);

        // Setup information activity
        ImageButton infoButton = dialogView.findViewById(R.id.infoBtn);
        infoButton.setBackgroundColor(Color.WHITE);
        infoButton.setOnClickListener(v -> {
            Intent intent = GroupMembersInfo.makeIntent(MapsActivity.this, group);
            startActivity(intent);
        });
    }

    private void getUsers(List<User> userList) {
        ModelFacade.getInstance().setUsers(userList);
    }

    private void initializeAlertDialog(AlertDialog.Builder builder, Group selectedGroup, User selectedUser) {

        builder.setPositiveButton(getString(R.string.add_user), (dialog, which) -> {
            ServerProxy proxy = ServerManager.getServerRequest();
            Call<List<User>> call = proxy.addUserToGroup(selectedGroup.getId(), selectedUser);
            ServerManager.serverRequest(call, result -> addGroupMemberResult(result, selectedUser), MapsActivity.this::error);
        });

        builder.setNegativeButton(getString(R.string.remove_user), (dialog, which) -> {
            ServerProxy proxy = ServerManager.getServerRequest();
            Call<Void> call = proxy.deleteUserFromGroup(selectedGroup.getId(), selectedUser.getId());
            ServerManager.serverRequest(call, result -> removeGroupMemberResult(result, selectedUser), MapsActivity.this::error);
        });

        builder.setNeutralButton(getString(R.string.cancel), null);
        builder.show();
    }

    private void addGroupMemberResult(List<User> users, User user) {
        Toast.makeText(this, String.format("Added %s to group", user.getName()),
                Toast.LENGTH_SHORT).show();
    }

    private void removeGroupMemberResult(Void result, User user) {
        Toast.makeText(this, String.format("Removed %s from group", user.getName()),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void update(Observable observable, Object o) {
        addMarker((Group) o);
    }
}
