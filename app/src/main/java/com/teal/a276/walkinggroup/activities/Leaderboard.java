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

        User bob = new User();
        bob.setName("Bob");
        bob.setTotalPointsEarned(1000);

        testingUsers.add(bob);

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
                itemView = inflater.inflate(R.layout.list_item, parent, false);
            }
            User selectedUser = listItems.get(position);

            TextView nameTextView = itemView.findViewById(R.id.userName);
            nameTextView.setText(selectedUser.getName());

            return itemView;
        }
    }


    public static Intent makeIntent(Context context){
        return new Intent(context, Leaderboard.class);
    }
}
