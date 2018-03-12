package com.teal.a276.walkinggroup.model.serverstrategy;

import android.support.annotation.NonNull;

import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerError;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverproxy.ServerResult;

import java.util.Observable;

import retrofit2.Call;

/**
 * Created by scott on 11/03/18.
 */

abstract class ServerRequestSolution extends Observable implements ServerRequestStrategy {
    final ServerError errorCallback;
    final User currentUser;

    ServerRequestSolution(User currentUser, ServerError errorCallback) {
        this.currentUser = currentUser;
        this.errorCallback = errorCallback;
    }

    void getUserForEmail(String email, @NonNull final ServerResult<User> resultCallback) {
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<User> userByEmailCall = proxy.getUserByEmail(email);
        ServerManager.serverRequest(userByEmailCall, resultCallback, errorCallback);
    }
}
