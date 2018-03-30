package com.teal.a276.walkinggroup.activities.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;

import java.util.Observable;
import java.util.Observer;

/**
 * Class to create new groups, allowing users to select meeting/dest on mapView
 */

public class EmbeddedCreateGroup extends AbstractMapActivity {

    private LatLng meetingLocation;
    private LatLng destinationLocation;
    private Marker meetingMarker;
    private Marker destinationMarker;
    private boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_embedded_create_group);

        setupSelectDestinationButton();
        setupCreateButton();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.embeddedFragment);
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

    public static Intent makeIntent(Context context){
        return new Intent(context, EmbeddedCreateGroup.class);
    }

    private void setMeetingCoordinates(){
        map.setOnMapClickListener(latLng -> {
            meetingMarker.remove();
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            meetingMarker = placeMarkerWithColor(
                    latLng,
                    getString(R.string.embedded_set_meeting),
                    MarkerColor.CYAN);
            meetingMarker.showInfoWindow();

            meetingLocation = latLng;
            Log.d("Lat (meeting)", "Lat: " + meetingLocation.latitude + "Long: " + meetingLocation.longitude);
        });
    }

    private void setupSelectDestinationButton(){
        Button btn = findViewById(R.id.selectDestButton);
        btn.setOnClickListener(v -> {
            isClicked = !isClicked;
            if(isClicked) {
                Toast.makeText(EmbeddedCreateGroup.this, R.string.leader_destination, Toast.LENGTH_SHORT).show();
                btn.setText(R.string.embedded_set_meeting);
            } else {
                Toast.makeText(EmbeddedCreateGroup.this, R.string.user_destination, Toast.LENGTH_SHORT).show();
                btn.setText(R.string.embedded_set_dest);
                setMeetingCoordinates();
                return;
            }
            map.setOnMapClickListener(latLng -> {
                destinationMarker.remove();
                destinationMarker = placeMarkerWithColor(
                        latLng,
                        getString(R.string.embedded_destination),
                        MarkerColor.RED);
                destinationMarker.setVisible(true);
                destinationMarker.showInfoWindow();

                destinationLocation = latLng;
                Log.d("Lat (Dest)", "DestLat: " + destinationLocation.latitude +
                        "DestLng: " + destinationLocation.longitude);
            });
        });
    }

    private void setupCreateButton() {
        Button btn = findViewById(R.id.embeddedCreateButton);
        btn.setOnClickListener(v ->{
            EditText nameVal = findViewById(R.id.embeddedNameEdit);
            String nameValStr = nameVal.getText().toString();

            EditText leadersEmailVal = findViewById(R.id.embeddedEmailEdit);
            String leadersEmailStr = leadersEmailVal.getText().toString();

            if(nameValStr.isEmpty()) {
                nameVal.setError(getString(R.string.empty_group_name));
                return;
            }
            if(!User.validateEmail(leadersEmailStr)) {
                leadersEmailVal.setError(getString(R.string.invalid_email));
                return;
            }
            if((meetingLocation.latitude == 0) && (meetingLocation.longitude == 0)) {
                Toast.makeText(
                        EmbeddedCreateGroup.this,
                        R.string.embedded_location_not_set,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("Coords", "meetingLat/Lng: " + meetingLocation.latitude + "/"
                    + meetingLocation.longitude + " destLat/Lng:" + destinationLocation.latitude
                    + "/" + destinationLocation.longitude);

            ModelFacade.getInstance().getGroupManager().addNewGroup(
                    leadersEmailStr,
                    nameValStr,
                    meetingLocation,
                    destinationLocation,
                    EmbeddedCreateGroup.this::error);

            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        map = googleMap;
        setMeetingCoordinates();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
        placeCurrentLocationMarker(false, R.mipmap.ic_user_location);
        if(updateLocation) {
            startLocationUpdates();
        }
        addInitialMarkers();
    }

    private void addInitialMarkers() {
        meetingLocation = locationToLatLng(lastLocation);
        meetingMarker = map.addMarker(new MarkerOptions().
                position(meetingLocation).
                title(getString(R.string.meeting)));

        Log.d("initial location", "Lat" + meetingLocation.latitude + "Lng" + meetingLocation.longitude);

        destinationMarker = map.addMarker(new MarkerOptions().
                position(meetingLocation).
                title(getString(R.string.destination)));
        destinationMarker.setVisible(false);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}