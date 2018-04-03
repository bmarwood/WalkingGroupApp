package com.teal.a276.walkinggroup.activities.permission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by scott on 02/04/18.
 */

public class PermissionAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> headerData;
    private HashMap<String, List<String>> childData;

    public PermissionAdapter(Context context, List<String> headerData,
                             HashMap<String, List<String>> childData) {
        this.context = context;
        this.headerData = headerData;
        this.childData = childData;
    }

    @Override
    public int getGroupCount() {
        return headerData.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return getChildList(i).size();
    }

    @Override
    public String getGroup(int i) {
        return headerData.get(i);
    }

    @Override
    public String getChild(int headerIndex, int childIndex) {
        return getChildList(headerIndex).get(childIndex);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int headerIndex, boolean b, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.permission_list_item_header, null);
        }

        TextView textView = view.findViewById(R.id.headerTitle);
        textView.setText(getGroup(headerIndex));

        return view;
    }

    @Override
    public View getChildView(int headerIndex, int childIndex, boolean b, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.permission_list_item, null);
        }

        TextView textView = view.findViewById(R.id.name);
        textView.setText(getChild(headerIndex, childIndex));

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    private List<String> getChildList(int index) {
        String headerItem = headerData.get(index);
        return childData.get(headerItem);
    }
}
