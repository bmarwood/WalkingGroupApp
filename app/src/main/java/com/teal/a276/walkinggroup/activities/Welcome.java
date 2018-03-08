package com.teal.a276.walkinggroup.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.dataobjects.User;
import com.teal.a276.walkinggroup.proxy.AbstractServerManager;
import com.teal.a276.walkinggroup.proxy.ServerManager;
import com.teal.a276.walkinggroup.proxy.ServerResult;

import retrofit2.Call;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        User user = new User();
        user.setEmail("testuser11@test.com");
        user.setName("Test User 11");
        user.setPassword("justtesting");

        // Make call
        AbstractServerManager proxy = ServerManager.getProxy(null);
        Call<User> caller = proxy.createNewUser(user);
        ServerManager.callProxy(caller, new ServerResult<User>() {
            @Override
            public void result(User ans) {
                Toast.makeText(Welcome.this, user.toString(), Toast.LENGTH_LONG).show();
                Log.w("ServerResponse: ", "Server replied with user: " + user.toString());
            }

            @Override
            public void error(String error) {
                Toast.makeText(Welcome.this, error, Toast.LENGTH_LONG).show();
                Log.e("ServerError: ", error);
            }
        });
    }
}
