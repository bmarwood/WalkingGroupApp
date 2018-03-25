package com.teal.a276.walkinggroup.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.auth.AuthenticationActivity;

import java.text.DateFormat;
import java.util.Calendar;

public class UserProfile extends AuthenticationActivity {
    DateFormat formatDate = DateFormat.getDateInstance();
    Calendar dateTime = Calendar.getInstance();
    private Button btn_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setupBirthdayBtn();

    }

    private void setupBirthdayBtn() {
        btn_date = findViewById(R.id.btn_datePicker);
        btn_date.setOnClickListener(v -> updateDate());

        btn_date.setText(formatDate.format(dateTime.getTime()));

    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, UserProfile.class);
    }
    private void updateDate(){
        new DatePickerDialog(this, datePicker, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            btn_date.setText(formatDate.format(dateTime.getTime()));
        }
    };



}