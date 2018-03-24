package com.teal.a276.walkinggroup.activities.message;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.BaseActivity;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.Message;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.MessageRequestConstant;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

public class Messages extends BaseActivity {
    RecyclerView unreadMessagesView;
    ArrayAdapter<String> readMessagesAdapter;
    ListView readMessagesView;
    Drawable readIcon;
    List<Message> unreadMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        User user = ModelFacade.getInstance().getCurrentUser();
        readIcon = getResources().getDrawable(R.drawable.ic_read);
        unreadMessagesView = findViewById(R.id.unreadMessages);
        readMessagesView = findViewById(R.id.readMessages);

        unreadMessagesView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper.SimpleCallback messageTouchHelper = new MessageTouchHelper(0, ItemTouchHelper.RIGHT, readIcon) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Message message = unreadMessages.get(viewHolder.getAdapterPosition());
                ServerProxy proxy = ServerManager.getServerRequest();
                Call<User> call = proxy.setMessageRead(message.getId(), user.getId(), true);
                ServerManager.serverRequest(call,
                        result -> messageMarkedAsRead(result, viewHolder.getAdapterPosition()),
                        error -> resetList(error, viewHolder.getAdapterPosition()));
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(messageTouchHelper);
        helper.attachToRecyclerView(unreadMessagesView);

        Call<List<Message>> call = requestMessages(user.getId(), MessageRequestConstant.UNREAD);
        ServerManager.serverRequest(call, this::unreadMessagesResult, this::error);
        call = requestMessages(user.getId(), MessageRequestConstant.READ);
        ServerManager.serverRequest(call, this::readMessagesResult, this::error);
    }


    private Call<List<Message>> requestMessages(Long userId, String status) {
        HashMap<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(MessageRequestConstant.FOR_USER, userId);
        requestParameters.put(MessageRequestConstant.STATUS, status);
        ServerProxy proxy = ServerManager.getServerRequest();

        return proxy.getMessages(requestParameters);
    }

    private void unreadMessagesResult(List<Message> messages) {
        this.unreadMessages = messages;
        RecyclerView.Adapter adapter = new MessagesAdapter(messages);
        unreadMessagesView.setAdapter(adapter);
    }

    private void readMessagesResult(List<Message> messages) {
        readMessagesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, messagesToStringList(messages));
        readMessagesView.setAdapter(readMessagesAdapter);
    }

    private List<String> messagesToStringList(List<Message> messages) {
        ArrayList<String> messagesText = new ArrayList<>(messages.size());
        for(Message message : messages) {
            messagesText.add(message.getText());
        }

        return messagesText;
    }

    private void messageMarkedAsRead(User user, int messageIndex) {
        Message readMessage = this.unreadMessages.get(messageIndex);
        MessagesAdapter adapter = (MessagesAdapter) unreadMessagesView.getAdapter();
        adapter.removeAt(messageIndex);

        readMessagesAdapter.add(readMessage.getText());
        readMessagesAdapter.notifyDataSetChanged();
        readMessagesView.invalidateViews();
    }

    private void resetList(String error, int messageIndex) {
        Message swipedMessage = this.unreadMessages.get(messageIndex);
        MessagesAdapter adapter = (MessagesAdapter) unreadMessagesView.getAdapter();
        adapter.removeAt(messageIndex);
        adapter.addMessageAt(swipedMessage, messageIndex);

        super.error(error);
    }

    public void sendMessage(View v) {
        Message message = new Message();
        message.setText("This is a test message");

        ServerProxy proxy = ServerManager.getServerRequest();
        Call<Message> call = proxy.sendMessageToMonitors(52L, message);
        ServerManager.serverRequest(call, this::messageSent, this::error);
    }

    private void messageSent(Message m) {
        Log.d("Message sent", m.getText());
    }
}
