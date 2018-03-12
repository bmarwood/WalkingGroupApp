package com.teal.a276.walkinggroup.activities;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

abstract class BaseActivity extends AppCompatActivity {

    protected void error(String error) {
        Log.e("Login Error", error);
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}
