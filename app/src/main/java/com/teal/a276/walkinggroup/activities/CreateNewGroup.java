package com.teal.a276.walkinggroup.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.User;


public class CreateNewGroup extends BaseActivity {

    //for lat, lng retrieval
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);


        setupAddGroupButton();
        setupMapButton();
    }

    private void setupMapButton() {
        Button btn = findViewById(R.id.meetingMapBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SelectLocationOnMap.makeIntent(CreateNewGroup.this);
                startActivityForResult(intent, 1010);
                //TODO: extract lat long



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




    private void setupAddGroupButton() {
        Button btn = findViewById(R.id.meetingMapBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameVal = findViewById(R.id.createGroupNameEdit);
                String nameValStr = nameVal.getText().toString();

                EditText leadersEmailInput = findViewById(R.id.createGroupEmailEdit);
                String leadersEmail = leadersEmailInput.getText().toString();

                //TODO: interact with server


            }
        });

    }

    private void userFromEmail(User user, String groupDes){
        Group group = new Group();
        group.setLeader(user);
        group.setId(-1L);
        group.setGroupDescription(groupDes);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, CreateNewGroup.class);
    }
}
