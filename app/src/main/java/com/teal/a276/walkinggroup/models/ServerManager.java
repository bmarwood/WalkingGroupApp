package com.teal.a276.walkinggroup.models;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by scott on 26/02/18.
 */

public class ServerManager implements AbstractServerManager {
    private final String API_KEY = "BB23730A-C1B3-4B65-855E-C538EE143FDC\n";
    private final URL SERVER_URL;
    private String AUTH_TOKEN = "";

    public ServerManager() throws MalformedURLException {
        SERVER_URL = new URL("http://cmpt276-1177-bf.cmpt.sfu.ca:8080");
    }

    @Override
    public boolean authUser() throws IOException {
        return false;
    }

    @Override
    public String getUser(String userId) {
        return null;
    }

    @Override
    public String getGroup() {
        return null;
    }

    @Override
    public void addUserToGroup(String userId, String groupId) {

    }

    @Override
    public String getMonitorForUser(String userId) {
        return null;
    }

    @Override
    public void monitorUser(String monitorId, String userId) {

    }
}
