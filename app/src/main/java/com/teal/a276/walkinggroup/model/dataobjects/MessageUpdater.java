package com.teal.a276.walkinggroup.model.dataobjects;

import com.teal.a276.walkinggroup.model.serverproxy.MessageRequestConstant;
import com.teal.a276.walkinggroup.model.serverproxy.ServerError;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;

/**
 * Created by scott on 24/03/18.
 */

public class MessageUpdater extends Observable {
    private Timer timer = new Timer();
    private HashSet<Message> messageCache = new HashSet<>();

    public MessageUpdater(final User user, final ServerError errorCallback, long updateRate) {
        subscribeForUpdates(user, errorCallback, updateRate);
    }

    private void subscribeForUpdates(final User user, final ServerError errorCallback, long updateRate) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getMessages(user, errorCallback);
            }
        }, updateRate, updateRate);
    }

    private void getMessages(User user, ServerError errorCallback) {
        HashMap<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(MessageRequestConstant.FOR_USER, user.getId());
        requestParameters.put(MessageRequestConstant.STATUS, MessageRequestConstant.UNREAD);
        ServerProxy proxy = ServerManager.getServerRequest();

        Call<List<Message>> call = proxy.getMessages(requestParameters);
        ServerManager.serverRequest(call, this::unreadMessages, errorCallback);
    }

    private void unreadMessages(List<Message> messages) {
        List<Message> newMessages = getNewMessages(messages);
        if (!newMessages.isEmpty()) {
            setChanged();
            notifyObservers(getNewMessages(messages));
            messageCache.addAll(messages);
        }
    }

    private List<Message> getNewMessages(List<Message> messages) {
        List<Message> newMessages = new ArrayList<>();
        for(Message message : messages) {
            if (!messageCache.contains(message)) {
                newMessages.add(message);
            }
        }

        return newMessages;
    }

    public void addCacheItems(List<Message> messages) {
        messageCache.addAll(messages);
    }

    public void unsubscribeFromUpdates() {
        timer.cancel();
    }
}
