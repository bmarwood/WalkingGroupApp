package com.teal.a276.walkinggroup.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;


public class AddNewGroup extends AppCompatActivity {

    public static final String EXTRA_GROUPNAME = "com.teal.a276.walkinggroup-extra_name";
    public static final String EXTRA_MEETINGLOCATION_ = "com.teal.a276.walkinggroup-extra_meetinglocation";
    public static final String EXTRA_DESTINATION = "com.teal.a276.walkinggroup-extra_destination";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_group);

        setupAddGroupButton();
    }

    private void setupAddGroupButton() {
         Button btn = (Button) findViewById(R.id.addGroupBtn);
         btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 //TODO: interact with server (add new group, with this user as one member)
                 //CHANGE LATER TO INTERACT WITH SERVER
                 EditText nameVal = (EditText)findViewById(R.id.groupNameEdit);
                 String nameValStr = nameVal.getText().toString();

                 EditText meetingVal= (EditText)findViewById(R.id.meetingLocationEdit);
                 String meetingValStr = meetingVal.getText().toString();

                 EditText destinationVal = (EditText)findViewById(R.id.destinationEdit);
                 String destinationValStr = destinationVal.getText().toString();

                 //Check if any text fields are empty
                 if(nameValStr.equals("")|meetingValStr.equals("")|destinationValStr.equals("")){
                     Toast.makeText(AddNewGroup.this, "One or more fields empty, please check again.", Toast.LENGTH_SHORT).show();
                 } else {
                     Intent intent = new Intent();
                     intent.putExtra(EXTRA_GROUPNAME, nameValStr);
                     intent.putExtra(EXTRA_MEETINGLOCATION_, meetingValStr);
                     intent.putExtra(EXTRA_DESTINATION, destinationValStr);
                     setResult(Activity.RESULT_OK, intent);
                     finish();
                 }
             }
         });
    }






    public static Intent makeIntent(Context context){
        return new Intent(context, AddNewGroup.class);
    }
}