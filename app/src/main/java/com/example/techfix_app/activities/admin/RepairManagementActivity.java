package com.example.techfix_app.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.adapters.AdminRepairAdapter;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.Repair;

import java.util.ArrayList;
import java.util.List;

public class RepairManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvNoRepairs;

    private AdminRepairAdapter adapter;
    private final List<Repair> repairList = new ArrayList<>();

    private FirestoreManager firestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_repair_management);

        recyclerView = findViewById(R.id.recyclerViewRepairs);
        progressBar = findViewById(R.id.progressBar);
        tvNoRepairs = findViewById(R.id.tvNoRepairs);

        firestoreManager = new FirestoreManager();

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new AdminRepairAdapter(
                repairList,
                repair -> {
                    Intent intent = new Intent(
                            RepairManagementActivity.this, RepairDetailsActivity.class
                    );

                    intent.putExtra("repairId",repair.getRepairId());

                    startActivity(intent);
                }
        );

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRepairs();
    }

    private void loadRepairs() {

        progressBar.setVisibility(View.VISIBLE);
        tvNoRepairs.setVisibility(View.GONE);

        firestoreManager.getActiveRepairs()
                .addOnSuccessListener(querySnapshot -> {

                    repairList.clear();

                    for (com.google.firebase.firestore.DocumentSnapshot document
                            : querySnapshot.getDocuments()) {

                        Repair repair =
                                document.toObject(Repair.class);

                        if (repair != null) {

                            if (repair.getRepairId() == null
                                    || repair.getRepairId().isEmpty()) {

                                repair.setRepairId(
                                        document.getId()
                                );
                            }

                            repairList.add(repair);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                    if (repairList.isEmpty()) {
                        tvNoRepairs.setVisibility(View.VISIBLE);
                    }

                })
                .addOnFailureListener(e -> {

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(
                            RepairManagementActivity.this,
                            "Failed to load repairs: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}