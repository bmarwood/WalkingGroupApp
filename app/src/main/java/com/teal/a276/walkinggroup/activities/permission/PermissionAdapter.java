package com.teal.a276.walkinggroup.activities.permission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.Authorizor;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.Permission;

import java.util.HashMap;
import java.util.List;

/**
 * Created by scott on 02/04/18.
 */

public class PermissionAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Permission> headerData;
    private HashMap<Permission, List<Authorizor>> childData;


    PermissionAdapter(Context context, List<Permission> headerData,
                             HashMap<Permission, List<Authorizor>> childData) {
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
    public Permission getGroup(int i) {
        return headerData.get(i);
    }

    @Override
    public Authorizor getChild(int headerIndex, int childIndex) {
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
        Permission permission = getGroup(headerIndex);
        textView.setText(String.format("%s Wants to lead group %s", permission.getUserA().getName(),
                permission.getGroupG().getGroupDescription()));

        return view;
    }

    @Override
    public View getChildView(int headerIndex, int childIndex, boolean b, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.permission_list_item, null);
        }

        Authorizor authorizor = getChild(headerIndex, childIndex);
        User authUser = authorizor.getUsers().get(0);
        User currentUser = ModelFacade.getInstance().getCurrentUser();
        TextView status = view.findViewById(R.id.permissionStatus);

        if(authUser.equals(currentUser)) {
            ImageButton accept = view.findViewById(R.id.acceptPermission);
            ImageButton decline = view.findViewById(R.id.declinePermission);
            accept.setVisibility(View.VISIBLE);
            decline.setVisibility(View.VISIBLE);
            status.setVisibility(View.INVISIBLE);
        } else {
            status.setText(authorizor.getStatus());
        }

        TextView textView = view.findViewById(R.id.name);
        textView.setText(authorizor.getUsers().get(0).getName());

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    private List<Authorizor> getChildList(int index) {
        Permission headerItem = headerData.get(index);
        return childData.get(headerItem);
    }
}
