package com.teal.a276.walkinggroup.activities;

import android.content.Context;
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
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;

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

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 2;


    // TODO: Populate markers array from existing groups
//    ArrayList<MarkerData> markersArray = new ArrayList<MarkerData>();

//    public void populateMarkerArrayList (ArrayList<MarkerData> markersArray) {
//
//    }


    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private boolean locationUpdateState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Instantiates the googleApiClient field if it’s null
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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Add zoom controls /  make marker clickable
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMarkerClickListener(this);
    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, MapsActivity.class);
        return intent;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Initiates a background connection of the client to Google Play services.
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Closes the connection to Google Play services if the client is not null and is connected
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

        // Enables the my-location layer which draws a light blue dot on the user’s location
        map.setMyLocationEnabled(true);

        // Set map type
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        // Determines the availability of location data on the device
        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            // Gives you the most recent location currently available
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            // Move the camera to the user’s current location
            if (lastLocation != null) {
                LatLng currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation
                        .getLongitude());
                // Add pin at user's location
                placeMarkerOnMap(currentLocation);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
            }
        }
    }

    protected void placeMarkerOnMap(LatLng location) {

        map.clear();

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
        try {
            // Asks the geocoder to get the address from the location passed to the method.
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            // If the response contains any address, then append it to a string and return.
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses.get(0);

                // TODO: find out if this is required with updated location
//                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                    addressText += (i == 0)?address.getAddressLine(i):("\n" + address.getAddressLine(i));
//                }

                addressText = addressText.append(address.getAddressLine(0));
            }
        } catch (IOException e) {
        }
        return addressText.toString();
    }

    // Request permission and check for location updates
    protected void startLocationUpdates() {
        // If permission is not granted request it now and return
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        //If there is permission, request for location updates.
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,
                this);
    }

    // Handles any changes to be made based on the current state of the user’s location settings
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        // Specifies the rate at which your app will like to receive updates
        locationRequest.setInterval(10000);
        // Specifies the fastest rate at which the app can handle updates
        locationRequest.setFastestInterval(5000);
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
                    // A SUCCESS status means all is well and you can go ahead and initiate a location request
                    case LocationSettingsStatusCodes.SUCCESS:
                        locationUpdateState = true;
                        startLocationUpdates();
                        break;
                    // A RESOLUTION_REQUIRED status means the location settings have some issues which can be fixed
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    // A SETTINGS_CHANGE_UNAVAILABLE status means the location settings have some issues that you can’t fix
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    // Start the update request if it has a RESULT_OK result for a REQUEST_CHECK_SETTINGS request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                locationUpdateState = true;
                startLocationUpdates();
            }
        }
    }

    // Stop location update request on pause
    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    // Restart the location update request
    @Override
    public void onResume() {
        super.onResume();
        if (googleApiClient.isConnected() && !locationUpdateState) {
            startLocationUpdates();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Update lastLocation with the new location and update the map
        lastLocation = location;
        if (null != lastLocation) {
            placeMarkerOnMap(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
        // Start location updates if user's location settings are turned on.
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
        return false;
    }
}
