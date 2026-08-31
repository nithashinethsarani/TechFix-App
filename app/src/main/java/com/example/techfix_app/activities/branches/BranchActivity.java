package com.example.techfix_app.activities.branches;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.techfix_app.R;
import com.example.techfix_app.models.Branch;
import com.example.techfix_app.utils.LocationUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class BranchActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvNearestBranch;
    private Branch selectedBranch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);

        tvNearestBranch = findViewById(R.id.tvNearestBranch);
        Button btnConfirmBranch = findViewById(R.id.btnConfirmBranch);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        checkLocationPermissionAndFetch();

        btnConfirmBranch.setOnClickListener(v -> {
            if (selectedBranch != null) {
                // TODO: pass selectedBranch.getBranchId() back to AppointmentActivity
                Toast.makeText(this, "Branch confirmed: " + selectedBranch.getName(), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Still detecting location...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchLocationAndFindBranch();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndFindBranch();
            } else {
                Toast.makeText(this, "Location permission is required to find nearest branch", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void fetchLocationAndFindBranch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double userLat = location.getLatitude();
                double userLng = location.getLongitude();

                // TODO: Replace loadDummyBranches() with Member 1's FirestoreManager call
                List<Branch> branches = loadDummyBranches();

                selectedBranch = LocationUtils.findNearestBranch(userLat, userLng, branches);

                if (selectedBranch != null) {
                    tvNearestBranch.setText(selectedBranch.getName());
                }
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Temporary dummy branches - remove once Firestore data is connected
    private List<Branch> loadDummyBranches() {
        List<Branch> list = new ArrayList<>();
        list.add(new Branch("branch_colombo", "TechFix Colombo", 6.9271, 79.8612));
        list.add(new Branch("branch_galle", "TechFix Galle", 6.0535, 80.2210));
        return list;
    }
}