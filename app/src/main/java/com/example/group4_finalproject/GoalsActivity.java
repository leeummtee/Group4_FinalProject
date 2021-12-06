//this class will handle inputs from the user
//currently this class can set the amount of steps that the user wants to set up as a goal

package com.example.group4_finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Calendar;

public class GoalsActivity extends AppCompatActivity {
    private Integer goal;
    private EditText stepGoalEditText, destinationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goals);
        stepGoalEditText = (EditText)findViewById(R.id.stepGoalEditText);
        destinationEditText = (EditText)findViewById(R.id.destinationEditText);
    }

    //this method will take the input from the user as a string and then parse it as an int
    //this will then be placed within a sharedpreference method which then be passed to the main activity
    public void setGoals (View view) {
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
//        editor.putString("inputtedStepGoal", stepGoalEditText.getText().toString());
        goal = Integer.parseInt(stepGoalEditText.getText().toString());
        editor.putInt("inputtedStepGoal", goal);
        editor.commit();


        Toast.makeText(this, "Goals saved. Heading to the Tracking page.", Toast.LENGTH_LONG).show();
        Intent intent= new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToHome(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToProfile(View v) {
        Intent intent = new Intent(this, ProfileStats.class);
        startActivity(intent);
    }

}
