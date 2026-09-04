package com.example.techfix_app.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.activities.MainActivity;
import com.example.techfix_app.activities.auth.LoginActivity;
import com.example.techfix_app.activities.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminDashboardActivity extends AppCompatActivity {

    private LinearLayout llBranchManagement, llServiceManagement,
            llTechnicianManagement, llInventoryManagement, llRepairImages;

    private TextView tvProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvProfile = findViewById(R.id.tvProfile);
        llBranchManagement = findViewById(R.id.llBranchManagement);
        llServiceManagement = findViewById(R.id.llServiceManagement);
        llTechnicianManagement = findViewById(R.id.llTechnicianManagement);
        llInventoryManagement = findViewById(R.id.llInventoryManagement);
        llRepairImages = findViewById(R.id.llRepairImages);

        tvProfile.setOnClickListener(view ->
                startActivity(new Intent(AdminDashboardActivity.this, ProfileActivity.class))
        );
        llBranchManagement.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, AddEditBranchActivity.class)));

        llTechnicianManagement.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, TechnicianManagementActivity.class)));

        llInventoryManagement.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, InventoryManagementActivity.class)));

        llRepairImages.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, UploadRepairImageActivity.class)));

        llServiceManagement.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, ServiceManagementActivity.class)));
    }

    @Override
    protected void onStart()
    {

        super.onStart();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);

            startActivity(intent);

            finish();
        }
    }
}