package com.teal.a276.walkinggroup.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverrequest.requestimplementation.CompleteUserRequest;

/**
 * Abstract class that encapsulates the shared login code for create account and logging in.
 */

public abstract class AuthenticationActivity extends BaseActivity {
    static final String sharePrefLogger = "Logger";
    static final String sharePrefUser = "userName";
    static final String sharePrefPassword = "password";

    User user = new User();

    void toggleSpinner(int visibility) {
        final ProgressBar spinner = findViewById(R.id.authenticationProgress);
        runOnUiThread(() -> spinner.setVisibility(visibility));
    }

    void successfulLogin(Void ans) {
        CompleteUserRequest request = new CompleteUserRequest(user, this::error);
        request.makeServerRequest();
        request.addObserver((observable, o) -> {
            ModelFacade.getInstance().setCurrentUser((User) o);
            storeLogin();

            Intent intent = MapsActivity.makeIntent(this);
            startActivity(intent);
            finish();
        });
    }

    void storeLogin() {
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

    boolean validInput(EditText emailInput, EditText passwordInput) {
        boolean validInputs = true;

        String password = passwordInput.getText().toString();
        if (password.isEmpty()) {
            passwordInput.setError(getString(R.string.empty_password));
            validInputs = false;
        }

        String email = emailInput.getText().toString();
        if (!User.validateEmail(email)) {
            emailInput.setError(getString(R.string.invalid_email));
            validInputs = false;
        }

        return validInputs;
    }

    void authError(String error) {
        toggleSpinner(View.INVISIBLE);
        super.error(error);
    }

}
