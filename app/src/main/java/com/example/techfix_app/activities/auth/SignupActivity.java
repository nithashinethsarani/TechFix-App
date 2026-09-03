package com.example.techfix_app.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.activities.MainActivity;
import com.example.techfix_app.firebase.FirebaseAuthManager;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.User;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnSignup;

    private TextView tvLogin;

    private FirebaseAuthManager authManager;
    private FirestoreManager firestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase managers
        authManager = new FirebaseAuthManager();
        firestoreManager = new FirestoreManager();

        // Connect Java variables to XML views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);

        // Signup button
        btnSignup.setOnClickListener(v -> handleSignup());

        // go to Login
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(
                    SignupActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);
        });
    }

    private void handleSignup() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = "user";

        // Basic validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Password validation
        if (password.length() < 6) {
            Toast.makeText(
                    this,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Create Firebase Authentication account
        authManager.registerUser(email, password, task -> {

            if (task.isSuccessful()) {

                // Get the newly created Firebase user
                String uid = authManager.getCurrentUserId();

                if (uid == null) {
                    Toast.makeText(
                            SignupActivity.this,
                            "Unable to get user information",
                            Toast.LENGTH_LONG
                    ).show();

                    return;
                }

                // Create our User model
                User user = new User(uid, name, email,role);

                // Save additional customer information to Firestore
                firestoreManager.saveUser(
                        uid,
                        user,
                        firestoreTask -> {

                            if (firestoreTask.isSuccessful()) {

                                Toast.makeText(
                                        SignupActivity.this,
                                        "Account Created Successfully!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intent = new Intent(
                                        SignupActivity.this,
                                        MainActivity.class
                                );

                                startActivity(intent);
                                finish();

                            } else {

                                Toast.makeText(
                                        SignupActivity.this,
                                        "Account created, but profile could not be saved.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );

            } else {

                String errorMessage = "Signup failed";

                if (task.getException() != null) {
                    errorMessage = task.getException().getMessage();
                }

                Toast.makeText(
                        SignupActivity.this,
                        errorMessage,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}