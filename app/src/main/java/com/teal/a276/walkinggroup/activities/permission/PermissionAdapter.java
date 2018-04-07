package com.teal.a276.walkinggroup.activities.permission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.Authorizor;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.Permission;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.PermissionStatus;
import com.teal.a276.walkinggroup.model.serverproxy.ServerError;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by scott on 02/04/18.
 */

public class PermissionAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Permission> headerData;
    private Map<Permission, List<Authorizor>> childData;
    private ServerError errorCallback;


    PermissionAdapter(Context context,
                      List<Permission> headerData, Map<Permission, List<Authorizor>> childData,
                      ServerError errorCallback) {
        this.context = context;
        this.headerData = headerData;
        this.childData = childData;
        this.errorCallback = errorCallback;
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

        Permission permission = getGroup(headerIndex);
        Authorizor authorizor = getChild(headerIndex, childIndex);
        User authUser = authorizor.getUsers().get(0);
        User currentUser = ModelFacade.getInstance().getCurrentUser();
        TextView status = view.findViewById(R.id.permissionStatus);

        //TODO check that the current users authStatus is pending
        if(authUser.equals(currentUser) &&
                permission.getStatus().equals(PermissionStatus.PENDING.getValue())) {
            ImageView accept = view.findViewById(R.id.acceptPermission);
            accept.setVisibility(View.VISIBLE);
            accept.setOnClickListener(view1 -> setPermissionStatus(permission.getId(), PermissionStatus.APPROVED));

            ImageView decline = view.findViewById(R.id.declinePermission);
            decline.setOnClickListener(view1 -> setPermissionStatus(permission.getId(), PermissionStatus.DECLINED));

            decline.setVisibility(View.VISIBLE);
            status.setVisibility(View.INVISIBLE);
        } else {
            status.setText(authorizor.getStatus());
        }

        TextView textView = view.findViewById(R.id.name);
        textView.setText(authorizor.getUsers().get(0).getName());

        return view;
    }

    private void setPermissionStatus(Long id, PermissionStatus status) {
        ServerProxy proxy = ServerManager.getServerProxy();
        Call<Permission> call = proxy.setPermissionStatus(id, status.getValue());
        ServerManager.serverRequest(call, this::updateListItem, errorCallback);
    }

    private void updateListItem(Permission result) {
       headerData.remove(result);
       childData.remove(result);
       notifyDataSetChanged();
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
