package com.teal.a276.walkinggroup.models;

import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.GroupManager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GroupManagerTest {

    GroupManager groupManager = new GroupManager();

    //Check if the added object is the same as the one we passed in,
    //Counts the number of groups each time we add a new one
    @Test
    public void addGroup() throws Exception {

        int numberOfGroups = groupManager.countJoinGroups();
        assertEquals(0, numberOfGroups);

        Group group = new Group("Teal", "SFU", "UBC");
        groupManager.addJoinGroup(group);

        assertEquals(1, groupManager.countJoinGroups());
        assertEquals("Teal", group.getGroupName());
        assertEquals("SFU", group.getMeetingLocation());
        assertEquals("UBC", group.getDestination());

        Group secondGroup = new Group("Red","1", "2");
        groupManager.addJoinGroup(secondGroup);
        numberOfGroups = groupManager.countJoinGroups();
        assertEquals(2, numberOfGroups);
    }

    //Same concept as above, almost same code.
    @Test
    public void addJoinedGroup() throws Exception {
        int numberOfJoinedGroups = groupManager.countJoinedGroups();
        assertEquals(0, numberOfJoinedGroups);

        Group group = new Group("Teal", "SFU", "UBC");

        groupManager.addJoinedGroup(group);
        assertEquals(0, groupManager.countJoinGroups());
        assertEquals(1, groupManager.countJoinedGroups());

        assertEquals("Teal", group.getGroupName());
        assertEquals("SFU", group.getMeetingLocation());
        assertEquals("UBC", group.getDestination());

        Group secondGroup = new Group("Red","1", "2");
        groupManager.addJoinedGroup(group);
        assertEquals(0, groupManager.countJoinGroups());
        assertEquals(2, groupManager.countJoinedGroups());


    }

    //Verifies group count each time a new group is added to JoinGroups
    @Test
    public void countGroups() throws Exception {

        assertEquals(0, groupManager.countJoinGroups());

        Group group = new Group("Teal", "SFU", "UBC");
        groupManager.addJoinGroup(group);
        assertEquals(1, groupManager.countJoinGroups());

        Group secondGroup = new Group("Red","1", "2");
        groupManager.addJoinGroup(secondGroup);
        assertEquals(2, groupManager.countJoinGroups());
    }

    //Same concept as above
    @Test
    public void countJoinedGroups() throws Exception {
        int numberOfGroups = groupManager.countJoinedGroups();
        assertEquals(0, groupManager.countJoinedGroups());

        Group group = new Group("Teal", "SFU", "UBC");
        groupManager.addJoinedGroup(group);
        assertEquals(1, groupManager.countJoinedGroups());

        Group secondGroup = new Group("Red","1", "2");
        groupManager.addJoinedGroup(secondGroup);
        assertEquals(2, groupManager.countJoinedGroups());
    }

    //Verifies the object getJoinGroup returns and see if the content is the same
    @Test
    public void getGroup() throws Exception {

        assertEquals(0, groupManager.countJoinGroups());
        Group group = new Group("Teal", "SFU", "UBC");
        groupManager.addJoinGroup(group);

        assertEquals(1, groupManager.countJoinGroups());
        Group testGroup = groupManager.getJoinGroup(0);

        assertEquals("Teal", testGroup.getGroupName());
        assertEquals("SFU", testGroup.getMeetingLocation());
        assertEquals("UBC", testGroup.getDestination());


    }

    //same testing concept as above, 1 bug found! Fixed occurances when calling
    //ValidateIndexWithException. Old version will only count countJoinGroups.
    //Solution: added new subroutine called ValidateIndexWithException that
    //calculates joinedGroups. Bug fixed.

    @Test
    public void getJoinedGroup() throws Exception {

        assertEquals(0, groupManager.countJoinedGroups());
        Group group = new Group("Teal", "SFU", "UBC");
        groupManager.addJoinedGroup(group);

        assertEquals(1, groupManager.countJoinedGroups());
        Group testGroup = groupManager.getJoinedGroup(0);

        assertEquals("Teal", testGroup.getGroupName());
        assertEquals("SFU", testGroup.getMeetingLocation());
        assertEquals("UBC", testGroup.getDestination());

    }

    //Tests if the user is already in a specific joined group
    @Test
    public void checkIfUserAlreadyInSameGroup() throws Exception {
        Group group = new Group("Teal", "SFU", "UBC");
        groupManager.addJoinedGroup(group);

        Group testingGroup = groupManager.getJoinedGroup(0);
        assertEquals(group, testingGroup);

        Group secondGroup = new Group("Red","1", "2");
        assertNotEquals(secondGroup, group);

    }

    //similar test method as abovve function.
    @Test
    public void removeFromJoinedGroups() throws Exception {
        Group group = new Group("Teal", "SFU", "UBC");
        groupManager.addJoinedGroup(group);

        assertEquals(1, groupManager.countJoinedGroups());

        groupManager.removeFromJoinedGroups(group);

        assertEquals(0, groupManager.countJoinedGroups());

    }
}