package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.models.UserModel;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpLoginButton();
        setupCreateAccountButton();
    }

    private void setUpLoginButton() {
        Button btn = (Button) findViewById(R.id.signInBtn);
        final ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBar);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
                String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setVisibility(View.VISIBLE);
                    }
                });
                Toast.makeText(getApplicationContext(),"password is " + email + password + UserModel.checkInputs(email, password),Toast.LENGTH_SHORT).show();

                if(UserModel.checkInputs(email, password)){
                    Intent intent = MapsActivity.makeIntent(Login.this);
                    startActivity(intent);
                    finish();
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
