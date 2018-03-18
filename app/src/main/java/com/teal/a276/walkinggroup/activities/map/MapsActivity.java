package com.teal.a276.walkinggroup.activities.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.CreateNewGroup;
import com.teal.a276.walkinggroup.activities.Monitor;
import com.teal.a276.walkinggroup.activities.auth.Login;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Displays Google maps interface for user to interact with
 */

public class MapsActivity extends AbstractMapActivity {
    private static final int REQUEST_CHECK_SETTINGS = 2;

    private static final String sharePrefLogger = "Logger";
    private static final String sharePrefUser = "userName";

    private List<Group> activeGroups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<Group>> call = proxy.getGroups();
        ServerManager.serverRequest(call, this::groupsResult, this::error);
    }

    private void populateGroupsOnMap(){

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
                LatLng marker = new LatLng(routeLatArray.get(j), routeLngArray.get(j));
                MarkerOptions markerOptions = new MarkerOptions().position(marker);
                String titleStr = group.getGroupDescription();
                markerOptions.title(titleStr);
                markerOptions.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                map.addMarker(markerOptions);
        }
    }

    private void groupsResult(List<Group> groups) {
        activeGroups = groups;
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
                //TODO: FIX THIS
                CreateNewGroup.setGroupResultCallback((o, arg) -> {
                    Group group = (Group) arg;
                    activeGroups.add(group);
                    if(map != null && googleApiClient.isConnected()) {
                        addMarker(group);
                    }
                });
                startActivity(CreateNewGroup.makeIntent(this));
                break;
            case R.id.logoutItem:
                logoutPrefs();
                startActivity(Login.makeIntent(this));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
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
        String title = marker.getTitle();

        // Find the group that matches the title
        for (int i = 0; i < activeGroups.size(); i++) {
            String groupTitle = activeGroups.get(i).getGroupDescription();
            if (title.equals(groupTitle)) {
                Group selectedGroup = activeGroups.get(i);

                User user = ModelFacade.getInstance().getCurrentUser();
                List<User> currentUsers = new ArrayList<>(user.getMonitoredByUsers().size() + 1);
                currentUsers.addAll(user.getMonitorsUsers());
                currentUsers.add(user);
                final User selectedUser = new User();

                List<String> userNames = new ArrayList<>();
                for (int j = 0; j < currentUsers.size(); j++) {
                    userNames.add(currentUsers.get(j).getName());
                }

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                final View myView = inflater.inflate(R.layout.join_leave_alertdialog, null);
                alertDialogBuilder.setView(myView);
                Spinner spinner = myView .findViewById(R.id.groupsSpinner);

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, userNames);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedUser.copyUser(currentUsers.get(i));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                alertDialogBuilder.setPositiveButton(getString(R.string.add_user), (dialog, which) -> {
                    ServerProxy proxy = ServerManager.getServerRequest();
                    Call<List<User>> call = proxy.addUserToGroup(selectedGroup.getId(), selectedUser);
                    ServerManager.serverRequest(call, result -> addGroupMemberResult(result, selectedUser), MapsActivity.this::error);
                });

                alertDialogBuilder.setNegativeButton(getString(R.string.remove_user), (dialog, which) -> {
                    ServerProxy proxy = ServerManager.getServerRequest();
                    Call<Void> call = proxy.deleteUserFromGroup(selectedGroup.getId(), selectedUser.getId());
                    ServerManager.serverRequest(call, result -> removeGroupMemberResult(result, selectedUser), MapsActivity.this::error);
                });

                alertDialogBuilder.setNeutralButton(getString(R.string.cancel), null);
                alertDialogBuilder.show();

                return true;
            }
        }
        return false;
    }

    private void addGroupMemberResult(List<User> users, User user) {
        Toast.makeText(this, String.format("Added %s to group", user.getName()),
                Toast.LENGTH_SHORT).show();
    }

    private void removeGroupMemberResult(Void result, User user) {
        Toast.makeText(this, String.format("Removed %s from group", user.getName()),
                Toast.LENGTH_SHORT).show();
    }
}
