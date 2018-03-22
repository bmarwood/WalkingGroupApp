package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.map.MapsActivity;

public class GroupMembersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, GroupMembersActivity.class);
    }
}
