package com.teal.a276.walkinggroup.models;

import java.util.ArrayList;
import java.util.List;


public class GroupManager {

   // private final AbstractServerManager serverManager;
   // public GroupManager(AbstractServerManager serverManager) {
   //     this.serverManager = serverManager;
   // }

    private List<Group> groups = new ArrayList<>();

    public void addGroup(Group group){
        groups.add(group);
    }


    public int countGroups(){
        return groups.size();
    }


    public Group getGroup(int index){
        validateIndexWithException(index);
        return groups.get(index);
    }


    private void validateIndexWithException(int index) {
        if(index < 0 || index >= countGroups()){
            throw new IllegalArgumentException();
        }
    }


    public String[] getGroupDescriptions(){
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


}
