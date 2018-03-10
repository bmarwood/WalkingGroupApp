package com.teal.a276.walkinggroup.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.teal.a276.walkinggroup.BuildConfig;
import com.teal.a276.walkinggroup.R;

abstract class BaseActivity extends AppCompatActivity {

    private boolean viewFinalized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        viewFinalized = true;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    protected void disableMenuItem(MenuItem item) {

        //idea for setting the disabled look from here https://stackoverflow.com/a/19008520/5323434
        if (viewFinalized) {
            SpannableString string = new SpannableString(item.getTitle());
            string.setSpan(new ForegroundColorSpan(Color.GRAY), 0, string.length(), 0);
            item.setTitle(string);

            item.setEnabled(false);
        }
    }

    protected void error(String error) {
        Log.e("Login Error", error);
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}
