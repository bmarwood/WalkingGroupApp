package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        initializeLeaderboardListView();
    }

    private void initializeLeaderboardListView() {

        User bob1 = new User();
        bob1.setName("Bob1");
        bob1.setTotalPointsEarned(1);

        User bob2 = new User();
        bob2.setName("Bob2");
        bob2.setTotalPointsEarned(2);

        User bob3 = new User();
        bob3.setName("Bob3");
        bob3.setTotalPointsEarned(3);

        User bob4 = new User();
        bob4.setName("Bob4");
        bob4.setTotalPointsEarned(4);

        testingUsers.add(bob1);
        testingUsers.add(bob4);
        testingUsers.add(bob2);
        testingUsers.add(bob3);

        System.out.println(Arrays.deepToString(testingUsers.toArray()));

        //Sort users from max to min
        Collections.sort(testingUsers, ((o1, o2) -> o2.getTotalPointsEarned()
                .compareTo(o1.getTotalPointsEarned())));

        //shrink size of listArray to 100 (top 100 users)
        if(testingUsers.size()>100){
            testingUsers.subList(100, testingUsers.size());
        }

        leaderboardAdapter = new ListItemAdapter(this, testingUsers, true);
        ListView leaderboardList = findViewById(R.id.leaderboardLv);
        leaderboardList.setAdapter(leaderboardAdapter);
    }


    private class ListItemAdapter extends ArrayAdapter<User> {
        private final List<User> listItems;
        private final Context context;
        private final boolean leaderList;

        public ListItemAdapter(Context context, List<User> listItems, boolean leaderList) {
            super(context, R.layout.list_item, listItems);
            this.listItems = listItems;
            this.context = context;
            this.leaderList = leaderList;
        }

        //@Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                itemView = inflater.inflate(R.layout.leaderboard_list, parent, false);
            }
            User selectedUser = listItems.get(position);

            TextView nameTextView = itemView.findViewById(R.id.userName);
            String displayText = selectedUser.getName() + "    -    " +
                    selectedUser.getTotalPointsEarned() + " Points";
            nameTextView.setText(displayText);

            return itemView;
        }
    }


    public static Intent makeIntent(Context context){
        return new Intent(context, Leaderboard.class);
    }
}
