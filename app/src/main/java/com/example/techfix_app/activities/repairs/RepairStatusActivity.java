package com.example.techfix_app.activities.repairs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.activities.payment.PaymentActivity;
import com.example.techfix_app.database.AppDatabase;
import com.example.techfix_app.database.entities.RepairEntity;
import com.google.firebase.firestore.FirebaseFirestore;

public class RepairStatusActivity extends AppCompatActivity {

    private TextView tvDeviceName;
    private TextView tvStatus;
    private TextView tvTechnician;
    private TextView tvSpareParts;
    private TextView tvTotalAmount;

    private Button btnProceedToPay;

    private FirebaseFirestore db;

    // Temporary test repair ID
    // Later this should come from the logged-in customer's repair.
    private String currentRepairId = "REPAIR_1001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_status);

        initializeViews();

        btnProceedToPay.setOnClickListener(v -> openPayment());

        initializeFirebase();
    }

    private void initializeViews() {

        tvDeviceName = findViewById(R.id.tvDeviceName);
        tvStatus = findViewById(R.id.tvStatus);
        tvTechnician = findViewById(R.id.tvTechnician);
        tvSpareParts = findViewById(R.id.tvSpareParts);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        btnProceedToPay = findViewById(R.id.btnProceedToPay);
    }

    private void initializeFirebase() {

        try {

            db = FirebaseFirestore.getInstance();

            fetchRepairDetails(currentRepairId);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Firebase not configured yet - Loading offline UI",
                    Toast.LENGTH_SHORT
            ).show();

            loadSampleData();
        }
    }

    private void openPayment() {

        Intent intent = new Intent(
                RepairStatusActivity.this,
                PaymentActivity.class
        );

        intent.putExtra(
                "REPAIR_ID",
                currentRepairId
        );

        startActivity(intent);
    }

    private void fetchRepairDetails(String repairId) {

        db.collection("repairs")
                .document(repairId)
                .get()

                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        String deviceName =
                                documentSnapshot.getString("deviceName");

                        String status =
                                documentSnapshot.getString("status");

                        String technicianName =
                                documentSnapshot.getString("technicianName");

                        String spareParts =
                                documentSnapshot.getString("sparePartsDetails");

                        Double amount =
                                documentSnapshot.getDouble("totalAmount");

                        displayRepairDetails(
                                deviceName,
                                status,
                                technicianName,
                                spareParts,
                                amount
                        );

                        saveRepairToLocalDatabase(
                                repairId,
                                status,
                                amount
                        );

                    } else {

                        Toast.makeText(
                                this,
                                "Repair details not found!",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })

                .addOnFailureListener(e -> {

                    loadRepairFromLocalDatabase(repairId);
                });
    }

    private void displayRepairDetails(
            String deviceName,
            String status,
            String technicianName,
            String spareParts,
            Double amount
    ) {

        tvDeviceName.setText(
                "Device: "
                        + getSafeValue(deviceName)
        );

        tvStatus.setText(
                "Status: "
                        + getSafeValue(status)
        );

        tvTechnician.setText(
                "Technician: "
                        + getSafeValue(technicianName)
        );

        tvSpareParts.setText(
                "Spare Parts: "
                        + getSafeValue(spareParts)
        );

        tvTotalAmount.setText(
                "Total: Rs. "
                        + (amount != null ? amount : 0.0)
        );
    }

    private void saveRepairToLocalDatabase(
            String repairId,
            String status,
            Double amount
    ) {

        AppDatabase localDatabase =
                AppDatabase.getInstance(this);

        RepairEntity localData =
                new RepairEntity(
                        repairId,
                        status != null
                                ? status
                                : "Pending",
                        amount != null
                                ? amount
                                : 0.0
                );

        localDatabase
                .repairDao()
                .saveRepair(localData);
    }

    private void loadRepairFromLocalDatabase(
            String repairId
    ) {

        AppDatabase localDatabase =
                AppDatabase.getInstance(this);

        RepairEntity localData =
                localDatabase
                        .repairDao()
                        .getLocalRepair(repairId);

        if (localData != null) {

            tvStatus.setText(
                    "Status (Offline): "
                            + localData.status
            );

            tvTotalAmount.setText(
                    "Total (Offline): Rs. "
                            + localData.totalAmount
            );

            Toast.makeText(
                    this,
                    "Loaded from Offline Local Cache",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "No offline repair data available",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void loadSampleData() {

        tvDeviceName.setText(
                "Device: Sample Laptop"
        );

        tvStatus.setText(
                "Status: In Progress"
        );

        tvTechnician.setText(
                "Technician: Kamal Perera"
        );

        tvSpareParts.setText(
                "Spare Parts: Display Cable"
        );

        tvTotalAmount.setText(
                "Total: Rs. 12,500.00"
        );
    }

    private String getSafeValue(String value) {

        if (value == null || value.isEmpty()) {
            return "Not available";
        }

        return value;
    }
}