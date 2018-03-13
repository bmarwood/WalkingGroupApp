package com.teal.a276.walkinggroup.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Activity to create a new group: Users are able to set group name, set leader's ID, and
 * select the meeting location on the map by dragging the marker to the desired destination.
 */

public class CreateNewGroup extends BaseActivity {

    //for lat, lng retrieval
    double lat=0;
    double lng=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);


        setCreateNewGroupButton();
        setupMapButton();
    }

    private void setupMapButton() {
        Button btn = findViewById(R.id.meetingMapBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SelectLocationOnMap.makeIntent(CreateNewGroup.this);
                startActivityForResult(intent, 1010);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case 1010:
                if(resultCode == Activity.RESULT_OK){
                    //double lat = data.getExtras().getDouble(EXTRA_LAT);
                    lat = data.getDoubleExtra("latitude", 0);
                    lng = data.getDoubleExtra("longitude", 0);

                    Toast.makeText(
                            CreateNewGroup.this,
                            "Lat " + lat + "\nLong " +
                                    lng,
                            Toast.LENGTH_SHORT).show();
                }
        }
    }



    private void setCreateNewGroupButton() {
        Button btn = findViewById(R.id.createNewGroupBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameVal = findViewById(R.id.createGroupNameEdit);
                String nameValStr = nameVal.getText().toString();

                EditText leadersEmailVal = findViewById(R.id.createGroupEmailEdit);
                String leadersEmailStr = leadersEmailVal.getText().toString();

                EditText destinationVal = findViewById(R.id.createDestinationEdit);
                String  destination = destinationVal.getText().toString();

//                if(nameValStr.isEmpty() || leadersEmail.isEmpty()){
//                    Toast.makeText(
//                            CreateNewGroup.this,
//                            "good to go",
//                            Toast.LENGTH_SHORT).show();
//                }

                //check if user selected a meeting place on the map
                if(lat==0 && lng==0){
                    Toast.makeText(
                            CreateNewGroup.this,
                            "lat lng not set",
                            Toast.LENGTH_SHORT).show();
                }
                ServerProxy proxy = ServerManager.getServerRequest();
                Call<User> call = proxy.getUserByEmail(leadersEmailStr);
                ServerManager.serverRequest(call, result -> userFromEmail(result,
                        nameValStr, lat, lng), CreateNewGroup.this::error);

                finish();
            }
        });

    }

    private void userFromEmail(User user, String groupDes, double Lat, double Lng){
        Group group = new Group();
        group.setLeader(user);
        group.setId(-1L);
        group.setGroupDescription(groupDes);


       // Double latDouble = Double.valueOf(Lat);
       // Double lngDouble = Double.valueOf(Lng);
        List<Double> latArray = new ArrayList<>();
        List<Double> lngArray = new ArrayList<>();

        latArray.add(Lat);
        lngArray.add(Lng);

        group.setRouteLatArray(latArray);
        group.setRouteLngArray(lngArray);

        ServerProxy proxy = ServerManager.getServerRequest() ;
        Call<Group> call = proxy.createGroup(group);
        ServerManager.serverRequest(call, this::groupCreated, this::error);



    }
    private void groupCreated(Group group){
        Log.d("Group Created", group.toString());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, CreateNewGroup.class);
    }
}
