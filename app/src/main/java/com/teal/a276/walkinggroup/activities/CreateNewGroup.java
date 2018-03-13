package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.User;


public class CreateNewGroup extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);


        setupAddGroupButton();
    }

    private void setupAddGroupButton() {
        Button btn = findViewById(R.id.meetingMapBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameVal = findViewById(R.id.createGroupNameEdit);
                String nameValStr = nameVal.getText().toString();

                EditText leadersEmailInput = findViewById(R.id.createGroupEmailEdit);
                String leadersEmail = leadersEmailInput.getText().toString();

                //TODO: interact with server


            }
        });

    }

    private void userFromEmail(User user, String groupDes){
        Group group = new Group();
        group.setLeader(user);
        group.setId(-1L);
        group.setGroupDescription(groupDes);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, CreateNewGroup.class);
    }
}
