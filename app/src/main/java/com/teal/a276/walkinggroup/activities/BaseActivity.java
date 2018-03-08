package com.teal.a276.walkinggroup.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.teal.a276.walkinggroup.BuildConfig;
import com.teal.a276.walkinggroup.R;

abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapItem:
                startActivity(new Intent(this, MapsActivity.class));
                break;
            case R.id.groupItem:
                Toast.makeText(this, "Clicked group item!", Toast.LENGTH_LONG).show();
                break;
            case R.id.monitorItem:
                Toast.makeText(this, "Clicked monitor item!", Toast.LENGTH_LONG).show();
                break;
            case R.id.logoutItem:
                Toast.makeText(this, "Clicked logout item!", Toast.LENGTH_LONG).show();
                break;
            default:
                if (BuildConfig.DEBUG) {
                    throw new IllegalArgumentException("Unsupported menu item tapped. Missing case from switch?");
                }

                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
