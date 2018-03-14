/*
package com.teal.a276.walkinggroup.model.serverrequest.requestimplementation;

import android.support.annotation.NonNull;
import android.util.Log;

import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerError;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverrequest.AbstractServerRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;


public class CreateGroupRequest extends AbstractServerRequest {

    private Group group;

    public CreateGroupRequest(String leaderEmail, String groupDes, double Lat, double Lng , @NonNull ServerError errorCallback) {
        super(currentUser, errorCallback);
    }

    @Override
    public void makeServerRequest() {
        getUserFromEmail(email, this::userFromEmail);

    }

//    private void userResult(User) {
//        ServerProxy proxy = ServerManager.getServerRequest();
//        Call<User> call = proxy.getUserByEmail(leadersEmail);
//        ServerManager.serverRequest(call, result -> userFromEmail(result,
//                nameValStr, lat, lng), errorCallback);
//
//    }

    private void userFromEmail(User user, String groupDes, double Lat, double Lng) {
        Group group = new Group();
        group.setLeader(user);

        //TODO: Remove this line of code after new server has been pushed.
        group.setId(-1L);

        group.setGroupDescription(groupDes);


        List<Double> latArray = new ArrayList<>();
        List<Double> lngArray = new ArrayList<>();

        latArray.add(Lat);
        lngArray.add(Lng);

        group.setRouteLatArray(latArray);
        group.setRouteLngArray(lngArray);

        ServerProxy proxy = ServerManager.getServerRequest();
        Call<Group> call = proxy.createGroup(group);
        ServerManager.serverRequest(call, this::groupCreated, errorCallback);
    }
    private void groupCreated(Group group){
        Log.d("Group Created", group.toString());
    }

    private void groupResult(Group group){
        setDataChanged(group);


    }
}
*/