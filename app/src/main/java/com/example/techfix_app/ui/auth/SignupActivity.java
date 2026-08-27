package com.example.techfix_app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.MainActivity;
import com.example.techfix_app.R;
import com.example.techfix_app.data.firebase.FirebaseRepository;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Button btnSignup;
    private FirebaseRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        repository = new FirebaseRepository();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(v -> handleSignup());
    }

    private void handleSignup() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        repository.registerUser(name, email, password, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignupActivity.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignupActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}