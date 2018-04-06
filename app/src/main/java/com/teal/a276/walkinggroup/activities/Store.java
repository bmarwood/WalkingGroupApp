package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class Store extends BaseActivity implements View.OnClickListener {

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
        //TODO: server check to see which items are already unlocked and then enable the unlocked items

        retrievedJson = user.getCustomJson();

        //one.setEnabled(true);
    }


    private void setupDefaultBtn() {
        Button button = findViewById(R.id.storeDefaultBtn);
        button.setOnClickListener((View v) -> {
            //TODO: setup background/color palette to default

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
        switch(v.getId()){
            case R.id.one:
                break;
            case R.id.two:
                break;
            case R.id.three:
                break;
            case R.id.four:
                break;
            case R.id.five:
                break;
            case R.id.six:
                break;
        }
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
