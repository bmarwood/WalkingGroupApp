package com.teal.a276.walkinggroup.activities.map;

import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.BaseActivity;

import java.io.IOException;
import java.util.List;

/**
 * Abstract class to store the shared logic between all map activities
 */

@SuppressWarnings("deprecation")
public abstract class AbstractMapActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {

    private static final int MAX_RESULTS = 1;
    private final int ZOOM_LEVEL = 10;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    final int REQUEST_CHECK_SETTINGS = 2;

    GoogleMap map;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    private LocationRequest locationRequest;
    boolean updateLocation;

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

        createLocationRequest(0L,0L);
    }

    // Handles any changes to be made based on the current state of the user’s location settings
    protected void createLocationRequest(Long interval, Long fastestInterval) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(fastestInterval);
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
                    startLocationUpdates();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        Log.getStackTraceString(e);
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        });
    }

    void startLocationUpdates() {
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

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected() && !updateLocation) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMarkerClickListener(this);
    }


    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Map Connection Error", connectionResult.getErrorMessage() +
                "\nError code: " + connectionResult.getErrorCode());
        this.error(connectionResult.getErrorMessage());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private boolean checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    @SuppressLint("MissingPermission")
    protected void setUpMap() {
        if (!checkAndRequestPermissions()) {
            return;
        }

        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @SuppressLint("MissingPermission")
    void placeCurrentLocationMarker(boolean draggable, Integer iconId) {
        if(!checkAndRequestPermissions()) {
            return;
        }

        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                LatLng currentLocation = locationToLatLng(lastLocation);
                placeMarkerOnMap(currentLocation, draggable, iconId);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, ZOOM_LEVEL));
            }
        }
    }

    private void placeMarkerOnMap(LatLng markerLocation, boolean draggable, Integer iconId) {
        String title = getAddress(markerLocation);
        MarkerOptions marker = new MarkerOptions().position(markerLocation)
                                            .draggable(draggable).title(title);

        if(iconId != null) {
            marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource
                    (getResources(), iconId)));
        }

        map.addMarker(marker);
    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        StringBuilder addressText = new StringBuilder();
        List<Address> addresses;
        Address address;

        if (!isValidLatLng(latLng.latitude, latLng.longitude)) {
            addressText = addressText.append(getString(R.string.address_error));
            return addressText.toString();
        }

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, MAX_RESULTS);
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses.get(0);
                addressText = addressText.append(address.getAddressLine(0));
            }
        } catch (IOException e) {
            Log.getStackTraceString(e);
        }

        return addressText.toString();
    }

    // Source: https://stackoverflow.com/questions/7356373/android-how-to-validate-locations-latitude-and-longtitude-values
    private boolean isValidLatLng(double lat, double lng) {
        return lat < -90 || lat > 90 || lng < -180 || lng > 180;
    }

    @NonNull
    protected LatLng locationToLatLng(Location location) {
        if (location == null) {
            return new LatLng(0, 0);
        }

        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
