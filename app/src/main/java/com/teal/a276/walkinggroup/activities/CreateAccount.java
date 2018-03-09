package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.teal.a276.walkinggroup.ServerProxy.ServerManager;
import com.teal.a276.walkinggroup.ServerProxy.ServerProxy;
import com.teal.a276.walkinggroup.ServerProxy.ServerResult;
import com.teal.a276.walkinggroup.dataobjects.User;
import com.teal.a276.walkinggroup.models.UserModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;

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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setVisibility(View.VISIBLE);
                    }
                });

                ArrayList<String> errors =  checkCreateInputs(firstName, lastName, email, password);
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
                    errorsForUser.setTextColor(Color.RED);
                }else{

                    User user = new User();
                    user.setAccountInfo(firstName + " " + lastName, email, password);

                    ServerProxy proxy = ServerManager.getServerRequest();
                    Call<User> caller = proxy.createNewUser(user);
                    ServerManager.serverRequest(caller, new ServerResult<User>() {
                        @Override
                        public void result(User user) {
                            Intent intent = MapsActivity.makeIntent(CreateAccount.this);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void error(String error) {
                            Toast.makeText(CreateAccount.this, error, Toast.LENGTH_LONG).show();
                        }
                    });

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


    public ArrayList<String> checkCreateInputs(String firstName, String lastName, String email, String password) {
        ArrayList<String> returnErrorMessages = new ArrayList<>();

        if (firstName.length() == 0) {
            returnErrorMessages.add("First Name must be inputted");
        }
        if (lastName.length() == 0) {
            returnErrorMessages.add("Last Name must be inputted");
        }
        if (email.length() == 0) {
            returnErrorMessages.add("Email must be inputted");
        }
        if(password.length() == 0) {
            returnErrorMessages.add("password must be inputted");
        }
        if(email.length() > 0) {
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher m = p.matcher(email);
            if (! m.matches()) {
                returnErrorMessages.add("Email must be formatted correctly");
            }
        }
        return returnErrorMessages;
    }
}
