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
import android.widget.ListView;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.map.MapsActivity;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.GroupManager;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MyGroups extends BaseActivity {
    private User user = new User();
    private List<Group> leadsGroups;
    private List<Group> memberOfGroups;
    private List<String> leadsGroupNames = new ArrayList<>();
    private List<String> memberOfGroupNames = new ArrayList<>();

    public static Intent makeIntent(Context context) {
        return new Intent(context, MyGroups.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        user = ModelFacade.getInstance().getCurrentUser();

        // TODO: Call Server to update user model
        callServerForUserList();
//        setInfo();
        setGroupInfo();
//        initializeListViews();
    }

    private void callServerForUserList() {
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<User> call = proxy.getUserById(user.getId());
        ServerManager.serverRequest(call, this::setInfo, this::error);
    }

    private void setInfo(User user) {
        memberOfGroups = user.getMemberOfGroups();
        leadsGroups = user.getLeadsGroups();
//        String s = "";
    }

//    private void setInfo() {
//        user = ModelFacade.getInstance().getCurrentUser();
//        leadsGroups = user.getLeadsGroups();
//        memberOfGroups = user.getMemberOfGroups();
//    }

    private void setGroupInfo() {
        GroupManager groupManager = ModelFacade.getInstance().getGroupManager();
        List<Group> groups = groupManager.getGroups();

        for(int i = 0; i < groups.size(); i++){
            for (int j = 0; j < leadsGroups.size(); j++){
                if(groups.get(i).getId().equals(leadsGroups.get(j).getId())) {
                    leadsGroupNames.add(groups.get(i).getGroupDescription());
                }
            }
        }

        for(int i = 0; i < groups.size(); i++){
            for (int j = 0; j < memberOfGroups.size(); j++){
                if(groups.get(i).getId().equals(memberOfGroups.get(j).getId())) {
                    memberOfGroupNames.add(groups.get(i).getGroupDescription());
                }
            }
        }
    }

    private void initializeListViews() {
        ArrayAdapter<String> groupsILeadAdapter = new MyGroups.ListItemAdapter(this, leadsGroupNames);
        ListView groupsILead = findViewById(R.id.groupsILead);
        groupsILead.setAdapter(groupsILeadAdapter);

        ArrayAdapter<String> groupsImInAdapter = new MyGroups.ListItemAdapter(this, memberOfGroupNames);
        ListView groupsImIn = findViewById(R.id.groupsImIn);
        groupsImIn.setAdapter(groupsImInAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private class ListItemAdapter extends ArrayAdapter<String> {
        private final List<String> listItems;
        private final Context context;

        private ListItemAdapter(Context context, List<String> listItems) {
            super(context, R.layout.group_info_list_item, listItems);
            this.listItems = listItems;
            this.context = context;
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                itemView = inflater.inflate(R.layout.my_groups_list_item, parent, false);
            }

            String item = listItems.get(position);
            TextView groupNameTextView = itemView.findViewById(R.id.groupName);
            groupNameTextView.setText(item);

            return itemView;
        }
    }
}
