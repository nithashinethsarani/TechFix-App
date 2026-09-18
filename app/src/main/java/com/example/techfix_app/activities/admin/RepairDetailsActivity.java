package com.example.techfix_app.activities.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.Repair;

import java.util.HashMap;
import java.util.Map;

public class RepairDetailsActivity extends AppCompatActivity {

    private TextView tvRepairId;
    private TextView tvCustomerId;
    private TextView tvAppointmentId;
    private TextView tvServiceId;
    private TextView tvBranchId;
    private TextView tvTechnicianId;
    private TextView tvDevice;
    private TextView tvDeviceCategory;
    private TextView tvProblem;

    private EditText etDiagnosis;
    private EditText etRepairNotes;
    private EditText etFinalPrice;

    private Spinner spinnerStatus;

    private Button btnSave;

    private ProgressBar progressBar;

    private FirestoreManager firestoreManager;

    private String repairId;
    private Repair repair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_repair_details);

        initializeViews();

        firestoreManager = new FirestoreManager();

        repairId = getIntent()
                .getStringExtra("repairId");

        if (repairId == null || repairId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Invalid repair ID",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        setupStatusSpinner();

        loadRepair();
    }

    private void initializeViews() {

        tvRepairId = findViewById(R.id.tvRepair);
        tvCustomerId = findViewById(R.id.tvCustomerId);
        tvAppointmentId = findViewById(R.id.tvAppointment);
        tvServiceId = findViewById(R.id.tvServiceId);
        tvBranchId = findViewById(R.id.tvBranchId);
        tvTechnicianId = findViewById(R.id.tvTechnicianId);

        tvDevice = findViewById(R.id.tvDevice);
        tvDeviceCategory = findViewById(R.id.tvDeviceCategory);
        tvProblem = findViewById(R.id.tvProblem);

        etDiagnosis = findViewById(R.id.etDiagnosis);
        etRepairNotes = findViewById(R.id.etRepairNotes);
        etFinalPrice = findViewById(R.id.etFinalPrice);

        spinnerStatus = findViewById(R.id.spinnerStatus);

        btnSave = findViewById(R.id.btnSave);

        progressBar = findViewById(R.id.progressBar);
    }

    private void setupStatusSpinner() {

        String[] statuses = {
                "received",
                "diagnosing",
                "waiting_for_parts",
                "repairing",
                "ready_for_collection",
                "completed",
                "cancelled"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        statuses
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerStatus.setAdapter(adapter);

        btnSave.setOnClickListener(
                v -> saveRepair()
        );
    }

    private void loadRepair() {

        progressBar.setVisibility(View.VISIBLE);

        firestoreManager.getRepair(repairId)
                .addOnSuccessListener(documentSnapshot -> {

                    progressBar.setVisibility(View.GONE);

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(
                                this,
                                "Repair not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                        return;
                    }

                    repair =
                            documentSnapshot.toObject(
                                    Repair.class
                            );

                    if (repair == null) {

                        Toast.makeText(
                                this,
                                "Unable to load repair",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                        return;
                    }

                    repair.setRepairId(
                            documentSnapshot.getId()
                    );

                    displayRepair();

                })
                .addOnFailureListener(e -> {

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(
                            this,
                            "Failed to load repair: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void displayRepair() {


        firestoreManager.getAppointmentUserName(
                repair.getAppointmentId(),
                new FirestoreManager.OnUserNameLoadedListener() {
                    @Override
                    public void onLoaded(String userName) {
                        tvRepairId.setText(
                                "Repair For: "+userName
                        );
                    }

                    @Override
                    public void onError(Exception e) {
                        tvRepairId.setText(
                                "Repair For: Customer"
                        );
                    }
                });

        tvCustomerId.setText(
                "Customer ID: " + safe(repair.getCustomerId())
        );

        tvAppointmentId.setText(
                "Appointment ID: " + safe(repair.getAppointmentId())
        );

        tvServiceId.setText(
                "Service ID: " + safe(repair.getServiceId())
        );

        tvBranchId.setText(
                "Branch ID: " + safe(repair.getBranchId())
        );

        tvTechnicianId.setText(
                "Technician ID: " + safe(repair.getTechnicianId())
        );

        tvDevice.setText(
                "Device: " + safe(repair.getDeviceName())
        );

        tvDeviceCategory.setText(
                "Category: " + safe(repair.getDeviceCategory())
        );

        tvProblem.setText(
                "Problem: " +
                        safe(repair.getDeviceDescription())
        );

        etDiagnosis.setText(
                safe(repair.getDiagnosis())
        );

        etRepairNotes.setText(
                safe(repair.getRepairNotes())
        );

        if (repair.getFinalPrice() > 0) {

            etFinalPrice.setText(
                    String.valueOf(
                            repair.getFinalPrice()
                    )
            );
        }

        setSpinnerStatus(
                repair.getStatus()
        );
    }

    private void setSpinnerStatus(String status) {

        if (status == null) {
            return;
        }

        ArrayAdapter adapter =
                (ArrayAdapter) spinnerStatus.getAdapter();

        int position =
                adapter.getPosition(status);

        if (position >= 0) {
            spinnerStatus.setSelection(position);
        }
    }

    private void saveRepair() {

        String diagnosis = etDiagnosis.getText().toString().trim();
        String notes = etRepairNotes.getText().toString().trim();
        String finalPriceText = etFinalPrice.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();

        if (TextUtils.isEmpty(diagnosis)) {
            Toast.makeText(this, "Please enter the diagnosis", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(finalPriceText)) {
            Toast.makeText(this, "Please enter the final price", Toast.LENGTH_SHORT).show();
            return;
        }

        double finalPrice;
        try {
            finalPrice = Double.parseDouble(finalPriceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid final price", Toast.LENGTH_SHORT).show();
            return;
        }

        if (finalPrice < 0) {
            Toast.makeText(this, "Final price cannot be negative", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("diagnosis", diagnosis);
        updates.put("repairNotes", notes);
        updates.put("finalPrice", finalPrice);
        updates.put("status", status);

        updates.put("updatedAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

        if ("completed".equals(status)) {
            updates.put("completedAt", com.google.firebase.firestore.FieldValue.serverTimestamp());
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        firestoreManager.updateRepair(repairId, updates)
                .addOnSuccessListener(unused -> {

                    if (repair != null && repair.getAppointmentId() != null) {
                        Map<String, Object> apptUpdates = new HashMap<>();
                        apptUpdates.put("status", status);
                        firestoreManager.updateAppointment(repair.getAppointmentId(), apptUpdates);
                    }

                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);

                    Toast.makeText(this, "Repair updated successfully", Toast.LENGTH_SHORT).show();
                    finish();

                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);

                    Toast.makeText(this, "Failed to update repair: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String safe(String value) {

        if (value == null || value.isEmpty()) {
            return "Not available";
        }

        return value;
    }
}