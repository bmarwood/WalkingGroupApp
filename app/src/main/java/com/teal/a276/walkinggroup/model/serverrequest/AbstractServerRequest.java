package com.teal.a276.walkinggroup.model.serverrequest;

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

public abstract class AbstractServerRequest extends Observable {
    protected final ServerError errorCallback;
    protected final User currentUser;

    protected AbstractServerRequest(User currentUser, ServerError errorCallback) {
        this.currentUser = currentUser;
        this.errorCallback = errorCallback;
    }

    protected abstract void makeServerRequest();

    public void getUserForEmail(String email, @NonNull final ServerResult<User> resultCallback) {
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<User> userByEmailCall = proxy.getUserByEmail(email);
        ServerManager.serverRequest(userByEmailCall, resultCallback, errorCallback);
    }

    protected <T> void setDataChanged(T data) {
        setChanged();
        notifyObservers(data);
    }
}
