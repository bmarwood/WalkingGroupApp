package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverrequest.requestimplementation.CompleteUserRequest;

import retrofit2.Call;


/**
 * Checks Server and validates login information
 */

public class Login extends AuthenticationActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    Boolean loginLayout = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkForLogin();

        setUpPermissions();
        setUpLoginButton();
        setupCreateAccountButton();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, Login.class);
    }


    private void setUpPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void checkForLogin() {
        SharedPreferences prefs = getSharedPreferences(sharePrefLogger, MODE_PRIVATE);
        String userName = prefs.getString(sharePrefUser, null);
        String password = prefs.getString(sharePrefPassword, null);

        if (userName != null) {
            loginLayout = true;
            layoutLoggingIn(true);
            EditText emailInput = findViewById(R.id.emailEditText);
            EditText passwordInput = findViewById(R.id.passwordEditText);
            emailInput.setText(userName, TextView.BufferType.EDITABLE);
            passwordInput.setText(password, TextView.BufferType.EDITABLE);

            user.setEmail(userName);
            user.setPassword(password);
            ServerProxy proxy = ServerManager.getServerRequest();
            Call<Void> caller = proxy.login(user);
            ServerManager.serverRequest(caller, this::successfulLogin, this::errorLogin);
        }
    }

    private void setUpLoginButton() {
        Button btn = findViewById(R.id.signInBtn);
        btn.setOnClickListener(v -> {
            EditText emailInput = findViewById(R.id.emailEditText);
            EditText passwordInput = findViewById(R.id.passwordEditText);

            TextView errorsForUser = findViewById(R.id.errorInput);
            errorsForUser.setVisibility(View.INVISIBLE);
            TextView signInError = findViewById(R.id.issueSignInTxt);
            if (signInError.getVisibility() == View.VISIBLE) {
                signInError.setVisibility(View.INVISIBLE);
            }

            if (!validInput(emailInput, passwordInput)) {
                return;
            }

            toggleSpinner(View.VISIBLE);
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            user.setEmail(email);
            user.setPassword(password);

            ServerProxy proxy = ServerManager.getServerRequest();
            Call<Void> caller = proxy.login(user);
            ServerManager.serverRequest(caller, Login.this::successfulLogin, Login.this::errorLogin);
        });
    }

    private void setupCreateAccountButton() {
        Button btn = findViewById(R.id.createAccntBtn);
        btn.setOnClickListener(v -> {
            Intent intent = CreateAccount.makeIntent(Login.this);
            startActivity(intent);
            finish();
        });
    }

    private void errorLogin(String error) {
        if (loginLayout) {
            layoutLoggingIn(false);
            TextView signInError = findViewById(R.id.issueSignInTxt);
            signInError.setVisibility(View.VISIBLE);
            loginLayout = false;
        } else {
            TextView errorsForUser = findViewById(R.id.errorInput);
            errorsForUser.setVisibility(View.VISIBLE);
            toggleSpinner(View.INVISIBLE);
        }
    }

    private void layoutLoggingIn(boolean currLoggingIn) {
        TextView txtLogin = findViewById(R.id.loginText);
        TextView txtEmail = findViewById(R.id.emailTxt);
        TextView txtPassword = findViewById(R.id.passwordTxt);
        TextView txtCreate = findViewById(R.id.createAccountTxt);
        EditText editEmail = findViewById(R.id.emailEditText);
        EditText editPassword = findViewById(R.id.passwordEditText);
        Button createAccountBtn = findViewById(R.id.createAccntBtn);
        Button signInBtn = findViewById(R.id.signInBtn);

        ProgressBar loginSpin = findViewById(R.id.loggingInSpinner);
        TextView txtLogIn = findViewById(R.id.loggingInText);
        int main;
        int loadingScreen;
        if (currLoggingIn) {
            main = View.INVISIBLE;
            loadingScreen = View.VISIBLE;
        } else {
            main = View.VISIBLE;
            loadingScreen = View.INVISIBLE;
        }
        txtLogin.setVisibility(main);
        txtEmail.setVisibility(main);
        txtPassword.setVisibility(main);
        txtCreate.setVisibility(main);
        editEmail.setVisibility(main);
        editPassword.setVisibility(main);
        createAccountBtn.setVisibility(main);
        signInBtn.setVisibility(main);

        loginSpin.setVisibility(loadingScreen);
        txtLogIn.setVisibility(loadingScreen);
    }
}

