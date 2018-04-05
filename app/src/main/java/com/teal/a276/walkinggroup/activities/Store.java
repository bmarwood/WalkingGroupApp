package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.teal.a276.walkinggroup.R;

public class Store extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
    }



    public static Intent makeIntent(Context context) {
        return new Intent(context, Store.class);
    }
}
