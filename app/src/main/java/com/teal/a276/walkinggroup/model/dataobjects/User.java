package com.teal.a276.walkinggroup.model.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Simple User class to store the data the server expects and returns.
 * (Incomplete: Needs support for monitoring and groups).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private List<Group> memberOfGroups = new ArrayList<>();
    private List<Group> leadsGroups = new ArrayList<>();

    private List<User> monitoredByUsers = new ArrayList<>();
    private List<User> monitorsUsers = new ArrayList<>();

    private String href;

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

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

    public List<Group> getMemberOfGroups() {
        return memberOfGroups;
    }

    public void setMemberOfGroups(List<Group> memberOfGroups) {
        this.memberOfGroups = memberOfGroups;
    }

    public List<Group> getLeadsGroups() {
        return leadsGroups;
    }

    public void setLeadsGroups(List<Group> leadsGroups) {
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

    public void updateGroup(Group newGroup) {
        Group[] groupsArray = getMemberOfGroups().toArray(new Group[this.memberOfGroups.size()]);
        for(int i = 0; i < groupsArray.length; i++) {
            Group group = groupsArray[i];
            if(group.getId().equals(newGroup.getId())) {
                groupsArray[i] = newGroup;
            }
        }
        this.memberOfGroups = Arrays.asList(groupsArray);
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

    //below are added by Jamie, managing users
    private void validateIndexWithExceptionMonitoredBy(int index){
        if(index < 0 || index >= monitoredByUsers.size()){
            throw new IllegalArgumentException();
        }
    }

    private void validateIndexWithExceptionMonitors(int index){
        if(index < 0 || index >= monitorsUsers.size()){
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

    public static boolean validateEmail(String email) {
        Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        return p.matcher(email).matches();
    }

    public void copyUser(User user) {
        setId(user.getId());
        setName(user.getName());
        setEmail(user.getEmail());
        setPassword(user.getPassword());
        setMemberOfGroups(user.getMemberOfGroups());
        setLeadsGroups(user.getLeadsGroups());
        setMonitoredByUsers(user.getMonitoredByUsers());
        setMonitorsUsers(user.getMonitorsUsers());
        setHref(user.getHref());
    }
}