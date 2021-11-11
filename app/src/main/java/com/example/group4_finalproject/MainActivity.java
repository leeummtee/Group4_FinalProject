package com.example.group4_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//    https://www.youtube.com/watch?v=NnvJylicKvE&ab_channel=SarthiTechnology
    private TextView textViewStepCounter, textViewStepDetector;
    private SensorManager sensorManger;
    private Sensor myStepCounter;
    private boolean isCounterSensorPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_1);
    }

    @Override
    public void onClick(View view) {
    }

    //hello, how are you, I am underwater now HUhuhuHUHUHUHUH
    //YO WAT UP
}