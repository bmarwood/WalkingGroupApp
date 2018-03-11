package com.teal.a276.walkinggroup.activities;

import android.os.Bundle;
import com.teal.a276.walkinggroup.R;

import android.view.MenuItem;

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
                startActivity(MapsActivity.makeIntent(this));
                break;
            case R.id.groupItem:
                startActivity(JoinGroup.makeIntent(this));
                break;
            case R.id.monitorItem:
                startActivity(Monitor.makeIntent(this));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
