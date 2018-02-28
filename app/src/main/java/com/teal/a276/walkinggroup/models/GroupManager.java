package com.teal.a276.walkinggroup.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by egg on 2018-02-28.
 */

public class GroupManager {
     private List<User> groups = new ArrayList<>();

     public void addToGroup(User user){
          groups.add(user);
     }



     private int countGroups(){
          return groups.size();
     }

     private User getUser(int index){
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
               User user = getUser(i);
               descriptions[i] = user.getUserName() + " - " + user.getUserEmail();
          }
          return descriptions;
     }

}
