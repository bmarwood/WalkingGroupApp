package com.teal.a276.walkinggroup.model.dataobjects;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Simple User class to store the data the server expects and returns.
 */
@SuppressWarnings("WeakerAccess")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    //added
    private LatLng lastGPSLocation;
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

    public void updateExistingGroup(@NonNull Group newGroup) {
        boolean updatedGroup = false;

        Group[] groupsArray = getMemberOfGroups().toArray(new Group[this.memberOfGroups.size()]);
        for(int i = 0; i < groupsArray.length; i++) {
            Group group = groupsArray[i];
            if(group.getId().equals(newGroup.getId())) {
                groupsArray[i] = newGroup;
                updatedGroup = true;
                break;
            }
        }

        if (!updatedGroup) {
            throw new IllegalArgumentException(String.format("Group %s not found in memberOfGroups %s", newGroup, memberOfGroups));
        }

        this.memberOfGroups = Arrays.asList(groupsArray);
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
                ", monitoredByUsers=" + monitoredByUsers + '\'' +
                ", monitorsUsers=" + monitorsUsers + '\'' +
                ", leadsGroups=" + leadsGroups + '\'' +
                "memberOfGroups=" + memberOfGroups + '\'' +
                "href=" + href + '\'' +
                '}';
    }

    public static boolean validateEmail(String email) {
        Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        return email != null && p.matcher(email).matches();
    }

    public void copyUser(@NonNull User user) {
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

    //added for Dashboard
    public LatLng getLastGPSLocation(){
        return lastGPSLocation;
    }
    public void setLastGPSLocation(LatLng location){
        this.lastGPSLocation = location;
    }
}