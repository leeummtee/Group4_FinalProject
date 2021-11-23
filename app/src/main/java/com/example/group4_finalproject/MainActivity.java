package com.example.group4_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group4_finalproject.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
    //step counter reference from https://www.youtube.com/watch?v=o-qpVefrfVA&ab_channel=ProgrammerWorld
    private TextView textViewStepCounter, dateTextView;
    private double MagnitudePrevious = 0;
    private Integer stepCount = 0;
    private ProgressBar progressBar;
    int i = 0;
    public static final String DEFAULT = "not available";
    MyDatabase db;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //maps
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setContentView(R.layout.tracking_page);
        progressBar = findViewById(R.id.progressBar);

        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        //retrieving the registered login data from SharedPrefs
        int inputtedStepGoal = sharedPrefs.getInt("inputtedStepGoal", 100);

        progressBar.setMax(inputtedStepGoal);

        //https://www.youtube.com/watch?v=Le47R9H3qow&ab_channel=CodinginFlow
        dateTextView = (TextView) findViewById(R.id.dateText);
        Calendar calendar = Calendar.getInstance();
        String date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        dateTextView.setText(date);

        textViewStepCounter = findViewById(R.id.stepCounterTextView);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
//        mapIntent();

        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent != null) {
                    float x_acceleration = sensorEvent.values[0];
                    float y_acceleration = sensorEvent.values[1];
                    float z_acceleration = sensorEvent.values[2];

                    double Magnitude = Math.sqrt(x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration);
                    double MagnitudeDelta = Magnitude - MagnitudePrevious;
                    MagnitudePrevious = Magnitude;

                    if (MagnitudeDelta > 1) {
                        stepCount++;
                        progressBar.setProgress(stepCount);
                    }
                    textViewStepCounter.setText(stepCount.toString());
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng surreyCentral = new LatLng(49.2827,-123.1287);
        googleMap.addMarker(new MarkerOptions()
                .position(surreyCentral)
                .title("Marker"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(surreyCentral, 10));
    }

    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount", stepCount);
        editor.apply();
    }

    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        stepCount = sharedPreferences.getInt("stepCount", 0);
    }

    public void goToGoals(View view) {
        Intent intent = new Intent(this, GoalsActivity.class);
        startActivity(intent);
    }

    public void mapIntent() {
        //map point based on latitude/longitude//
        Uri location = Uri.parse("geo:37.422219,-122.08364?z=14"); // z param is zoom level
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        startActivity(mapIntent);
    }

//    private void setUpMapIfNeeded() {
//        // Do a null check to confirm that we have not already instantiated the map.
//        if (mMap == null) {
//            // Try to obtain the map from the SupportMapFragment.
//            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView)).getMap();
//            mMap.setMyLocationEnabled(true);
//            // Check if we were successful in obtaining the map.
//            if (mMap != null) {
//                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//                    @Override
//                    public void onMyLocationChange(Location arg0) {
//                        mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
//                    }
//                });
//            }
//        }
//    }

    public void addData(View view) {
        String name = textViewStepCounter.getText().toString();
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
    }

    public void calculateKCal(Integer g) {

    }

}