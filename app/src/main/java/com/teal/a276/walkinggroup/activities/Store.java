package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.map.MapsActivity;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.UnlockedRewards;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class Store extends BaseActivity implements View.OnClickListener {

    private static final String PREFS_NAME = "prefs";

    private User user;
    private Integer remainingPoints;
    private ImageView itemOne;
    private ImageView itemTwo;
    private ImageView itemThree;
    private ImageView itemFour;
    private ImageView itemFive;
    private ImageView itemSix;
    private Button btnOne;
    private Button btnTwo;
    private Button btnThree;
    private Button btnFour;
    private Button btnFive;
    private Button btnSix;
    List<ImageView> allItems = new ArrayList<>();
    String retrievedJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        user = ModelFacade.getInstance().getCurrentUser();

        setupAllElements();
        updateRemainingPoints();
        updateAvailableItems();
        setupItemClickListeners();
        setupPurchaseButtonClickListener();
        setupDefaultBtn();
        setupApplyBtn();
    }


    private void setupAllElements() {
        itemOne = findViewById(R.id.one);
        itemOne.setEnabled(false);
        itemTwo = findViewById(R.id.two);
        itemTwo.setEnabled(false);
        itemThree = findViewById(R.id.three);
        itemThree.setEnabled(false);
        itemFour = findViewById(R.id.four);
        itemFour.setEnabled(false);
        itemFive = findViewById(R.id.five);
        itemFive.setEnabled(false);
        itemSix = findViewById(R.id.six);
        itemSix.setEnabled(false);

        allItems.add(itemOne);
        allItems.add(itemTwo);
        allItems.add(itemThree);
        allItems.add(itemFour);
        allItems.add(itemFive);
        allItems.add(itemSix);

        btnOne = findViewById(R.id.button1);
        btnTwo = findViewById(R.id.button2);
        btnThree = findViewById(R.id.button3);
        btnFour= findViewById(R.id.button4);
        btnFive = findViewById(R.id.button5);
        btnSix = findViewById(R.id.button6);

    }

    private void updateAvailableItems(){

        retrievedJson = user.getCustomJson();
        UnlockedRewards retrievedRewards = new UnlockedRewards();

        if(retrievedJson != null) {

            try {
                retrievedRewards =
                        new ObjectMapper().readValue(
                                retrievedJson,
                                UnlockedRewards.class);
                Log.w("deserialize", "De-serialized embedded rewards object: " + retrievedRewards);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<Integer> retrieved = retrievedRewards.getUnlockedItems();

        if(retrieved.contains(1)){
            itemOne.setEnabled(true);
            btnOne.setVisibility(View.GONE);
        }
        if(retrieved.contains(2)){
            itemTwo.setEnabled(true);
            btnTwo.setVisibility(View.GONE);
        }
        if(retrieved.contains(3)){
            itemThree.setEnabled(true);
            btnThree.setVisibility(View.GONE);
        }
        if(retrieved.contains(4)){
            itemFour.setEnabled(true);
            btnFour.setVisibility(View.GONE);
        }
        if(retrieved.contains(5)){
            itemFive.setEnabled(true);
            btnFive.setVisibility(View.GONE);
        }
        if(retrieved.contains(6)){
            itemSix.setEnabled(true);
            btnFive.setVisibility(View.GONE);
        }

    }


    private void setupDefaultBtn() {

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt("currTheme", R.style.AppTheme);
        editor.apply();

        Button button = findViewById(R.id.storeDefaultBtn);
        button.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    private void setupApplyBtn() {
        Button button = findViewById(R.id.storeApplyBtn);
        button.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void updateRemainingPoints() {
        TextView tv = findViewById(R.id.storeRemainingPointsTv);
        remainingPoints = user.getCurrentPoints();
        String remainingPointsString = getString(R.string.store_remaining_points) + remainingPoints;
        tv.setText(remainingPointsString);
    }

    //If item is already bought, remove purchase button.
    public void setupItemClickListeners(){
        for(ImageView imageView : allItems){
            if(imageView.isEnabled()){
                imageView.setOnClickListener(this);
            }
        }
    }

    //ClickListner for Switching themes/backgrounds
    @Override
    public void onClick(View v) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        switch(v.getId()){
            case R.id.one:
                switchToItem(1);
                editor.putInt("currTheme", getNewTheme("boxes", null));
                editor.apply();
                break;
            case R.id.two:
                switchToItem(2);
                editor.putInt("currTheme", getNewTheme("circle", null));
                editor.apply();
                break;
            case R.id.three:
                switchToItem(3);
                editor.putInt("currTheme", getNewTheme("wave", null));
                editor.apply();
                break;
            case R.id.four:
                switchToItem(4);
                editor.putInt("currTheme", getNewTheme(null, "blue"));
                editor.apply();
                break;
            case R.id.five:
                switchToItem(5);
                editor.putInt("currTheme", getNewTheme(null, "green"));
                editor.apply();
                break;
            case R.id.six:
                switchToItem(6);
                editor.putInt("currTheme", getNewTheme(null, "purple"));
                editor.apply();
                break;

        }
    }

    private int getNewTheme(String newBackground, String newColor) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currTheme = preferences.getInt("currTheme", -1);
        String type = "default";
        String color = "default";

        if (currTheme == R.style.AppTheme || currTheme == R.style.AppTheme_Dark_Green_Box ||
                currTheme == R.style.AppTheme_Dark_Purple_Box || currTheme == R.style.AppTheme_Light_Blue_Box) {
            type = "boxes";
        } else if (currTheme == R.style.AppTheme_circle || currTheme == R.style.AppTheme_Dark_Green_Circle ||
                currTheme == R.style.AppTheme_Dark_Purple_Circle || currTheme == R.style.AppTheme_Light_Blue_Circle) {
            type = "circle";
        } else if (currTheme == R.style.AppTheme_wave || currTheme == R.style.AppTheme_Dark_Green_Wave ||
                currTheme == R.style.AppTheme_Dark_Purple_Wave || currTheme == R.style.AppTheme_Light_Blue_Wave) {
            type = "wave";
        }

        if (currTheme == R.style.AppTheme_Light_Blue_Box || currTheme == R.style.AppTheme_Light_Blue_Circle ||
                currTheme == R.style.AppTheme_Light_Blue_Wave) {
            color = "blue";
        } else if (currTheme == R.style.AppTheme_Dark_Purple_Box || currTheme == R.style.AppTheme_Dark_Purple_Circle ||
                currTheme == R.style.AppTheme_Dark_Purple_Wave) {
            color = "purple";
        } else if (currTheme == R.style.AppTheme_Dark_Green_Box || currTheme == R.style.AppTheme_Dark_Green_Circle ||
                currTheme == R.style.AppTheme_Dark_Green_Wave) {
            color = "green";
        }

        //dealing with new background
        if (newColor == null) {
            if (newBackground.equals("boxes")) {
                if (color.equals("blue")) {
                    return R.style.AppTheme_Light_Blue_Box;
                } else if (color.equals("purple")) {
                    return R.style.AppTheme_Dark_Purple_Box;

                } else if (color.equals("green")) {
                    return R.style.AppTheme_Dark_Green_Box;

                } else {
                    return R.style.AppTheme;
                }
            } else if (newBackground.equals("circle")) {
                if (color.equals("blue")) {
                    return R.style.AppTheme_Light_Blue_Circle;
                } else if (color.equals("purple")) {
                    return R.style.AppTheme_Dark_Purple_Circle;

                } else if (color.equals("green")) {
                    return R.style.AppTheme_Dark_Green_Circle;

                } else {
                    return R.style.AppTheme_circle;
                }
            } else if (newBackground.equals("wave")) {
                if (color.equals("blue")) {
                    return R.style.AppTheme_Light_Blue_Wave;
                } else if (color.equals("purple")) {
                    return R.style.AppTheme_Dark_Purple_Wave;
                } else if (color.equals("green")) {
                    return R.style.AppTheme_Dark_Green_Wave;

                } else {
                    return R.style.AppTheme_wave;
                }
            }
        }


        //dealing with new color
        if (newBackground == null) {
            if (newColor.equals("blue")) {
                if (type.equals("boxes")) {
                    return R.style.AppTheme_Light_Blue_Box;
                } else if (type.equals("wave")) {
                    return R.style.AppTheme_Light_Blue_Wave;

                } else if (type.equals("circle")) {
                    return R.style.AppTheme_Light_Blue_Circle;

                } else {
                    return R.style.AppTheme;
                }
            } else if (newColor.equals("purple")) {
                if (type.equals("boxes")) {
                    return R.style.AppTheme_Dark_Purple_Box;
                } else if (type.equals("wave")) {
                    return R.style.AppTheme_Dark_Purple_Wave;

                } else if (type.equals("circle")) {
                    return R.style.AppTheme_Dark_Purple_Circle;

                } else {
                    return R.style.AppTheme_circle;
                }
            } else if (newColor.equals("green")) {
                if (type.equals("boxes")) {
                    return R.style.AppTheme_Dark_Green_Box;
                } else if (type.equals("wave")) {
                    return R.style.AppTheme_Dark_Green_Wave;
                } else if (type.equals("circle")) {
                    return R.style.AppTheme_Dark_Green_Circle;

                } else {
                    return R.style.AppTheme_wave;
                }
            }
        }
        return -1;
    }



    public void switchToItem(int id){
        Toast.makeText(this, "The Item you clicked is: " + id, Toast.LENGTH_SHORT).show();






        /*
        switch(id){
            case 1:

        }
*/
    }


    public void setupPurchaseButtonClickListener(){
        btnOne.setOnClickListener((View v) -> purchaseItem(1));
        btnTwo.setOnClickListener((View v) -> purchaseItem(2));
        btnThree.setOnClickListener((View v) -> purchaseItem(3));
        btnFour.setOnClickListener((View v) -> purchaseItem(4));
        btnFive.setOnClickListener((View v) -> purchaseItem(5));
        btnSix.setOnClickListener((View v) -> purchaseItem(6));

    }


    public static Intent makeIntent(Context context) {
        return new Intent(context, Store.class);
    }


    public void purchaseItem(int id){
        switch(id) {
            case 1:
                if (remainingPoints >= 100) {
                    Toast.makeText(this, R.string.store_purchase_successful, Toast.LENGTH_SHORT).show();
                    btnOne.setVisibility(View.GONE);
                    itemOne.setEnabled(true);
                    user.setCurrentPoints(user.getCurrentPoints() - 100);

                    ServerProxy proxy = ServerManager.getServerProxy();
                    Call<User> caller = proxy.updateUser(user.getId(), user, 1L);
                    ServerManager.serverRequest(caller, this::updatePoints, this::error);


                    retrievedJson = user.getCustomJson();

                    UnlockedRewards retrievedRewards = new UnlockedRewards();

                    if(retrievedJson != null) {

                        try {
                            retrievedRewards =
                                    new ObjectMapper().readValue(
                                            retrievedJson,
                                            UnlockedRewards.class);
                            Log.w("deserialize", "De-serialized embedded rewards object: " + retrievedRewards);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    List<Integer> retrieved = retrievedRewards.getUnlockedItems();
                    retrieved.add(1);
                    UnlockedRewards reward = new UnlockedRewards();
                    reward.setUnlockedItems(retrieved);

                    String customJson = null;
                    try {
                        // Convert custom object to a JSON string:
                        customJson = new ObjectMapper().writeValueAsString(reward);
                        // Store JSON string into the user object, which will be sent to server.
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    if (customJson != null) {
                        user.setCustomJson(customJson);
                        Call<User> result = proxy.updateUser(user.getId(), user, 1L);
                        ServerManager.serverRequest(result, null, this::error);
                    }

                    updateRemainingPoints();

                } else {
                    Toast.makeText(this, R.string.store_not_enough_points, Toast.LENGTH_SHORT).show();
                }
                break;

            case 2:
                if (remainingPoints >= 100) {
                    Toast.makeText(this, R.string.store_purchase_successful, Toast.LENGTH_SHORT).show();
                    btnTwo.setVisibility(View.GONE);
                    itemTwo.setEnabled(true);
                    user.setCurrentPoints(user.getCurrentPoints() - 100);

                    ServerProxy proxy = ServerManager.getServerProxy();
                    Call<User> caller = proxy.updateUser(user.getId(), user, 1L);
                    ServerManager.serverRequest(caller, this::updatePoints, this::error);

                    retrievedJson = user.getCustomJson();
                    UnlockedRewards retrievedRewards = new UnlockedRewards();

                    if(retrievedJson != null) {
                        try {
                            retrievedRewards =
                                    new ObjectMapper().readValue(
                                            retrievedJson,
                                            UnlockedRewards.class);
                            Log.w("deserialize", "De-serialized embedded rewards object: " + retrievedRewards);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    List<Integer> retrieved = retrievedRewards.getUnlockedItems();
                    retrieved.add(2);
                    UnlockedRewards reward = new UnlockedRewards();
                    reward.setUnlockedItems(retrieved);

                    String customJson = null;
                    try {
                        // Convert custom object to a JSON string:
                        customJson = new ObjectMapper().writeValueAsString(reward);
                        // Store JSON string into the user object, which will be sent to server.
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    if (customJson != null) {
                        user.setCustomJson(customJson);
                        Call<User> result = proxy.updateUser(user.getId(), user, 1L);
                        ServerManager.serverRequest(result, null, this::error);
                    }

                    updateRemainingPoints();

                } else {
                    Toast.makeText(this, R.string.store_not_enough_points, Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                if (remainingPoints >= 100) {
                Toast.makeText(this, R.string.store_purchase_successful, Toast.LENGTH_SHORT).show();
                btnThree.setVisibility(View.GONE);
                itemThree.setEnabled(true);
                user.setCurrentPoints(user.getCurrentPoints() - 100);

                ServerProxy proxy = ServerManager.getServerProxy();
                Call<User> caller = proxy.updateUser(user.getId(), user, 1L);
                ServerManager.serverRequest(caller, this::updatePoints, this::error);

                retrievedJson = user.getCustomJson();
                UnlockedRewards retrievedRewards = new UnlockedRewards();

                if(retrievedJson != null) {
                    try {
                        retrievedRewards =
                                new ObjectMapper().readValue(
                                        retrievedJson,
                                        UnlockedRewards.class);
                        Log.w("deserialize", "De-serialized embedded rewards object: " + retrievedRewards);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                List<Integer> retrieved = retrievedRewards.getUnlockedItems();
                retrieved.add(3);
                UnlockedRewards reward = new UnlockedRewards();
                reward.setUnlockedItems(retrieved);

                String customJson = null;
                try {
                    // Convert custom object to a JSON string:
                    customJson = new ObjectMapper().writeValueAsString(reward);
                    // Store JSON string into the user object, which will be sent to server.
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                if (customJson != null) {
                    user.setCustomJson(customJson);
                    Call<User> result = proxy.updateUser(user.getId(), user, 1L);
                    ServerManager.serverRequest(result, null, this::error);
                }

                updateRemainingPoints();

                } else {
                    Toast.makeText(this, R.string.store_not_enough_points, Toast.LENGTH_SHORT).show();
                }
                break;

            case 4:
                if (remainingPoints >= 100) {
                    Toast.makeText(this, R.string.store_purchase_successful, Toast.LENGTH_SHORT).show();
                    btnFour.setVisibility(View.GONE);
                    itemFour.setEnabled(true);
                    user.setCurrentPoints(user.getCurrentPoints() - 100);

                    ServerProxy proxy = ServerManager.getServerProxy();
                    Call<User> caller = proxy.updateUser(user.getId(), user, 1L);
                    ServerManager.serverRequest(caller, this::updatePoints, this::error);

                    retrievedJson = user.getCustomJson();
                    UnlockedRewards retrievedRewards = new UnlockedRewards();

                    if(retrievedJson != null) {
                        try {
                            retrievedRewards =
                                    new ObjectMapper().readValue(
                                            retrievedJson,
                                            UnlockedRewards.class);
                            Log.w("deserialize", "De-serialized embedded rewards object: " + retrievedRewards);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    List<Integer> retrieved = retrievedRewards.getUnlockedItems();
                    retrieved.add(4);
                    UnlockedRewards reward = new UnlockedRewards();
                    reward.setUnlockedItems(retrieved);

                    String customJson = null;
                    try {
                        // Convert custom object to a JSON string:
                        customJson = new ObjectMapper().writeValueAsString(reward);
                        // Store JSON string into the user object, which will be sent to server.
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    if (customJson != null) {
                        user.setCustomJson(customJson);
                        Call<User> result = proxy.updateUser(user.getId(), user, 1L);
                        ServerManager.serverRequest(result, null, this::error);
                    }

                    updateRemainingPoints();

                } else {
                    Toast.makeText(this, R.string.store_not_enough_points, Toast.LENGTH_SHORT).show();
                }
                break;


            case 5:
                if (remainingPoints >= 100) {
                    Toast.makeText(this, R.string.store_purchase_successful, Toast.LENGTH_SHORT).show();
                    btnFive.setVisibility(View.GONE);
                    itemFive.setEnabled(true);
                    user.setCurrentPoints(user.getCurrentPoints() - 100);

                    ServerProxy proxy = ServerManager.getServerProxy();
                    Call<User> caller = proxy.updateUser(user.getId(), user, 1L);
                    ServerManager.serverRequest(caller, this::updatePoints, this::error);

                    retrievedJson = user.getCustomJson();
                    UnlockedRewards retrievedRewards = new UnlockedRewards();

                    if(retrievedJson != null) {
                        try {
                            retrievedRewards =
                                    new ObjectMapper().readValue(
                                            retrievedJson,
                                            UnlockedRewards.class);
                            Log.w("deserialize", "De-serialized embedded rewards object: " + retrievedRewards);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    List<Integer> retrieved = retrievedRewards.getUnlockedItems();
                    retrieved.add(5);
                    UnlockedRewards reward = new UnlockedRewards();
                    reward.setUnlockedItems(retrieved);

                    String customJson = null;
                    try {
                        // Convert custom object to a JSON string:
                        customJson = new ObjectMapper().writeValueAsString(reward);
                        // Store JSON string into the user object, which will be sent to server.
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    if (customJson != null) {
                        user.setCustomJson(customJson);
                        Call<User> result = proxy.updateUser(user.getId(), user, 1L);
                        ServerManager.serverRequest(result, null, this::error);
                    }

                    updateRemainingPoints();

                } else {
                    Toast.makeText(this, R.string.store_not_enough_points, Toast.LENGTH_SHORT).show();
                }
                break;


            case 6:
                if (remainingPoints >= 100) {
                    Toast.makeText(this, R.string.store_purchase_successful, Toast.LENGTH_SHORT).show();
                    btnSix.setVisibility(View.GONE);
                    itemSix.setEnabled(true);
                    user.setCurrentPoints(user.getCurrentPoints() - 100);

                    ServerProxy proxy = ServerManager.getServerProxy();
                    Call<User> caller = proxy.updateUser(user.getId(), user, 1L);
                    ServerManager.serverRequest(caller, this::updatePoints, this::error);

                    retrievedJson = user.getCustomJson();
                    UnlockedRewards retrievedRewards = new UnlockedRewards();

                    if(retrievedJson != null) {
                        try {
                            retrievedRewards =
                                    new ObjectMapper().readValue(
                                            retrievedJson,
                                            UnlockedRewards.class);
                            Log.w("deserialize", "De-serialized embedded rewards object: " + retrievedRewards);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    List<Integer> retrieved = retrievedRewards.getUnlockedItems();
                    retrieved.add(6);
                    UnlockedRewards reward = new UnlockedRewards();
                    reward.setUnlockedItems(retrieved);

                    String customJson = null;
                    try {
                        // Convert custom object to a JSON string:
                        customJson = new ObjectMapper().writeValueAsString(reward);
                        // Store JSON string into the user object, which will be sent to server.
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    if (customJson != null) {
                        user.setCustomJson(customJson);
                        Call<User> result = proxy.updateUser(user.getId(), user, 1L);
                        ServerManager.serverRequest(result, null, this::error);
                    }

                    updateRemainingPoints();

                } else {
                    Toast.makeText(this, R.string.store_not_enough_points, Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }
    public void updatePoints(User user){
        ModelFacade.getInstance().setCurrentUser(user);
    }
}
