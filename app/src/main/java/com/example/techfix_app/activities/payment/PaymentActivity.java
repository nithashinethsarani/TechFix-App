package com.example.techfix_app.activities.payment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private EditText etCardNumber, etExpiry, etCVV;
    private Button btnPayNow;
    private TextView tvPaymentAmount;

    private FirebaseFirestore db;

    private String repairId;
    private double finalPrice;
    private boolean repairIsCompleted;
    private boolean paymentAlreadyPaid;
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etCVV = findViewById(R.id.etCVV);
        btnPayNow = findViewById(R.id.btnPayNow);

        tvPaymentAmount = findViewById(R.id.tvPaymentAmount);

        db = FirebaseFirestore.getInstance();

        repairId = getIntent().getStringExtra("repairId");

        if (repairId == null || repairId.trim().isEmpty()) {
            Toast.makeText(this, "Repair ID is missing.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnPayNow.setEnabled(false);
        btnPayNow.setText("Loading...");

        setupCardNumberFormatter();
        setupExpiryFormatter();

        loadRepair();

        btnPayNow.setOnClickListener(v -> processPayment());
    }

    private void loadRepair() {
        db.collection("repairs")
                .document(repairId)
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        Toast.makeText(this, "Repair not found.", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    String status = document.getString("status");
                    String paymentStatus = document.getString("paymentStatus");

                    repairIsCompleted = status != null
                            && status.equalsIgnoreCase("completed");

                    paymentAlreadyPaid = paymentStatus != null
                            && paymentStatus.equalsIgnoreCase("paid");

                    Number amount = document.getDouble("finalPrice");

                    if (amount == null) {
                        amount = document.getLong("finalPrice");
                    }

                    finalPrice = amount == null ? 0.0 : amount.doubleValue();

                    tvPaymentAmount.setText(
                            "Amount to Pay: " + formatRupees(finalPrice)
                    );

                    if (paymentAlreadyPaid) {
                        btnPayNow.setText("Already Paid");
                        btnPayNow.setEnabled(false);
                        Toast.makeText(this,
                                "This repair has already been paid.",
                                Toast.LENGTH_LONG).show();
                    } else if (!repairIsCompleted) {
                        btnPayNow.setText("Repair Not Completed");
                        btnPayNow.setEnabled(false);
                        Toast.makeText(this,
                                "Payment is available after the repair is completed.",
                                Toast.LENGTH_LONG).show();
                    } else if (finalPrice <= 0) {
                        btnPayNow.setText("Amount Not Available");
                        btnPayNow.setEnabled(false);
                        Toast.makeText(this,
                                "The final repair amount is not available.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        btnPayNow.setText("Pay Now");
                        btnPayNow.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Could not load repair details.",
                            Toast.LENGTH_LONG).show();

                    btnPayNow.setText("Try Again");
                    btnPayNow.setEnabled(true);

                    btnPayNow.setOnClickListener(v -> loadRepair());
                });
    }

    private void processPayment() {
        if (isProcessing) {
            return;
        }

        String cardNum = etCardNumber.getText()
                .toString()
                .replaceAll("\\s", "");

        String expiry = etExpiry.getText().toString().trim();
        String cvv = etCVV.getText().toString().trim();

        if (!repairIsCompleted || paymentAlreadyPaid || finalPrice <= 0) {
            Toast.makeText(this,
                    "This repair is not eligible for payment.",
                    Toast.LENGTH_LONG).show();
            loadRepair();
            return;
        }

        if (!cardNum.matches("\\d{16}")) {
            etCardNumber.setError("Enter a 16-digit test card number");
            return;
        }

        if (!isValidExpiry(expiry)) {
            etExpiry.setError("Enter a valid MM/YY expiry date");
            return;
        }

        if (!cvv.matches("\\d{3}")) {
            etCVV.setError("Enter a 3-digit test CVV");
            return;
        }

        isProcessing = true;
        btnPayNow.setEnabled(false);
        btnPayNow.setText("Processing...");

        db.collection("repairs")
                .document(repairId)
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        paymentFailed("Repair not found.");
                        return;
                    }

                    String latestStatus = document.getString("status");
                    String latestPaymentStatus =
                            document.getString("paymentStatus");

                    boolean completed = latestStatus != null
                            && latestStatus.equalsIgnoreCase("completed");

                    boolean alreadyPaid = latestPaymentStatus != null
                            && latestPaymentStatus.equalsIgnoreCase("paid");

                    if (!completed || alreadyPaid) {
                        paymentFailed(alreadyPaid
                                ? "This repair is already paid."
                                : "Repair is not completed yet.");
                        loadRepair();
                        return;
                    }

                    Number latestAmount = document.getDouble("finalPrice");

                    if (latestAmount == null) {
                        latestAmount = document.getLong("finalPrice");
                    }

                    if (latestAmount == null
                            || latestAmount.doubleValue() <= 0
                            || Double.compare(
                            latestAmount.doubleValue(),
                            finalPrice) != 0) {
                        paymentFailed(
                                "The repair amount changed. Please check again.");
                        loadRepair();
                        return;
                    }

                    markPaymentAsPaid();
                })
                .addOnFailureListener(e ->
                        paymentFailed("Could not verify repair details."));
    }

    private void markPaymentAsPaid() {
        db.collection("repairs")
                .document(repairId)
                .update("paymentStatus", "paid")
                .addOnSuccessListener(unused -> {
                    isProcessing = false;

                    Toast.makeText(this,
                            "Dummy payment successful!",
                            Toast.LENGTH_LONG).show();

                    finish();
                })
                .addOnFailureListener(e ->
                        paymentFailed("Payment update failed. Please try again."));
    }

    private void paymentFailed(String message) {
        isProcessing = false;
        btnPayNow.setEnabled(true);
        btnPayNow.setText("Pay Now");

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private boolean isValidExpiry(String expiry) {
        if (expiry == null) {
            return false;
        }

        // Clean digits only
        String digitsOnly = expiry.replaceAll("\\D", "");

        if (digitsOnly.length() != 4) {
            return false;
        }

        try {
            int month = Integer.parseInt(digitsOnly.substring(0, 2));
            int year = Integer.parseInt(digitsOnly.substring(2, 4));

            if (month < 1 || month > 12) {
                return false;
            }

            Calendar now = Calendar.getInstance();
            int currentYear = now.get(Calendar.YEAR) % 100; // e.g. 26
            int currentMonth = now.get(Calendar.MONTH) + 1; // 1-12

            if (year < currentYear) {
                return false;
            }

            return year > currentYear || month >= currentMonth;
        } catch (Exception e) {
            return false;
        }
    }

    private void setupCardNumberFormatter() {
        etCardNumber.addTextChangedListener(new TextWatcher() {
            private boolean updating;

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (updating) return;

                updating = true;

                String digits = editable.toString()
                        .replaceAll("\\D", "");

                if (digits.length() > 16) {
                    digits = digits.substring(0, 16);
                }

                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(' ');
                    }
                    formatted.append(digits.charAt(i));
                }

                editable.replace(0, editable.length(), formatted.toString());
                etCardNumber.setSelection(editable.length());

                updating = false;
            }
        });
    }

    private void setupExpiryFormatter() {
        etExpiry.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) {
                    return;
                }

                isUpdating = true;

                // Clear error automatically as the user types
                etExpiry.setError(null);

                String digits = s.toString().replaceAll("\\D", "");

                if (digits.length() > 4) {
                    digits = digits.substring(0, 4);
                }

                StringBuilder formatted = new StringBuilder();

                if (digits.length() >= 2) {
                    formatted.append(digits.substring(0, 2)).append("/");
                    if (digits.length() > 2) {
                        formatted.append(digits.substring(2));
                    }
                } else {
                    formatted.append(digits);
                }

                s.replace(0, s.length(), formatted.toString());
                etExpiry.setSelection(s.length());

                isUpdating = false;
            }
        });
    }

    private String formatRupees(double amount) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return "Rs. " + format.format(amount);
    }
}