package com.teal.a276.walkinggroup.models;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model holds Current User info and communicated with server manager
 */

public class UserModel {
    private  long id;
    private static String name;
    private static String email;
    private static String password;
    public ArrayList<Object> memberOfGroups;
    public ArrayList<Object> leadsGroups;

//    public static boolean checkInputs(String email, String password) {
//
//        return email.equals(UserModel.email) && password.equals(UserModel.password);
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UserModel.email = email;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        UserModel.password = password;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        UserModel.name = name;
    }

    public static ArrayList<String> checkCreateInputs(String firstName, String lastName, String email, String password) {
        ArrayList<String> returnErrorMessages = new ArrayList<>();

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


    public static void setAccountInfo(String name, String email, String password) {
        setEmail(email);
        setPassword(password);
        setName(name);
    }
    //TODO: interact with server to check if email and string are correct


}
