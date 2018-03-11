package com.teal.a276.walkinggroup.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.GroupManager;

import static com.teal.a276.walkinggroup.activities.AddNewGroup.EXTRA_DESTLAT;
import static com.teal.a276.walkinggroup.activities.AddNewGroup.EXTRA_DESTLNG;
import static com.teal.a276.walkinggroup.activities.AddNewGroup.EXTRA_GROUPNAME;
import static com.teal.a276.walkinggroup.activities.AddNewGroup.EXTRA_MEETINGLAT;
import static com.teal.a276.walkinggroup.activities.AddNewGroup.EXTRA_MEETINGLNG;

//import static com.teal.a276.walkinggroup.activities.AddNewGroup.EXTRA_DESTINATION;
//import static com.teal.a276.walkinggroup.activities.AddNewGroup.EXTRA_MEETINGLOCATION_;


//DO NOT IMPORT THIS, OR REFACTOR THE GROUP CLASS LATER!!
//import java.security.acl.Group;

public class JoinGroup extends BaseActivity {

    GroupManager groupManager = new GroupManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_join_group);

        setupCreateNewGroupButton();
//        populateJoinGroupsListView();
//        populateJoinedGroupsListView();
        registerJoinGroupClickCallback();
        registerJoinedGroupClickCallback();

    }


//    private void populateJoinGroupsListView() {
//        String[] joinGroupsItems = groupManager.getJoinGroupDescriptions();
//
//        //Array Adapter
//        ArrayAdapter<String> joinGroupsAdapter = new ArrayAdapter<String>(
//                this,
//                R.layout.joingroups,
//                joinGroupsItems);
//
//        ListView list = (ListView) findViewById(R.id.joinGroupsListView);
//        list.setAdapter(joinGroupsAdapter);
//    }
//
//
//    private void populateJoinedGroupsListView() {
//        String[] joinedGroupItems = groupManager.getJoinedGroupDescriptions();
//
//        //Array Adapter
//        ArrayAdapter<String> joinedGroupsAdapter = new ArrayAdapter<String>(
//                this,
//                R.layout.joinedgroups,
//                joinedGroupItems);
//
//        ListView list = (ListView) findViewById(R.id.joinedGroupsListView);
//        list.setAdapter(joinedGroupsAdapter);
//    }


    private void setupCreateNewGroupButton() {
        Button btn = (Button) findViewById(R.id.addNewGroupBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddNewGroup.makeIntent(JoinGroup.this);
                startActivityForResult(intent, 1234);
            }
        });
    }

    //Result from Adding new Group
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
         switch(requestCode){
             case 1234:
                 if(resultCode == Activity.RESULT_OK) {
                     String groupName = intent.getStringExtra(EXTRA_GROUPNAME);
                     //String meetingLocation= intent.getStringExtra(EXTRA_MEETINGLOCATION_);
                     //String destination = intent.getStringExtra(EXTRA_DESTINATION);
                     String meetingLat = intent.getStringExtra(EXTRA_MEETINGLAT);
                     String meetingLng = intent.getStringExtra(EXTRA_MEETINGLNG);
                     String destinationLat = intent.getStringExtra(EXTRA_DESTLAT);
                     String destinationLng = intent.getStringExtra(EXTRA_DESTLNG);
                     Group group = new Group();
                     //Group group = new Group(groupName, meetingLocation, destination)
                     group.setGroupDescription(groupName);
//                     group.setMeetingLat(meetingLat);
//                     group.setMeetingLng(meetingLng);
//                     group.setDestinationLat(destinationLat);
//                     group.setDestinationLng(destinationLng);

                     //added 16:57 Mar9

                     //send this new group to server and addnew group on server


                     groupManager.addJoinGroup(group);


//                     populateJoinGroupsListView();


                 }
         }
    }

//added 3/3, to be completed. ***COMPLETED 3/4***
    private void registerJoinGroupClickCallback() {
        ListView list = (ListView) findViewById(R.id.joinGroupsListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Group group = groupManager.getJoinGroup(position);
//                String displayGroupName = group.getGroupName();

                //Before dialog opens, check if the group is already joined
                if(groupManager.checkIfUserAlreadyInSameGroup(group)){
                    //if(groupManager.checkIfUserAlreadyInSameGroup(group)||groupManager.checkIfUserInGroup()){
                    return;
                }

                AlertDialog.Builder adb = new AlertDialog.Builder(JoinGroup.this);
                adb.setTitle("Join?");
//                adb.setMessage("Are you sure you want to join " + displayGroupName + "?");
                final int joinPosition = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Join", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                            Group group;
                            group = groupManager.getJoinGroup(joinPosition);

                            groupManager.addJoinedGroup(group);
//                            populateJoinedGroupsListView();

                    }});
                   adb.show();
            }
        });
    }


    private void registerJoinedGroupClickCallback(){
        ListView list = (ListView) findViewById(R.id.joinedGroupsListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Group group = groupManager.getJoinGroup(position);
//                String displayGroupName = group.getGroupName();
                AlertDialog.Builder adb = new AlertDialog.Builder(JoinGroup.this);
                adb.setTitle("Remove?");
//                adb.setMessage("Are you sure you want to be removed from " + displayGroupName + "?");
                final int removePosition = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Remove", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Group group = groupManager.getJoinGroup(removePosition);
                        groupManager.removeFromJoinedGroups(group);
//                        populateJoinedGroupsListView();

                    }
                });
                adb.show();
        }});
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    public static Intent makeIntent(Context context){
       return new Intent(context, JoinGroup.class);
    }



}
