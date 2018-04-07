package com.teal.a276.walkinggroup.activities.permission;

import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.BaseActivity;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.Authorizor;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.Permission;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.PermissionStatus;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

//General idea taken from here https://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
public class PermissionRequest extends BaseActivity {
    ExpandableListAdapter permissionAdapter;
    ExpandableListView permissionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_request);

        permissionView = findViewById(R.id.permissions);

        ServerProxy proxy = ServerManager.getServerProxy();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", ModelFacade.getInstance().getCurrentUser().getId());
        map.put("statusForUser", PermissionStatus.PENDING);
        Call<List<Permission>> call = proxy.getPermissions(map, 1L);
        ServerManager.serverRequest(call, this::test, this::error);
    }

    private void test(List<Permission> permissions) {
        HashMap<Permission, List<Authorizor>> authorizors = new HashMap<>();
        for(Permission p : permissions) {
            authorizors.put(p, p.getAuthorizors());
        }

        permissionAdapter = new PermissionAdapter(this, permissions, authorizors,  this::error);
        permissionView.setAdapter(permissionAdapter);

       for (int i = 0; i < permissionAdapter.getGroupCount(); i++) {
           permissionView.expandGroup(i);
       }
    }
}
