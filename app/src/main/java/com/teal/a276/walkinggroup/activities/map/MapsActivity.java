package com.teal.a276.walkinggroup.activities.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.CreateGroup;
import com.teal.a276.walkinggroup.activities.message.Messages;
import com.teal.a276.walkinggroup.activities.GroupMembersInfo;
import com.teal.a276.walkinggroup.activities.Monitor;
import com.teal.a276.walkinggroup.activities.auth.Login;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.GroupManager;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import retrofit2.Call;

import static com.teal.a276.walkinggroup.activities.auth.AuthenticationActivity.sharePrefLogger;
import static com.teal.a276.walkinggroup.activities.auth.AuthenticationActivity.sharePrefUser;

/**
 * Displays Google maps interface for user to interact with
 */

public class MapsActivity extends AbstractMapActivity implements Observer {
    private final HashMap<Marker, Group> markerGroupHashMap = new HashMap<>();
    GroupManager groupManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        groupManager = ModelFacade.getInstance().getGroupManager();

        ServerProxy getGroupsProxy = ServerManager.getServerRequest();
        Call<List<Group>> getGroupsCall = getGroupsProxy.getGroups();
        ServerManager.serverRequest(getGroupsCall, this::groupsResult, this::error);

        ServerProxy getUsersProxy = ServerManager.getServerRequest();
        Call<List<User>> getUsersCall = getUsersProxy.getUsers();
        ServerManager.serverRequest(getUsersCall, this::getUsers, this::error);
    }

    private void populateGroupsOnMap(){
        List<Group> activeGroups = groupManager.getGroups();
        for(Group group : activeGroups) {
            addMarker(group);
        }
    }

    private void addMarker(Group group) {
        List<Double> routeLatArray = group.getRouteLatArray();
        List<Double> routeLngArray = group.getRouteLngArray();

        if(routeLatArray.size() != routeLngArray.size()) {
            Log.e("Array lengths error ", String.format("Expected matching lengths. " +
                    "Got latArrayLength %d, lngArrayLength %d",
                    routeLatArray.size(), routeLngArray.size()));
            return;
        }

        for (int j = 0; j < routeLatArray.size(); j++){
                LatLng markerLocation = new LatLng(routeLatArray.get(j), routeLngArray.get(j));
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
        if(googleApiClient.isConnected() && map != null) {
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
                startActivity(CreateGroup.makeIntent(this));
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
        SharedPreferences prefs = getSharedPreferences(sharePrefLogger,MODE_PRIVATE);
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
    }

    @Override
    public void onPause() {
        super.onPause();
        groupManager.deleteObserver(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        if (null != lastLocation) {
            refreshGroupMarkers();
            placeCurrentLocationMarker(false, R.mipmap.ic_user_location);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
        refreshGroupMarkers();
        placeCurrentLocationMarker(false, R.mipmap.ic_user_location);
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

        User user = ModelFacade.getInstance().getCurrentUser();
        List<User> currentUsers = new ArrayList<>(user.getMonitoredByUsers().size() + 1);
        currentUsers.addAll(user.getMonitorsUsers());
        currentUsers.add(user);

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
            public void onNothingSelected(AdapterView<?> adapterView) {}
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
        for(int i = 0; i < users.size(); i++){
            if(group.getLeader().getId().equals(users.get(i).getId())){
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
