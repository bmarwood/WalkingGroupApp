package com.teal.a276.walkinggroup.models;

public class Group {

    //private List<User> users = new ArrayList<>();

    private String groupName;
    private String meetingLocation;
    private String destination;
    //private User user;


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

}
