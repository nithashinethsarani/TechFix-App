package com.example.techfix_app.activities.repairs;

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
import com.example.techfix_app.activities.payment.PaymentActivity;
import com.example.techfix_app.adapters.CustomerRepairAdapter;
import com.example.techfix_app.adapters.RepairHistoryAdapter;
import com.example.techfix_app.firebase.FirestoreManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CustomerRepairsActivity extends AppCompatActivity {

    private RecyclerView rvRepairHistory;
    private ProgressBar progressBar;
    private TextView tvMessage;

    private CustomerRepairAdapter adapter;
    private final List<DocumentSnapshot> repairs = new ArrayList<>();

    private FirebaseAuth auth;
    private FirestoreManager firestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_repairs);

        rvRepairHistory = findViewById(R.id.rvRepairHistory);
        progressBar = findViewById(R.id.progressBarRepairs);
        tvMessage = findViewById(R.id.tvRepairHistoryMessage);

        auth = FirebaseAuth.getInstance();
        firestoreManager = new FirestoreManager();

        rvRepairHistory.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new CustomerRepairAdapter(
                repairs,
                this::openRepair
        );

        rvRepairHistory.setAdapter(adapter);

        loadCustomerRepairs();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (rvRepairHistory != null && firestoreManager != null) {
            loadCustomerRepairs();
        }
    }

    private void loadCustomerRepairs() {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(
                    this,
                    "Please log in to view your repairs.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        showLoading(true);
        tvMessage.setVisibility(View.GONE);

        firestoreManager.getCustomerRepairs(user.getUid())
                .addOnSuccessListener(querySnapshot -> {
                    repairs.clear();
                    repairs.addAll(querySnapshot.getDocuments());

                    adapter.notifyDataSetChanged();

                    showLoading(false);

                    if (repairs.isEmpty()) {
                        rvRepairHistory.setVisibility(View.GONE);
                        tvMessage.setText(
                                "You don't have any repairs yet."
                        );
                        tvMessage.setVisibility(View.VISIBLE);
                    } else {
                        rvRepairHistory.setVisibility(View.VISIBLE);
                        tvMessage.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    rvRepairHistory.setVisibility(View.GONE);

                    tvMessage.setText(
                            "Could not load your repairs. Please try again."
                    );
                    tvMessage.setVisibility(View.VISIBLE);
                });
    }

    private void openRepair(String repairId) {
        Intent intent = new Intent(
                CustomerRepairsActivity.this,
                RepairStatusActivity.class
        );

        intent.putExtra("repairId", repairId);
        startActivity(intent);
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(
                isLoading ? View.VISIBLE : View.GONE
        );

        if (isLoading) {
            rvRepairHistory.setVisibility(View.GONE);
        }
    }
}