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


    public void addJoinGroup(Group group){
        groups.add(group);
    }
    public void addJoinedGroup(Group group){
        joinedGroups.add(group);
    }



    public int countJoinGroups(){
        return groups.size();
    }
    public int countJoinedGroups(){
        return joinedGroups.size();
    }



    public Group getJoinGroup(int index){
        validateIndexWithException(index);
        return groups.get(index);
    }
    public Group getJoinedGroup(int index){
        validateIndexWithExceptionJoined(index);
        return joinedGroups.get(index);
    }


    //This Function only counts join Groups, NOT JoinedGroups
    private void validateIndexWithException(int index) {
        if(index < 0 || index >= countJoinGroups()){
            throw new IllegalArgumentException();
        }
    }


    private void validateIndexWithExceptionJoined(int index){
        if(index < 0 || index >= countJoinedGroups()){
            throw new IllegalArgumentException();
        }
    }


    //public boolean checkIfUserInGroup(){
    //    return joinedGroups.size() > 0;
    //    //if(joinedGroups.size() >0) {
    //    //    return true;
    //    //}
    //    //return false;
    //j

    //Prevents the user from joining the same group if the user
    //is already in the group.
    public boolean checkIfUserAlreadyInSameGroup(Group group){
        return (joinedGroups.contains(group));
    }


    public void removeFromJoinedGroups(Group group){
        joinedGroups.remove(group);
//        joinedGroups.clear();
    }


    public String[] getJoinGroupDescriptions(){
        String[] descriptions = new String[countJoinGroups()];

        for(int i = 0; i< countJoinGroups(); i++){
            Group group = getJoinGroup(i);
            descriptions[i] = "Name :" + group.getGroupName() + "\nMeeting Location: " +
                    group.getMeetingLocation() + "\nDestination: " + group.getDestination();
        }
        return descriptions;
    }

    public String[] getJoinedGroupDescriptions(){
        String[] descriptions = new String[countJoinedGroups()];

        for(int i=0;i<countJoinedGroups();i++){
            Group group = getJoinedGroup(i);
            descriptions[i] = "Name :" + group.getGroupName() + "\nMeeting Location: " +
                    group.getMeetingLocation() + "\nDestination: " + group.getDestination();
        }
        return descriptions;
    }


}
