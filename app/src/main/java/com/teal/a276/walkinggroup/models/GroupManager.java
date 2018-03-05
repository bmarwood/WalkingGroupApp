package com.teal.a276.walkinggroup.models;

import java.util.ArrayList;
import java.util.List;


public class GroupManager {

   // private final AbstractServerManager serverManager;
   // public GroupManager(AbstractServerManager serverManager) {
   //     this.serverManager = serverManager;
   // }

    private List<Group> groups = new ArrayList<>();
    private List<Group> joinedGroups = new ArrayList<>();

    //check if already in group
    boolean checkInGroup = false;

    public void addGroup(Group group){
        groups.add(group);
    }

    public void addJoinedGroup(Group group){
        joinedGroups.add(group);
    }




    public int countGroups(){
        return groups.size();
    }
    public int countJoinedGroups(){
        return joinedGroups.size();
    }





    public Group getGroup(int index){
        validateIndexWithException(index);
        return groups.get(index);
    }
    public Group getJoinedGroup(int index){
        validateIndexWithException(index);
        return joinedGroups.get(index);
    }




    private void validateIndexWithException(int index) {
        if(index < 0 || index >= countGroups()){
            throw new IllegalArgumentException();
        }
    }


    public boolean checkIfUserInGroup(){
        if(joinedGroups.size() >0) {
            return true;
        }
        return false;
    }


    public String[] getJoinGroupDescriptions(){
        String[] descriptions = new String[countGroups()];

        for(int i=0;i<countGroups();i++){
            //User user = getUser(i);
            //descriptions[i] = user.getUserName() + " - " + user.getUserEmail();
            Group group = getGroup(i);
            descriptions[i] = "Name :" + group.getGroupName() + "\nMeeting Location: " +
                    group.getMeetingLocation() + "\nDestination: " + group.getDestination();
        }
        return descriptions;
    }

    public String[] getJoinedGroupDescriptions(){
        String[] descriptions = new String[countJoinedGroups()];

        for(int i=0;i<countJoinedGroups();i++){
            //User user = getUser(i);
            //descriptions[i] = user.getUserName() + " - " + user.getUserEmail();
            Group group = getJoinedGroup(i);
            descriptions[i] = "Name :" + group.getGroupName() + "\nMeeting Location: " +
                    group.getMeetingLocation() + "\nDestination: " + group.getDestination();
        }
        return descriptions;
    }


}
