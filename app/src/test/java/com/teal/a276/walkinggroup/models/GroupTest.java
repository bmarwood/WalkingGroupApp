package com.teal.a276.walkinggroup.models;

import com.teal.a276.walkinggroup.model.dataobjects.Group;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GroupTest {


    Group group = new Group("Teal", "SFU Surrey", "SFU Burnaby");

    @Test
    public void getGroupName() throws Exception {
        assertEquals("Teal", group.getGroupName());
    }

    @Test
    public void setGroupName() throws Exception {
        group.setGroupName("Red");
        assertEquals("Red", group.getGroupName());
    }

    @Test
    public void getMeetingLocation() throws Exception {
        assertEquals("SFU Surrey", group.getMeetingLocation());
    }

    @Test
    public void setMeetingLocation() throws Exception {
        group.setMeetingLocation("Alberta");
        assertEquals("Alberta", group.getMeetingLocation());
    }

    @Test
    public void getDestination() throws Exception {
        assertEquals("SFU Burnaby", group.getDestination());
    }

    @Test
    public void setDestination() throws Exception {
        group.setDestination("Toronto");
        assertEquals("Toronto", group.getDestination());
    }

}