package com.teal.a276.walkinggroup.activities.permission;

import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.BaseActivity;
import com.teal.a276.walkinggroup.model.dataobjects.Permission;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.Arrays;
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
        List<String> fakeHeaderData = Arrays.asList("Test1", "test2", "test3");
        HashMap<String, List<String>> fakeChildData = new HashMap<>();
        fakeChildData.put(fakeHeaderData.get(0), Arrays.asList("child1", "child2", "child3"));
        fakeChildData.put(fakeHeaderData.get(1), Arrays.asList("child4", "child5", "child6"));
        fakeChildData.put(fakeHeaderData.get(2), Arrays.asList("child7", "child8", "child9"));

        permissionAdapter = new PermissionAdapter(this, fakeHeaderData, fakeChildData);
        permissionView.setAdapter(permissionAdapter);

        ServerProxy proxy = ServerManager.getServerProxy();
        Map<String, Object> map = new HashMap<>();
        Call<List<Permission>> call = proxy.getPermissions(map);
        ServerManager.serverRequest(call, this::test, this::error);
    }

    private void test(List<Permission> permissions) {
        for(Permission p : permissions) {
            Log.d("permission", p.toString());
        }
    }
}
