package com.teal.a276.walkinggroup.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Displays Google maps interface for user to interact with
 */

public class MapsActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    public static final int MAX_RESULTS = 1;

    private final int ZOOM_LEVEL = 10;

    private static final String sharePrefLogger = "Logger";
    private static final String sharePrefUser = "userName";

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private boolean locationUpdateState;
    private List<Group> activeGroups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMarkerClickListener(this);
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
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                LatLng currentLocation = locationToLatLng();
                placeMarkerOnMap(currentLocation);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ZOOM_LEVEL));
            }
        }
    }

    protected void placeMarkerOnMap(LatLng location) {

        map.clear();
        populateGroupsOnMap();

        MarkerOptions markerOptions = new MarkerOptions().position(location);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM_LEVEL));

        String titleStr = getAddress(location);
        markerOptions.title(titleStr);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource
                (getResources(), R.mipmap.ic_user_location)));
        map.addMarker(markerOptions);
    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        StringBuilder addressText = new StringBuilder();
        List<Address> addresses;
        Address address;

        if (!isValidLatLng(latLng.latitude, latLng.longitude)) {
            addressText = addressText.append("Address not available");
            return addressText.toString();
        }

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, MAX_RESULTS);
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses.get(0);
                addressText = addressText.append(address.getAddressLine(0));
            }
        } catch (IOException e) {
            Log.e("WalkingGroupApp", " Error: Geocoder was unable to retrieve current location");
        }

        return addressText.toString();
    }

    // Source: https://stackoverflow.com/questions/7356373/android-how-to-validate-locations-latitude-and-longtitude-values
    public boolean isValidLatLng(double lat, double lng) {
        if (lat < -90 || lat > 90) {
            return false;
        } else if (lng < -180 || lng > 180) {
            return false;
        }
        return true;
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,
                this);
    }

    // Handles any changes to be made based on the current state of the user’s location settings
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();;
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                        builder.build());

        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    locationUpdateState = true;
                    startLocationUpdates();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {
            locationUpdateState = true;
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected() && !locationUpdateState) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (null != lastLocation) {
            placeMarkerOnMap(locationToLatLng());
        }
    }

    @NonNull
    private LatLng locationToLatLng() {
        return new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
        if (locationUpdateState) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
                    ServerManager.serverRequest(call, MapsActivity.this::addGroupMemberResult, MapsActivity.this::error);
                });

                alertDialogBuilder.setNegativeButton(getString(R.string.remove_user), (dialog, which) -> {
                    ServerProxy proxy = ServerManager.getServerRequest();
                    Call<Void> call = proxy.deleteUserFromGroup(selectedGroup.getId(), selectedUser.getId());
                    ServerManager.serverRequest(call, MapsActivity.this::removeGroupMemberResult, MapsActivity.this::error);
                });

                alertDialogBuilder.setNeutralButton(getString(R.string.cancel), null);
                alertDialogBuilder.show();

                return false;
            }
        }
        return false;
    }

    private void addGroupMemberResult(List<User> users) {
        Toast.makeText(this, "User added to group", Toast.LENGTH_SHORT).show();
    }

    private void removeGroupMemberResult(Void aVoid) {
        Toast.makeText(this, "User removed to group", Toast.LENGTH_SHORT).show();
    }
}
