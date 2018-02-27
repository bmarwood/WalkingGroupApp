package com.teal.a276.walkinggroup.models;

import java.io.IOException;

/**
 * Created by scott on 26/02/18.
 */

interface AbstractServerManager {
     boolean authUser() throws IOException;
     String getUser(String userId);
     String getGroup();
     void addUserToGroup(String userId, String groupId);
     String getMonitorForUser(String userId);
     void monitorUser(String monitorId, String userId);
}
