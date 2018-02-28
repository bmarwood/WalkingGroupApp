package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.teal.a276.walkinggroup.R;

public class JoinGroup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_join_group);



        setupCreateNewGroupButton();
    }

    private void setupCreateNewGroupButton() {
        Button btn = (Button) findViewById(R.id.addNewGroupBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddNewGroup.makeIntent(JoinGroup.this);
                startActivity(intent);
            }
        });
    }


    public static Intent makeIntent(Context context){
       return new Intent(context, JoinGroup.class);
    }



}
