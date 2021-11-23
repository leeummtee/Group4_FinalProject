package com.example.group4_finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.location.LocationListener;
import android.location.LocationManager;

import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
    private static final int PERMISSION_REQUEST_CODE = 1;
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
    private boolean isMoving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("numOfStepsOnCreate", String.valueOf(stepCount));


        //maps
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setContentView(R.layout.tracking_page);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(stepCount);

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


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }



        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (isMoving == false) stepCount = 0;
                if (sensorEvent != null) {
                    isMoving = true;
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




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

//        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//            && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                Log.d("testtt", "YOOOO");
//                return;
//        }
//        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        double longitude = location.getLongitude();
//        double latitude = location.getLatitude();
//
////        Log.d("longTest", String.valueOf(longitude));
////        Log.d("latTest", String.valueOf(latitude));
////
////        Log.d("testtt", "YOOOO");

        LatLng surreyCentral = new LatLng(49.1879,-122.8500);
//        LatLng surreyCentral = new LatLng(latitude,longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(surreyCentral)
                .title("Marker"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(surreyCentral, 15));
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