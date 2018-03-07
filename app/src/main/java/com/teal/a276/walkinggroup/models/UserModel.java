package com.teal.a276.walkinggroup.models;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Brian on 3/5/2018.
 */

public class UserModel {
    private static String userEmail = "1";
    private static String userPassword = "2";

    public static boolean checkInputs(String email, String password) {
        if (email.equals(userEmail) && password.equals(userPassword)) {
            return true;
        } else return false;
    }

    public static ArrayList<String> checkCreateInputs(String firstName, String lastName, String email, String password) {
        ArrayList<String> returnErrorMessages = new ArrayList<String>();

        if (firstName.length() == 0) {
            returnErrorMessages.add("First Name must be inputted");
        }
        if (lastName.length() == 0) {
            returnErrorMessages.add("Last Name must be inputted");

        }
        if (email.length() == 0) {
            returnErrorMessages.add("Email must be inputted");

        }
        if(password.length() == 0) {
            returnErrorMessages.add("password must be inputted");

        }
        if(email.length() > 0) {
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher m = p.matcher(email);
            if (! m.matches()) {
                returnErrorMessages.add("Email must be formatted correctly");
            }
        }
        return returnErrorMessages;
    }
    //TODO: interact with server to check if email and string are correct


}
