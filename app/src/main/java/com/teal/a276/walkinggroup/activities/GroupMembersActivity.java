package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

import retrofit2.Call;

public class GroupMembersActivity extends AppCompatActivity {
    private static final String EXTRA_GROUPNAME = "com.teal.a276.walkinggroup.activities.GroupMemberActivity - the groupName";
    private GroupManager groupManager;
    private String groupName;
    private Group groupSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        groupManager = ModelFacade.getInstance().getGroupManager();

        extractDatafromIntent();
        setGroup();
        setText();
        initializeListViews();
    }

    private void setText() {
        TextView groupNameTv = findViewById(R.id.groupNameTxt);
        String groupsName = getString(R.string.group_1_s, groupName);
        groupNameTv.setText(groupsName);

        // Get leaders info
        List<User> users = ModelFacade.getInstance().getUsers();
        User leader = new User();
        for(int i = 0; i < users.size(); i++){
            if(groupSelected.getLeader().getId().equals(users.get(i).getId())){
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

    private void setGroup() {
        List<Group> currentGroups = groupManager.getGroups();
        for(int i= 0; i < currentGroups.size(); i++){
            if(currentGroups.get(i).getGroupDescription().equals(groupName)){
                groupSelected = currentGroups.get(i);
            }
        }
    }

    private void initializeListViews() {
        ArrayAdapter<User> groupMembersAdapter = new ListItemAdapter(this, groupSelected.getMemberUsers());
        ListView groupMembersList = findViewById(R.id.groupMembersListView);
        groupMembersList.setAdapter(groupMembersAdapter);
    }

    private class ListItemAdapter extends ArrayAdapter<User> {
        private final List<User> listItems;
        private final Context context;

        public ListItemAdapter(Context context, List<User> listItems) {
            super(context, R.layout.list_item, listItems);
            this.listItems = listItems;
            this.context = context;
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                itemView = inflater.inflate(R.layout.list_item, parent, false);
            }

            User selectedUser = listItems.get(position);
            List<User> users = ModelFacade.getInstance().getUsers();
            User userWithInfo = new User();

            for(int i = 0; i < users.size(); i++){
                if(selectedUser.getId().equals(users.get(i).getId())){
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

    public static Intent makeIntent(Context context, String groupName) {
        Intent intent = new Intent(context, GroupMembersActivity.class);
        intent.putExtra(EXTRA_GROUPNAME, groupName);
        return intent;
    }

    private void extractDatafromIntent() {
        Intent intent = getIntent();
        groupName = intent.getStringExtra(EXTRA_GROUPNAME);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
