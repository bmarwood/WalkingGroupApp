package com.teal.a276.walkinggroup.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.models.ServerManager;

import java.net.MalformedURLException;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ServerManager test = null;
        try {
            test = new ServerManager();
        } catch(MalformedURLException e) {
            Toast.makeText(this, "Error connecting to server: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
