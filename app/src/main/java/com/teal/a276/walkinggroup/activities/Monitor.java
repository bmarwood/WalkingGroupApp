package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.models.User;

public class Monitor extends AppCompatActivity {

    //use singleton, change later on
    User user = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        populateMonitoredByListView();
        populateMonitoringListView();
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

    private void populateMonitoringListView() {
        String[] monitoringItems = user.getMonitoringUsersDescriptions();

        //ARRAY ADAPTER
        ArrayAdapter<String> monitoringAdapter = new ArrayAdapter<String>(
                this,
                R.layout.monitoring,
                monitoringItems);

            ListView list = (ListView) findViewById(R.id.monitoringListView);
            list.setAdapter(monitoringAdapter);
   }



    public static Intent makeIntent(Context context){
        return new Intent(context, Monitor.class);
    }
}


