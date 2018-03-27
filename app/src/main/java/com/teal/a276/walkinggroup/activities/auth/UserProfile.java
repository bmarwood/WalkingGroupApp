package com.teal.a276.walkinggroup.activities.auth;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.GroupManager;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverrequest.requestimplementation.CompleteUserRequest;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;

public class UserProfile extends AuthenticationActivity {
    DateFormat formatDate = DateFormat.getDateInstance();
    Calendar dateTime = Calendar.getInstance();
    private Button btn_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ModelFacade model = ModelFacade.getInstance();
        user = model.getCurrentUser();

        fillKnownInfo();
        setupBirthdayBtn();
        setUpSaveButton();
    }

    private void fillKnownInfo() {
        EditText nameInput = findViewById(R.id.editName);
        EditText addressInput = findViewById(R.id.editAddress);
        EditText homePhoneInput = findViewById(R.id.editHome);
        homePhoneInput.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        EditText cellPhoneInput = findViewById(R.id.editCell);
        cellPhoneInput.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        EditText emailInput = findViewById(R.id.editEmail);
        EditText gradeInput = findViewById(R.id.editGrade);
        EditText teachersNameInput = findViewById(R.id.editTeacherName);
        EditText contactInfoInput = findViewById(R.id.editContactInfo);


        if (!(user.getName() == null)) {

            nameInput.setText(user.getName(), TextView.BufferType.EDITABLE);
        }
        if (!(user.getAddress() == null)) {
            addressInput.setText(user.getAddress(), TextView.BufferType.EDITABLE);
        }
        if (!(user.getHomePhone() == null)) {
            homePhoneInput.setText(user.getHomePhone(), TextView.BufferType.EDITABLE);
        }
        if (!(user.getCellPhone() == null)) {
            cellPhoneInput.setText(user.getCellPhone(), TextView.BufferType.EDITABLE);
        }
        if (!(user.getEmail() == null)) {
            emailInput.setText(user.getEmail(), TextView.BufferType.EDITABLE);
        }
        if (!(user.getGrade() == null)) {
            gradeInput.setText(user.getGrade(), TextView.BufferType.EDITABLE);
        }
        if (!(user.getTeacherName() == null)) {
            teachersNameInput.setText(user.getTeacherName(), TextView.BufferType.EDITABLE);
        }
        if (!(user.getEmergencyContactInfo() == null)) {
            contactInfoInput.setText(user.getEmergencyContactInfo(), TextView.BufferType.EDITABLE);
        }
        if (!(user.getBirthYear() == 0)) {
            dateTime.set(user.getBirthYear(), user.getBirthMonth(), Calendar.DATE);

        }else {

            dateTime.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DATE));
        }

    }

    private void setUpSaveButton() {
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


            if (!hasValidProfileInfo(nameInput, addressInput, homePhoneInput, cellPhoneInput, emailInput)) {
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

            if(!User.validateEmail(email)){
                emailInput.setError("Email address is invalid");
                toggleSpinner(View.INVISIBLE);
                return;
            }
            if (!User.validatePhoneNumber(homePhone)) {
                homePhoneInput.setError("Phone is invalid");
                toggleSpinner(View.INVISIBLE);
                return;
            }
            if (!User.validatePhoneNumber(cellPhone)) {
                cellPhoneInput.setError("Phone is invalid");
                toggleSpinner(View.INVISIBLE);
                return;
            }

            user.setBirthMonth(dateTime.get(Calendar.MONTH));
            user.setBirthYear(dateTime.get(Calendar.YEAR));
            user.setName(name);
            user.setAddress(address);
            user.setHomePhone(homePhone);
            user.setCellPhone(cellPhone);
            user.setEmail(email);
            user.setGrade(grade);
            user.setTeacherName(teachersName);
            user.setEmergencyContactInfo(contactInfo);

            //update shared Prefs
            SharedPreferences prefs = getSharedPreferences(sharePrefLogger, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(sharePrefUser, email);
            editor.apply();

            ServerProxy proxy = ServerManager.getServerRequest();
            Call<User> caller = proxy.updateUser(user.getId(), user);
            ServerManager.serverRequest(caller, result -> this.successfulSave(),
                    UserProfile.this::authError);
        });
    }


    private void setupBirthdayBtn() {
        btn_date = findViewById(R.id.btn_datePicker);
        btn_date.setOnClickListener(v -> updateDate());

        btn_date.setText(formatDate.format(dateTime.getTime()));

    }

    private void updateDate() {
        new DatePickerDialog(this, datePicker, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
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