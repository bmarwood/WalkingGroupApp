package com.teal.a276.walkinggroup.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.Message;
import com.teal.a276.walkinggroup.model.dataobjects.MessageQueryKey;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

public class Messages extends BaseActivity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        user = ModelFacade.getInstance().getCurrentUser();
        HashMap<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(MessageQueryKey.FOR_USER.toString(), user.getId());
        ServerProxy proxy = ServerManager.getServerRequest();
        Call<List<Message>> call = proxy.getMessages(requestParameters);
        ServerManager.serverRequest(call, this::userMessages, this::error);
    }

    @Override
    public void onResume() {
        super.onResume();
        user = ModelFacade.getInstance().getCurrentUser();
    }

    private void userMessages(List<Message> messages) {
        Log.d("Got messages", messages.toString());
        List<String> messagesStrings = new ArrayList<>();
        for(Message m : messages) {
            messagesStrings.add(m.getText());
        }

        ArrayAdapter<String> messagesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, messagesStrings);
        ListView unreadMessages = findViewById(R.id.unreadMessages);
        unreadMessages.setAdapter(messagesAdapter);
    }

    public void sendMessage(View v) {

        Message message = new Message();
        message.setText("This is a test message");

        ServerProxy proxy = ServerManager.getServerRequest();
        Call<Message> call = proxy.sendMessageToMonitors(237L, message);
        ServerManager.serverRequest(call, this::messageSent, this::error);
    }

    private void messageSent(Message m) {
        Log.d("Message sent", m.getText());
    }
}
