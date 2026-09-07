package com.example.techfix_app.activities.repairs;

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

import java.util.List;

public class RepairHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private TextView textNoHistory;
    private RepairHistoryDatabaseHelper dbHelper;
    private FirestoreManager firestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_history);

        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        textNoHistory = findViewById(R.id.textNoHistory);

        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new RepairHistoryDatabaseHelper(this);
        firestoreManager = new FirestoreManager();

        // 1. Load local SQLite history first
        loadLocalHistory();

        // 2. Fetch completed repairs from Firestore and sync to SQLite
        syncCompletedRepairs();
    }

    private void loadLocalHistory() {
        List<RepairHistory> historyList = dbHelper.getAllRepairHistory();

        if (historyList == null || historyList.isEmpty()) {
            textNoHistory.setVisibility(View.VISIBLE);
            recyclerViewHistory.setVisibility(View.GONE);
        } else {
            textNoHistory.setVisibility(View.GONE);
            recyclerViewHistory.setVisibility(View.VISIBLE);

            RepairHistoryAdapter adapter = new RepairHistoryAdapter(historyList);
            recyclerViewHistory.setAdapter(adapter);
        }
    }

    private void syncCompletedRepairs() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        firestoreManager.getCompletedRepairsForCustomer(currentUser.getUid(), new FirestoreManager.OnCompletedRepairsLoadedListener() {
            @Override
            public void onSuccess(List<DocumentSnapshot> repairDocuments) {
                for (DocumentSnapshot doc : repairDocuments) {
                    RepairHistory item = new RepairHistory();
                    item.setRepairId(doc.getId());
                    item.setAppointmentId(doc.getString("appointmentId"));
                    item.setDeviceName(doc.getString("deviceName"));
                    item.setServiceName(doc.getString("serviceName"));

                    Double price = doc.getDouble("finalPrice");
                    item.setFinalPrice(price != null ? price : 0.0);

                    item.setCompletedDate(doc.getString("completedDate"));
                    item.setStatus(doc.getString("status"));

                    // Store into SQLite local database
                    dbHelper.saveRepairHistory(item);
                }

                // Refresh SQLite list UI
                loadLocalHistory();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(RepairHistoryActivity.this, "Failed to sync online history", Toast.LENGTH_SHORT).show();
            }
        });
    }
}