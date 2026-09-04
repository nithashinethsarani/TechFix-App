
package com.example.techfix_app.activities.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.Branch;

import java.util.HashMap;
import java.util.Map;

public class AddEditBranchActivity extends AppCompatActivity {

    private EditText etBranchName, etAddress, etPhone;
    private Button btnSave;
    private FirestoreManager firestoreManager;
    private String branchId = null;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_branch);

        etBranchName = findViewById(R.id.etBranchName);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);

        // Start Firestore
        firestoreManager = new FirestoreManager();

        // this checks if activity was opend to edit a branch
        if (getIntent().hasExtra("branchId")) {

            branchId = getIntent().getStringExtra("branchId");

            if (branchId != null && !branchId.isEmpty()) {
                isEditMode = true;
                loadBranchData();
            }
        }

        btnSave.setOnClickListener(v -> saveBranch());
    }

    // Load existing branch from Firestore
    private void loadBranchData() {

        if (branchId == null || branchId.isEmpty()) {
            return;
        }

        firestoreManager.getDocument("branches", branchId)
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        Branch branch =
                                documentSnapshot.toObject(Branch.class);

                        if (branch != null) {

                            // Use Firestore document ID as branch ID
                            branch.setBranchId(documentSnapshot.getId());

                            etBranchName.setText(branch.getName());
                            etAddress.setText(branch.getAddress());
                            etPhone.setText(branch.getPhone());
                        }

                    } else {

                        Toast.makeText(
                                this,
                                "Branch not found",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load branch: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // Add or update branch
    private void saveBranch() {

        String name = etBranchName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Validate required fields
        if (name.isEmpty() || address.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all required fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Branch branch = new Branch();

        branch.setBranchId(branchId);
        branch.setName(name);
        branch.setAddress(address);
        branch.setPhone(phone);

        if (isEditMode) {
            updateBranch(branch);
        } else {
            addBranch(branch);
        }
    }

    // Add a new branch to Firestore
    private void addBranch(Branch branch) {

        firestoreManager.addDocument(
                "branches",
                branchToMap(branch)
        ).addOnSuccessListener(documentReference -> {

            String generatedBranchId =
                    documentReference.getId();

            firestoreManager.updateDocument(
                    "branches",
                    generatedBranchId,
                    java.util.Collections.singletonMap(
                            "branchId",
                            generatedBranchId
                    )
            ).addOnSuccessListener(unused -> {

                Toast.makeText(
                        this,
                        "Branch added successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            }).addOnFailureListener(e -> {

                Toast.makeText(
                        this,
                        "Branch added but ID could not be saved",
                        Toast.LENGTH_LONG
                ).show();
            });

        }).addOnFailureListener(e -> {

            Toast.makeText(
                    this,
                    "Failed to add branch: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        });
    }

    // Update an existing branch in Firestore
    private void updateBranch(Branch branch) {

        if (branchId == null || branchId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Invalid branch ID",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        firestoreManager.setDocument(
                "branches",
                branchId,
                branchToMap(branch)
        ).addOnSuccessListener(unused -> {

            Toast.makeText(
                    this,
                    "Branch updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        }).addOnFailureListener(e -> {

            Toast.makeText(
                    this,
                    "Failed to update branch: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        });
    }

    // Convert Branch object into Firestore data
    private Map<String, Object> branchToMap(Branch branch) {

        Map<String, Object> data = new HashMap<>();

        data.put("branchId", branch.getBranchId());
        data.put("name", branch.getName());
        data.put("address", branch.getAddress());
        data.put("phone", branch.getPhone());
        data.put("latitude", branch.getLatitude());
        data.put("longitude", branch.getLongitude());

        return data;
    }
}
