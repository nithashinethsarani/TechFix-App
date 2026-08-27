package com.example.techfix_app;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.techfix_app.ui.auth.SignupActivity;

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