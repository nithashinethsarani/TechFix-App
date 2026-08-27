package com.example.techfix_app.data.firebase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.techfix_app.data.model.User;

public class FirebaseRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void registerUser(String name, String email, String password, OnCompleteListener<AuthResult> onComplete) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && auth.getCurrentUser() != null) {
                        String uid = auth.getCurrentUser().getUid();
                        User user = new User(uid, name, email);

                        // Save additional user info in Firestore
                        db.collection("users").document(uid).set(user);
                    }
                    onComplete.onComplete(task);
                });
    }
}
