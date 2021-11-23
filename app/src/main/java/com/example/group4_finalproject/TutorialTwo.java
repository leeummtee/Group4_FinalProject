package com.example.group4_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

//page for displaying the first page of the tutorial
public class TutorialTwo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_2);
    }

    public void tutButton (View view) {
        Intent intent= new Intent(this, TutorialThree.class);
        startActivity(intent);
    }
}
