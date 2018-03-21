package com.teal.a276.walkinggroup.model.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by scott on 20/03/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {


    private Long id;
    private Long timeStamp;
    private String text;
    private List<User> fromUsers;
    private List<Group> toGroup;
    private boolean emergency;
    private String href;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<User> getFromUsers() {
        return fromUsers;
    }

    public void setFromUsers(List<User> fromUsers) {
        this.fromUsers = fromUsers;
    }

    public List<Group> getToGroup() {
        return toGroup;
    }

    public void setToGroup(List<Group> toGroup) {
        this.toGroup = toGroup;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
