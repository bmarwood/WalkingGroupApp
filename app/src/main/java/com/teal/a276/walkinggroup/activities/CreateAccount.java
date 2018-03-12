package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.ServerProxy.ServerManager;
import com.teal.a276.walkinggroup.ServerProxy.ServerProxy;
import com.teal.a276.walkinggroup.ServerProxy.ServerResult;
import com.teal.a276.walkinggroup.dataobjects.User;


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

        btn.setOnClickListener(v -> {
            String firstName = ((EditText) findViewById(R.id.firstName)).getText().toString();
            String lastName = ((EditText) findViewById(R.id.lastName)).getText().toString();
            String email = ((EditText) findViewById(R.id.email)).getText().toString();
            String password = ((EditText) findViewById(R.id.password)).getText().toString();

            toggleSpinner(View.VISIBLE);

            ArrayList<String> errors =  checkCreateInputs(firstName, lastName, email, password);


            if(!errors.isEmpty()) {
                ErrorStringGen(errors);
                return;
            }

                User user = new User(firstName + " " + lastName, email, password);

                ServerProxy proxy = ServerManager.getServerRequest();
                Call<User> caller = proxy.createNewUser(user);
                ServerManager.serverRequest(caller, new ServerResult<User>() {
                    @Override
                    public void result(User user) {
                        Intent intent = MapsActivity.makeIntent(CreateAccount.this);

                        toggleSpinner(View.INVISIBLE);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void error(String error) {
                        TextView errorsForUser = (TextView) findViewById(R.id.badEmailOrPassword);

                        errorsForUser.setText(R.string.email_in_use);
                        errorsForUser.setVisibility(View.VISIBLE);
                        errorsForUser.setTextColor(Color.RED);

                        toggleSpinner(View.INVISIBLE);
                    }
                });

        });
    }

    private void ErrorStringGen(ArrayList<String> errors) {
        Iterator<String> foreach = errors.iterator();
        StringBuilder stringForTextView = new StringBuilder();
        while (foreach.hasNext()) {
            stringForTextView.append(foreach.next()).append("\n");
        }
        TextView errorsForUser = (TextView) findViewById(R.id.badEmailOrPassword);
        errorsForUser.setText(stringForTextView.toString());
        errorsForUser.setVisibility(View.VISIBLE);
        errorsForUser.setTextColor(Color.RED);
        toggleSpinner(View.INVISIBLE);
    }

    private void toggleSpinner(int visibility) {
        final ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBarCreate);
        runOnUiThread(() -> {
            spinner.setVisibility(visibility);
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, CreateAccount.class);
    }


    public ArrayList<String> checkCreateInputs(String firstName, String lastName, String email, String password) {
        ArrayList<String> returnErrorMessages = new ArrayList<>();

        if (firstName.isEmpty()) {
            returnErrorMessages.add(getString(R.string.first_name_must_be_inputted));
        }
        if (lastName.isEmpty()) {
            returnErrorMessages.add(getString(R.string.last_name_must_be_inputted));
        }
        if (email.isEmpty()) {
            returnErrorMessages.add(getString(R.string.email_must_be_inputted));
        }
        if(password.isEmpty()) {
            returnErrorMessages.add(getString(R.string.password_must_be_inputted));
        }
        if(!email.isEmpty()) {
            Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(email);
            if (! m.matches()) {
                returnErrorMessages.add("Email must be formatted correctly");
            }
        }

        return returnErrorMessages;
    }
}
