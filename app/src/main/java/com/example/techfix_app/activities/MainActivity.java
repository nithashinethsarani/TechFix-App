package com.example.techfix_app.activities;

import static androidx.core.content.ContextCompat.startActivity;

import android.os.Bundle;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import com.example.techfix_app.activities.auth.SignupActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Open Signup screen
        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity
    }
}