package com.example.techfix_app.activities.appointments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.techfix_app.R;
import com.example.techfix_app.activities.branches.BranchActivity;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.Appointment;
import com.example.techfix_app.models.Branch;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AppointmentActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private TextView textSelectedService;
    private TextView textNearestBranch;

    private EditText editCustomerName;
    private EditText editPhoneNumber;
    private EditText editPreferredDate;
    private EditText editDeviceName;
    private EditText editDeviceDescription;
    private Spinner spinnerDeviceCategory;
    private Spinner spinnerTimeSlot;
    private Button btnSubmitBooking;

    private String serviceId;
    private String serviceName;
    private String deviceCategory;
    private double price;

    private FirestoreManager firestoreManager;
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;

    private List<Branch> branchList;
    private ActivityResultLauncher<Intent> branchActivityLauncher;
    private String selectedBranchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        firestoreManager = new FirestoreManager();
        db = FirebaseFirestore.getInstance();

        branchActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        String bId = data.getStringExtra("branch_id");
                        if (bId == null) {
                            bId = data.getStringExtra("branchId");
                        }

                        String branchName = data.getStringExtra("branch_name");
                        if (branchName == null) {
                            branchName = data.getStringExtra("branchName");
                        }

                        if (bId != null) {
                            selectedBranchId = bId;
                            textNearestBranch.setText("Selected Branch: " + (branchName != null ? branchName : "Branch Selected"));
                        }
                    }
                }
        );

        initializeViews();
        setupDeviceCategorySpinner();
        getSelectedServiceDetails();
        setupLocation();

        branchList = new ArrayList<>();

        setupTimeSlotSpinner();
        setupSubmitButton();
        setupDatePicker();
        setupBranchCardClick();

        loadBranchesFromFirestore();
    }

    private void initializeViews() {
        textSelectedService = findViewById(R.id.textSelectedService);
        textNearestBranch = findViewById(R.id.textNearestBranch);

        editCustomerName = findViewById(R.id.editCustomerName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editPreferredDate = findViewById(R.id.editPreferredDate);
        editDeviceName = findViewById(R.id.editDeviceName);
        spinnerDeviceCategory = findViewById(R.id.spinnerDeviceCategory);
        editDeviceDescription = findViewById(R.id.editDeviceDescription);
        spinnerTimeSlot = findViewById(R.id.spinnerTimeSlot);

        btnSubmitBooking = findViewById(R.id.btnSubmitBooking);
    }

    private void setupDeviceCategorySpinner() {
        String[] deviceCategories = {"Select", "Mobile", "Computer", "Laptop"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                deviceCategories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeviceCategory.setAdapter(adapter);
    }

    private void getSelectedServiceDetails() {
        serviceId = getIntent().getStringExtra("serviceId");
        serviceName = getIntent().getStringExtra("serviceName");
        deviceCategory = getIntent().getStringExtra("deviceCategory");
        price = getIntent().getDoubleExtra("price", 0.0);

        if (serviceName == null) serviceName = "Unknown Service";
        if (deviceCategory == null) deviceCategory = "Unknown Category";

        textSelectedService.setText(
                String.format(
                        Locale.getDefault(),
                        "%s (%s) - Rs. %.2f",
                        serviceName,
                        deviceCategory,
                        price
                )
        );
    }

    private void setupLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void setupBranchCardClick() {
        textNearestBranch.setOnClickListener(v -> {
            Intent intent = new Intent(AppointmentActivity.this, BranchActivity.class);
            branchActivityLauncher.launch(intent);
        });
    }

    private void setupTimeSlotSpinner() {
        String[] timeSlots = {
                "9:00 AM - 10:00 AM",
                "10:00 AM - 11:00 AM",
                "11:00 AM - 12:00 PM",
                "1:00 PM - 2:00 PM",
                "2:00 PM - 3:00 PM",
                "3:00 PM - 4:00 PM"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                timeSlots
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeSlot.setAdapter(adapter);
    }

    private void setupSubmitButton() {
        btnSubmitBooking.setOnClickListener(v -> submitAppointment());
    }

    private void setupDatePicker() {
        editPreferredDate.setFocusable(false);
        editPreferredDate.setClickable(true);

        editPreferredDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format(
                                Locale.getDefault(),
                                "%02d/%02d/%04d",
                                selectedDay,
                                selectedMonth + 1,
                                selectedYear
                        );
                        editPreferredDate.setText(formattedDate);
                    },
                    year,
                    month,
                    day
            );

            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });
    }

    private void submitAppointment() {
        String name = editCustomerName.getText().toString().trim();
        String dName = editDeviceName.getText().toString().trim();
        String dDesc = editDeviceDescription.getText().toString().trim();
        String dCat = spinnerDeviceCategory.getSelectedItem() != null
                ? spinnerDeviceCategory.getSelectedItem().toString()
                : "";
        String phone = editPhoneNumber.getText().toString().trim();
        String date = editPreferredDate.getText().toString().trim();
        String timeSlot = spinnerTimeSlot.getSelectedItem() != null
                ? spinnerTimeSlot.getSelectedItem().toString()
                : "";

        if (name.isEmpty() || phone.isEmpty() || date.isEmpty() || dName.isEmpty() || dDesc.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dCat.equals("Select")) {
            Toast.makeText(this, "Please select a device category", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fallback: If selectedBranchId is still missing, set it to the first available branch
        if ((selectedBranchId == null || selectedBranchId.isEmpty()) && branchList != null && !branchList.isEmpty()) {
            selectedBranchId = branchList.get(0).getBranchId();
        }

        if (selectedBranchId == null || selectedBranchId.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please select a branch or wait for location detection",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference document = db.collection("appointments").document();

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(document.getId());
        appointment.setCustomerId(currentUser.getUid());
        appointment.setCustomerName(name);
        appointment.setServiceId(serviceId);
        appointment.setBranchId(selectedBranchId);
        appointment.setTechnicianId(null); //TODO: Assign a technician for service
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(timeSlot);
        appointment.setDeviceCategory(dCat);
        appointment.setDeviceName(dName);
        appointment.setDeviceDescription(dDesc);
        appointment.setStatus("Submitted");
        appointment.setCreatedAt(FieldValue.serverTimestamp());

        btnSubmitBooking.setEnabled(false);

        firestoreManager.addAppointment(appointment).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Appointment was made Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            btnSubmitBooking.setEnabled(true);
            Toast.makeText(this, "Failed to add Appointment: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void loadBranchesFromFirestore() {
        textNearestBranch.setText("Loading branches...");

        firestoreManager.getAllBranches(new FirestoreManager.OnBranchesLoadedListener() {
            @Override
            public void onSuccess(List<Branch> branches) {
                branchList = branches;

                if (branchList == null || branchList.isEmpty()) {
                    textNearestBranch.setText("No branches available");
                    return;
                }

                // Guarantee selectedBranchId is never null once branches load
                if (selectedBranchId == null || selectedBranchId.isEmpty()) {
                    selectedBranchId = branchList.get(0).getBranchId();
                }

                checkLocationPermissionAndFindBranch();
            }

            @Override
            public void onFailure(Exception e) {
                textNearestBranch.setText("Failed to load branches");
                Toast.makeText(
                        AppointmentActivity.this,
                        "Failed to load branches: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void checkLocationPermissionAndFindBranch() {
        boolean hasFineLocation = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasCoarseLocation = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (hasFineLocation || hasCoarseLocation) {
            findNearestBranch();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    @SuppressWarnings("MissingPermission")
    private void findNearestBranch() {
        boolean hasFineLocation = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasCoarseLocation = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!hasFineLocation && !hasCoarseLocation) {
            textNearestBranch.setText("Location permission required");
            return;
        }

        textNearestBranch.setText("Getting your location...");

        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.getToken()
        ).addOnSuccessListener(location -> {
            if (location != null) {
                calculateAndSetNearestBranch(location);
            } else {
                if (branchList != null && !branchList.isEmpty()) {
                    textNearestBranch.setText("Default Branch: " + branchList.get(0).getName());
                } else {
                    textNearestBranch.setText("Unable to get location");
                }
            }
        }).addOnFailureListener(e -> {
            if (branchList != null && !branchList.isEmpty()) {
                textNearestBranch.setText("Default Branch: " + branchList.get(0).getName());
            } else {
                textNearestBranch.setText("Location error");
            }
        });
    }

    private void calculateAndSetNearestBranch(Location userLocation) {
        Branch nearestBranch = null;
        float minimumDistanceInMeters = Float.MAX_VALUE;

        for (Branch branch : branchList) {
            float[] distanceResult = new float[1];
            Location.distanceBetween(
                    userLocation.getLatitude(),
                    userLocation.getLongitude(),
                    branch.getLatitude(),
                    branch.getLongitude(),
                    distanceResult
            );

            if (distanceResult[0] < minimumDistanceInMeters) {
                minimumDistanceInMeters = distanceResult[0];
                nearestBranch = branch;
            }
        }

        if (nearestBranch != null) {
            float distanceKm = minimumDistanceInMeters / 1000f;
            selectedBranchId = nearestBranch.getBranchId();

            textNearestBranch.setText(
                    String.format(
                            Locale.getDefault(),
                            "Nearest Branch: %s (%.1f km away)",
                            nearestBranch.getName(),
                            distanceKm
                    )
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean locationPermissionGranted = false;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    break;
                }
            }

            if (locationPermissionGranted) {
                findNearestBranch();
            } else {
                if (branchList != null && !branchList.isEmpty()) {
                    textNearestBranch.setText("Default Branch: " + branchList.get(0).getName());
                } else {
                    textNearestBranch.setText("Location permission denied");
                }
            }
        }
    }
}