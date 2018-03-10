package com.teal.a276.walkinggroup.model;

import com.teal.a276.walkinggroup.model.dataobjects.User;

/**
 * Created by scott on 09/03/18.
 */

public class ModelFacade {

    private static ModelFacade instance;
    private User currentUser = null;
    private ModelFacade() {

    }

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
}
