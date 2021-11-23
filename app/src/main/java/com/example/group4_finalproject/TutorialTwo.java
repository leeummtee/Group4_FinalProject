package com.example.group4_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialTwo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_2);
    }

    public void tutButton (View view) {
//        Toast.makeText(this, "Goals saved. Heading to the Tracking page.", Toast.LENGTH_LONG).show();
        Intent intent= new Intent(this, TutorialThree.class);
        startActivity(intent);
    }
}
