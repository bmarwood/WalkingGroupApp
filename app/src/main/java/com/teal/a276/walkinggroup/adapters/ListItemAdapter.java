package com.teal.a276.walkinggroup.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;

import java.util.List;

/**
 * ArrayAdapter for all list views
 */
public class ListItemAdapter<T extends DisplayData> extends ArrayAdapter<T> {
    private final List<T> listItems;
    private final Context context;

    public ListItemAdapter(Context context, List<T> listItems) {
        super(context, R.layout.list_item, listItems);
        this.listItems = listItems;
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

        T item = listItems.get(position);
        TextView itemText = itemView.findViewById(R.id.displayText);
        itemText.setText(item.getDisplayData());

        return itemView;
    }
}
