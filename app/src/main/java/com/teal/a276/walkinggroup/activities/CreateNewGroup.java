package com.teal.a276.walkinggroup.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverrequest.requestimplementation.CreateGroupRequest;

import java.util.Observer;

import static com.teal.a276.walkinggroup.activities.SelectLocationOnMap.EXTRA_LATITUDE;
import static com.teal.a276.walkinggroup.activities.SelectLocationOnMap.EXTRA_LONGITUDE;

/**
 * Activity to create a new group: Users are able to set group name, set leader's ID, and
 * select the meeting location on the map by dragging the marker to the desired destination.
 */

public class CreateNewGroup extends BaseActivity {

    public static final int REQUEST_CODE_MAP = 1010;
    double lat=0;
    double lng=0;
    LatLng latlng;
    private static Observer newGroupObserver;


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
            Intent intent = SelectLocationOnMap.makeIntent(CreateNewGroup.this);
            startActivityForResult(intent, REQUEST_CODE_MAP);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case REQUEST_CODE_MAP:
                if(resultCode == Activity.RESULT_OK){

                    lat = data.getDoubleExtra(EXTRA_LATITUDE, 0);
                    lng = data.getDoubleExtra(EXTRA_LONGITUDE, 0);
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
            if((lat==0) && (lng==0)) {
                Toast.makeText(
                        CreateNewGroup.this,
                        "Location NOT set",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            CreateGroupRequest request = new CreateGroupRequest(leadersEmailStr, nameValStr, latlng, CreateNewGroup.this::error);
            request.makeServerRequest();
            request.addObserver(newGroupObserver);

            finish();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, CreateNewGroup.class);
    }

    public static void setGroupResultCallback(Observer obs) {
        newGroupObserver = obs;
    }
}
