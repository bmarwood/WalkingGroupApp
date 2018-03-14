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

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

import static com.teal.a276.walkinggroup.activities.SelectLocationOnMap.EXTRA_LATITUDE;
import static com.teal.a276.walkinggroup.activities.SelectLocationOnMap.EXTRA_LONGITUDE;

/**
 * Activity to create a new group: Users are able to set group name, set leader's ID, and
 * select the meeting location on the map by dragging the marker to the desired destination.
 */

public class CreateNewGroup extends BaseActivity {

    interface ObservableCallback{
        void makeRequest(String email);
    }

    public static final int REQUEST_CODE_MAP = 1010;
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
            if(!User.validateEmail(leadersEmailStr)){
                Toast.makeText(
                        CreateNewGroup.this,
                        "Email is in incorrect format!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if(leadersEmailStr.isEmpty() || nameValStr.isEmpty()){
                Toast.makeText(
                        CreateNewGroup.this,
                        "Name or Email empty",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if((lat==0) && (lng==0)) {
                Toast.makeText(
                        CreateNewGroup.this,
                        "Lat/Lng NOT set",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            //If passed all input checking, continue to push group to server
//            ObservableCallback.makeRequest(leadersEmailStr);
            ServerProxy proxy = ServerManager.getServerRequest();
            Call<User> call = proxy.getUserByEmail(leadersEmailStr);
            ServerManager.serverRequest(call, result -> userFromEmail(result,
                    nameValStr, lat, lng), CreateNewGroup.this::error);

//            CreateGroupRequest request = new CreateGroupRequest(leadersEmailStr, nameValStr);
//            request.makeServerRequest();
//            request.addObserver((observable, o) -> {
//                Group group = (Group)o;
//
//
//
//
//
//
 //                   })







            finish();
        });

    }





    private void userFromEmail(User user, String groupDes, double Lat, double Lng){
        Group group = new Group();
        group.setLeader(user);

        //TODO: Remove this line of code after new server has been pushed.
        group.setId(-1L);

        group.setGroupDescription(groupDes);


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
