package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;
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
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Instantiates the googleApiClient field if it’s null
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        LatLng myPlace = new LatLng(40.73, -73.99);  // New York
//        mMap.addMarker(new MarkerOptions().position(myPlace).title("My Favorite City"));
//        // Supports zoom levels 0-20
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 12));

        // Add zoom controls /  make marker clickable
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);
    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, MapsActivity.class);
        return intent;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Initiates a background connection of the client to Google Play services.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Closes the connection to Google Play services if the client is not null and is connected
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
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
        mMap.setMyLocationEnabled(true);

        // Set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        // Determines the availability of location data on the device
        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            // Gives you the most recent location currently available
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            // Move the camera to the user’s current location
            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation
                        .getLongitude());
                // Add pin at user's location
                placeMarkerOnMap(currentLocation);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
            }
        }
    }

    protected void placeMarkerOnMap(LatLng location) {

        // Create a MarkerOptions object and sets the user’s current location as the position for the marker
        MarkerOptions markerOptions = new MarkerOptions().position(location);

//        // Add address to marker
        String titleStr = getAddress(location);
        markerOptions.title(titleStr);

//        // Create a marker with a custom icon
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource
                (getResources(), R.mipmap.ic_user_location)));

        // Add the marker to the map
        mMap.addMarker(markerOptions);
    }

    private String getAddress( LatLng latLng ) {
        // Creates a Geocoder object to turn a latitude and longitude coordinate into an address
        Geocoder geocoder = new Geocoder( this );
        String addressText = "";
        List<Address> addresses = null;
        Address address = null;
        try {
            // Asks the geocoder to get the address from the location passed to the method.
            addresses = geocoder.getFromLocation( latLng.latitude, latLng.longitude, 1 );
            // If the response contains any address, then append it to a string and return.
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressText += (i == 0)?address.getAddressLine(i):("\n" + address.getAddressLine(i));
                }
            }
        } catch (IOException e ) {
        }
        return addressText;
    }







    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
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
