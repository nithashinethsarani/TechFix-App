package com.example.techfix_app.activities.services;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.activities.appointments.AppointmentActivity;

public class ServiceDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        TextView tvServiceName = findViewById(R.id.tvServiceName);
        TextView tvDeviceCategory = findViewById(R.id.tvDeviceCategory);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvAvailability = findViewById(R.id.tvAvailability);
        Button btnBookAppointment = findViewById(R.id.btnBookAppointment);

        String serviceId = getIntent().getStringExtra("service_id");
        String serviceName = getIntent().getStringExtra("service_name");
        String deviceCategory = getIntent().getStringExtra("device_category");
        double price = getIntent().getDoubleExtra("price", 0.0);
        String description = getIntent().getStringExtra("description");
        String availability = getIntent().getStringExtra("available");

        tvServiceName.setText(serviceName);
        tvDeviceCategory.setText(deviceCategory);
        tvPrice.setText(String.format("Rs. %.2f", price));
        tvDescription.setText(description);

        boolean isAvailable = "Available".equalsIgnoreCase(availability);

        if (isAvailable) {
            tvAvailability.setText("Available");
            tvAvailability.setTextColor(0xFF2E7D32);
            btnBookAppointment.setEnabled(true);
        } else {
            tvAvailability.setText("Unavailable - Spare parts not in stock");
            tvAvailability.setTextColor(0xFFC62828);
            btnBookAppointment.setEnabled(false);
        }

        btnBookAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(ServiceDetailsActivity.this, AppointmentActivity.class);
            intent.putExtra("serviceName", serviceName);
            intent.putExtra("deviceCategory", deviceCategory);
            intent.putExtra("price", price);
            startActivity(intent);
        });
    }
}