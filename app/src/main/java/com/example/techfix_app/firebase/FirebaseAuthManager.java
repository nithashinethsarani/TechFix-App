package com.example.techfix_app.firebase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthManager {

    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthManager() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // Register a new user
    public void registerUser(
            String email,
            String password,
            OnCompleteListener<AuthResult> listener) {

        firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    // Login an existing user
    public void loginUser(
            String email,
            String password,
            OnCompleteListener<AuthResult> listener) {

        firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    // Logout current user
    public void logoutUser() {
        firebaseAuth.signOut();
    }

    // Get currently logged-in user
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    // Check whether a user is logged in
    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    // Get current Firebase user's UID
    public String getCurrentUserId() {

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            return user.getUid();
        }

        return null;
    }
}