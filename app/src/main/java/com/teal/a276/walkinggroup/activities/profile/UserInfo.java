package com.teal.a276.walkinggroup.activities.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.Monitor;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.List;

import retrofit2.Call;

public class UserInfo extends UserProfile {
    private static final String USER = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        getDataFromIntent();
        fillBasicInfo(false);
        populateListView();
    }

    public static Intent makeIntent(Context context, User user) {
        Gson gson = new Gson();
        Intent intent = new Intent(context, UserInfo.class);
        intent.putExtra(USER, gson.toJson(user));
        return intent;
    }

    public void populateListView() {
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<User> call = proxy.getUserByEmail(user.getEmail(), 1L);
        ServerManager.serverRequest(call, this::getUser, this::error);
    }

    private void getUser(User user) {
        ArrayAdapter<User> monitorsAdapter = new UserInfo.ListItemAdapter(this, user.getMonitoredByUsers());
        ListView monitoringList = findViewById(R.id.parentListView);
        monitoringList.setAdapter(monitorsAdapter);
    }

    private class ListItemAdapter extends ArrayAdapter<User> {
        private final List<User> listItems;
        private final Context context;

        private ListItemAdapter(Context context, List<User> listItems) {
            super(context, R.layout.parents_list_items, listItems);
            this.listItems = listItems;
            this.context = context;
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                itemView = inflater.inflate(R.layout.parents_list_items, parent, false);
            }

            User selectedUser = listItems.get(position);

            TextView name = itemView.findViewById(R.id.nameTxt);
            name.setText(selectedUser.getName());

            TextView email = itemView.findViewById(R.id.emailTxt);
            email.setText(selectedUser.getEmail());

            TextView homePhone = itemView.findViewById(R.id.homeTxt);
            homePhone.setText(selectedUser.getHomePhone());

            TextView cellPhone = itemView.findViewById(R.id.cellTxt);
            cellPhone.setText(selectedUser.getCellPhone());

            return itemView;
        }
    }
}

