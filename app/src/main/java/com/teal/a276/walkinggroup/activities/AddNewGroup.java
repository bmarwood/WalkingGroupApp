package com.teal.a276.walkinggroup.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import retrofit2.Call;


public class AddNewGroup extends BaseActivity {

    public static final String EXTRA_GROUPNAME = "com.teal.a276.walkinggroup-extra_name";
    //public static final String EXTRA_MEETINGLOCATION_ = "com.teal.a276.walkinggroup-extra_meetinglocation";
    //public static final String EXTRA_DESTINATION = "com.teal.a276.walkinggroup-extra_destination";
    public static final String EXTRA_MEETINGLAT = "com.teal.a276.walkinggroup-extra_meeting_lat";
    public static final String EXTRA_MEETINGLNG = "com.teal.a276.walkinggroup-extra_meeting_lng";
    public static final String EXTRA_DESTLAT = "com.teal.a276.walkinggroup-extra_dest_lat";
    public static final String EXTRA_DESTLNG = "com.teal.a276.walkinggroup-extra_dest_lng";


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
//
//                 EditText meetingLatVal = (EditText)findViewById(R.id.meetingLatEdit);
//                 String meetingLatStr = meetingLatVal.getText().toString();
//
//                 EditText meetingLngVal = (EditText)findViewById(R.id.meetingLngEdit);
//                 String meetingLngStr = meetingLngVal.getText().toString();
//
//                 EditText destLatVal = (EditText)findViewById(R.id.destLatEdit);
//                 String destLatStr = destLatVal.getText().toString();
//
//                 EditText destLngVal = (EditText)findViewById(R.id.destLngEdit);
//                 String destLngStr = destLngVal.getText().toString();

                 EditText leadersEmailInput = (EditText)findViewById(R.id.leaderEmail);
                 String leadersEmail = leadersEmailInput.getText().toString();

                 if(!leadersEmail.isEmpty() && !nameValStr.isEmpty()) {

                     ServerProxy proxy = ServerManager.getServerRequest();
                     Call<User> call = proxy.getUserByEmail(leadersEmail);
                     ServerManager.serverRequest(call, result -> userFromEmail(result, nameValStr), AddNewGroup.this::error);
                 }

                 //EditText meetingVal= (EditText)findViewById(R.id.meetingLocationEdit);
                 //String meetingValStr = meetingVal.getText().toString();

                 //EditText destinationVal = (EditText)findViewById(R.id.destinationEdit);
                 //String destinationValStr = destinationVal.getText().toString();

                 //Check if any text fields are empty
//                 if(nameValStr.equals("")|meetingLatStr.equals("")|meetingLngStr.equals("")
//                         |destLatStr.equals("")|destLngStr.equals("")) {
//                     Toast.makeText(AddNewGroup.this, "One or more fields empty, please check again.", Toast.LENGTH_SHORT).show();
//                 }
//                 } else {
//                     Intent intent = new Intent();
//                     intent.putExtra(EXTRA_GROUPNAME, nameValStr);
//                     //intent.putExtra(EXTRA_MEETINGLOCATION_, meetingValStr);
//                     //intent.putExtra(EXTRA_DESTINATION, destinationValStr);
//                     intent.putExtra(EXTRA_MEETINGLAT, meetingLatStr);
//                     intent.putExtra(EXTRA_MEETINGLNG, meetingLngStr);
//                     intent.putExtra(EXTRA_DESTLAT, destLatStr);
//                     intent.putExtra(EXTRA_DESTLNG, destLngStr);
//                     setResult(Activity.RESULT_OK, intent);
//                     finish();
//                 }
             }
         });
    }


    private void userFromEmail(User user, String groupDes) {
        Group group = new Group();
        group.setLeader(user);
        group.setId(-1L);
        group.setGroupDescription(groupDes);

        ServerProxy proxy = ServerManager.getServerRequest();
        Call<Group> call = proxy.createGroup(group);
        ServerManager.serverRequest(call, this::groupCreated, this::error);
    }

    private void groupCreated(Group group) {
        Log.d("Group created", group.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    public static Intent makeIntent(Context context){
        return new Intent(context, AddNewGroup.class);
    }
}