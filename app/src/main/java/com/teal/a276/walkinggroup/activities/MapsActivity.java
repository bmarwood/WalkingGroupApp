package com.teal.a276.walkinggroup.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
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
import com.google.android.gms.location.LocationListener;

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

public class MapsActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    public static final int MAX_RESULTS = 1;

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private boolean locationUpdateState;
    private List<Group> activeGroups = new ArrayList<Group>();
    Group selectedGroup;

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

        for(int i = 0; i < activeGroups.size(); i++) {
            Group group = activeGroups.get(i);
            List<Double> routeLatArray = group.getRouteLatArray();
            List<Double> routeLngArray = group.getRouteLngArray();

            // TODO: test that this works correctly / extract a method
            for (int j = 0; j < routeLatArray.size(); j++){
                // Even: The fist set of locations in the arrays represents the start of the route
                if ( j % 2 == 0 ) {
                    // even...
                    LatLng marker = new LatLng(routeLatArray.get(j), routeLngArray.get(j));
                    MarkerOptions markerOptions = new MarkerOptions().position(marker);
                    String titleStr = group.getGroupDescription();
                    markerOptions.title(titleStr);
                    markerOptions.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    map.addMarker(markerOptions);
                } else {
                    // Odd: The second set of locations repents the finish
                    LatLng marker = new LatLng(routeLatArray.get(j), routeLngArray.get(j));
                    MarkerOptions markerOptions = new MarkerOptions().position(marker);
                    String titleStr = group.getGroupDescription();
                    markerOptions.title(titleStr);
                    markerOptions.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    map.addMarker(markerOptions);
                }
            }
        }
    }

    private void groupsResult(List<Group> groups) {
        activeGroups = groups;
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
            case R.id.groupItem:
                startActivity(JoinGroup.makeIntent(this));
                break;
            case R.id.monitorItem:
                startActivity(Monitor.makeIntent(this));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, MapsActivity.class);
        return intent;
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
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
            }
        }
    }

    protected void placeMarkerOnMap(LatLng location) {

        map.clear();

        populateGroupsOnMap();

        // Create a MarkerOptions object and sets the user’s current location as the position for the marker
        MarkerOptions markerOptions = new MarkerOptions().position(location);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));

        // Add address to marker
        String titleStr = getAddress(location);
        markerOptions.title(titleStr);

        // Create a marker with a custom icon
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource
                (getResources(), R.mipmap.ic_user_location)));

        // Add the marker to the map
        map.addMarker(markerOptions);

    }

    private String getAddress(LatLng latLng) {
        // Creates a Geocoder object to turn a latitude and longitude coordinate into an address
        Geocoder geocoder = new Geocoder(this);
        StringBuilder addressText = new StringBuilder();
        List<Address> addresses = null;
        Address address = null;

        if (!isValidLatLng(latLng.latitude, latLng.longitude)) {
            addressText = addressText.append("Address not available");
            return addressText.toString();
        }

        try {
            // Asks the geocoder to get the address from the location passed to the method.
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, MAX_RESULTS);
            // If the response contains any address, then append it to a string and return.
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
        locationRequest = new LocationRequest();
        // Specifies the rate at which your app will like to receive updates
        locationRequest.setInterval(100000);
        // Specifies the fastest rate at which the app can handle updates
        locationRequest.setFastestInterval(50000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
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
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleApiClient.isConnected() && !locationUpdateState) {
            startLocationUpdates();
        }
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
        // Make only the group markers clickable
        String title = marker.getTitle();

        // Find the group that matches the title
        for (int i = 0; i < activeGroups.size(); i++) {
            String groupTitle = activeGroups.get(i).getGroupDescription();
            if (title.equals(groupTitle)) {

                // Get selected group
                selectedGroup = activeGroups.get(i);

                // Build the alert dialog box
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                alertDialogBuilder.setTitle("Join Group " + title + "?");

                alertDialogBuilder.setPositiveButton("Join", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User user = ModelFacade.getInstance().getCurrentUser();
                        ServerProxy proxy = ServerManager.getServerRequest();
                        Call<List<User>> call = proxy.addUserToGroup(selectedGroup.getId(), user);
                        ServerManager.serverRequest(call, MapsActivity.this::addGroupMemberResult, MapsActivity.this::error);
                    }});

                alertDialogBuilder.setNegativeButton("Leave", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User user = ModelFacade.getInstance().getCurrentUser();
                        ServerProxy proxy = ServerManager.getServerRequest();
                        Call<Void> call = proxy.deleteUserFromGroup(selectedGroup.getId(), user.getId());
                        ServerManager.serverRequest(call, MapsActivity.this::removeGroupMemberResult, MapsActivity.this::error);
                    }});
                alertDialogBuilder.show();

                return false;
            }
        }
        return false;
    }

    private void addGroupMemberResult(List<User> users) {
        // Do nothing
    }

    private void removeGroupMemberResult(Void aVoid) {
        // Do nothing
    }
}
