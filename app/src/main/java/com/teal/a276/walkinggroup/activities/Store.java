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
    private int remainingPoints = 1000;
    private ImageView itemOne;
    private ImageView itemTwo;
    private ImageView itemThree;
    private Button btn1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        user = ModelFacade.getInstance().getCurrentUser();

        itemOne = findViewById(R.id.one);
        itemOne.setEnabled(false);
        itemTwo = findViewById(R.id.two);
        itemTwo.setEnabled(true);
        itemThree = findViewById(R.id.three);
        itemThree.setEnabled(false);

        btn1 = findViewById(R.id.button1);


        updateRemainingPoints();
        updateAvailableItems();
        setupItemClickListeners();
        setupPurchaseButtonClickListener();
        setupDefaultBtn();
    }

    private void updateAvailableItems(){
        //TODO: server check to see which items are already unlocked and then enable the unlocked items





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

        //TODO: Get remaining points from server.

        //fake the points first

        String remainingPointsString = "Remaining Points: " + remainingPoints;

        tv.setText(remainingPointsString);
    }

    //If item is already bought, remove purchase button.
    public void setupItemClickListeners(){
        if(itemOne.isEnabled()) {
            itemOne.setOnClickListener(this);
            btn1.setVisibility(View.GONE);
        }
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
        btn1.setOnClickListener((View v) ->{
            purchaseItem(1);
        });

    }


    public static Intent makeIntent(Context context) {
        return new Intent(context, Store.class);
    }


    public void purchaseItem(int id){
        switch(id) {
            case 1:
                if (remainingPoints >= 100) {
                    //TODO: unlock items, push customJson to server
                    Toast.makeText(this, "Purchase successful!", Toast.LENGTH_SHORT).show();


                    btn1.setVisibility(View.GONE);
                    itemOne.setEnabled(true);

                    //TODO: push remaining points to server after subtraction of 100
                    remainingPoints -= 100;
                    updateRemainingPoints();



                } else {
                    Toast.makeText(this, "Not Enough Points!", Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }
}
