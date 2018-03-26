package com.teal.a276.walkinggroup.activities.auth;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.text.DateFormat;
import java.util.Calendar;

import retrofit2.Call;

public class UserProfile extends AuthenticationActivity {
    DateFormat formatDate = DateFormat.getDateInstance();
    Calendar dateTime = Calendar.getInstance();
    private Button btn_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setupBirthdayBtn();
        fillKnownInfo();
        setUpSaveButton();

    }

    private void fillKnownInfo() {
        EditText nameInput = findViewById(R.id.editName);
        EditText addressInput = findViewById(R.id.editAddress);
        EditText homePhoneInput = findViewById(R.id.editHome);
        EditText cellPhoneInput = findViewById(R.id.editCell);
        EditText emailInput = findViewById(R.id.editEmail);
        EditText gradeInput = findViewById(R.id.editGrade);
        EditText teachersNameInput = findViewById(R.id.editTeacherName);
        EditText contactInfoInput = findViewById(R.id.editContactInfo);


        if(!(user.getName() == null)){
            nameInput.setText(user.getName(), TextView.BufferType.EDITABLE);
        }
        if(!(user.getAddress() == null)) {
            addressInput.setText(user.getAddress(), TextView.BufferType.EDITABLE);
        }
        if(!(user.getHomePhone() == null)) {
            homePhoneInput.setText(user.getHomePhone(), TextView.BufferType.EDITABLE);
        }
        if(!(user.getCellPhone() == null)) {
            cellPhoneInput.setText(user.getCellPhone(), TextView.BufferType.EDITABLE);
        }

            emailInput.setText(user.getEmail(), TextView.BufferType.EDITABLE);

        if(!(user.getGrade() == null)) {
            gradeInput.setText(user.getGrade(),TextView.BufferType.EDITABLE);
        }
        if(!(user.getTeacherName() == null)) {
            teachersNameInput.setText(user.getTeacherName(), TextView.BufferType.EDITABLE);
        }
        if(!(user.getEmergencyContactInfo() == null)) {
            contactInfoInput.setText(user.getEmergencyContactInfo(), TextView.BufferType.EDITABLE);
        }



    }

    private void setUpSaveButton(){
        Button btn = findViewById(R.id.save_Btn);
        btn.setOnClickListener((View v) -> {
            EditText nameInput = findViewById(R.id.editName);
            EditText addressInput = findViewById(R.id.editAddress);
            EditText homePhoneInput = findViewById(R.id.editHome);
            EditText cellPhoneInput = findViewById(R.id.editCell);
            EditText emailInput = findViewById(R.id.editEmail);
            EditText gradeInput = findViewById(R.id.editGrade);
            EditText teachersNameInput = findViewById(R.id.editTeacherName);
            EditText contactInfoInput = findViewById(R.id.editContactInfo);

            if (!hasValidProfileInfo(nameInput, addressInput, homePhoneInput, cellPhoneInput, emailInput, gradeInput)){
                return;
            }
            toggleSpinner(View.VISIBLE);




            String name = nameInput.getText().toString();
            String address = addressInput.getText().toString();
            String homePhone = homePhoneInput.getText().toString();
            String cellPhone = cellPhoneInput.getText().toString();
            String email = emailInput.getText().toString();
            String grade = gradeInput.getText().toString();
            String teachersName = teachersNameInput.getText().toString();
            String contactInfo = contactInfoInput.getText().toString();

            user.setName(name);
            user.setAddress(address);
            user.setHomePhone(homePhone);
            user.setCellPhone(cellPhone);
            user.setEmail(email);
            user.setGrade(grade);
            user.setTeacherName(teachersName);
            user.setEmergencyContactInfo(contactInfo);

            ServerProxy proxy = ServerManager.getServerRequest();
            Call<User> caller = proxy.updateUser(user.getId(), user);
            ServerManager.serverRequest(caller, result -> this.successfulSave(),
                    UserProfile.this::authError);
        });
    }

    private void successfulResult(User result) {
    }


    private void setupBirthdayBtn() {
        btn_date = findViewById(R.id.btn_datePicker);
        btn_date.setOnClickListener(v -> updateDate());

        btn_date.setText(formatDate.format(dateTime.getTime()));

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

    public static Intent makeIntent(Context context) {
        return new Intent(context, UserProfile.class);
    }



}