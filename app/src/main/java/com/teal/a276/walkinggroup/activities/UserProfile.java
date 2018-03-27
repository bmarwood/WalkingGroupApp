package com.teal.a276.walkinggroup.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.auth.AuthenticationActivity;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.GroupManager;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverrequest.requestimplementation.CompleteUserRequest;

import java.text.DateFormat;
import java.util.Calendar;

import retrofit2.Call;

public class UserProfile extends AuthenticationActivity {
    private DateFormat formatDate = DateFormat.getDateInstance();
    private Calendar dateTime = Calendar.getInstance();
    private Button btnDate;
    private User user;
    private DatePickerDialog.OnDateSetListener datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ModelFacade model = ModelFacade.getInstance();
        user = model.getCurrentUser();

        fillKnownInfo();
        datePicker = (view, year, monthOfYear, dayOfMonth) -> {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            btnDate.setText(formatDate.format(dateTime.getTime()));
        };

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
        Button btn = findViewById(R.id.saveBtn);
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
                emailInput.setError(getString(R.string.email_invalid));
                toggleSpinner(View.INVISIBLE);
                return;
            }
            if (!User.validatePhoneNumber(homePhone)) {
                homePhoneInput.setError(getString(R.string.phone_invalid));
                toggleSpinner(View.INVISIBLE);
                return;
            }
            if (!User.validatePhoneNumber(cellPhone)) {
                cellPhoneInput.setError(getString(R.string.phone_invalid));
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


            ServerProxy proxy = ServerManager.getServerRequest();
            Call<User> caller = proxy.updateUser(user.getId(), user);
            ServerManager.serverRequest(caller, result -> this.successfulSave(),
                    UserProfile.this::authError);

            //update shared Prefs
            SharedPreferences prefs = getSharedPreferences(sharePrefLogger, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(sharePrefUser, email);
            editor.apply();
        });
    }


    private void setupBirthdayBtn() {
        btnDate = findViewById(R.id.btn_datePicker);
        btnDate.setOnClickListener(v -> updateDate());

        btnDate.setText(formatDate.format(dateTime.getTime()));

    }

    private void updateDate() {
        new DatePickerDialog(this, datePicker, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    boolean hasValidProfileInfo(EditText name, EditText address, EditText homePhone, EditText cellPhone,
                                EditText email){
        boolean validInputs = true;

        if(isEmpty(name)){
            name.setError("name is empty");
            validInputs = false;
        }
        if (isEmpty(address)){
            address.setError("address is empty");
            validInputs = false;
        }
        if (isEmpty(homePhone)){
            homePhone.setError("homePhone is empty");
            validInputs = false;
        }
        if (isEmpty(cellPhone)) {
            cellPhone.setError("cellPhone is empty");
            validInputs = false;
        }
        if (isEmpty(email)) {
            email.setError("email is empty");
            validInputs = false;
        }
        return validInputs;

    }

    private boolean isEmpty(EditText name) {
        return name.getText().toString().isEmpty();
    }
    void authError(String error) {
        toggleSpinner(View.INVISIBLE);
        super.error(error);
    }

    void successfulSave(){
        CompleteUserRequest request = new CompleteUserRequest(user, this::error);
        request.makeServerRequest();
        request.addObserver((observable, o) -> {
            ModelFacade.getInstance().setCurrentUser((User) o);
            ModelFacade.getInstance().setGroupManager(new GroupManager());

            finish();
        });

    }
    void toggleSpinner(int visibility) {
        final ProgressBar spinner = findViewById(R.id.authenticationProgress);
        runOnUiThread(() -> spinner.setVisibility(visibility));
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, UserProfile.class);
    }


}