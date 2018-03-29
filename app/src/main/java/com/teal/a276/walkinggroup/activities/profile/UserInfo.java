package com.teal.a276.walkinggroup.activities.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.User;

public class UserInfo extends UserProfile {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
    }

    public static Intent makeIntent(Context context, User user) {
        Gson gson = new Gson();
        Intent intent = new Intent(context, UserInfo.class);
        intent.putExtra("user", gson.toJson(user));
        return intent;
    }
}

