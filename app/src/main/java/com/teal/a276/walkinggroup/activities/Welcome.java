package com.teal.a276.walkinggroup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.teal.a276.walkinggroup.R;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        setupTempButton();
    }

    private void setupTempButton() {
        Button btn = (Button) findViewById(R.id.tempButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Monitor.makeIntent(Welcome.this);
                startActivity(intent);
            }
        });
    }

}
