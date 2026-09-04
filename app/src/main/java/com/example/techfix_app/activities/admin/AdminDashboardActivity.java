package com.example.techfix_app.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;

public class AdminDashboardActivity extends AppCompatActivity {

    private LinearLayout llBranchManagement, llServiceManagement,
            llTechnicianManagement, llInventoryManagement, llRepairImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        llBranchManagement = findViewById(R.id.llBranchManagement);
        llServiceManagement = findViewById(R.id.llServiceManagement);
        llTechnicianManagement = findViewById(R.id.llTechnicianManagement);
        llInventoryManagement = findViewById(R.id.llInventoryManagement);
        llRepairImages = findViewById(R.id.llRepairImages);

        // Member 4 scope - real navigation
        llBranchManagement.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, AddEditBranchActivity.class)));

        llTechnicianManagement.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, TechnicianManagementActivity.class)));

        llInventoryManagement.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, InventoryManagementActivity.class)));

        llRepairImages.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboardActivity.this, UploadRepairImageActivity.class)));

        // Wena member ge scope - placeholder witharak
        llServiceManagement.setOnClickListener(v ->
                Toast.makeText(this, "Service Management - coming soon", Toast.LENGTH_SHORT).show());
    }
}