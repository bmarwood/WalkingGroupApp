package com.teal.a276.walkinggroup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.teal.a276.walkinggroup.R;

import android.view.MenuItem;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;

public class Welcome extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapItem:
                startActivity(MapsActivity.makeIntent(Welcome.this));
                break;
            case R.id.groupItem:
                Toast.makeText(this, "Clicked group item!", Toast.LENGTH_LONG).show();
                break;
            case R.id.monitorItem:
                Toast.makeText(this, "Clicked monitor item!", Toast.LENGTH_LONG).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
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
