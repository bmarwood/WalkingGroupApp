package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.adapters.ListItemAdapter;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverstrategy.MonitorStrategy;
import com.teal.a276.walkinggroup.model.serverstrategy.MonitoredByStrategy;

import java.util.List;

import retrofit2.Call;

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
        monitorsAdapter = new ListItemAdapter<>(this, user.getMonitorsUsers());

        ListView monitoringList = findViewById(R.id.monitoringListView);
        monitoringList.setAdapter(monitorsAdapter);
        monitoringList.setOnItemClickListener((adapterView, view, i, l) -> {
            User selectedUser = user.getMonitorsUser(i);

            ServerProxy proxy = ServerManager.getServerRequest();
            Call<Void> call = proxy.endMonitoring(user.getId(), selectedUser.getId());
            ServerManager.serverRequest(call, result -> removeMonitoree(result, selectedUser), Monitor.this::error);
        });


        monitoredByAdapter = new ListItemAdapter<>(this, user.getMonitoredByUsers());

        ListView monitoredBy = findViewById(R.id.monitoredByListView);
        monitoredBy.setAdapter(monitoredByAdapter);
        monitoredBy.setOnItemClickListener((adapterView, view, i, l) -> {
            User selectedMonitor = user.getMonitoredByUser(i);

            ServerProxy proxy = ServerManager.getServerRequest();
            Call<Void> call = proxy.endMonitoring(selectedMonitor.getId(), user.getId());
            ServerManager.serverRequest(call, result -> removeMonitor(result, selectedMonitor), Monitor.this::error);
        });
    }

    private void removeMonitoree(Void ans, User user) {
        this.user.getMonitorsUsers().remove(user);
        updateListAdapter(R.id.monitoringListView, user, monitorsAdapter);
    }

    private void removeMonitor(Void ans, User user) {
        this.user.getMonitoredByUsers().remove(user);
        updateListAdapter(R.id.monitoredByListView, user, monitoredByAdapter);
    }

    private void updateListAdapter(int id, User user, ArrayAdapter<User> adapter) {
        adapter.remove(user);
        adapter.notifyDataSetChanged();
        findViewById(id).invalidate();
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
                        Log.i("CHECK", getEmail);

                        MonitorStrategy strategy = new MonitorStrategy(user, getEmail, Monitor.this::error);
                        strategy.makeServerRequest();
                        strategy.addObserver((observable, o) -> {
                            MonitorStrategy strategy1 = (MonitorStrategy)observable;
                            List<User> users = strategy1.getServerResult();

                            user.setMonitorsUsers(users);
                            monitorsAdapter.clear();
                            monitorsAdapter.addAll(users);
                            monitorsAdapter.notifyDataSetChanged();
                        });
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

                        MonitoredByStrategy strategy = new MonitoredByStrategy(user, getEmail, Monitor.this::error);
                        strategy.makeServerRequest();
                        strategy.addObserver((observable, o) -> {
                            MonitoredByStrategy strategy1 = (MonitoredByStrategy)observable;
                            List<User> users = strategy1.getServerResult();

                            user.setMonitorsUsers(users);
                            monitoredByAdapter.clear();
                            monitoredByAdapter.addAll(users);
                            monitoredByAdapter.notifyDataSetChanged();
                        });
                    }
                });
                adb.show();
            }
        });
    }

    private void login(Void ans) {
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<User> userByEmailCall = proxy.getUserByEmail(user.getEmail());
        ServerManager.serverRequest(userByEmailCall, this::getUser, this::error);

    }

    private void getUser(User user) {
        this.user = user;
        ModelFacade.getInstance().setCurrentUser(user);
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<User>> call = proxy.getMonitors(user.getId());
        ServerManager.serverRequest(call, this::monitors, this::error);
    }

    private void monitors(List<User> users) {
        user.setMonitorsUsers(users);

        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<User>> call = proxy.getMonitoredBy(user.getId());
        ServerManager.serverRequest(call, this::monitoredBy, this::error);
    }

    private void monitoredBy(List<User> users) {
        user.setMonitoredByUsers(users);
        initializeListViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, Monitor.class);
    }
}


