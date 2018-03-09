package com.teal.a276.walkinggroup.models;

import java.util.ArrayList;
import java.util.List;


//added for JsonIgnore

public class Group {

    //private List<User> users = new ArrayList<>();

    private String groupName;
    private String meetingLocation;
    private String destination;

    private Long id;
    private User leader;
    private List<String> routeLatArray = new ArrayList<>();
    private List<String> routeLngArray = new ArrayList<>();
    private List<User> memberUsers = new ArrayList<>();

    //private User user;
    //@JsonIgnore
    public Group(String groupName, String meetingLocation, String destination) {
        this.groupName = groupName;
        this.meetingLocation = meetingLocation;
        this.destination = destination;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            throw new IllegalArgumentException("Name is empty or NULL");
        }
        this.groupName = groupName;
    }


    public String getMeetingLocation() {
        return meetingLocation;
    }

    public void setMeetingLocation(String meetingLocation) {
        if (meetingLocation == null || meetingLocation.isEmpty()) {
            throw new IllegalArgumentException("Name is empty or NULL");
        }
        this.meetingLocation = meetingLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        if (destination == null || destination.isEmpty()) {
            throw new IllegalArgumentException("Name is empty or NULL");
        }
        this.destination = destination;
    }





    //added to match Dr Brian's Json retrofit

    public Long getGroupId(){
        return id;
    }
    public void setGroupId(Long id){
        this.id = id;
    }


    public User getLeader(){
        return leader;
    }
    public void setLeader(User user){
        this.leader = user;
    }


    public String getGroupDescription(){
        String groupDescription = groupName;
        return groupDescription;
    }
    public void setGroupDescription(String descriptions){
        this.groupName = descriptions;
    }



    public List<User> getMemberUsers(){
        return memberUsers;
    }
    public void setMemberUsers(List<User> memberUsers){
        this.memberUsers = memberUsers;
    }


    public List<String> getRouteLatArray(){
        return routeLatArray;
    }
    public void setRouteLatArray(List<String> routeLatArray){
        this.routeLatArray = routeLatArray;
    }


    public List<String> getRouteLngArray(){
       return routeLngArray;
    }
    public void setRouteLngArray(List<String> routeLngArray){
        this.routeLngArray = routeLngArray;
    }


    @Override
    public String toString(){
        return "groupDescription=" + groupName +
               ", leader='" + leader + '\'' +

               ", routeLatArray='" + routeLatArray + '\'' +
               ", routeLngArray='" + routeLngArray + '\'' +
               ", memberUsers='" + memberUsers + '\'' +
               '}';

    }
}
