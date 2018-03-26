package com.teal.a276.walkinggroup.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.map.SelectLocationOnMap;
import com.teal.a276.walkinggroup.model.dataobjects.User;

import static com.teal.a276.walkinggroup.activities.map.SelectLocationOnMap.EXTRA_LATITUDE;
import static com.teal.a276.walkinggroup.activities.map.SelectLocationOnMap.EXTRA_LONGITUDE;

/**
 * Activity to create a new group: Users are able to set group name, set leader's ID, and
 * select the meeting location on the map by dragging the marker to the desired destination.
 */

public class CreateGroup extends BaseActivity {
    private final int REQUEST_CODE_MAP = 1010;
    private LatLng latlng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);

        setCreateNewGroupButton();
        setupMapButton();
    }

    private void setupMapButton() {
        Button btn = findViewById(R.id.meetingMapBtn);
        btn.setOnClickListener(v -> {
            Intent intent = SelectLocationOnMap.makeIntent(CreateGroup.this);
            startActivityForResult(intent, REQUEST_CODE_MAP);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case REQUEST_CODE_MAP:
                if(resultCode == Activity.RESULT_OK){

                    Double lat = data.getDoubleExtra(EXTRA_LATITUDE, 0);
                    Double lng = data.getDoubleExtra(EXTRA_LONGITUDE, 0);
                    latlng = new LatLng(lat, lng);
                    Log.d("Lat Long", "Lat: " + lat + "Long: " + lng);
                }
        }
    }


    private void setCreateNewGroupButton() {
        Button btn = findViewById(R.id.createNewGroupBtn);
        btn.setOnClickListener(v -> {
            EditText nameVal = findViewById(R.id.createGroupNameEdit);
            String nameValStr = nameVal.getText().toString();

            EditText leadersEmailVal = findViewById(R.id.createGroupEmailEdit);
            String leadersEmailStr = leadersEmailVal.getText().toString();

            //Input checking: If there are empty fields
            if(nameValStr.isEmpty()){
                nameVal.setError(getString(R.string.empty_group_name));
                return;
            }
            if(!User.validateEmail(leadersEmailStr)){
                leadersEmailVal.setError(getString(R.string.invalid_email));
                return;
            }
            if((latlng.latitude == 0) && (latlng.longitude == 0)) {
                Toast.makeText(
                        CreateGroup.this,
                        getString(R.string.location_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            //ModelFacade.getInstance().getGroupManager().addNewGroup(leadersEmailStr, nameValStr, latlng, CreateGroup.this::error);
            finish();
        });

    }

    public static Intent makeIntent(Context context){
        return new Intent(context, CreateGroup.class);
    }
}
