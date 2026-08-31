package com.example.techfix_app.activities.services;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;

public class ServiceDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        TextView tvDetails = findViewById(R.id.tvServiceDetails);

        String serviceId = getIntent().getStringExtra("service_id");
        // TODO: fetch full service details using serviceId
        // (via Member 1's FirestoreManager once ready)

        tvDetails.setText("Service ID: " + serviceId);
    }
}