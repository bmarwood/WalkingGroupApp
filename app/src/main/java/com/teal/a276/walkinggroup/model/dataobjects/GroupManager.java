package com.teal.a276.walkinggroup.model.dataobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages joining groups, and groups the user could join
 */
public class GroupManager {

   // private final AbstractServerManager serverManager;
   // public GroupManager(AbstractServerManager serverManager) {
   //     this.serverManager = serverManager;
   // }

    private List<Group> activeGroups = new ArrayList<>();

    public void setActiveGroups(List<Group> groups) {
        activeGroups = groups;
    }
    public void addActiveGroup(Group group){
        activeGroups.add(group);
    }

    public List<Group> getActiveGroups() {
        return activeGroups;
    }

    public Group getActiveGroup(int index){
        validateIndexWithException(index);
        return activeGroups.get(index);
    }

    private void validateIndexWithException(int index){
        if(index < 0 || index >= activeGroups.size()){
            throw new IllegalArgumentException();
        }
    }

    public void removeActiveGroup(Group group){
        activeGroups.remove(group);
    }
}
