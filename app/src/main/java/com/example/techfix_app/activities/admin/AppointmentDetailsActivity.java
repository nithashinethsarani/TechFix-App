package com.example.techfix_app.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.Appointment;
import com.example.techfix_app.models.Repair;
import com.example.techfix_app.models.Technician;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentDetailsActivity extends AppCompatActivity {

    private TextView tvAppointmentId;
    private TextView tvCustomerId;
    private TextView tvServiceId;
    private TextView tvBranchId;
    private TextView tvDevice;
    private TextView tvCategory;
    private TextView tvProblem;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvStatus;
    private TextView tvTechnician;

    private Spinner spinnerTechnician;

    private Button btnAssignTechnician;
    private Button btnCreateRepair;
    private Button btnDeleteAppointment;

    private ProgressBar progressBar;

    private FirestoreManager firestoreManager;
    private FirebaseFirestore db;

    private Appointment appointment;

    private final List<Technician> technicianList = new ArrayList<>();
    private final List<String> technicianNames = new ArrayList<>();

    private ArrayAdapter<String> technicianAdapter;

    private String appointmentId;
    private String selectedTechnicianName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        appointmentId = getIntent().getStringExtra("appointmentId");

        if (appointmentId == null || appointmentId.isEmpty()) {
            Toast.makeText(
                    this,
                    "Invalid appointment",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        initializeViews();

        firestoreManager = new FirestoreManager();
        db = FirebaseFirestore.getInstance();

        setupTechnicianSpinner();
        loadAppointment();
    }

    private void initializeViews() {
        tvAppointmentId = findViewById(R.id.tvAppointment);
        tvCustomerId = findViewById(R.id.tvCustomerId);
        tvServiceId = findViewById(R.id.tvServiceId);
        tvBranchId = findViewById(R.id.tvBranchId);
        tvDevice = findViewById(R.id.tvDevice);
        tvCategory = findViewById(R.id.tvCategory);
        tvProblem = findViewById(R.id.tvProblem);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvStatus = findViewById(R.id.tvStatus);
        tvTechnician = findViewById(R.id.tvTechnician);

        spinnerTechnician = findViewById(R.id.spinnerTechnician);

        btnAssignTechnician = findViewById(R.id.btnAssignTechnician);
        btnCreateRepair = findViewById(R.id.btnCreateRepair);
        btnDeleteAppointment = findViewById(R.id.btnDeleteAppointment);

        progressBar = findViewById(R.id.progressBar);

        btnAssignTechnician.setOnClickListener(v -> assignTechnician());
        btnCreateRepair.setOnClickListener(v -> createRepair());

        if (btnDeleteAppointment != null) {
            btnDeleteAppointment.setOnClickListener(v -> deleteAppointment());
        }
    }

    private void setupTechnicianSpinner() {
        technicianAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                technicianNames
        );

        technicianAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerTechnician.setAdapter(technicianAdapter);
    }

    private void loadAppointment() {
        progressBar.setVisibility(View.VISIBLE);

        firestoreManager.getAppointment(appointmentId)
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);

                    if (!documentSnapshot.exists()) {
                        Toast.makeText(
                                AppointmentDetailsActivity.this,
                                "Appointment not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                        return;
                    }

                    appointment = documentSnapshot.toObject(Appointment.class);

                    if (appointment == null) {
                        Toast.makeText(
                                AppointmentDetailsActivity.this,
                                "Could not read appointment",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                        return;
                    }

                    appointment.setAppointmentId(documentSnapshot.getId());

                    displayAppointment();
                    loadAvailableTechnicians();

                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(
                            AppointmentDetailsActivity.this,
                            "Failed to load appointment: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void displayAppointment() {
        tvAppointmentId.setText(
                "Appointment For: " + safe(appointment.getCustomerName())
        );

        tvCustomerId.setText(
                "Customer ID: " + safe(appointment.getCustomerId())
        );

        tvServiceId.setText(
                "Service ID: " + safe(appointment.getServiceId())
        );

        tvBranchId.setText(
                "Branch : " + safe(appointment.getBranchId())
        );

        tvDevice.setText(
                "Device: " + safe(appointment.getDeviceName())
        );

        tvCategory.setText(
                "Device Category: " + safe(appointment.getDeviceCategory())
        );

        tvProblem.setText(
                "Problem: " + safe(appointment.getDeviceDescription())
        );

        tvDate.setText(
                "Preferred Date: " + safe(appointment.getAppointmentDate())
        );

        tvTime.setText(
                "Time Slot: " + safe(appointment.getAppointmentTime())
        );

        tvStatus.setText(
                "Status: " + safe(appointment.getStatus())
        );

        String techId = appointment.getTechnicianId();
        if (techId == null || techId.isEmpty()) {
            tvTechnician.setText("Technician: Unassigned");
        } else {
            firestoreManager.getTechnicianById(techId)
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Technician tech = documentSnapshot.toObject(Technician.class);
                            if (tech != null && tech.getName() != null && !tech.getName().isEmpty()) {
                                tvTechnician.setText("Technician: " + tech.getName());
                            } else {
                                tvTechnician.setText("Technician: " + techId);
                            }
                        } else {
                            tvTechnician.setText("Technician: " + techId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        tvTechnician.setText("Technician: " + techId);
                    });
        }
    }

    private void loadAvailableTechnicians() {
        technicianNames.clear();
        technicianList.clear();

        technicianNames.add("Select Technician");

        firestoreManager.getAvailableTechniciansByBranch(appointment.getBranchId())
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Technician technician = document.toObject(Technician.class);

                        if (technician != null) {
                            if (technician.getTechnicianId() == null ||
                                    technician.getTechnicianId().isEmpty()) {
                                technician.setTechnicianId(document.getId());
                            }

                            technicianList.add(technician);

                            String displayName = technician.getName();
                            if (displayName == null || displayName.isEmpty()) {
                                displayName = technician.getTechnicianId();
                            }

                            technicianNames.add(displayName);
                        }
                    }

                    technicianAdapter.notifyDataSetChanged();

                    if (technicianList.isEmpty()) {
                        Toast.makeText(
                                AppointmentDetailsActivity.this,
                                "No available technicians for this branch",
                                Toast.LENGTH_LONG
                        ).show();
                    }

                }).addOnFailureListener(e -> {
                    Toast.makeText(
                            AppointmentDetailsActivity.this,
                            "Failed to load technicians: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void assignTechnician() {
        int selectedPosition = spinnerTechnician.getSelectedItemPosition();

        if (selectedPosition <= 0 || selectedPosition > technicianList.size()) {
            Toast.makeText(
                    this,
                    "Please select a technician",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Technician selectedTechnician = technicianList.get(selectedPosition - 1);
        String technicianId = selectedTechnician.getTechnicianId();
        selectedTechnicianName = selectedTechnician.getName();

        if (technicianId == null || technicianId.isEmpty()) {
            Toast.makeText(
                    this,
                    "Invalid technician",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("technicianId", technicianId);
        updates.put("status", "confirmed");

        firestoreManager.updateAppointment(appointmentId, updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(
                            AppointmentDetailsActivity.this,
                            "Technician assigned successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    appointment.setTechnicianId(technicianId);
                    appointment.setStatus("confirmed");

                    tvStatus.setText("Status: confirmed");
                    tvTechnician.setText("Technician: " + (selectedTechnicianName != null ? selectedTechnicianName : technicianId));

                }).addOnFailureListener(e -> {
                    Toast.makeText(
                            AppointmentDetailsActivity.this,
                            "Failed to assign technician: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void createRepair() {
        if (appointment == null || appointment.getAppointmentId() == null) {
            Toast.makeText(this, "Invalid appointment details", Toast.LENGTH_SHORT).show();
            return;
        }

        if (appointment.getTechnicianId() == null || appointment.getTechnicianId().isEmpty()) {
            Toast.makeText(
                    AppointmentDetailsActivity.this,
                    "Please assign a technician first",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firestoreManager.getRepairByAppointmentId(appointment.getAppointmentId())
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(
                                AppointmentDetailsActivity.this,
                                "A repair has already been created for this appointment!",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    DocumentReference document = db.collection("repairs").document();
                    String generatedRepairId = document.getId();

                    Repair repair = new Repair();
                    repair.setRepairId(generatedRepairId);
                    repair.setAppointmentId(appointment.getAppointmentId());
                    repair.setCustomerId(appointment.getCustomerId());
                    repair.setServiceId(appointment.getServiceId());
                    repair.setBranchId(appointment.getBranchId());
                    repair.setTechnicianId(appointment.getTechnicianId());
                    repair.setDeviceCategory(appointment.getDeviceCategory());
                    repair.setDeviceName(appointment.getDeviceName());
                    repair.setDeviceDescription(appointment.getDeviceDescription());
                    repair.setStatus("received");

                    firestoreManager.createRepair(repair)
                            .addOnSuccessListener(unused -> {
                                String apptId = appointment.getAppointmentId();
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("status", "received");

                                firestoreManager.updateAppointment(apptId, updates)
                                        .addOnSuccessListener(unused1 -> {
                                            progressBar.setVisibility(View.GONE);
                                            appointment.setStatus("received");
                                            tvStatus.setText("Status: received");

                                            Toast.makeText(
                                                    AppointmentDetailsActivity.this,
                                                    "Repair created successfully",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(
                                                    AppointmentDetailsActivity.this,
                                                    "Failed to update appointment: " + e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(
                                        AppointmentDetailsActivity.this,
                                        "Failed to create repair: " + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(
                            AppointmentDetailsActivity.this,
                            "Error checking existing repair: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void deleteAppointment() {
        if (appointmentId == null || appointmentId.isEmpty()) {
            Toast.makeText(this, "Cannot delete: Invalid Appointment ID", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        setButtonsEnabled(false);

        firestoreManager.deleteAppointment(appointmentId)
                .addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(
                            AppointmentDetailsActivity.this,
                            "Appointment deleted successfully",
                            Toast.LENGTH_SHORT
                    ).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    setButtonsEnabled(true);
                    Toast.makeText(
                            AppointmentDetailsActivity.this,
                            "Failed to delete appointment: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void setButtonsEnabled(boolean enabled) {
        if (btnAssignTechnician != null) btnAssignTechnician.setEnabled(enabled);
        if (btnCreateRepair != null) btnCreateRepair.setEnabled(enabled);
        if (btnDeleteAppointment != null) btnDeleteAppointment.setEnabled(enabled);
    }

    private String safe(String value) {
        if (value == null || value.isEmpty()) {
            return "N/A";
        }
        return value;
    }
}