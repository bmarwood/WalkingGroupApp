package com.teal.a276.walkinggroup.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.User;

import java.util.List;

/**
 * ArrayAdapter for all list views
 */
public class ListItemAdapter extends ArrayAdapter<User> {
    private final List<User> userList;
    private final Context context;

    public ListItemAdapter(Context context, List<User> users) {
        super(context, R.layout.list_item, users);
        userList = users;
        this.context = context;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if(itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            itemView = inflater.inflate(R.layout.list_item, parent, false);
        }

        User currentUser = userList.get(position);
        TextView userText = itemView.findViewById(R.id.userName);
        userText.setText(currentUser.getEmail());

        return itemView;
    }
}
