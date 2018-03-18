package com.teal.a276.walkinggroup.activities.map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.BaseActivity;


public class SelectLocationOnMap extends AbstractMapActivity {
    public static final String EXTRA_LATITUDE = "latitude-extra";
    public static final String EXTRA_LONGITUDE = "longitude-extra";
    private LatLng markerEndLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                markerEndLocation = marker.getPosition();
                Log.d("after moved", "Lat" + markerEndLocation.latitude +
                        "Lng" + markerEndLocation.longitude);
            }
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, SelectLocationOnMap.class);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
        placeCurrentLocationMarker(true, null);
        if (updateLocation) {
            startLocationUpdates();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("click listener", "LatLng" + markerEndLocation);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SelectLocationOnMap.this);
        alertDialog.setTitle(R.string.useThisLocation);
        alertDialog.setNegativeButton((R.string.cancel), null);
        alertDialog.setPositiveButton((R.string.ok), (dialog, which) -> {

            Intent intent = new Intent();
            intent.putExtra(EXTRA_LATITUDE, markerEndLocation.latitude);
            intent.putExtra(EXTRA_LONGITUDE, markerEndLocation.longitude);
            setResult(Activity.RESULT_OK, intent);
            finish();

        });
        alertDialog.show();

        return true;
    }
}
