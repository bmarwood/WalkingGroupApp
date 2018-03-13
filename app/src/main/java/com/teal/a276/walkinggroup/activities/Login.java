package com.teal.a276.walkinggroup.activities;

import android.content.Intent;
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

public class Login extends BaseActivity {
    User user = new User();

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView errorsForUser = findViewById(R.id.errorInput);
        errorsForUser.setTextColor(Color.RED);
        setUpPermissions();
        setUpLoginButton();
        setupCreateAccountButton();
    }

    private void setUpPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void setUpLoginButton() {
        Button btn = findViewById(R.id.signInBtn);


        btn.setOnClickListener(v -> {
            TextView errorsForUser = findViewById(R.id.errorInput);
            errorsForUser.setVisibility(View.INVISIBLE);
            String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
            String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();

            toggleSpinner(View.VISIBLE);

            user.setEmail(email);
            user.setPassword(password);

            ServerProxy proxy = ServerManager.getServerRequest();
            Call<Void> caller = proxy.login(user);
            ServerManager.serverRequest(caller, Login.this::successLogin, Login.this::errorLogin);
        });
    }

    private void successLogin(Void ans) {
        CompleteUserRequest request = new CompleteUserRequest(user, this::error);
        request.makeServerRequest();
        request.addObserver((observable, o) -> {
            ModelFacade.getInstance().setCurrentUser((User)o);

            Intent intent = MapsActivity.makeIntent(Login.this);
            startActivity(intent);
            finish();
        });
    }

    private void errorLogin(String error) {
        TextView errorsForUser = findViewById(R.id.errorInput);
        errorsForUser.setVisibility(View.VISIBLE);
        toggleSpinner(View.INVISIBLE);
    }

    private void toggleSpinner(int visibility) {
        final ProgressBar spinner = findViewById(R.id.progressBar);
        runOnUiThread(() -> spinner.setVisibility(visibility));
    }

    private void setupCreateAccountButton() {
        Button btn = findViewById(R.id.createAccntBtn);
        btn.setOnClickListener(v -> {
            Intent intent = CreateAccount.makeIntent(Login.this);
            startActivity(intent);
            finish();
        });
    }
}
