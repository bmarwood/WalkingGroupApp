package com.teal.a276.walkinggroup.activities.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.BaseActivity;
import com.teal.a276.walkinggroup.activities.map.MapsActivity;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.GroupManager;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverrequest.requestimplementation.CompleteUserRequest;

/**
 * Abstract class that encapsulates the shared login code for create account and logging in.
 */

public abstract class AuthenticationActivity extends BaseActivity {
    public static final String sharePrefLogger = "Logger";
    public static final String sharePrefUser = "userName";
    static final String sharePrefPassword = "password";

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ModelFacade.getInstance().setAppResources(getResources());
    }

    void toggleSpinner(int visibility) {
        final ProgressBar spinner = findViewById(R.id.authenticationProgress);
        runOnUiThread(() -> spinner.setVisibility(visibility));
    }

    void successfulLogin(Void ans) {
        CompleteUserRequest request = new CompleteUserRequest(user, this::authError);
        request.makeServerRequest();
        request.addObserver((observable, o) -> {
            ModelFacade.getInstance().setCurrentUser((User) o);
            ModelFacade.getInstance().setGroupManager(new GroupManager());
            storeLogin();
            Intent intent = MapsActivity.makeIntent(this);
            startActivity(intent);

            finish();
        });


    }

    void successfulSave(){
        CompleteUserRequest request = new CompleteUserRequest(user, this::error);
        request.makeServerRequest();
        request.addObserver((observable, o) -> {
            ModelFacade.getInstance().setCurrentUser((User) o);
            ModelFacade.getInstance().setGroupManager(new GroupManager());

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

    boolean hasValidProfileInfo(EditText name, EditText address, EditText homePhone, EditText cellPhone,
                                EditText email, EditText grade){
        boolean validInputs = true;

        if(isEmpty(name)){
            name.setError("name is empty");
            validInputs = false;
        }
        if (isEmpty(address)){
            address.setError("address is empty");
            validInputs = false;
        }
        if (isEmpty(homePhone)){
            homePhone.setError("homePhone is empty");
            validInputs = false;
        }
        if (isEmpty(cellPhone)) {
            cellPhone.setError("cellPhone is empty");
            validInputs = false;
        }
        if (isEmpty(email)) {
            email.setError("email is empty");
            validInputs = false;
        }
        if(isEmpty(grade)) {
            grade.setError("grade is empty");
            validInputs = false;
        }
        return validInputs;

    }
    private boolean isEmpty(EditText name) {
        if(name.getText().toString().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    void authError(String error) {
        toggleSpinner(View.INVISIBLE);
        super.error(error);
    }
}
