package com.teal.a276.walkinggroup.models;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;

/**
 * Created by Brian on 3/5/2018.
 */

public class UserModel {
    private static String userEmail = "1";
    private static String userPassword = "2";
    public static boolean checkInputs(String email, String password) {
        if(email.equals(userEmail) && password.equals(userPassword)) {
            return true;
        }
        else return false;
    }
    //TODO: interact with server to check if email and string are correct

}
