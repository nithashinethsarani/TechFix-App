package com.example.techfix_app.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.adapters.TechnicianAdapter;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.Technician;

import java.util.ArrayList;
import java.util.List;

public class TechnicianManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TechnicianAdapter adapter;

    private List<Technician> technicianList = new ArrayList<>();

    private FirestoreManager firestoreManager;

    private View fabAddTechnician;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_management);

        recyclerView = findViewById(R.id.recyclerViewTechnicians);
        fabAddTechnician = findViewById(R.id.fabAddTechnician);

        firestoreManager = new FirestoreManager();

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new TechnicianAdapter(
                technicianList,
                new TechnicianAdapter.OnTechnicianClickListener() {

                    @Override
                    public void onEditClick(Technician technician) {

                        Intent intent = new Intent(
                                TechnicianManagementActivity.this,
                                AddEditTechnicianActivity.class
                        );

                        intent.putExtra(
                                "technicianId",
                                technician.getTechnicianId()
                        );

                        startActivity(intent);
                    }

                    @Override
                    public void onDeleteClick(Technician technician) {

                        deleteTechnician(technician);
                    }
                }
        );

        recyclerView.setAdapter(adapter);

        // Add technician
        fabAddTechnician.setOnClickListener(v -> {

            Intent intent = new Intent(
                    TechnicianManagementActivity.this,
                    AddEditTechnicianActivity.class
            );

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTechnicians();
    }


    //Load all technicians from Firestore

    private void loadTechnicians() {

        firestoreManager.getAllTechnicians()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    technicianList.clear();

                    for (com.google.firebase.firestore.DocumentSnapshot document
                            : queryDocumentSnapshots.getDocuments()) {

                        Technician technician =
                                document.toObject(Technician.class);

                        if (technician != null) {

                            if (technician.getTechnicianId() == null ||
                                    technician.getTechnicianId().isEmpty()) {

                                technician.setTechnicianId(
                                        document.getId()
                                );
                            }

                            technicianList.add(technician);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            TechnicianManagementActivity.this,
                            "Failed to load technicians: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    //Delete technician from Firestore
    private void deleteTechnician(Technician technician) {

        String technicianId = technician.getTechnicianId();

        if (technicianId == null || technicianId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Invalid technician ID",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        firestoreManager.deleteDocument(
                "technicians",
                technicianId
        ).addOnSuccessListener(unused -> {

            Toast.makeText(
                    TechnicianManagementActivity.this,
                    "Technician deleted successfully",
                    Toast.LENGTH_SHORT
            ).show();

            // Remove from current RecyclerView list
            technicianList.remove(technician);
            adapter.notifyDataSetChanged();

        }).addOnFailureListener(e -> {

            Toast.makeText(
                    TechnicianManagementActivity.this,
                    "Delete failed: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        });
    }
}