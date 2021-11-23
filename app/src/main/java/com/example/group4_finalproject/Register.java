package com.example.group4_finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//We used the "SharedPrefsNew" example as the base code
public class Register extends Activity {
    EditText usernameEditText, passwordEditText, confirmPasswordEditText, emailEditText;
    View mainView;
    Button loginButton;
    public static final String DEFAULT = "not available";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        usernameEditText = (EditText)findViewById(R.id.usernameEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText)findViewById(R.id.confirmPasswordEditText);
        emailEditText = (EditText)findViewById(R.id.emailEditText);
        mainView = (View) findViewById(R.id.signUpActivity);

    }

    public void submit (View view) {
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("registeredUsername", usernameEditText.getText().toString());
        editor.putString("registeredPassword", passwordEditText.getText().toString());
        editor.putString("confirmedPassword", confirmPasswordEditText.getText().toString());
        editor.putString("registeredEmail", emailEditText.getText().toString());
        editor.commit();

        //retrieving the registered login data from SharedPrefs
        String registeredPassword = sharedPrefs.getString("registeredPassword", DEFAULT);

        //comparing the registered info with the inputted login info
        if (registeredPassword.equals(confirmPasswordEditText.getText().toString()))
        {
            Toast.makeText(this, "Username and password saved to Preferences. Going to Login page", Toast.LENGTH_LONG).show();
            Intent intent= new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_LONG).show();
        }

//        Toast.makeText(this, "Username and password saved to Preferences. Going to Login page", Toast.LENGTH_LONG).show();
//        Intent intent= new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        editor.commit();
    }
}
