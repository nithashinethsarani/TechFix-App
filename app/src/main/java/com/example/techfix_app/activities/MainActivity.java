package com.example.techfix_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.techfix_app.R;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.activities.appointments.AppointmentActivity;
import com.example.techfix_app.activities.branches.BranchActivity;
import com.example.techfix_app.activities.profile.ProfileActivity;
import com.example.techfix_app.activities.repairs.RepairHistoryActivity;
import com.example.techfix_app.activities.repairs.RepairStatusActivity;
import com.example.techfix_app.activities.services.ServicesActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnServices;
    private Button btnAppointments;
    private Button btnTrackRepair;
    private Button btnRepairHistory;
    private Button btnBranches;
    private Button btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {

        btnServices = findViewById(R.id.btnServices);
        btnAppointments = findViewById(R.id.btnAppointments);
        btnTrackRepair = findViewById(R.id.btnTrackRepair);
        btnRepairHistory = findViewById(R.id.btnRepairHistory);
        btnBranches = findViewById(R.id.btnBranches);
        btnProfile = findViewById(R.id.btnProfile);
    }

    private void setupClickListeners() {

        // View Services
        btnServices.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ServicesActivity.class
            );
            startActivity(intent);
        });

        // Book Appointment
        // Customer should select a service first
        btnAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ServicesActivity.class
            );
            startActivity(intent);
        });

        // Track Repair
        btnTrackRepair.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RepairStatusActivity.class
            );
            startActivity(intent);
        });

        // Repair History
        btnRepairHistory.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RepairHistoryActivity.class
            );
            startActivity(intent);
        });

        // Branches
        btnBranches.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    BranchActivity.class
            );
            startActivity(intent);
        });

        // Profile
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ProfileActivity.class
            );
            startActivity(intent);
        });
    }
}
