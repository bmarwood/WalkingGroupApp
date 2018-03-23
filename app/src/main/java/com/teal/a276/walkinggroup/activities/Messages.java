package com.teal.a276.walkinggroup.activities;

import android.os.Bundle;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.Message;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.MessageQueryKey;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;

public class Messages extends BaseActivity {
    RecyclerView unreadMessages;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);


        unreadMessages = findViewById(R.id.unreadMessages);
        unreadMessages.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;
                // Draw the red delete background
                ColorDrawable background = new ColorDrawable(Color.BLUE);
                background.setBounds(itemView.getLeft() + (int)dX, itemView.getTop(),
                        itemView.getLeft(), itemView.getBottom());
                background.draw(canvas);

                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d("Swiped item: ", viewHolder.getAdapterPosition() + "");

            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(itemTouchCallback);
        helper.attachToRecyclerView(unreadMessages);

        user = ModelFacade.getInstance().getCurrentUser();
        HashMap<String, Object> requestParameters = new HashMap<>();
        requestParameters.put(MessageQueryKey.FOR_USER, user.getId());
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

        RecyclerView.Adapter adapter = new TestAdapter(messagesStrings);
        RecyclerView unreadMessages = findViewById(R.id.unreadMessages);
        unreadMessages.setAdapter(adapter);
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

    private class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {
        private List<String> messages;

         class ViewHolder extends RecyclerView.ViewHolder {
             TextView mTextView;
             ViewHolder(TextView v) {
                super(v);
                mTextView = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public TestAdapter(List<String> messages) {
            this.messages = messages;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public TestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText(messages.get(position));
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }
}
