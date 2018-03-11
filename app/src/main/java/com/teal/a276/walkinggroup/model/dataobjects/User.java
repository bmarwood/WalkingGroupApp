package com.teal.a276.walkinggroup.model.dataobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple User class to store the data the server expects and returns.
 * (Incomplete: Needs support for monitoring and groups).
 */

public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private ArrayList<Group> memberOfGroups;
    private ArrayList<Group> leadsGroups;

    private List<User> monitoredByUsers = new ArrayList<>();
    private List<User> monitorsUsers = new ArrayList<>();
   // private List<Void> walkingGroups = new ArrayList<>();   // <-- TO BE IMPLEMENTED

    private String href;

    public Long getId() {
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

    public List<User> getMonitoredByUsers() {
        return monitoredByUsers;
    }

    public void setMonitoredByUsers(List<User> monitoredByUsers) {
        this.monitoredByUsers = monitoredByUsers;
    }

    public List<User> getMonitorsUsers() {
        return monitorsUsers;
    }

    public void setMonitorsUsers(List<User> monitorsUsers) {
        this.monitorsUsers = monitorsUsers;
    }

//    public List<Void> getWalkingGroups() {
//        return walkingGroups;
//    }
//
//    public void setWalkingGroups(List<Void> walkingGroups) {
//        this.walkingGroups = walkingGroups;
//    }


    public ArrayList<Group> getMemberOfGroups() {
        return memberOfGroups;
    }

    public void setMemberOfGroups(ArrayList<Group> memberOfGroups) {
        this.memberOfGroups = memberOfGroups;
    }

    public ArrayList<Group> getLeadsGroups() {
        return leadsGroups;
    }

    public void setLeadsGroups(ArrayList<Group> leadsGroups) {
        this.leadsGroups = leadsGroups;
    }

    public void addGroupToLead(Group group) {
        this.leadsGroups.add(group);
    }

    public void removeGroupToLead(Group group) {
        this.leadsGroups.remove(group);
    }

    public void joinGroup(Group group) {
        this.memberOfGroups.add(group);
    }

    public void leaveGroup(Group group) {
        this.memberOfGroups.remove(group);
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
                ", monitorsUsers=" + monitorsUsers +
                //", walkingGroups=" + walkingGroups +
                '}';
    }
    public int countMonitoredByUsers(){
        return monitoredByUsers.size();
    }
    public int countMonitorsUsers(){
        return monitorsUsers.size();
    }


    //below are added by Jamie, managing users
    //TODO: add @JsonIgnore to these



    private void validateIndexWithExceptionMonitoredBy(int index){
        if(index < 0 || index >= countMonitoredByUsers()){
            throw new IllegalArgumentException();
        }
    }

    private void validateIndexWithExceptionMonitors(int index){
        if(index < 0 || index >= countMonitorsUsers()){
            throw new IllegalArgumentException();
        }
    }

    //RETURNS SINGLE USER, NOTE THAT THIS FUNC IS VERY SIMILAR TO THE ORIGINAL
    //FUNCTION THAT RETURNS A LIST
    public User getMonitoredByUser(int index){
        validateIndexWithExceptionMonitoredBy(index);
        return monitoredByUsers.get(index);
    }

    public User getMonitorsUser(int index){
        validateIndexWithExceptionMonitors(index);
        return monitorsUsers.get(index);
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


    public String[] getMonitorsUsersDescriptions() {
        String[] descriptions = new String[monitorsUsers.size()];
        for (int i = 0; i < countMonitorsUsers(); i++) {
            User user = getMonitorsUser(i);
            descriptions[i] = "Name: " + user.getName() + "\nEmail: " +
                    user.getEmail();
        }
        return descriptions;
    }

}
