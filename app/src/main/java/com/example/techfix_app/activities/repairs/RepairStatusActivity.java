package com.example.techfix_app.activities.repairs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.activities.payment.PaymentActivity;
import com.example.techfix_app.adapters.SparePartAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RepairStatusActivity extends AppCompatActivity {
    public static boolean isPaymentCompletedLocally = false; // Local Flag for instant status update

    private TextView tvDeviceName, tvStatus, tvTechnician, tvTotalAmount;
    private RecyclerView rvSpareParts;
    private Button btnProceedToPay;
    private FirebaseFirestore db;
    private String currentRepairId = "REPAIR_1001";
    private boolean isAllPartsAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_status);

        tvDeviceName = findViewById(R.id.tvDeviceName);
        tvStatus = findViewById(R.id.tvStatus);
        tvTechnician = findViewById(R.id.tvTechnician);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        rvSpareParts = findViewById(R.id.rvSpareParts);
        btnProceedToPay = findViewById(R.id.btnProceedToPay);

        if (rvSpareParts != null) {
            rvSpareParts.setLayoutManager(new LinearLayoutManager(this));
        }

        btnProceedToPay.setOnClickListener(v -> {
            if (!isAllPartsAvailable) {
                Toast.makeText(this, "Cannot proceed! Required spare part is Out of Stock.", Toast.LENGTH_LONG).show();
                return;
            }
            openPaymentScreen("All Required Parts");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            db = FirebaseFirestore.getInstance();
            fetchRepairDetails();
        } catch (Exception e) {
            loadDefaultData();
        }
    }

    private void fetchRepairDetails() {
        db.collection("repairs").document(currentRepairId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String device = doc.getString("deviceName");
                        String status = doc.getString("status");
                        String tech = doc.getString("technicianName");
                        Double amount = doc.getDouble("totalAmount");
                        String payStatus = doc.getString("paymentStatus");

                        List<SparePartAdapter.SpareItem> partsList = new ArrayList<>();
                        List<java.util.Map<String, Object>> partsData = (List<java.util.Map<String, Object>>) doc.get("spareParts");

                        if (partsData != null && !partsData.isEmpty()) {
                            for (java.util.Map<String, Object> item : partsData) {
                                String name = (String) item.get("name");
                                Boolean available = (Boolean) item.get("isAvailable");
                                partsList.add(new SparePartAdapter.SpareItem(
                                        name != null ? name : "Spare Part",
                                        available != null ? available : true
                                ));
                            }
                        } else {
                            partsList.add(new SparePartAdapter.SpareItem("Display Cable", true));
                            partsList.add(new SparePartAdapter.SpareItem("RAM Module 8GB", true));
                        }

                        boolean isPaid = "Paid".equalsIgnoreCase(payStatus) || isPaymentCompletedLocally;
                        String displayStatus = isPaid ? "Ready for Delivery (Paid)" : (status != null ? status : "In Progress");

                        setUIData(
                                device != null ? device : "Sample Laptop",
                                displayStatus,
                                tech != null ? tech : "Kamal Perera",
                                partsList,
                                amount != null ? amount : 12500.00,
                                isPaid
                        );
                    } else {
                        loadDefaultData();
                    }
                })
                .addOnFailureListener(e -> loadDefaultData());
    }

    private void loadDefaultData() {
        List<SparePartAdapter.SpareItem> defaultParts = new ArrayList<>();
        defaultParts.add(new SparePartAdapter.SpareItem("Display Cable", true));
        defaultParts.add(new SparePartAdapter.SpareItem("RAM Module 8GB", true));

        String status = isPaymentCompletedLocally ? "Ready for Delivery (Paid)" : "In Progress";
        setUIData("Sample Laptop", status, "Kamal Perera", defaultParts, 12500.00, isPaymentCompletedLocally);
    }

    private void setUIData(String device, String status, String tech, List<SparePartAdapter.SpareItem> partsList, double amount, boolean isPaid) {
        if (tvDeviceName != null) tvDeviceName.setText("Device: " + device);
        if (tvStatus != null) {
            tvStatus.setText("Status: " + status);
            if (isPaid) {
                tvStatus.setTextColor(0xFF008800); // Green Color for Paid Status
            }
        }
        if (tvTechnician != null) tvTechnician.setText("Technician: " + tech);
        if (tvTotalAmount != null) tvTotalAmount.setText(String.format("Total: Rs. %,.2f", amount));

        this.isAllPartsAvailable = true;
        for (SparePartAdapter.SpareItem item : partsList) {
            if (!item.isAvailable) {
                this.isAllPartsAvailable = false;
                break;
            }
        }

        if (rvSpareParts != null) {
            SparePartAdapter adapter = new SparePartAdapter(partsList, selectedItem -> {
                if (isPaid) {
                    Toast.makeText(this, "Payment already completed for this repair!", Toast.LENGTH_SHORT).show();
                } else if (selectedItem.isAvailable) {
                    openPaymentScreen(selectedItem.name);
                } else {
                    Toast.makeText(this, selectedItem.name + " is Out of Stock!", Toast.LENGTH_SHORT).show();
                }
            });
            rvSpareParts.setAdapter(adapter);
        }

        if (btnProceedToPay != null) {
            if (isPaid) {
                btnProceedToPay.setEnabled(false);
                btnProceedToPay.setText("Payment Completed");
                btnProceedToPay.setAlpha(0.5f);
            } else {
                btnProceedToPay.setEnabled(isAllPartsAvailable);
                btnProceedToPay.setText("Proceed to Payment");
                btnProceedToPay.setAlpha(isAllPartsAvailable ? 1.0f : 0.5f);
            }
        }
    }

    private void openPaymentScreen(String itemDetail) {
        Intent intent = new Intent(RepairStatusActivity.this, PaymentActivity.class);
        intent.putExtra("REPAIR_ID", currentRepairId);
        intent.putExtra("SELECTED_ITEM", itemDetail);
        startActivity(intent);
    }
}