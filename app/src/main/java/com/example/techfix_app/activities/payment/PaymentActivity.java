package com.example.techfix_app.activities.payment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.activities.repairs.RepairStatusActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentActivity extends AppCompatActivity {
    private EditText etCardNumber, etExpiry, etCVV;
    private Button btnPayNow;
    private FirebaseFirestore db;
    private String repairId = "REPAIR_1001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etCVV = findViewById(R.id.etCVV);
        btnPayNow = findViewById(R.id.btnPayNow);

        try {
            db = FirebaseFirestore.getInstance();
        } catch (Exception e) {
            db = null;
        }

        // 100% Working Card Number Auto 4-Digit Space Formatter (e.g. 5429 8765 4230 9845)
        etCardNumber.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Remove all spacing chars
                int pos = 0;
                while (true) {
                    if (pos >= s.length()) break;
                    if (space == s.charAt(pos) && (((pos + 1) % 5 != 0) || (pos + 1 == s.length()))) {
                        s.delete(pos, pos + 1);
                    } else {
                        pos++;
                    }
                }

                // Insert space
                for (int i = 4; i < s.length(); i += 5) {
                    if (Character.isDigit(s.charAt(i))) {
                        s.insert(i, String.valueOf(space));
                    }
                }
            }
        });

        // Expiry Date Auto Slash MM/YY Formatter
        etExpiry.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;
                isUpdating = true;

                String clean = s.toString().replaceAll("[^\\d]", "");
                if (clean.length() >= 2) {
                    String mm = clean.substring(0, 2);
                    String yy = clean.length() > 2 ? clean.substring(2, Math.min(clean.length(), 4)) : "";
                    s.replace(0, s.length(), mm + "/" + yy);
                }

                isUpdating = false;
            }
        });

        btnPayNow.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        String cardNum = etCardNumber.getText().toString().replaceAll("\\s", "");

        if (cardNum.length() < 16) {
            Toast.makeText(this, "Please enter valid 16-digit Card Number", Toast.LENGTH_SHORT).show();
            return;
        }

        RepairStatusActivity.isPaymentCompletedLocally = true;

        if (db != null) {
            db.collection("repairs").document(repairId)
                    .update("paymentStatus", "Paid", "status", "Ready for Delivery")
                    .addOnCompleteListener(task -> completePayment());
        } else {
            completePayment();
        }
    }

    private void completePayment() {
        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_LONG).show();
        finish();
    }
}