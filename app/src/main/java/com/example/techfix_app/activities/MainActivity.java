package com.example.techfix_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.activities.admin.AdminDashboardActivity;
import com.example.techfix_app.activities.auth.LoginActivity;
import com.example.techfix_app.activities.branches.BranchActivity;
import com.example.techfix_app.activities.profile.ProfileActivity;
import com.example.techfix_app.activities.repairs.RepairHistoryActivity;
import com.example.techfix_app.activities.repairs.RepairStatusActivity;
import com.example.techfix_app.activities.services.ServicesActivity;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.User;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // Fixed: Generic View object to support both Button & CardView/LinearLayout
    private View btnServices;
    private View btnAppointments;
    private View btnTrackRepair;
    private View btnRepairHistory;
    private View btnBranches;
    private View btnProfile;

    private FirestoreManager firestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestoreManager = new FirestoreManager();

        initializeViews();
        setupClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Check user role from Firestore
            checkUserRole(currentUser.getUid());
        }
    }

    private void checkUserRole(String uid) {
        firestoreManager.getUser(uid, new FirestoreManager.OnUserLoadedListener() {
            @Override
            public void onSuccess(User user) {
                if (user != null && user.getRole() != null) {
                    if (user.getRole().equalsIgnoreCase("admin")) {
                        Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, "Error fetching user role", Toast.LENGTH_SHORT).show();
            }
        });
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
        // Services
        if (btnServices != null) {
            btnServices.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ServicesActivity.class);
                startActivity(intent);
            });
        }

        // Book Appointment
        if (btnAppointments != null) {
            btnAppointments.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ServicesActivity.class);
                startActivity(intent);
            });
        }

        // Track Repair
        if (btnTrackRepair != null) {
            btnTrackRepair.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, RepairStatusActivity.class);
                startActivity(intent);
            });
        }

        // Repair History
        if (btnRepairHistory != null) {
            btnRepairHistory.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, RepairHistoryActivity.class);
                startActivity(intent);
            });
        }

        // Branches
        if (btnBranches != null) {
            btnBranches.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, BranchActivity.class);
                startActivity(intent);
            });
        }

        // Profile
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }
    }
}