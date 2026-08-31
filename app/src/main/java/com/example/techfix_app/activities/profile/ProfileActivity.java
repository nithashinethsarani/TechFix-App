package com.example.techfix_app.activities.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    // XML fields
    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etAddress;

    private Button btnSaveProfile;
    private Button btnLogout;

    private ProgressBar progressBar;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirestoreManager firestoreManager;

    // Currently logged-in user's Firebase UID
    private String currentUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect this Activity with activity_profile.xml
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnLogout = findViewById(R.id.btnLogout);

        progressBar = findViewById(R.id.progressBar);


        firebaseAuth = FirebaseAuth.getInstance();

        firestoreManager = new FirestoreManager();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {

            Toast.makeText(
                    this,
                    "No user is logged in.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // Get Firebase Authentication UID
        currentUid = firebaseUser.getUid();


        // Display email from Firebase Authentication
        if (firebaseUser.getEmail() != null) {

            etEmail.setText(firebaseUser.getEmail());
        }


        // ----------------------------------------------------
        // LOAD PROFILE
        // ----------------------------------------------------

        loadUserProfile();


        // ----------------------------------------------------
        // SAVE BUTTON
        // ----------------------------------------------------

        btnSaveProfile.setOnClickListener(v -> saveProfile());


        // ----------------------------------------------------
        // LOGOUT BUTTON
        // ----------------------------------------------------

        btnLogout.setOnClickListener(v -> logoutUser());
    }


    // ========================================================
    // LOAD PROFILE FROM FIRESTORE
    // ========================================================

    private void loadUserProfile() {

        showLoading(true);


        firestoreManager
                .getDocument("users", currentUid)

                .addOnSuccessListener(documentSnapshot -> {

                    showLoading(false);


                    if (documentSnapshot.exists()) {

                        // Convert Firestore document
                        // into User Java object
                        User user =
                                documentSnapshot.toObject(User.class);


                        if (user != null) {

                            // Display user information
                            etName.setText(user.getName());
                            etEmail.setText(user.getEmail());
                            etPhone.setText(user.getPhone());
                            etAddress.setText(user.getAddress());
                        }

                    } else {

                        Toast.makeText(
                                ProfileActivity.this,
                                "Profile not found. Please enter your details.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })

                .addOnFailureListener(e -> {

                    showLoading(false);

                    Toast.makeText(
                            ProfileActivity.this,
                            "Failed to load profile: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    // ========================================================
    // SAVE PROFILE TO FIRESTORE
    // ========================================================

    private void saveProfile() {

        // Get values from EditTexts
        String name =
                etName.getText().toString().trim();

        String email =
                etEmail.getText().toString().trim();

        String phone =
                etPhone.getText().toString().trim();

        String address =
                etAddress.getText().toString().trim();


        // ----------------------------------------------------
        // VALIDATION
        // ----------------------------------------------------

        if (TextUtils.isEmpty(name)) {

            etName.setError("Name is required");
            etName.requestFocus();

            return;
        }


        if (TextUtils.isEmpty(phone)) {

            etPhone.setError("Phone number is required");
            etPhone.requestFocus();

            return;
        }


        if (TextUtils.isEmpty(address)) {

            etAddress.setError("Address is required");
            etAddress.requestFocus();

            return;
        }


        // ----------------------------------------------------
        // CREATE USER OBJECT
        // ----------------------------------------------------

        User user = new User(
                currentUid,
                name,
                email,
                phone,
                address
        );


        // ----------------------------------------------------
        // CONVERT USER OBJECT TO MAP
        // ----------------------------------------------------

        Map<String, Object> userData =
                new HashMap<>();

        userData.put("userId", user.getUserId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("phone", user.getPhone());
        userData.put("address", user.getAddress());


        // Show loading
        showLoading(true);


        // ----------------------------------------------------
        // SAVE TO FIRESTORE
        // ----------------------------------------------------

        firestoreManager
                .setDocument(
                        "users",
                        currentUid,
                        userData
                )

                .addOnSuccessListener(unused -> {

                    showLoading(false);

                    Toast.makeText(
                            ProfileActivity.this,
                            "Profile saved successfully",
                            Toast.LENGTH_SHORT
                    ).show();
                })

                .addOnFailureListener(e -> {

                    showLoading(false);

                    Toast.makeText(
                            ProfileActivity.this,
                            "Failed to save profile: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    // ========================================================
    // LOGOUT
    // ========================================================

    private void logoutUser() {

        firebaseAuth.signOut();

        Toast.makeText(
                this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }


    // ========================================================
    // LOADING INDICATOR
    // ========================================================

    private void showLoading(boolean loading) {

        if (loading) {

            progressBar.setVisibility(View.VISIBLE);

            btnSaveProfile.setEnabled(false);

        } else {

            progressBar.setVisibility(View.GONE);

            btnSaveProfile.setEnabled(true);
        }
    }
}