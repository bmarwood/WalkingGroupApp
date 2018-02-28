package com.teal.a276.walkinggroup.models;

/**
 * Created by egg on 2018-02-28.
 */

public class User {

    private int userID;
    private String userName;
    private String userEmail;
    private String userPassword;


    public User(int userID, String userName, String userEmail, String userPassword){
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }


    public String getUserName(){
        return userName;
    }

    public String getUserEmail(){
        return userEmail;
    }








}
