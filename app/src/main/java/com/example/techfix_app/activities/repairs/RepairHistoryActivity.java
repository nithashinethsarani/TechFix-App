package com.example.techfix_app.activities.repairs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.adapters.RepairHistoryAdapter;
import com.example.techfix_app.database.RepairHistoryDatabaseHelper;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.RepairHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RepairHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private TextView textNoHistory;

    private RepairHistoryDatabaseHelper dbHelper;
    private FirestoreManager firestoreManager;

    private RepairHistoryAdapter adapter;
    private List<RepairHistory> historyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_history);

        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        textNoHistory = findViewById(R.id.textNoHistory);

        recyclerViewHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );

        dbHelper = new RepairHistoryDatabaseHelper(this);
        firestoreManager = new FirestoreManager();

        // 1. Load local SQLite history first
        loadLocalHistory();

        // 2. Sync completed repairs from Firestore
        syncCompletedRepairs();
    }

    private void loadLocalHistory() {

        historyList = dbHelper.getAllRepairHistory();

        if (historyList == null) {
            historyList = new ArrayList<>();
        }

        if (historyList.isEmpty()) {
            textNoHistory.setVisibility(View.VISIBLE);
            recyclerViewHistory.setVisibility(View.GONE);
            return;
        }

        textNoHistory.setVisibility(View.GONE);
        recyclerViewHistory.setVisibility(View.VISIBLE);

        // Create adapter with both delete and click listeners
        adapter = new RepairHistoryAdapter(
                historyList,

                // DELETE LISTENER
                this::deleteRecordDirectly,

                // CLICK LISTENER
                this::openRepairStatus
        );

        recyclerViewHistory.setAdapter(adapter);
    }


    //use to open repairStatus when cicked
    private void openRepairStatus(RepairHistory item) {

        if (item == null ||
                item.getRepairId() == null ||
                item.getRepairId().trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Repair ID is missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Intent intent = new Intent(
                RepairHistoryActivity.this,
                RepairStatusActivity.class
        );

        // RepairStatusActivity reads this exact extra key
        intent.putExtra("repairId", item.getRepairId());

        startActivity(intent);
    }

    //use to delete
    private void deleteRecordDirectly(
            RepairHistory item,
            int position) {

        if (item == null) {
            return;
        }

        boolean isDeleted = dbHelper.deleteRepairHistory(
                item.getRepairId()
        );

        if (isDeleted) {

            if (position >= 0 &&
                    position < historyList.size()) {

                historyList.remove(position);

                if (adapter != null) {
                    adapter.notifyItemRemoved(position);
                }
            }

            Toast.makeText(
                    this,
                    "Record deleted",
                    Toast.LENGTH_SHORT
            ).show();

            if (historyList.isEmpty()) {
                textNoHistory.setVisibility(View.VISIBLE);
                recyclerViewHistory.setVisibility(View.GONE);
            }

        } else {

            Toast.makeText(
                    this,
                    "Failed to delete record",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    //saves completed repairs
    private void syncCompletedRepairs() {

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            return;
        }

        firestoreManager.getCompletedRepairsForCustomer(
                currentUser.getUid(),

                new FirestoreManager.OnCompletedRepairsLoadedListener() {

                    @Override
                    public void onSuccess(
                            List<DocumentSnapshot> repairDocuments) {

                        if (repairDocuments == null ||
                                repairDocuments.isEmpty()) {

                            loadLocalHistory();
                            return;
                        }

                        final int totalItems =
                                repairDocuments.size();

                        final AtomicInteger processedCount =
                                new AtomicInteger(0);

                        for (DocumentSnapshot doc : repairDocuments) {

                            RepairHistory item = new RepairHistory();

                            // Store Firestore document ID as repair ID
                            item.setRepairId(doc.getId());

                            item.setAppointmentId(
                                    doc.getString("appointmentId")
                            );

                            item.setDeviceName(
                                    doc.getString("deviceName")
                            );

                            Double price =
                                    doc.getDouble("finalPrice");

                            item.setFinalPrice(
                                    price != null ? price : 0.0
                            );

                            // Completed date
                            com.google.firebase.Timestamp timestamp =
                                    doc.getTimestamp("completedAt");

                            if (timestamp != null) {

                                java.util.Date date =
                                        timestamp.toDate();

                                java.text.SimpleDateFormat sdf =
                                        new java.text.SimpleDateFormat(
                                                "dd-MM-yyyy | HH:mm",
                                                java.util.Locale.getDefault()
                                        );

                                item.setCompletedDate(
                                        sdf.format(date)
                                );

                            } else {
                                item.setCompletedDate("N/A");
                            }

                            item.setStatus(
                                    doc.getString("status")
                            );

                            // Fetch service name
                            String serviceId =
                                    doc.getString("serviceId");

                            if (serviceId != null &&
                                    !serviceId.isEmpty()) {

                                firestoreManager.getService(serviceId)
                                        .addOnSuccessListener(serviceDoc -> {

                                            if (serviceDoc.exists()) {

                                                String serviceName =
                                                        serviceDoc.getString("name");

                                                item.setServiceName(
                                                        serviceName != null
                                                                ? serviceName
                                                                : "Unknown Service"
                                                );

                                            } else {
                                                item.setServiceName(
                                                        "Service Not Found"
                                                );
                                            }

                                            saveAndCheckCompletion(
                                                    item,
                                                    processedCount,
                                                    totalItems
                                            );
                                        })
                                        .addOnFailureListener(e -> {

                                            item.setServiceName(
                                                    "Service Unavailable"
                                            );

                                            saveAndCheckCompletion(
                                                    item,
                                                    processedCount,
                                                    totalItems
                                            );
                                        });

                            } else {

                                item.setServiceName("N/A");

                                saveAndCheckCompletion(
                                        item,
                                        processedCount,
                                        totalItems
                                );
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(
                                RepairHistoryActivity.this,
                                "Failed to sync online history",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }
    private void saveAndCheckCompletion(
            RepairHistory item,
            AtomicInteger processedCount,
            int totalItems) {

        dbHelper.saveRepairHistory(item);

        if (processedCount.incrementAndGet() == totalItems) {
            loadLocalHistory();
        }
    }
}