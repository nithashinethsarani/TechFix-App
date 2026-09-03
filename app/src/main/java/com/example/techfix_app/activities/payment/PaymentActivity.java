package com.example.techfix_app.activities.payment;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.firebase.FirestoreManager;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentActivity extends AppCompatActivity {

    private EditText etCardNumber;
    private EditText etExpiry;
    private EditText etCVV;

    private Button btnPayNow;

    private FirebaseFirestore db;

    private String repairId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Get repair ID from previous activity
        repairId = getIntent().getStringExtra("REPAIR_ID");

        // Connect XML views
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etCVV = findViewById(R.id.etCVV);

        btnPayNow = findViewById(R.id.btnPayNow);

        // Initialize Firestore
        try {
            db = FirebaseFirestore.getInstance();
        } catch (Exception e) {
            db = null;
        }

        // Payment button
        btnPayNow.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {

        String cardNumber =
                etCardNumber.getText().toString().trim();

        String expiry =
                etExpiry.getText().toString().trim();

        String cvv =
                etCVV.getText().toString().trim();

        // Validate fields
        if (cardNumber.isEmpty()
                || expiry.isEmpty()
                || cvv.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all card details",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Update repair payment status
        if (db != null && repairId != null) {

            db.collection("repairs")
                    .document(repairId)
                    .update(
                            "paymentStatus",
                            "Paid",
                            "status",
                            "Ready for Delivery"
                    )
                    .addOnSuccessListener(aVoid -> {

                        Toast.makeText(
                                PaymentActivity.this,
                                "Payment Successful!",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                PaymentActivity.this,
                                "Payment Failed",
                                Toast.LENGTH_SHORT
                        ).show();
                    });

        } else {

            // Temporary dummy payment
            Toast.makeText(
                    this,
                    "Payment Successful! (Dummy Mode)",
                    Toast.LENGTH_LONG
            ).show();

            finish();
        }
    }
}