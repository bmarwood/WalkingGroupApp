package com.teal.a276.walkinggroup.model.dataobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scott on 06/04/18.
 */

public class Permission {
    private int id;
    private String action;
    private String status;
    private User userA;
    private User userB;
    private Group groupG;
    private User requestingUser;
    private List<Object> authorizors = new ArrayList<>();
    private String message;
    private String href;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUserA() {
        return userA;
    }

    public void setUserA(User userA) {
        this.userA = userA;
    }

    public User getUserB() {
        return userB;
    }

    public void setUserB(User userB) {
        this.userB = userB;
    }

    public Group getGroupG() {
        return groupG;
    }

    public void setGroupG(Group groupG) {
        this.groupG = groupG;
    }

    public User getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(User requestingUser) {
        this.requestingUser = requestingUser;
    }

    public List<Object> getAuthorizors() {
        return authorizors;
    }

    public void setAuthorizors(List<Object> authorizors) {
        this.authorizors = authorizors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
