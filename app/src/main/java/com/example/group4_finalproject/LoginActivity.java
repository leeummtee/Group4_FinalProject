package com.example.group4_finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//I used the "SharedPrefsNew" example as the base code
public class LoginActivity extends Activity {
    TextView usernameTextView, passwordTextView;
    EditText loginUsernameEditText, loginPasswordEditText;

    public static final String DEFAULT = "not available";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //login
        loginUsernameEditText = (EditText)findViewById(R.id.loginUsernameEditText);
        loginPasswordEditText = (EditText)findViewById(R.id.loginPasswordEditText);
    }

    public void retrieve (View view){
        //inputting the new login info
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("loginUsernameTest", loginUsernameEditText.getText().toString());
        editor.putString("loginPasswordTest", loginPasswordEditText.getText().toString());

        //retrieving the registered login data from SharedPrefs
        String registeredUsername = sharedPrefs.getString("registeredUsername", DEFAULT);
        String registeredPassword = sharedPrefs.getString("registeredPassword", DEFAULT);

        //comparing the registered info with the inputted login info
        if (registeredUsername.equals(loginUsernameEditText.getText().toString()) &&
                registeredPassword.equals(loginPasswordEditText.getText().toString()))
        {
            Intent intent= new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Incorrect Login", Toast.LENGTH_LONG).show();
        }
        editor.commit();
    }

    public void backToSignUp(View view){
        Toast.makeText(this, "backtoActivity1", Toast.LENGTH_LONG).show();
        Intent intent= new Intent(this, Register.class);
        startActivity(intent);
    }

}
