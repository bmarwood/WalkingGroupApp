package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.GroupManager;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class GroupMembersInfo extends BaseActivity {
    private static final String EXTRA_GROUP_NAME = "com.teal.a276.walkinggroup.activities.GroupMembersInfo - groupName";
    private static final String EXTRA_GROUP_ID = "com.teal.a276.walkinggroup.activities.GroupMembersInfo - groupID";
    private Group groupSelected = new Group();
    User leader = new User();
    List<User> groupMembersWithInfo = new ArrayList<>();

    public static Intent makeIntent(Context context, Group group) {
        Intent intent = new Intent(context, GroupMembersInfo.class);
        intent.putExtra(EXTRA_GROUP_NAME, group.getGroupDescription());
        intent.putExtra(EXTRA_GROUP_ID, group.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        extractDataFromIntent();
        setGroup();
        setText();
        callServerForUserList();
        registerClickCallback();
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        String groupName = intent.getStringExtra(EXTRA_GROUP_NAME);
        Long groupID = intent.getLongExtra(EXTRA_GROUP_ID, 0);
        groupSelected.setGroupDescription(groupName);
        groupSelected.setId(groupID);
    }

    private void setGroup() {
        GroupManager groupManager = ModelFacade.getInstance().getGroupManager();
        List<Group> currentGroups = groupManager.getGroups();
        for (int i = 0; i < currentGroups.size(); i++) {
            if (currentGroups.get(i).getGroupDescription().equals(groupSelected.getGroupDescription())) {
                groupSelected = currentGroups.get(i);
            }
        }
    }

    private void setText() {
        TextView groupNameTv = findViewById(R.id.groupNameTxt);
        String groupsName = getString(R.string.group_number, groupSelected.getGroupDescription());
        groupNameTv.setText(groupsName);

        // Get leaders info
        List<User> users = ModelFacade.getInstance().getUsers();
        for (int i = 0; i < users.size(); i++) {
            if (groupSelected.getLeader().getId().equals(users.get(i).getId())) {
                leader = users.get(i);
            }
        }

        TextView leaderName = findViewById(R.id.leadersNameTxt);
        String leadersName = getString(R.string.leaders_name, leader.getName());
        leaderName.setText(leadersName);

        TextView leaderEmail = findViewById(R.id.leadersEmailTxt);
        String leadersEmail = getString(R.string.leaders_email, leader.getEmail());
        leaderEmail.setText(leadersEmail);
    }

    private void callServerForUserList() {
           ServerProxy proxy = ServerManager.getServerRequest();
           Call<List<User>> call = proxy.getGroupMembers(groupSelected.getId());
           ServerManager.serverRequest(call, this::getUsers, this::error);
    }

    private void getUsers(List<User> userList) {
        List<User> currentUsers = ModelFacade.getInstance().getUsers();

        groupMembersWithInfo.add(leader);
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            for (int j = 0; j < currentUsers.size(); j++) {
                if (currentUsers.get(j).equals(user)) {
                    groupMembersWithInfo.add(currentUsers.get(j));
                }
            }
        }

        ArrayAdapter<User> groupMembersAdapter = new ListItemAdapter(this, groupMembersWithInfo);
        ListView groupMembersList = findViewById(R.id.groupMembersListView);
        groupMembersList.setAdapter(groupMembersAdapter);
    }

    private void registerClickCallback() {
        ListView list = findViewById(R.id.groupMembersListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                Log.d("GroupMembersInfo", "Clicked " + position);

                // TODO: Launch activity when profile issue is complete
                // Pass group member with info to profile activity
//                Intent intent = Profile.makeIntent(GroupMembersInfo.this, groupMembersWithInfo.get(position) );
//                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private class ListItemAdapter extends ArrayAdapter<User> {
        private final List<User> listItems;
        private final Context context;

        private ListItemAdapter(Context context, List<User> listItems) {
            super(context, R.layout.list_item, listItems);
            this.listItems = listItems;
            this.context = context;
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                itemView = inflater.inflate(R.layout.list_item, parent, false);
            }

            User selectedUser = listItems.get(position);
            List<User> users = ModelFacade.getInstance().getUsers();
            User userWithInfo = new User();

            for (int i = 0; i < users.size(); i++) {
                if (selectedUser.getId().equals(users.get(i).getId())) {
                    userWithInfo = users.get(i);
                }
            }

            TextView emailTextView = itemView.findViewById(R.id.userEmail);
            emailTextView.setText(userWithInfo.getEmail());

            TextView nameTextView = itemView.findViewById(R.id.userName);
            nameTextView.setText(userWithInfo.getName());

            ImageView removeView = itemView.findViewById(R.id.removeUser);
            removeView.setVisibility(View.INVISIBLE);

            return itemView;
        }
    }
}
