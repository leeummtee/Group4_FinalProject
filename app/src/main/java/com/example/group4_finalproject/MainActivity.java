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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group4_finalproject.databinding.ActivityMapsBinding;
import com.example.group4_finalproject.directionhelpers.FetchURL;
import com.example.group4_finalproject.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//tracking page, which includes the step counter and map
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,
        TaskLoadedCallback {
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

    private static final double
            VANCOUVER_LAT =  49.18953323364258,
            VANCOUVER_LNG = -122.84791564941406,
            DESTINATION_LAT = 49.185547,
            DESTINATION_LNG = -122.843441;

    MarkerOptions place1, place2;
    Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d("numOfStepsOnCreate", String.valueOf(stepCount));

        //maps
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setContentView(R.layout.tracking_page);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(stepCount);

        //creating a sharedprefs object
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        //retrieving the registered login data from SharedPrefs
        int inputtedStepGoal = sharedPrefs.getInt("inputtedStepGoal", 100);

        //setting the progress bar's max value to the goal set by the user
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
//        MapFragment mapFragment = (MapFragment) getFragmentManager()
//                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
//        mapIntent();

        //checking permissions
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }

        //step counter
        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Log.d("numOfStepsOnCreate", String.valueOf(stepCount));
                if (isMoving == false) stepCount = 0;
                if (sensorEvent != null) {
                    isMoving = true;
                    float x_acceleration = sensorEvent.values[0];   //retrieving the sensor value from array position 0
                    float y_acceleration = sensorEvent.values[1];   //retrieving the sensor value from array position 1
                    float z_acceleration = sensorEvent.values[2];   //retrieving the sensor value from array position 2

                    double Magnitude = Math.sqrt(x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration);
                    double MagnitudeDelta = Magnitude - MagnitudePrevious;
                    MagnitudePrevious = Magnitude;

                    //if there is a significant amount of change
                    if (MagnitudeDelta > 1) {
                        stepCount++;    //increase the step counter
                        progressBar.setProgress(stepCount); //set the progress bar's progress based on steps
                    }
                    textViewStepCounter.setText(stepCount.toString());
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        sensorManager.registerListener(stepDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        place1 = new MarkerOptions().position(new LatLng(VANCOUVER_LAT, VANCOUVER_LNG)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(DESTINATION_LAT, DESTINATION_LNG)).title("Location 2");

        String url = getUrl(place1.getPosition(), place2.getPosition(), "driving");
        new FetchURL(MainActivity.this).execute(url, "driving");
    }




//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//
////        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
////            && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////                Log.d("testtt", "YOOOO");
////                return;
////        }
////        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
////        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
////        double longitude = location.getLongitude();
////        double latitude = location.getLatitude();
////
//////        Log.d("longTest", String.valueOf(longitude));
//////        Log.d("latTest", String.valueOf(latitude));
//////
//////        Log.d("testtt", "YOOOO");
//
//        //setting the map to surrey central
//        LatLng surreyCentral = new LatLng(49.1879,-122.8500);
////        LatLng surreyCentral = new LatLng(latitude,longitude);
//        googleMap.addMarker(new MarkerOptions()
//                .position(surreyCentral)
//                .title("Marker"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(surreyCentral, 15));
//    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.addMarker(place1);
        mMap.addMarker(place2);

        checkLocationPermission();

        gotoLocation(VANCOUVER_LAT, VANCOUVER_LNG, 15);

//        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        //origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        //destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        //mode
        String mode = "mode=" + directionMode;

        //building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;

        //output format
        String output = "json";

        //building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);

        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if(currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    //lan lng and zoom
    private void gotoLocation(double lat, double lng, float zoom){
        LatLng latlng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoom);
        mMap.moveCamera(update);
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

//    public void geolocate(View v) {
//        Geocoder myGeocoder = new Geocoder(this);
//
//        hideSoftKeyboard(v);
//
//        if (v.getId() == R.id.locationButton){
//            locationString = locationEntry.getText().toString();
//            Toast.makeText(this, "Searching for " + locationString, Toast.LENGTH_SHORT).show();
//
//            List<Address> list = null;
//            try {
//                list = myGeocoder.getFromLocationName(locationString, 1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if (list.size() >0){
//                Address add = list.get(0);
//                String locality = add.getLocality();
//                Toast.makeText(this, "Found " + locality, Toast.LENGTH_SHORT).show();
//
//                double lat = add.getLatitude();
//                double lng = add.getLongitude();
//                gotoLocation(lat, lng, 15);
//
////                MarkerOptions options = new MarkerOptions()
////                        .title(locality)
////                        .position(new LatLng(lat, lng));
////                myMap.addMarker(options);
//            }
//        }
//
//        if (v.getId() == R.id.latLngButton){
//            latString = latEntry.getText().toString();
//            lngString = lngEntry.getText().toString();
//            Toast.makeText(this, "Searching for " + latString + " , " + lngString, Toast.LENGTH_SHORT).show();
//
//            List <Address> list = null;
//            try {
//                list = myGeocoder.getFromLocation(Double.parseDouble(latString), Double.parseDouble(lngString), 1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if (list.size() >0){
//                Address add = list.get(0);
//                String locality = add.getLocality();
//                Toast.makeText(this, "Found " + locality, Toast.LENGTH_SHORT).show();
//
//                double lat = add.getLatitude();
//                double lng = add.getLongitude();
//                gotoLocation(lat, lng, 15);
//
////                MarkerOptions options = new MarkerOptions()
////                        .title(locality)
////                        .position(new LatLng(lat, lng));
////                myMap.addMarker(options);
//            }
//        }
//    }

    private void hideSoftKeyboard(View v) {
        InputMethodManager imm =
                (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}