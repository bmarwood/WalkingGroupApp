package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.teal.a276.walkinggroup.R;

public class AddNewGroup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_group);

        setupAddGroupButton();
    }

    private void setupAddGroupButton() {
         Button btn = (Button) findViewById(R.id.addGroupBtn);
         btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 //TODO: interact with server (add new group, with this user as one member)




                 finish();
             }
         });




    }


    public static Intent makeIntent(Context context) {
        return new Intent(context, AddNewGroup.class);
    }
}