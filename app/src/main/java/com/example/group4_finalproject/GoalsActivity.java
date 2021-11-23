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
    private EditText stepGoalEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goals);
        stepGoalEditText = (EditText)findViewById(R.id.stepGoalEditText);
    }

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

//    public void addData(View view) {
//        String name = textViewStepCounter.getText().toString();
//        Toast.makeText(this, plantName, Toast.LENGTH_SHORT).show();
//        long id = db.insertData(steps, textViewStepCounter);
//        if (id < 0)
//        {
//            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
//        }
//    }
}
