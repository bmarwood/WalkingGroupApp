package com.teal.a276.walkinggroup.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.map.MapsActivity;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;

public class Store extends BaseActivity implements View.OnClickListener {

    private static final String PREFS_NAME = "prefs";

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
        setupApplyBtn();
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


























