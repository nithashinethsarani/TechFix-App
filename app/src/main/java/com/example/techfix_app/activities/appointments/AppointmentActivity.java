package com.example.techfix_app.activities.appointments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.techfix_app.R;
import com.example.techfix_app.models.Branch;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class AppointmentActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private TextView textSelectedService;
    private TextView textNearestBranch;

    private EditText editCustomerName;
    private EditText editPhoneNumber;
    private EditText editPreferredDate;

    private Button btnSubmitBooking;

    private String serviceName;
    private String deviceCategory;
    private double price;

    private FusedLocationProviderClient fusedLocationClient;

    private List<Branch> branchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        initializeViews();
        getSelectedServiceDetails();
        setupLocation();
        setupBranches();
        setupSubmitButton();

        checkLocationPermissionAndFindBranch();
    }

    /**
     * Connect Java variables with XML views.
     */
    private void initializeViews() {

        textSelectedService = findViewById(R.id.textSelectedService);
        textNearestBranch = findViewById(R.id.textNearestBranch);

        editCustomerName = findViewById(R.id.editCustomerName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editPreferredDate = findViewById(R.id.editPreferredDate);

        btnSubmitBooking = findViewById(R.id.btnSubmitBooking);
    }

    /**
     * Get the service selected by the customer.
     */
    private void getSelectedServiceDetails() {

        serviceName = getIntent().getStringExtra("serviceName");
        deviceCategory = getIntent().getStringExtra("deviceCategory");
        price = getIntent().getDoubleExtra("price", 0.0);

        if (serviceName == null) {
            serviceName = "Unknown Service";
        }

        if (deviceCategory == null) {
            deviceCategory = "Unknown Category";
        }

        textSelectedService.setText(
                serviceName
                        + " ("
                        + deviceCategory
                        + ") - Rs. "
                        + price
        );
    }

    /**
     * Setup Google's fused location provider.
     */
    private void setupLocation() {

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * Temporary branch data.
     *
     * Later this should come from Firestore.
     */
    private void setupBranches() {

        branchList = new ArrayList<>();

        branchList.add(
                new Branch(
                        "1",
                        "Colombo Branch",
                        6.9271,
                        79.8612
                )
        );

        branchList.add(
                new Branch(
                        "2",
                        "Galle Branch",
                        6.0535,
                        80.2210
                )
        );
    }

    /**
     * Setup the appointment submission button.
     */
    private void setupSubmitButton() {

        btnSubmitBooking.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        submitAppointment();
                    }
                }
        );
    }

    /**
     * Validate and submit the appointment.
     *
     * Currently this only displays a confirmation message.
     * Later the appointment will be saved to Firestore.
     */
    private void submitAppointment() {

        String name =
                editCustomerName.getText().toString().trim();

        String phone =
                editPhoneNumber.getText().toString().trim();

        String date =
                editPreferredDate.getText().toString().trim();

        if (name.isEmpty()
                || phone.isEmpty()
                || date.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Toast.makeText(
                this,
                "Appointment booked for "
                        + serviceName
                        + " on "
                        + date,
                Toast.LENGTH_LONG
        ).show();

        finish();
    }

    /**
     * Check whether location permission has already
     * been granted.
     */
    private void checkLocationPermissionAndFindBranch() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE
            );

        } else {

            findNearestBranch();
        }
    }

    /**
     * Get the customer's last known location
     * and find the nearest TechFix branch.
     */
    private void findNearestBranch() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        Branch nearestBranch = null;

                        float minimumDistance =
                                Float.MAX_VALUE;

                        for (Branch branch : branchList) {

                            float[] distanceResult =
                                    new float[1];

                            Location.distanceBetween(
                                    location.getLatitude(),
                                    location.getLongitude(),

                                    branch.getLatitude(),
                                    branch.getLongitude(),

                                    distanceResult
                            );

                            if (distanceResult[0]
                                    < minimumDistance) {

                                minimumDistance =
                                        distanceResult[0];

                                nearestBranch = branch;
                            }
                        }

                        if (nearestBranch != null) {

                            textNearestBranch.setText(
                                    "Nearest Branch: "
                                            + nearestBranch.getName()
                            );
                        }

                    } else {

                        textNearestBranch.setText(
                                "Nearest Branch: "
                                        + "Location not available"
                        );
                    }
                });
    }

    /**
     * Handle the result of the location permission request.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode ==
                LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                findNearestBranch();

            } else {

                textNearestBranch.setText(
                        "Nearest Branch: "
                                + "Permission denied"
                );
            }
        }
    }
}