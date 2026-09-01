package com.example.techfix_app.activities.branches;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private RecyclerView recyclerAllBranches;
    private Branch selectedBranch;
    private List<Branch> branchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);

        tvNearestBranch = findViewById(R.id.tvNearestBranch);
        recyclerAllBranches = findViewById(R.id.recyclerAllBranches);
        Button btnConfirmBranch = findViewById(R.id.btnConfirmBranch);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // TODO: Replace loadDummyBranches() with Member 1's FirestoreManager call
        branchList = loadDummyBranches();

        // Show all branches in the list at the top
        recyclerAllBranches.setLayoutManager(new LinearLayoutManager(this));
        recyclerAllBranches.setAdapter(new BranchListAdapter(branchList));

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

                selectedBranch = LocationUtils.findNearestBranch(userLat, userLng, branchList);

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

    // Simple inline adapter to show all branches in a list
    private static class BranchListAdapter extends RecyclerView.Adapter<BranchListAdapter.BranchViewHolder> {
        private final List<Branch> branches;

        BranchListAdapter(List<Branch> branches) {
            this.branches = branches;
        }

        @NonNull
        @Override
        public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_branch, parent, false);
            return new BranchViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
            holder.tvBranchName.setText(branches.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return branches.size();
        }

        static class BranchViewHolder extends RecyclerView.ViewHolder {
            TextView tvBranchName;

            BranchViewHolder(@NonNull View itemView) {
                super(itemView);
                tvBranchName = itemView.findViewById(R.id.tvBranchName);
            }
        }
    }
}