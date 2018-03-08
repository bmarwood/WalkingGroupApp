package com.teal.a276.walkinggroup.models;

import java.util.ArrayList;
import java.util.List;


public class User {

    private Long id;
    private String name;
    private String email;
    private String password;

    private List<User> monitoredByUsers = new ArrayList<>();
    private List<User> monitoringUsers = new ArrayList<>();
    private List<Void> walkingGroups = new ArrayList<>(); // not yet implemented

    private String href;

    public Long getId(){
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //RETURNS LIST
    public List<User> getMonitoredByUsers() {
        return monitoredByUsers;
    }

    public void setMonitoredByUsers(List<User> monitoredByUsers) {
        this.monitoredByUsers = monitoredByUsers;
    }

    public List<User> getMonitorsUsers() {
        return monitoringUsers;
    }

    public void setMonitorsUsers(List<User> monitorsUsers) {
        this.monitoringUsers = monitorsUsers;
    }

    public List<Void> getWalkingGroups() {
        return walkingGroups;
    }

    public void setWalkingGroups(List<Void> walkingGroups) {
        this.walkingGroups = walkingGroups;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", monitoredByUsers=" + monitoredByUsers +
                ", monitorsUsers=" + monitoringUsers +
                ", walkingGroups=" + walkingGroups +
                '}';
    }

    public int countMonitoredByUsers(){
        return monitoredByUsers.size();
    }
    public int countMonitoringUsers(){
        return monitoringUsers.size();
    }


    //below are added by Jamie

    private void validateIndexWithExceptionMonitoredBy(int index){
        if(index < 0 || index >= countMonitoredByUsers()){
            throw new IllegalArgumentException();
        }
    }

    private void validateIndexWithExceptionMonitoring(int index){
        if(index < 0 || index >= countMonitoringUsers()){
            throw new IllegalArgumentException();
        }
    }



    //RETURNS SINGLE USER, NOTE THAT THIS FUNC IS VERY SIMILAR TO THE ORIGINAL
    //FUNCTION THAT RETURNS A LIST
    public User getMonitoredByUser(int index){
        validateIndexWithExceptionMonitoredBy(index);
        return monitoredByUsers.get(index);
    }

    public User getMonitoringUser(int index){
        validateIndexWithExceptionMonitoring(index);
        return monitoringUsers.get(index);
    }






    public String[] getMonitoredByUsersDescriptions(){
        String[] descriptions = new String[monitoredByUsers.size()];
        for(int i = 0; i< countMonitoredByUsers(); i++){
            User user = getMonitoredByUser(i);
            descriptions[i] = "Name: " + user.getName() + "\nEmail: " +
                    user.getEmail();
        }
        return descriptions;
    }


    public String[] getMonitoringUsersDescriptions(){
        String[] descriptions = new String[monitoringUsers.size()];
        for(int i = 0; i< countMonitoringUsers(); i++) {
            User user = getMonitoringUser(i);
            descriptions[i] = "Name: " + user.getName() + "\nEmail: " +
                    user.getEmail();
        }
        return descriptions;

    }






















}
