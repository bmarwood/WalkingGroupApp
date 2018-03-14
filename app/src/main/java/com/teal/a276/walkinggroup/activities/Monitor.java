package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;
import com.teal.a276.walkinggroup.model.serverrequest.requestimplementation.MonitorRequest;
import com.teal.a276.walkinggroup.model.serverrequest.requestimplementation.MonitoredByRequest;

import java.util.List;

import retrofit2.Call;

/**
 * Activity for displaying who you monitor and who is monitoring you and for allowing the user
 * to add a monitor or a monitoree.
 */
public class Monitor extends BaseActivity {
    /**
     * Interface for abstracting out observable call
     */
    interface ObservableCallback {
        void makeRequest(String email);
    }

    User user;
    ArrayAdapter<User> monitorsAdapter;
    ArrayAdapter<User> monitoredByAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        user = ModelFacade.getInstance().getCurrentUser();
        initializeButton(R.id.addToMonitorBtn, R.string.add_user_to_monitor, this::monitorCallback);
        initializeButton(R.id.addToMonitoredByBtn, R.string.add_user_to_be_monitored_by, this::monitoredByCallback);
        initializeListViews();
    }

    private void initializeListViews() {
        monitorsAdapter = new ListItemAdapter(this, user.getMonitorsUsers());

        ListView monitoringList = findViewById(R.id.monitoringListView);
        monitoringList.setAdapter(monitorsAdapter);
        monitoringList.setOnItemClickListener((adapterView, view, i, l) -> {
            User selectedUser = user.getMonitorsUser(i);

            ServerProxy proxy = ServerManager.getServerRequest();
            Call<Void> call = proxy.endMonitoring(user.getId(), selectedUser.getId());
            ServerManager.serverRequest(call, result -> removeMonitoree(result, selectedUser), Monitor.this::error);
        });


        monitoredByAdapter = new ListItemAdapter(this, user.getMonitoredByUsers());

        ListView monitoredBy = findViewById(R.id.monitoredByListView);
        monitoredBy.setAdapter(monitoredByAdapter);
        monitoredBy.setOnItemClickListener((adapterView, view, i, l) -> {
            User selectedMonitor = user.getMonitoredByUser(i);

            ServerProxy proxy = ServerManager.getServerRequest();
            Call<Void> call = proxy.endMonitoring(selectedMonitor.getId(), user.getId());
            ServerManager.serverRequest(call, result -> removeMonitor(result, selectedMonitor), Monitor.this::error);
        });
    }

    private void removeMonitoree(Void ans, User user) {
        this.user.getMonitorsUsers().remove(user);
        updateListAdapter(R.id.monitoringListView, user, monitorsAdapter);
    }

    private void removeMonitor(Void ans, User user) {
        this.user.getMonitoredByUsers().remove(user);
        updateListAdapter(R.id.monitoredByListView, user, monitoredByAdapter);
    }

    private void updateListAdapter(int id, User user, ArrayAdapter<User> adapter) {
        adapter.remove(user);
        adapter.notifyDataSetChanged();
        findViewById(id).invalidate();
    }


    private void initializeButton(int viewId, int titleId, ObservableCallback observableCallback) {
        Button btn = findViewById(viewId);
        Resources res = getResources();
        btn.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Monitor.this);
            alertDialog.setTitle(res.getString(titleId));
            alertDialog.setCancelable(false);

            final EditText input = new EditText(Monitor.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            alertDialog.setView(input);

            alertDialog.setNegativeButton(R.string.cancel, null);
            alertDialog.setPositiveButton(R.string.add, (dialog, which) -> {
                String email = input.getText().toString();
                observableCallback.makeRequest(email);
            });

            alertDialog.show();
        });
    }

    private void monitorCallback(String email) {
        MonitorRequest strategy = new MonitorRequest(user, email, Monitor.this::error);
        strategy.makeServerRequest();
        strategy.addObserver((observable, o) -> {
            List<User> users = (List<User>) o;
            user.setMonitorsUsers(users);
            updateAdapter(monitorsAdapter, users);
        });
    }

    private void monitoredByCallback(String email) {
        MonitoredByRequest strategy = new MonitoredByRequest(user, email, Monitor.this::error);
        strategy.makeServerRequest();
        strategy.addObserver((observable, o) -> {
            List<User> users = (List<User>) o;
            user.setMonitorsUsers(users);
            updateAdapter(monitoredByAdapter, users);
        });
    }

    private void updateAdapter(ArrayAdapter<User> adapter, List<User> users) {
        adapter.clear();
        adapter.addAll(users);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, Monitor.class);
    }

    private class ListItemAdapter extends ArrayAdapter<User> {
        private final List<User> listItems;
        private final Context context;

        public ListItemAdapter(Context context, List<User> listItems) {
            super(context, R.layout.list_item, listItems);
            this.listItems = listItems;
            this.context = context;
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                itemView = inflater.inflate(R.layout.list_item, parent, false);
            }

            User user = listItems.get(position);
            TextView emailTextView = itemView.findViewById(R.id.userEmail);
            emailTextView.setText(user.getEmail());

            TextView nameTextView = itemView.findViewById(R.id.userName);
            nameTextView.setText(user.getName());

            return itemView;
        }
    }
}


