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

    protected void error(String error) {
        Log.e("Login Error", error);
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}
