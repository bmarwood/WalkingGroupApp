package com.teal.a276.walkinggroup.activities;

import android.os.Bundle;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverrequest.requestimplementation.CompleteUserRequest;

import android.view.MenuItem;

import retrofit2.Call;

public class Welcome extends BaseActivity {
    User user = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        user.setEmail("c@test.com");
        user.setPassword("1234");

        ServerProxy proxy = ServerManager.getServerRequest();
        Call<Void> call = proxy.login(user);
        ServerManager.serverRequest(call, this::login, this::error);


    }

    private void login(Void ans) {
        CompleteUserRequest strategy = new CompleteUserRequest(user, this::error);
        strategy.makeServerRequest();
        strategy.addObserver((observable, o) -> ModelFacade.getInstance().setCurrentUser((User)o));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapItem:
                startActivity(MapsActivity.makeIntent(this));
                break;
            case R.id.groupItem:
                startActivity(JoinGroup.makeIntent(this));
                break;
            case R.id.monitorItem:
                startActivity(Monitor.makeIntent(this));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
