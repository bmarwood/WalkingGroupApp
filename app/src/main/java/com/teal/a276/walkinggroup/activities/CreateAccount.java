package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.Iterator;

import retrofit2.Call;

/**
 * Creates User Account and sends information to the server
 */

public class CreateAccount extends AppCompatActivity {

    private static final String sharePrefLogger = "Logger";
    private static final String sharePrefUser = "userName";
    private static final String sharePrefPassword = "password";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setupCreateButton();
    }

    private void setupCreateButton() {
        Button btn =  findViewById(R.id.makeAccountBtn);

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
                ServerManager.serverRequest(caller, result -> successfulResult(result, password),
                        CreateAccount.this::errorCreateAccount);
        });
    }

    private void successfulResult(User user, String password) {
        user.setPassword(password);

        storeLogin();
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<Void> caller = proxy.login(user);
        ServerManager.serverRequest(caller, CreateAccount.this::successfulLogin,
                CreateAccount.this::errorCreateAccount);
    }

    private void storeLogin() {
        EditText emailInput = findViewById(R.id.email);
        EditText passwordInput = findViewById(R.id.password);

        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        SharedPreferences prefs = getSharedPreferences(sharePrefLogger, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(sharePrefUser, email);
        editor.putString(sharePrefPassword, password);
        editor.apply();
    }

    private void successfulLogin(Void ans) {
        Intent intent = MapsActivity.makeIntent(CreateAccount.this);
        toggleSpinner(View.INVISIBLE);
        startActivity(intent);
        finish();
    }

    private void errorCreateAccount(String error) {
        TextView errorsForUser = findViewById(R.id.badEmailOrPassword);

        errorsForUser.setText(R.string.email_in_use);
        errorsForUser.setVisibility(View.VISIBLE);
        errorsForUser.setTextColor(Color.RED);

        toggleSpinner(View.INVISIBLE);
    }

    private void ErrorStringGen(ArrayList<String> errors) {
        Iterator<String> foreach = errors.iterator();
        StringBuilder stringForTextView = new StringBuilder();
        while (foreach.hasNext()) {
            stringForTextView.append(foreach.next()).append("\n");
        }
        TextView errorsForUser = findViewById(R.id.badEmailOrPassword);
        errorsForUser.setText(stringForTextView.toString());
        errorsForUser.setVisibility(View.VISIBLE);
        errorsForUser.setTextColor(Color.RED);
        toggleSpinner(View.INVISIBLE);
    }

    private void toggleSpinner(int visibility) {
        final ProgressBar spinner =  findViewById(R.id.progressBarCreate);
        runOnUiThread(() -> spinner.setVisibility(visibility));
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
        if(!email.isEmpty() && !User.validateEmail(email)) {
            returnErrorMessages.add(getString(R.string.invalid_email));
        }

        return returnErrorMessages;
    }
}
