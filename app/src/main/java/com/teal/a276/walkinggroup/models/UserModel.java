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
    private static String id;
    private static String name;
    private static String userEmail = "1";
    private static String userPassword = "2";

    public static boolean checkInputs(String email, String password) {
        if (email.equals(userEmail) && password.equals(userPassword)) {
            return true;
        } else return false;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        UserModel.id = id;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void setUserEmail(String userEmail) {
        UserModel.userEmail = userEmail;
    }

    public static String getUserPassword() {
        return userPassword;
    }

    public static void setUserPassword(String userPassword) {
        UserModel.userPassword = userPassword;
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

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        UserModel.name = name;
    }

    public static void setAccountInfo(String name, String email, String password) {
        setUserEmail(email);
        setUserPassword(password);
        setName(name);
    }
    //TODO: interact with server to check if email and string are correct


}
