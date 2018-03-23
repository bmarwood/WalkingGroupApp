package com.teal.a276.walkinggroup.model.serverrequest.requestimplementation;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
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

    //private final LatLng latlng;
    private final String leaderEmail;
    private final String groupDes;

    //added
    private final LatLng meetingLatLng;
    private final LatLng destLatLng;

    //added
    public CreateGroupRequest(String leaderEmail, String groupDes, LatLng meetingLatLng, LatLng destLatLng, @NonNull ServerError errorCallback) {
    //public CreateGroupRequest(String leaderEmail, String groupDes, LatLng latLng, @NonNull ServerError errorCallback) {
        super(null, errorCallback);
        this.leaderEmail = leaderEmail;
        this.groupDes = groupDes;
        //this.latlng = latLng;

        //added
        this.meetingLatLng = meetingLatLng;
        this.destLatLng = destLatLng;
    }

    @Override
    public void makeServerRequest() {
        getUserForEmail(leaderEmail, this::userFromEmail);

    }

    private void userFromEmail(User user) {
        Group group = new Group();
        group.setLeader(user);

        //TODO: Remove this line of code after new server has been pushed.
        //group.setId(-1L);

        group.setGroupDescription(groupDes);

        List<Double> latArray = new ArrayList<>();
        List<Double> lngArray = new ArrayList<>();

        //latArray.add(latlng.latitude);
        //lngArray.add(latlng.longitude);

        //added
        latArray.add(meetingLatLng.latitude);
        latArray.add(destLatLng.latitude);
        lngArray.add(meetingLatLng.longitude);
        lngArray.add(destLatLng.longitude);

        group.setRouteLatArray(latArray);
        group.setRouteLngArray(lngArray);

        ServerProxy proxy = ServerManager.getServerRequest();
        Call<Group> call = proxy.createGroup(group);
        ServerManager.serverRequest(call, this::groupResult, errorCallback);
    }

    private void groupResult(Group group){
        setDataChanged(group);
    }
}
