package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Activity for displaying the top 100 users starting with the highest score
 */

public class Leaderboard extends BaseActivity {

    private ArrayAdapter<User> leaderboardAdapter;
    List<User> testingUsers = new ArrayList<>();
    String firstName= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);


        initializeLeaderboardListView();
        //getUsers();
    }
    /*
    private void getUsers() {
        ServerProxy getUsersProxy = ServerManager.getServerProxy();
        Call<List<User>> getUsersCall = getUsersProxy.getUsers();
        ServerManager.serverRequest(getUsersCall, this::initializeLeaderboardListView, this::error);
    }
    */

    private void initializeLeaderboardListView() {
        User bob1 = new User();
        bob1.setName("Bob1");
        bob1.setTotalPointsEarned(1);

        User bob3 = new User();
        bob3.setName("Bob3");
        bob3.setTotalPointsEarned(3);

        User bob4 = new User();
        bob4.setName("Bob4");

        User bob2 = new User();
        bob2.setName("Bob2");
        bob2.setTotalPointsEarned(2);

        User lastNameTest = new User();
        lastNameTest.setName("Jane Doe");
        lastNameTest.setTotalPointsEarned(100);

        testingUsers.add(bob1);
        testingUsers.add(bob3);
        testingUsers.add(bob4);
        testingUsers.add(lastNameTest);
        testingUsers.add(bob2);


        Log.d("listtesting", Arrays.deepToString(testingUsers.toArray()));

        for(User user : testingUsers){
            if(user.getTotalPointsEarned() == null){
                user.setTotalPointsEarned(0);
                //testingUsers.remove(testingUsers.indexOf(user));
            }
        }

        //Sort users from max to min
        Collections.sort(testingUsers, ((o1, o2) -> o2.getTotalPointsEarned()
                .compareTo(o1.getTotalPointsEarned())));

        //shrink size of listArray to 100 (top 100 users)
        if(testingUsers.size()>100){
            testingUsers.subList(100, testingUsers.size());
        }

        leaderboardAdapter = new ListItemAdapter(this, testingUsers);
        ListView leaderboardList = findViewById(R.id.leaderboardLv);
        leaderboardList.setAdapter(leaderboardAdapter);
    }

    private class ListItemAdapter extends ArrayAdapter<User> {
        private final List<User> listItems;
        private final Context context;

        public ListItemAdapter(Context context, List<User> listItems) {

            super(context, R.layout.list_item, listItems);
            this.listItems = listItems;
            this.context = context;
        }

        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                itemView = inflater.inflate(R.layout.leaderboard_list, parent, false);
            }
            User selectedUser = listItems.get(position);

            TextView nameTextView = itemView.findViewById(R.id.userName);


            //First Name Extraction
            String currentName = selectedUser.getName();

            if(currentName.split("\\w+").length>1){
                firstName = currentName.substring(0, currentName.lastIndexOf(' '));
            } else {
                firstName = currentName;
            }

            //Last Name Extraction
            char lastInitial = '\0';
            for(int i=1;i<currentName.length();i++){
                char c = currentName.charAt(i);
                if(c == ' '){
                    lastInitial = currentName.charAt(i+1);
                }
            }

            String displayText =  firstName +  " " + lastInitial + "    -    " +
                    selectedUser.getTotalPointsEarned() + " Points";

            nameTextView.setText(displayText);


            return itemView;
        }
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, Leaderboard.class);
    }
}
