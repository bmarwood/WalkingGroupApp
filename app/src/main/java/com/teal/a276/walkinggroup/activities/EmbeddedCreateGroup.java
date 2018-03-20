package com.teal.a276.walkinggroup.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.User;

import java.util.Observer;

public class EmbeddedCreateGroup extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private LatLng currentLocation;
    private static Observer newGroupObserver;
    private Marker meetingMarker;
    private Marker destinationMarker;
    double meetingLat = 0;
    double meetingLng = 0;
    double destLat = 0;
    double destLng = 0;
    boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_embedded_create_group);


        setupCreateButton();
        setupSelectDestinationButton();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey");
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        createLocationRequest();
    }

    private void setupCreateButton() {

        Button btn = findViewById(R.id.embeddedCreateButton);
        btn.setOnClickListener(v ->{
            EditText nameVal = findViewById(R.id.embeddedNameEdit);
            String nameValStr = nameVal.getText().toString();

            EditText leadersEmailVal = findViewById(R.id.embeddedEmailEdit);
            String leadersEmailStr = leadersEmailVal.getText().toString();

            if(nameValStr.isEmpty()){
                nameVal.setError(getString(R.string.empty_group_name));
                return;
            }
            if(!User.validateEmail(leadersEmailStr)){
                leadersEmailVal.setError(getString(R.string.invalid_email));
                return;
            }
            if((meetingLat==0) && (meetingLng==0)){
                Toast.makeText(
                        EmbeddedCreateGroup.this,
                        "Location not set",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("Coords", "meetingLat/Lng: " + meetingLat + ", " + meetingLng + "destLat/Lng:" + destLat + destLng);

            //Server
            //after implementing server code, comment out code in setGroupResult method below
            //LatLng latlng = new LatLng(lat, lng);
            //CreateGroupRequest request = new CreateGroupRequest(leadersEmailStr, nameValStr,
            //        latlng, EmbeddedCreateGroup.this::error);
            //request.makeServerRequest();
            //request.addObserver(newGroupObserver);
            //finish();
        });
    }
    private void setupSelectDestinationButton(){
        Button btn = findViewById(R.id.selectDestButton);
        btn.setOnClickListener(v -> {
            isClicked = !isClicked;
            if(isClicked){
                Toast.makeText(EmbeddedCreateGroup.this, "clicked", Toast.LENGTH_SHORT).show();
                //String setDest = "Set Dest";
                btn.setText(R.string.embedded_set_meeting);
            }else{
                Toast.makeText(EmbeddedCreateGroup.this, "unClicked", Toast.LENGTH_SHORT).show();
                btn.setText(R.string.embedded_set_dest);
                setMeetingCoordinates();
                return;
            }
            //Toast.makeText(this, "Please select destination", Toast.LENGTH_SHORT).show();

            map.setOnMapClickListener(latLng -> {
                destinationMarker.remove();
                destinationMarker = map.addMarker(new MarkerOptions().
                        position(latLng).
                        title("Destination").
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                destinationMarker.setVisible(true);

                destLat = latLng.latitude;
                destLng = latLng.longitude;

                Log.d("Lat (Dest)", "DestLat: " + destLat + "DestLng: " + destLng);


            });
        });
    }

    //Locations Methods
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        setMeetingCoordinates();

    }
    private void setMeetingCoordinates(){
        map.setOnMapClickListener(latLng -> {
            //clear any previous marker
            meetingMarker.remove();
            //map.clear();
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            meetingMarker = map.addMarker(new MarkerOptions().
                    position(latLng).
                    title("Meeting"));

            meetingLat = latLng.latitude;
            meetingLng = latLng.longitude;
            Log.d("Lat (meeting)", "Lat: " + meetingLat + "Long: " + meetingLng);
        });

    }

    private void createLocationRequest() {
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
                    startLocationUpdates();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(this, 2);
                    } catch (IntentSender.SendIntentException e) {
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        });
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

    @Override
    protected void onStart(){
        super.onStart();
        googleApiClient.connect();
        mapView.onStart();
    }

    private void setUpMap(){
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation
                        .getLongitude());
                currentLocation = locationToLatLng();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
            }
        }
        meetingMarker = map.addMarker(new MarkerOptions().
                position(currentLocation).
                title("Meeting"));
        Log.d("initial location", "Lat" + currentLocation.latitude + "Lng" + currentLocation.longitude);

        //For case when user wants to choose current location as starting location.
        meetingLat = currentLocation.latitude;
        meetingLng = currentLocation.longitude;
        Log.d("initial location", "Lat" + meetingLat + "Lng" + meetingLng);

        //destination
        destinationMarker = map.addMarker(new MarkerOptions().
                position(currentLocation).
                title("Destination"));
        destinationMarker.setVisible(false);


    }

    @NonNull
    private LatLng locationToLatLng(){
        return new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }
    public static void setGroupResultCallback(Observer obs){
        newGroupObserver = obs;
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, EmbeddedCreateGroup.class);
    }

}
