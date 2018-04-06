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

public class Store extends BaseActivity implements View.OnClickListener {

    private User user;
    private int remainingPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        user = ModelFacade.getInstance().getCurrentUser();

        setupRemainingPoints();
        setupItemClickListeners();
        setupPurchaseButtonClickListener();
        setupDefaultBtn();
    }

    private void setupDefaultBtn() {
        Button button = findViewById(R.id.storeDefaultBtn);
        button.setOnClickListener((View v) -> {
            //TODO: setup background/color palette to default

        });
    }

    private void setupRemainingPoints() {
        TextView tv = findViewById(R.id.storeRemainingPointsTv);

        //TODO: Get remaining points from server.

        int points = 1000;
        String remainingPoints = "Remaining Points: " + points;
        tv.setText(remainingPoints);
    }

    public void setupItemClickListeners(){
        ImageView one = findViewById(R.id.one);
        one.setOnClickListener(this);
        ImageView two = findViewById(R.id.two);
        two.setOnClickListener(this);
        ImageView three = findViewById(R.id.three);
        three.setOnClickListener(this);
        ImageView four = findViewById(R.id.four);
        four.setOnClickListener(this);
        ImageView five = findViewById(R.id.five);
        five.setOnClickListener(this);
        ImageView six = findViewById(R.id.six);
        six.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.one:
                switchToItem(1);
                break;
            case R.id.two:
                switchToItem(2);
                break;
            case R.id.three:
                switchToItem(3);
                break;
            case R.id.four:
                switchToItem(4);
                break;
            case R.id.five:
                switchToItem(5);
                break;
            case R.id.six:
                switchToItem(6);
                break;

        }
    }
    public void switchToItem(int id){
        Toast.makeText(this, "The Item you clicked is: " + id, Toast.LENGTH_SHORT).show();

        //TODO: switch background/colour palette based on item
        /*
        switch(id){
            case 1:

        }
*/
    }


    public void setupPurchaseButtonClickListener(){
        Button one = findViewById(R.id.button1);
        one.setOnClickListener((View v) ->{
            purchaseItem(1);
        });

        Button two = findViewById(R.id.button2);
        one.setOnClickListener((View v) ->{
            purchaseItem(2);
        });

        Button three = findViewById(R.id.button3);
        one.setOnClickListener((View v) ->{
            purchaseItem(3);
        });

        Button four = findViewById(R.id.button4);
        one.setOnClickListener((View v) ->{
            purchaseItem(4);
        });

        Button five = findViewById(R.id.button5);
        one.setOnClickListener((View v) ->{
            purchaseItem(5);
        });

        Button six = findViewById(R.id.button6);
        one.setOnClickListener((View v) ->{
            purchaseItem(6);
        });

    }


    public static Intent makeIntent(Context context) {
        return new Intent(context, Store.class);
    }


    public void purchaseItem(int id){
        switch(id) {
            case 1:
                if (remainingPoints >= 100) {
                    //TODO: unlock items


                    //TODO: push remaining points to server after subtraction of 100
                } else {
                    Toast.makeText(this, "Not Enough Points!", Toast.LENGTH_SHORT).show();
                }
                break;




        }

    }
}


























