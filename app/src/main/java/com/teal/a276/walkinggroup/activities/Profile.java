package com.teal.a276.walkinggroup.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //TODO: get user profile
        setTextViewText(R.id.username, "PlaceholderName");
        setTextViewText(R.id.email, "email@placeholder.com");
    }

    private void setTextViewText(int id, String text) {
        TextView textView = findViewById(id);
        textView.setText(text);
    }

    public void logout(View view) {
        //TODO: write empty string to shared preference. Pop all views off the stack.
    }
}
