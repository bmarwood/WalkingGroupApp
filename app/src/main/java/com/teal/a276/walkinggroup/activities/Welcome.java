package com.teal.a276.walkinggroup.activities;

import android.content.Intent;
import android.os.Bundle;
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
                startActivity(new Intent(this, MapsActivity.class));
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
}
