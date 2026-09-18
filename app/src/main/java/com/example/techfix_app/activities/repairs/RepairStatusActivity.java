
package com.example.techfix_app.activities.repairs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.activities.payment.PaymentActivity;
import com.example.techfix_app.firebase.FirestoreManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class RepairStatusActivity extends AppCompatActivity {

    private TextView tvDeviceName;
    private TextView tvStatus;
    private TextView tvTechnician;
    private TextView tvTotalAmount;
    private TextView tvRepairDescription;
    private TextView tvInventoryMessage;

    private LinearLayout layoutRepairInventoryItems;
    private Button btnProceedToPay;
    private ProgressBar progressBar;

    private FirestoreManager firestoreManager;
    private String currentRepairId;

    private String currentPaymentStatus = "unpaid";
    private double currentFinalPrice = 0;
    private String currentRepairStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_status);

        tvDeviceName = findViewById(R.id.tvDeviceName);
        tvStatus = findViewById(R.id.tvStatus);
        tvTechnician = findViewById(R.id.tvTechnician);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvRepairDescription = findViewById(R.id.tvRepairDescription);
        tvInventoryMessage = findViewById(R.id.tvInventoryMessage);

        layoutRepairInventoryItems =
                findViewById(R.id.layoutRepairInventoryItems);

        btnProceedToPay = findViewById(R.id.btnProceedToPay);
        progressBar = findViewById(R.id.progressBar);

        firestoreManager = new FirestoreManager();

        currentRepairId = getIntent().getStringExtra("repairId");

        if (currentRepairId == null ||
                currentRepairId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Repair ID is missing",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        btnProceedToPay.setEnabled(false);

        btnProceedToPay.setOnClickListener(v -> openPaymentScreen());

        fetchRepairDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (firestoreManager != null &&
                currentRepairId != null) {
            fetchRepairDetails();
        }
    }

    private void fetchRepairDetails() {

        showLoading(true);

        firestoreManager.getRepair(currentRepairId)
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        showLoading(false);
                        showRepairNotFound();
                        return;
                    }

                    loadRepairData(doc);
                })
                .addOnFailureListener(e -> {
                    showLoading(false);

                    Toast.makeText(
                            this,
                            "Failed to load repair: " +
                                    e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void loadRepairData(DocumentSnapshot doc) {

        String deviceName = doc.getString("deviceName");
        String status = doc.getString("status");
        String technicianId = doc.getString("technicianId");
        String description = doc.getString("problemDescription");
        String serviceId = doc.getString("serviceId");
        String paymentStatus = doc.getString("paymentStatus");

        Double finalPrice = doc.getDouble("finalPrice");

        currentRepairStatus =
                status != null ? status : "";

        currentPaymentStatus =
                paymentStatus != null
                        ? paymentStatus
                        : "unpaid";

        currentFinalPrice =
                finalPrice != null ? finalPrice : 0;

        tvDeviceName.setText(
                "Device: " +
                        (deviceName != null
                                ? deviceName
                                : "Not specified")
        );

        tvStatus.setText(
                "Status: " + formatStatus(currentRepairStatus)
        );

        tvTechnician.setText(
                "Technician: " +
                        (technicianId != null &&
                                !technicianId.isEmpty()
                                ? technicianId
                                : "Not assigned")
        );

        tvRepairDescription.setText(
                "Problem: " +
                        (description != null &&
                                !description.isEmpty()
                                ? description
                                : "No description available")
        );

        if (finalPrice != null) {
            tvTotalAmount.setText(
                    String.format(
                            "Final Price: Rs. %,.2f",
                            currentFinalPrice
                    )
            );
        } else {
            tvTotalAmount.setText(
                    "Final Price: Not set yet"
            );
        }

        updatePaymentButton();

        if (serviceId != null && !serviceId.isEmpty()) {
            loadServiceInventory(serviceId);
        } else {
            showInventoryMessage(
                    "Service information is unavailable"
            );
            showLoading(false);
        }
    }

    private void loadServiceInventory(String serviceId) {

        layoutRepairInventoryItems.removeAllViews();

        tvInventoryMessage.setVisibility(View.VISIBLE);
        tvInventoryMessage.setText("Loading service items...");

        firestoreManager.getService(serviceId)
                .addOnSuccessListener(serviceDoc -> {

                    if (!serviceDoc.exists()) {
                        showInventoryMessage("Service not found");
                        showLoading(false);
                        return;
                    }

                    List<String> itemIds =
                            (List<String>) serviceDoc.get(
                                    "inventoryItemIds"
                            );

                    // Support the alternate spelling if that is
                    // the field name in your Firestore documents.
                    if (itemIds == null) {
                        itemIds = (List<String>) serviceDoc.get(
                                "intentoryItemIds"
                        );
                    }

                    if (itemIds == null || itemIds.isEmpty()) {
                        showInventoryMessage(
                                "No inventory items linked to this service"
                        );
                        showLoading(false);
                        return;
                    }

                    tvInventoryMessage.setVisibility(View.GONE);

                    final int totalItems = itemIds.size();
                    final int[] loadedItems = {0};
                    final int[] displayedItems = {0};

                    for (String itemId : itemIds) {

                        if (itemId == null ||
                                itemId.trim().isEmpty()) {

                            loadedItems[0]++;
                            continue;
                        }

                        loadInventoryItem(
                                itemId,
                                totalItems,
                                loadedItems,
                                displayedItems
                        );
                    }

                    if (totalItems == 0) {
                        showInventoryMessage(
                                "No inventory items linked to this service"
                        );
                        showLoading(false);
                    }
                })
                .addOnFailureListener(e -> {
                    showInventoryMessage(
                            "Failed to load service items"
                    );
                    showLoading(false);
                });
    }

    private void loadInventoryItem(
            String itemId,
            int totalItems,
            int[] loadedItems,
            int[] displayedItems
    ) {

        firestoreManager.getInventoryItem(itemId)
                .addOnSuccessListener(itemDoc -> {

                    if (itemDoc.exists()) {

                        String itemName =
                                itemDoc.getString("itemName");

                        Double price =
                                itemDoc.getDouble("price");

                        addInventoryRow(
                                itemName != null
                                        ? itemName
                                        : "Inventory item",
                                price != null ? price : 0
                        );

                        displayedItems[0]++;
                    }

                    loadedItems[0]++;

                    finishInventoryLoading(
                            totalItems,
                            loadedItems[0],
                            displayedItems[0]
                    );
                })
                .addOnFailureListener(e -> {

                    loadedItems[0]++;

                    finishInventoryLoading(
                            totalItems,
                            loadedItems[0],
                            displayedItems[0]
                    );
                });
    }

    private void finishInventoryLoading(
            int totalItems,
            int loadedItems,
            int displayedItems
    ) {

        if (loadedItems >= totalItems) {

            if (displayedItems == 0) {
                showInventoryMessage(
                        "No matching inventory items were found"
                );
            } else {
                tvInventoryMessage.setVisibility(View.GONE);
            }

            showLoading(false);
        }
    }

    private void addInventoryRow(
            String itemName,
            double price
    ) {

        View row = LayoutInflater.from(this).inflate(
                R.layout.item_repair_inventory,
                layoutRepairInventoryItems,
                false
        );

        TextView tvName =
                row.findViewById(R.id.tvInventoryItemName);

        TextView tvPrice =
                row.findViewById(R.id.tvInventoryItemPrice);

        tvName.setText(itemName);

        tvPrice.setText(
                String.format("Rs. %,.2f", price)
        );

        layoutRepairInventoryItems.addView(row);
    }

    private void showInventoryMessage(String message) {

        layoutRepairInventoryItems.removeAllViews();

        tvInventoryMessage.setText(message);
        tvInventoryMessage.setVisibility(View.VISIBLE);
    }

    private void updatePaymentButton() {

        boolean repairCompleted =
                "completed".equalsIgnoreCase(
                        currentRepairStatus
                );

        boolean paymentCompleted =
                "paid".equalsIgnoreCase(
                        currentPaymentStatus
                );

        boolean priceAvailable = currentFinalPrice > 0;

        if (paymentCompleted) {

            btnProceedToPay.setEnabled(false);
            btnProceedToPay.setText("Payment Completed");

        } else if (repairCompleted && priceAvailable) {

            btnProceedToPay.setEnabled(true);
            btnProceedToPay.setText("Proceed to Payment");

        } else if (!repairCompleted) {

            btnProceedToPay.setEnabled(false);
            btnProceedToPay.setText(
                    "Payment Available After Completion"
            );

        } else {

            btnProceedToPay.setEnabled(false);
            btnProceedToPay.setText(
                    "Waiting for Final Price"
            );
        }
    }

    private void openPaymentScreen() {

        if (!"completed".equalsIgnoreCase(
                currentRepairStatus)) {

            Toast.makeText(
                    this,
                    "Repair is not completed yet",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if ("paid".equalsIgnoreCase(currentPaymentStatus)) {
            Toast.makeText(
                    this,
                    "Payment has already been completed",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (currentFinalPrice <= 0) {
            Toast.makeText(
                    this,
                    "Final price has not been set",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Intent intent = new Intent(
                RepairStatusActivity.this,
                PaymentActivity.class
        );

        intent.putExtra("repairId", currentRepairId);

        startActivity(intent);
    }

    private String formatStatus(String status) {

        if (status == null || status.trim().isEmpty()) {
            return "Unknown";
        }

        String readable = status.replace("_", " ");
        return readable.substring(0, 1).toUpperCase()
                + readable.substring(1);
    }

    private void showRepairNotFound() {

        tvDeviceName.setText("Repair not found");
        tvStatus.setText("Status: Unavailable");
        tvTechnician.setText("Technician: Unavailable");
        tvRepairDescription.setText("");
        tvTotalAmount.setText("Final Price: Unavailable");

        btnProceedToPay.setEnabled(false);
        btnProceedToPay.setText("Payment Unavailable");
    }

    private void showLoading(boolean isLoading) {

        if (progressBar != null) {
            progressBar.setVisibility(
                    isLoading ? View.VISIBLE : View.GONE
            );
        }
    }
}