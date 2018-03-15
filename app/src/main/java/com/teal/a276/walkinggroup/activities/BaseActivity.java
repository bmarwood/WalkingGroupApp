package com.teal.a276.walkinggroup.activities;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.teal.a276.walkinggroup.R;

/**
 * Class that other activities derive off of.
 * Has Shared Error Handling
 */

abstract class BaseActivity extends AppCompatActivity {

    protected void error(String error) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setPositiveButton(R.string.ok, null);
        alertDialogBuilder.setTitle(R.string.error);
        alertDialogBuilder.setMessage(error);

        alertDialogBuilder.show();
    }
}
