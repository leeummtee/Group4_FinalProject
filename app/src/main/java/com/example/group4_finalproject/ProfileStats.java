package com.example.group4_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ProfileStats extends MainActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_stats);
    }

    public void goToHome(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToGoals(View v) {
        Intent intent = new Intent(this, GoalsActivity.class);
        startActivity(intent);
    }
}
