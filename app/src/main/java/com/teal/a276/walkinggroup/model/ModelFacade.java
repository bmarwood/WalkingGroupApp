package com.teal.a276.walkinggroup.model;

import android.content.res.Resources;

import com.teal.a276.walkinggroup.model.dataobjects.User;

/**
 * Singleton for accessing groups/users
 */

public class ModelFacade {

    private static ModelFacade instance;
    private User currentUser = null;
    private Resources appResources;

    private ModelFacade() {}

    public static ModelFacade getInstance() {
        if(instance == null) {
            instance = new ModelFacade();
        }
        return instance;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            throw new IllegalStateException("User was not set before calling getUser");
        }

        return currentUser;
    }

    public void setCurrentUser(User newCurrentUser) {
        this.currentUser = newCurrentUser;
    }

    public void setAppResources(Resources resources) {
        appResources = resources;
    }

    public Resources getAppResources() {
        if (appResources == null) {
            throw new IllegalStateException("AppResources was not set before calling getAppResources");
        }

        return appResources;
    }
}
