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

import retrofit2.Call;

public class Login extends AppCompatActivity {

    private static final int VIEW_VISIBLE = -1;
    private static final int VIEW_INVISIBLE = -2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView errorsForUser = (TextView) findViewById(R.id.errorInput);
        errorsForUser.setTextColor(Color.RED);
        setUpLoginButton();
        setupCreateAccountButton();
    }

    private void setUpLoginButton() {
        Button btn = (Button) findViewById(R.id.signInBtn);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                TextView errorsForUser = (TextView) findViewById(R.id.errorInput);
                errorsForUser.setVisibility(View.INVISIBLE);
                String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
                String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();

                toggleSpinner(VIEW_VISIBLE);


                User user = new User();
                user.setEmail(email);
                user.setPassword(password);

                ServerProxy proxy = ServerManager.getServerRequest();
                Call<Void> caller = proxy.login(user);
                ServerManager.serverRequest(caller, new ServerResult<Void>() {
                    @Override
                    public void result(Void ans) {
                        Intent intent = MapsActivity.makeIntent(Login.this);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void error(String error) {
                        final ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBar);
                        errorsForUser.setVisibility(View.VISIBLE);
                        toggleSpinner(VIEW_INVISIBLE);
                    }
                });
            }
        });
    }

    private void toggleSpinner(int view) {
        final ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBar);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(view == VIEW_VISIBLE){
                    spinner.setVisibility(View.VISIBLE);
                }
                else if(view == VIEW_INVISIBLE){
                    spinner.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setupCreateAccountButton() {
        Button btn = (Button) findViewById(R.id.createAccntBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CreateAccount.makeIntent(Login.this);
                startActivity(intent);
                finish();
            }
        });
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, Login.class);
    }

}
