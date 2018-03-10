package com.teal.a276.walkinggroup.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.teal.a276.walkinggroup.R;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        setupJoinGroupsButton();
        setupMonitoringButton();
        setupMapsButton();
    }

    private void setupMapsButton() {
        Button btn = (Button) findViewById(R.id.mapBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MapsActivity.makeIntent(Welcome.this);
                startActivity(intent);
            }
        });
    }


    private void setupJoinGroupsButton() {
        Button btn = (Button) findViewById(R.id.tempGroupsButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = JoinGroup.makeIntent(Welcome.this);
                startActivity(intent);
            }
        });
    }

    private void setupMonitoringButton() {
        Button btn = (Button) findViewById(R.id.tempMonitorButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Monitor.makeIntent(Welcome.this);
                startActivity(intent);
            }
        });
    }
}
