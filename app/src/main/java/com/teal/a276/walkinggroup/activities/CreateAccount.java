package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.models.UserModel;

import java.util.ArrayList;
import java.util.Iterator;

public class CreateAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setupCreateButton();
    }

    private void setupCreateButton() {
        Button btn = (Button) findViewById(R.id.makeAccountBtn);
        final ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBarCreate);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = ((EditText) findViewById(R.id.firstName)).getText().toString();
                String lastName = ((EditText) findViewById(R.id.lastName)).getText().toString();
                String email = ((EditText) findViewById(R.id.email)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                Toast.makeText(getApplicationContext(),"in",Toast.LENGTH_SHORT).show();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setVisibility(View.VISIBLE);
                    }
                });
                ArrayList<String> errors =  UserModel.checkCreateInputs(firstName, lastName, email, password);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setVisibility(View.INVISIBLE);
                    }
                });

                if(!errors.isEmpty()) {
                    Iterator<String> foreach = errors.iterator();
                    String stringForTextView = "";
                    while (foreach.hasNext()) {
                        stringForTextView += foreach.next() + "\n";
                    }
                    TextView errorsForUser = (TextView) findViewById(R.id.badEmailOrPassword);
                    errorsForUser.setText(stringForTextView);
                    errorsForUser.setVisibility(View.VISIBLE);
                }else{
                    //TODO: call to actually make account
                    Intent intent = MapsActivity.makeIntent(CreateAccount.this);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, CreateAccount.class);
    }

}
