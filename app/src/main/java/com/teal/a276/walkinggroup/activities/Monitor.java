package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.List;

import retrofit2.Call;

//TODO: rethink how to handle chaining network calls to avoid code duplication
public class Monitor extends BaseActivity {

    //TODO: use singleton, change later on
    User user = new User();
    ArrayAdapter<User> monitorsAdapter;
    ArrayAdapter<User> monitoredByAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        setupAddToMonitorButton();
        setupAddToMonitoredByButton();

        user.setEmail("c@test.com");
        user.setPassword("1234");

        ServerProxy proxy = ServerManager.getServerRequest();
        Call<Void> call = proxy.login(user);
        ServerManager.serverRequest(call, Monitor.this::login, Monitor.this::error);
    }

    private void initializeListViews() {
        monitorsAdapter = new ListViewAdapter(user.getMonitorsUsers());
        monitorsAdapter.addAll(user.getMonitorsUsers());

        monitoredByAdapter = new ListViewAdapter(user.getMonitoredByUsers());
        monitoredByAdapter.addAll(user.getMonitoredByUsers());

        ListView monitoringList = findViewById(R.id.monitoringListView);
        monitoringList.setAdapter(monitorsAdapter);

        ListView monitoredBy = findViewById(R.id.monitoredByListView);
        monitoredBy.setAdapter(monitoredByAdapter);
    }

    private void setupAddToMonitorButton() {
        Button btn = (Button) findViewById(R.id.addToMonitorBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(Monitor.this);
                adb.setTitle("Add user to monitor");

                final EditText input = new EditText(Monitor.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                adb.setView(input);

                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Add", new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //TODO: server check if this email is valid, then add the user of this email
                        //after server check, populate monitoring listview again
                        String getEmail = input.getText().toString();
                        user.setEmail(getEmail);
                        Log.i("CHECK", getEmail);

                        ServerProxy proxy = ServerManager.getServerRequest();
                        Call<User> userByEmailCall = proxy.getUserByEmail(getEmail);
                        ServerManager.serverRequest(userByEmailCall, Monitor.this::monitor, Monitor.this::error);
                    }
                });
                adb.show();
            }
        });
    }

    private void setupAddToMonitoredByButton(){

        Button btn = (Button) findViewById(R.id.addToMonitoredByBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(Monitor.this);
                adb.setTitle("Add user to be monitored by");

                final EditText input = new EditText(Monitor.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                adb.setView(input);

                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Add", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String getEmail = input.getText().toString();
                        Log.i("CHECK", getEmail);

                        //TODO: server check if this email is valid, then add to monitored by.
                        //after server check, populate monitoredby listview again.

                        ServerProxy proxy = ServerManager.getServerRequest();
                        Call<User> userByEmailCall = proxy.getUserByEmail(getEmail);
                        ServerManager.serverRequest(userByEmailCall, Monitor.this::userByEmail, Monitor.this::error);
                    }
                });
                adb.show();
            }
        });
    }

    private void login(Void ans) {
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<User> userByEmailCall = proxy.getUserById(164L);
        ServerManager.serverRequest(userByEmailCall, this::getUser, this::error);

    }

    private void getUser(User user) {
        this.user = user;
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<User>> call = proxy.getMonitors(164L);
        ServerManager.serverRequest(call, this::monitors, this::error);
    }

    private void monitors(List<User> users) {
        user.setMonitorsUsers(users);

        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<User>> call = proxy.getMonitoredBy(164L);
        ServerManager.serverRequest(call, this::monitoredBy, this::error);
    }

    private void monitoredBy(List<User> users) {
        user.setMonitoredByUsers(users);
        initializeListViews();
    }

    private void monitor(User user) {
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<User>> call = proxy.monitorUser(164L, user);
        ServerManager.serverRequest(call, this::newMonitorees, this::error);
    }

    private void userByEmail(User user) {
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<User>> call = proxy.monitoredByUser(239L, user);
        ServerManager.serverRequest(call, Monitor.this::newMonitoredBy, Monitor.this::error);
    }

    private void newMonitorees(List<User> users) {
        monitorsAdapter.addAll(users);
        monitorsAdapter.notifyDataSetChanged();

        Log.d("Num of users monitored", "" + users.size());
        for (int i = 0; i < users.size(); i++) {
            Log.d("User " + i, users.get(i).toString());
        }
    }

    private void newMonitoredBy(List<User> users) {
        monitoredByAdapter.addAll(users);
        monitoredByAdapter.notifyDataSetChanged();

        for (int i = 0; i < users.size(); i++) {
            Log.d("User monitors you" + i, users.get(i).toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }



    public static Intent makeIntent(Context context){
        return new Intent(context, Monitor.class);
    }

    private class ListViewAdapter extends ArrayAdapter<User> {
        List<User> userList;
        ListViewAdapter(List<User> users) {
            super(Monitor.this, R.layout.list_item, users);
            userList = users;
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            }

            User currentUser = userList.get(position);
            TextView userText = itemView.findViewById(R.id.userName);
            userText.setText(currentUser.getEmail());

            ImageView removeUser = itemView.findViewById(R.id.removeUser);
            removeUser.setOnClickListener(view -> {
                ServerProxy proxy = ServerManager.getServerRequest();
                Call<Void> call = proxy.endMonitoring(user.getId(), currentUser.getId());
                if (userList == user.getMonitorsUsers()) {
                    ServerManager.serverRequest(call, result -> removeMonitoree(result, currentUser), Monitor.this::error);
                } else {
                    ServerManager.serverRequest(call, result -> removeMonitor(result, currentUser), Monitor.this::error);
                }
            });

            return itemView;
        }
    }


    private void removeMonitoree(Void ans, User user) {
        this.user.getMonitorsUsers().remove(user);
        monitorsAdapter.remove(user);
        monitorsAdapter.notifyDataSetChanged();

        findViewById(R.id.monitoringListView).invalidate();
    }

    private void removeMonitor(Void ans, User user) {
        this.user.getMonitoredByUsers().remove(user);
        monitoredByAdapter.remove(user);
        monitoredByAdapter.notifyDataSetChanged();

        findViewById(R.id.monitoredByListView).invalidate();
    }
}


