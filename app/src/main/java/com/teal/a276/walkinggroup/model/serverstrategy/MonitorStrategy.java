package com.teal.a276.walkinggroup.model.serverstrategy;

import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerError;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by scott on 11/03/18.
 */

public class MonitorStrategy extends ServerRequestSolution {
    private List<User> monitors = new ArrayList<>();
    private final String userEmail;

    public MonitorStrategy(User currentUser, String emailToMonitor, ServerError errorCallback) {
        super(currentUser, errorCallback);
        this.userEmail = emailToMonitor;
    }

    @Override
    public void makeServerRequest() {
        getUserForEmail(userEmail, this::userResult);
    }

    private void userResult(User user) {
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<User>> call = proxy.monitorUser(this.currentUser.getId(), user);
        ServerManager.serverRequest(call, this::monitorsResult, this.errorCallback);
    }

    private void monitorsResult(List<User> users) {
        monitors = users;
        setChanged();
        notifyObservers();
    }

    @Override
    public List<User> getServerResult() {
        return monitors;
    }
}
