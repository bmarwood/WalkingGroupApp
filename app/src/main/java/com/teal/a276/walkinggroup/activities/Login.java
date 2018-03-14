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
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverrequest.requestimplementation.CompleteUserRequest;

import org.w3c.dom.Text;

import retrofit2.Call;

public class Login extends BaseActivity {
    User user = new User();
    TextView errorsForUser;
    TextView signInError;
    Boolean layoutSwitchForLogin = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkForLogin();

        errorsForUser = findViewById(R.id.errorInput);
        errorsForUser.setTextColor(Color.RED);
        signInError = findViewById(R.id.issueSignInTxt);
        signInError.setTextColor(Color.RED);
        
        setUpPermissions();
        setUpLoginButton();
        setupCreateAccountButton();
    }

    private void checkForLogin() {
        SharedPreferences prefs = getSharedPreferences("loggedIn",MODE_PRIVATE);
        Boolean extractedBool = prefs.getBoolean("Logged in", false);
        layoutSwitchForLogin = extractedBool;
        String userName = prefs.getString("email", null);
        String password = prefs.getString("password", null);
        Toast.makeText(this, extractedBool.toString(), Toast.LENGTH_SHORT).show();

        if(extractedBool){
            LayoutLoggingIn(true);
            user.setEmail(userName);
            user.setPassword(password);
            ServerProxy proxy = ServerManager.getServerRequest();
            Call<Void> caller = proxy.login(user);
            ServerManager.serverRequest(caller, Login.this::successLogin, Login.this::errorLogin);
        }
    }

    private void LayoutLoggingIn(boolean on) {
        TextView txtLogin = findViewById(R.id.loginText);
        TextView txtEmnail = findViewById(R.id.emailTxt);
        TextView txtPassword = findViewById(R.id.passwordTxt);
        TextView txtCreate = findViewById(R.id.createAccountTxt);
        EditText editEmail = findViewById(R.id.emailEditText);
        EditText editPassword = findViewById(R.id.passwordEditText);
        Button createAccountbtn = findViewById(R.id.createAccntBtn);
        Button signInbtn = findViewById(R.id.signInBtn);

        ProgressBar loginSpin = findViewById(R.id.loggingInSpinner);
        TextView txtLoggin = findViewById(R.id.loggingInText);
        int main = 0;
        int opposite = 0;
        if(on) {
            main = View.INVISIBLE;
            opposite = View.VISIBLE;
        }else{
            main = View.VISIBLE;
            opposite = View.INVISIBLE;
        }
            txtLogin.setVisibility(main);
            txtEmnail.setVisibility(main);
            txtPassword.setVisibility(main);
            txtCreate.setVisibility(main);
            editEmail.setVisibility(main);
            editPassword.setVisibility(main);
            createAccountbtn.setVisibility(main);
            signInbtn.setVisibility(main);

            loginSpin.setVisibility(opposite);
            txtLoggin.setVisibility(opposite);

    }

    private void setUpPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void setUpLoginButton() {
        if(layoutSwitchForLogin){
            TextView signinError = findViewById(R.id.issueSignInTxt);
            signinError.setVisibility(View.INVISIBLE);
        }
        Button btn = findViewById(R.id.signInBtn);
        btn.setOnClickListener(v -> {
            EditText emailInput = findViewById(R.id.emailEditText);
            EditText passwordInput = findViewById(R.id.passwordEditText);

            TextView errorsForUser = findViewById(R.id.errorInput);
            errorsForUser.setVisibility(View.INVISIBLE);

            if(!validInput(emailInput, passwordInput)) {
                return;
            }

            toggleSpinner(View.VISIBLE);
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            user.setEmail(email);
            user.setPassword(password);

            ServerProxy proxy = ServerManager.getServerRequest();
            Call<Void> caller = proxy.login(user);
            ServerManager.serverRequest(caller, Login.this::successLogin, Login.this::errorLogin);
        });
    }

    private boolean validInput(EditText emailInput, EditText passwordInput) {
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

    private void successLogin(Void ans) {
        CompleteUserRequest request = new CompleteUserRequest(user, this::error);
        request.makeServerRequest();
        request.addObserver((observable, o) -> {
            ModelFacade.getInstance().setCurrentUser((User)o);

            storeLogin();

            Intent intent = MapsActivity.makeIntent(Login.this);
            startActivity(intent);
            finish();
        });
    }

    private void storeLogin() {
        EditText emailInput = findViewById(R.id.emailEditText);
        EditText passwordInput = findViewById(R.id.passwordEditText);

        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        SharedPreferences prefs = getSharedPreferences("loggedIn",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("Logged in", true);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.commit();
    }

    private void errorLogin(String error) {
        if(layoutSwitchForLogin){
            LayoutLoggingIn(false);
            TextView signinError = findViewById(R.id.issueSignInTxt);
            signinError.setVisibility(View.VISIBLE);
            layoutSwitchForLogin = false;
        }
        else {
            TextView errorsForUser = findViewById(R.id.errorInput);
            errorsForUser.setVisibility(View.VISIBLE);
            toggleSpinner(View.INVISIBLE);
        }
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

    public static Intent makeIntent(Context context){
        return new Intent(context, Login.class);
    }
}
