package com.teal.a276.walkinggroup.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teal.a276.walkinggroup.R;

public class SelectLocationOnMap extends BaseActivity implements OnMapReadyCallback {

    public static final String EXTRA_LATITUDE = "latitude-extra";
    public static final String EXTRA_LONGITUDE = "longitude-extra";
    public static final int ZOOM = 15;
    public static final int BEARING = 0;
    public static final int TILT = 0;
    public static final int VIEW = 13;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location_on_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //get current GPS location and put the Lat, Long in currentPosition
        LatLng currentPosition = moveToCurrentLocation();

        //add marker on current position and make it draggable
        Marker currMarker = mMap.addMarker(new MarkerOptions().
                position(currentPosition).
                draggable(true));


        //Listens to drags the user makes
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }
            @Override
            public void onMarkerDrag(Marker marker) {
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {

                LatLng afterDrag = marker.getPosition();
                Toast.makeText(
                        SelectLocationOnMap.this,
                        "Lat " + afterDrag.latitude + "\nLong " +
                                afterDrag.longitude,
                        Toast.LENGTH_SHORT).show();
            }
        });

        //Upon click, asks user if this is the location they want.
        mMap.setOnMarkerClickListener(marker -> {
            double lat = marker.getPosition().latitude;
            double lng = marker.getPosition().longitude;
            String latStr = String.valueOf(lat);
            String lonStr = String.valueOf(lng);
            Log.d("After Moved Coordinates", "Lat" + latStr + "Lng" + lonStr);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(SelectLocationOnMap.this);
            alertDialog.setTitle(R.string.useThisLocation);
            alertDialog.setNegativeButton((R.string.cancel), null);
            alertDialog.setPositiveButton((R.string.ok), (dialog, which) -> {

                Intent intent = new Intent();
                intent.putExtra(EXTRA_LATITUDE, lat);
                intent.putExtra(EXTRA_LONGITUDE, lng);
                setResult(Activity.RESULT_OK, intent);
                finish();

            });
            alertDialog.show();

            return false;
        });
    }



    // https://stackoverflow.com/questions/18425141/android-google-maps-api-v2-zoom-to-current-location/20930874
    public LatLng moveToCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        //this if statement is auto generated
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
      Location location = locationManager.getLastKnownLocation(
              locationManager.getBestProvider(criteria, false));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                    location.getLongitude()), VIEW));

      CameraPosition cameraPosition = new CameraPosition.Builder()
              .target(new LatLng(location.getLatitude(), location.getLongitude()))  // Sets the center of the map to location user
              .zoom(ZOOM)
              .bearing(BEARING)
              .tilt(TILT)
              .build();

        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        return currentPosition;

    }

    public static Intent makeIntent(Context context){
        return new Intent(context, SelectLocationOnMap.class);
    }


}
