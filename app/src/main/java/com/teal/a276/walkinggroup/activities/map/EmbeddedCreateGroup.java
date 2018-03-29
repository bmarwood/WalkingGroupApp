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

public class EmbeddedCreateGroup extends AbstractMapActivity implements Observer {

    private LatLng currentLocation;
    double meetingLat = 0;
    double meetingLng = 0;
    double destLat = 0;
    double destLng = 0;
    Marker meetingMarker;
    Marker destinationMarker;
    boolean isClicked = false;

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
            meetingMarker = map.addMarker(new MarkerOptions().
                    position(latLng).
                    title(getString(R.string.meeting)));
            meetingMarker.showInfoWindow();

            meetingLat = latLng.latitude;
            meetingLng = latLng.longitude;
            Log.d("Lat (meeting)", "Lat: " + meetingLat + "Long: " + meetingLng);
        });
    }

    private void setupSelectDestinationButton(){
        Button btn = findViewById(R.id.selectDestButton);
        btn.setOnClickListener(v -> {
            isClicked = !isClicked;
            if(isClicked){
                Toast.makeText(EmbeddedCreateGroup.this, R.string.leader_destination, Toast.LENGTH_SHORT).show();
                btn.setText(R.string.embedded_set_meeting);
            }else{
                Toast.makeText(EmbeddedCreateGroup.this, R.string.user_destination, Toast.LENGTH_SHORT).show();
                btn.setText(R.string.embedded_set_dest);
                setMeetingCoordinates();
                return;
            }
            map.setOnMapClickListener(latLng -> {
                destinationMarker.remove();
                destinationMarker = map.addMarker(new MarkerOptions().
                        position(latLng).
                        title(getString(R.string.embedded_destination)).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                destinationMarker.setVisible(true);
                destinationMarker.showInfoWindow();

                destLat = latLng.latitude;
                destLng = latLng.longitude;

                Log.d("Lat (Dest)", "DestLat: " + destLat + "DestLng: " + destLng);
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
                        R.string.embedded_location_not_set,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("Coords", "meetingLat/Lng: " + meetingLat + ", " + meetingLng + "destLat/Lng:" + destLat + destLng);

            LatLng meetingLatlng = new LatLng(meetingLat, meetingLng);
            LatLng destLatlng = new LatLng(destLat, destLng);

            ModelFacade.getInstance().getGroupManager().addNewGroup(leadersEmailStr, nameValStr, meetingLatlng, destLatlng, EmbeddedCreateGroup.this::error);

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
        currentLocation = locationToLatLng(lastLocation);
        meetingMarker = map.addMarker(new MarkerOptions().
                position(currentLocation).
                title("Meeting"));
        Log.d("initial location", "Lat" + currentLocation.latitude + "Lng" + currentLocation.longitude);

        //For case when user wants to choose current location as starting location.
        meetingLat = currentLocation.latitude;
        meetingLng = currentLocation.longitude;
        Log.d("initial location", "Lat" + meetingLat + "Lng" + meetingLng);

        destinationMarker = map.addMarker(new MarkerOptions().
                position(currentLocation).
                title("Destination"));
        destinationMarker.setVisible(false);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}