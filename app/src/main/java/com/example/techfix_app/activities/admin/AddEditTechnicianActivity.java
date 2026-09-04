package com.example.techfix_app.activities.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.Branch;
import com.example.techfix_app.models.Technician;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEditTechnicianActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etSpecialization;
    private EditText etPhone;
    private Spinner spinnerBranch;
    private Button btnSave;

    private FirestoreManager firestoreManager;

    private String technicianId = null;

    private String selectedTechnicianBranchId = null;

    private final List<Branch> branchList = new ArrayList<>();

    private ArrayAdapter<String> branchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_technician);

        // Initialize views
        etName = findViewById(R.id.etTechName);
        etSpecialization = findViewById(R.id.etSpecialization);
        etPhone = findViewById(R.id.etTechPhone);
        spinnerBranch = findViewById(R.id.spinnerBranch);
        btnSave = findViewById(R.id.btnSaveTechnician);

        firestoreManager = new FirestoreManager();

        // checks if edting technician
        if (getIntent().hasExtra("technicianId")) {

            technicianId = getIntent().getStringExtra("technicianId");

            if (technicianId != null && !technicianId.isEmpty()) {
                loadTechnicianData();
            }
        }

        // Load branches from Firestore
        loadBranchesToSpinner();

        btnSave.setOnClickListener(v -> saveTechnician());
    }


    //Load all branches from Firestore
    private void loadBranchesToSpinner() {

        firestoreManager.getAllBranches(
                new FirestoreManager.OnBranchesLoadedListener() {

                    @Override
                    public void onSuccess(List<Branch> branches) {

                        branchList.clear();
                        branchList.addAll(branches);

                        List<String> branchNames = new ArrayList<>();

                        for (Branch branch : branchList) {
                            branchNames.add(branch.getName());
                        }

                        branchAdapter = new ArrayAdapter<>(
                                AddEditTechnicianActivity.this,
                                android.R.layout.simple_spinner_item,
                                branchNames
                        );

                        branchAdapter.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item
                        );

                        spinnerBranch.setAdapter(branchAdapter);

                        // If editing, select the technician's branch
                        selectTechnicianBranch();
                    }

                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(
                                AddEditTechnicianActivity.this,
                                "Failed to load branches: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    //Load an existing technician from Firestore.
    private void loadTechnicianData() {

        if (technicianId == null || technicianId.isEmpty()) {
            return;
        }

        firestoreManager.getDocument(
                "technicians",
                technicianId
        ).addOnSuccessListener(documentSnapshot -> {

            if (documentSnapshot.exists()) {

                Technician technician =
                        documentSnapshot.toObject(Technician.class);

                if (technician != null) {

                    // Store the Firestore document ID
                    technician.setTechnicianId(
                            documentSnapshot.getId()
                    );

                    etName.setText(technician.getName());

                    etSpecialization.setText(
                            technician.getSpecialization()
                    );

                    etPhone.setText(
                            technician.getPhone()
                    );

                    // Store branch ID so the Spinner
                    // can select it after branches load.
                    selectedTechnicianBranchId =
                            technician.getBranchId();
                }

            } else {

                Toast.makeText(
                        this,
                        "Technician not found",
                        Toast.LENGTH_SHORT
                ).show();
            }

        }).addOnFailureListener(e -> {

            Toast.makeText(
                    this,
                    "Failed to load technician: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        });
    }


    //Select the technician's existing branch
    private void selectTechnicianBranch() {

        if (selectedTechnicianBranchId == null) {
            return;
        }

        for (int i = 0; i < branchList.size(); i++) {

            String branchId =
                    branchList.get(i).getBranchId();

            if (selectedTechnicianBranchId.equals(branchId)) {

                spinnerBranch.setSelection(i);
                break;
            }
        }
    }


    //Validate and save technician.

    private void saveTechnician() {

        String name = etName.getText()
                .toString()
                .trim();

        String specialization = etSpecialization.getText()
                .toString()
                .trim();

        String phone = etPhone.getText()
                .toString()
                .trim();


        if (name.isEmpty()
                || specialization.isEmpty()
                || phone.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Make sure branches are available
        if (branchList.isEmpty()) {

            Toast.makeText(
                    this,
                    "No branches available. Add a branch first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int selectedPosition =
                spinnerBranch.getSelectedItemPosition();

        if (selectedPosition < 0
                || selectedPosition >= branchList.size()) {

            Toast.makeText(
                    this,
                    "Please select a branch",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Branch selectedBranch =
                branchList.get(selectedPosition);


        Technician technician = new Technician();

        technician.setTechnicianId(technicianId);
        technician.setName(name);
        technician.setSpecialization(specialization);
        technician.setPhone(phone);
        technician.setBranchId(
                selectedBranch.getBranchId()
        );


        if (technicianId == null
                || technicianId.isEmpty()) {

            addTechnician(technician);

        } else {

            updateTechnician(technician);
        }
    }

    //Add new technician to Firestore
    private void addTechnician(Technician technician) {

        firestoreManager.addTechnician(technician)
                .addOnSuccessListener(documentReference -> {

                    String generatedTechnicianId =
                            documentReference.getId();

                    Map<String, Object> updates =
                            new HashMap<>();

                    updates.put(
                            "technicianId",
                            generatedTechnicianId
                    );

                    firestoreManager.updateTechnician(
                            generatedTechnicianId,
                            updates
                    ).addOnSuccessListener(unused -> {

                        Toast.makeText(
                                this,
                                "Technician added successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();

                    }).addOnFailureListener(e -> {

                        Toast.makeText(
                                this,
                                "Technician added, but ID could not "
                                        + "be saved: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to add technician: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    //Update existing technician in Firestore.
    private void updateTechnician(Technician technician) {

        if (technicianId == null
                || technicianId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Invalid technician ID",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        firestoreManager.setTechnician(
                technicianId,
                technician
        ).addOnSuccessListener(unused -> {

            Toast.makeText(
                    this,
                    "Technician updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        }).addOnFailureListener(e -> {

            Toast.makeText(
                    this,
                    "Failed to update technician: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        });
    }
}