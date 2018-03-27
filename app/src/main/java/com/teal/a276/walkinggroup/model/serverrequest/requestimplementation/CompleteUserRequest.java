package com.teal.a276.walkinggroup.model.serverrequest.requestimplementation;

import android.support.annotation.NonNull;

import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerError;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverrequest.AbstractServerRequest;

import java.util.List;

import retrofit2.Call;

/**
 * ServerRequest to fully populate a user object. By default user objects are returned from the server
 * with limited group/monitoring information.
 */
public class CompleteUserRequest extends AbstractServerRequest {
    private User user;

    public CompleteUserRequest(User currentUser, @NonNull ServerError errorCallback) {
        super(currentUser, errorCallback);
    }

    @Override
    public void makeServerRequest() {
        getUserForEmail(currentUser.getEmail(),
                this::userResult, 1L);
    }

    private void userResult(User user) {
        this.user = user;

        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<User>> call = proxy.getMonitors(user.getId());
        ServerManager.serverRequest(call, this::monitorsResult, errorCallback);
    }

    private void monitorsResult(List<User> users) {
        user.setMonitorsUsers(users);

        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<User>> call = proxy.getMonitoredBy(user.getId());
        ServerManager.serverRequest(call, this::monitoredByResult, errorCallback);
    }

    private void monitoredByResult(List<User> users) {
        user.setMonitoredByUsers(users);

        if (user.getMemberOfGroups().size() == 0) {
            setDataChanged(user);
            return;
        }

        ServerProxy proxy = ServerManager.getServerRequest();
        for(int i = 0; i < user.getMemberOfGroups().size(); i++) {
            Group group = user.getMemberOfGroups().get(i);
            Call<Group> joinedGroupCall = proxy.getGroup(group.getId());

            int currentIndex = i;
            ServerManager.serverRequest(joinedGroupCall, result -> groupResult(result, currentIndex), errorCallback);
        }
    }

    private void groupResult(Group group, int index) {
        user.updateExistingGroup(group);
        if(user.getMemberOfGroups().size() - 1 == index) {
            setDataChanged(user);
        }
    }
}
