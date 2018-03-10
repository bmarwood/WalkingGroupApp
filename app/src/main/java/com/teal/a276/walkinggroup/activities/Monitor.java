package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.List;

import retrofit2.Call;


public class Monitor extends BaseActivity {

    //use singleton, change later on
    User user = new User();
    ArrayAdapter<User> monitorsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        populateMonitoredByListView();
        initializeListViews(R.id.monitoringListView);
        setupAddToMonitorButton();
        setupAddToMonitoredByButton();
        registerMonitoredByClickCallback();
        registerMonitoringClickCallback();
    }

    private void initializeListViews(int id) {
        monitorsAdapter = new MonitorListAdapter();
        ListView list = findViewById(id);
        list.setAdapter(monitorsAdapter);
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

                        User test = new User();
                        test.setEmail("c@test.com");
                        test.setPassword("1234");

                        ServerProxy proxy = ServerManager.getServerRequest();
                        Call<Void> call = proxy.login(test);
                        ServerManager.serverRequest(call, Monitor.this::login, Monitor.this::error);

                        String getEmail = input.getText().toString();
                        user.setEmail(getEmail);
                        Log.i("CHECK", getEmail);

                    }
                });
                adb.show();
            }
        });
    }


    private void login(Void ans) {
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<User> userByEmailCall = proxy.getUserByEmail(user.getEmail());
        ServerManager.serverRequest(userByEmailCall, Monitor.this::monitor, Monitor.this::error);

    }

    private void monitor(User user) {
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<User>> call = proxy.monitorUser(164L, user);
        ServerManager.serverRequest(call, this::newMonitorees, this::error);
    }

    private void newMonitorees(List<User> users) {
        user.setMonitorsUsers(users);
        monitorsAdapter.addAll(users);
        monitorsAdapter.notifyDataSetChanged();

        Log.d("Num of users monitored", "" + users.size());
        for (int i = 0; i < users.size(); i++) {
            Log.d("User " + i, users.get(i).toString());
        }
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







                    }
                });
                adb.show();
            }
        });
    }




    private void populateMonitoredByListView() {
        String[] monitoredByItems = user.getMonitoredByUsersDescriptions();

       //ARRAY ADAPTER
        ArrayAdapter<String> monitoredByAdapter = new ArrayAdapter<String>(
                this,
                R.layout.monitoredby,
                monitoredByItems);

        ListView list = (ListView) findViewById(R.id.monitoredByListView);
        list.setAdapter(monitoredByAdapter);
   }




    private void registerMonitoredByClickCallback(){
        ListView list = (ListView) findViewById(R.id.monitoredByListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //TODO: Ask server to remove users that monitor current user by clicking on them

                //User user = user.getMonitoredBy(position);




            }
        });
    }


    private void registerMonitoringClickCallback(){
        ListView list = (ListView) findViewById(R.id.monitoringListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //TODO: Ask Server to remove users that are monitored by the current user







            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }



    public static Intent makeIntent(Context context){
        return new Intent(context, Monitor.class);
    }

    private class MonitorListAdapter extends ArrayAdapter<User> {
        MonitorListAdapter() {
            super(Monitor.this, R.layout.list_item, user.getMonitorsUsers());
        }

        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            }

            User currentUser = user.getMonitorsUser(position);
            TextView userText = itemView.findViewById(R.id.userName);
            userText.setText(currentUser.getEmail());

            ImageView removeUser = itemView.findViewById(R.id.removeUser);
            removeUser.setOnClickListener(view -> {
                //TODO: server request here
            });

            return itemView;
        }
    }
}


