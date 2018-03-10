package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        populateMonitoredByListView();
        populateMonitorsListView();
        setupAddToMonitorButton();
        setupAddToMonitoredByButton();
        registerMonitoredByClickCallback();
        registerMonitoringClickCallback();
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

                        getEmail.replaceAll("@", "%40");

                        Log.i("CHECK", getEmail);

                        ServerProxy proxy = ServerManager.getServerRequest();

                        User user = new User();
                        user.setEmail("c@test.com");
                        user.setPassword("1234");


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







    private void populateMonitorsListView() {
        String[] monitoringItems = user.getMonitorsUsersDescriptions();

        //ARRAY ADAPTER
        ArrayAdapter<String> monitoringAdapter = new ArrayAdapter<String>(
                this,
                R.layout.monitoring,
                monitoringItems);

           ListView list = (ListView) findViewById(R.id.monitoringListView);
          list.setAdapter(monitoringAdapter);
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
}


