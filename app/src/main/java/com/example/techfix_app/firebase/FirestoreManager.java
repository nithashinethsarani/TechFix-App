package com.example.techfix_app.firebase;

import com.example.techfix_app.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class FirestoreManager {

    private final FirebaseFirestore firestore;

    public FirestoreManager() {
        firestore = FirebaseFirestore.getInstance();
    }

    // Save a customer/user profile
    public void saveUser(
            String uid,
            User user,
            OnCompleteListener<Void> listener) {

        firestore
                .collection("users")
                .document(uid)
                .set(user)
                .addOnCompleteListener(listener);
    }

    // Add a document with an automatically generated ID
    public Task<DocumentReference> addDocument(
            String collection,
            Map<String, Object> data) {

        return firestore
                .collection(collection)
                .add(data);
    }

    // Add or replace a document using a specific ID
    public Task<Void> setDocument(
            String collection,
            String documentId,
            Map<String, Object> data) {

        return firestore
                .collection(collection)
                .document(documentId)
                .set(data);
    }

    // Get a single document
    public Task<DocumentSnapshot> getDocument(
            String collection,
            String documentId) {

        return firestore
                .collection(collection)
                .document(documentId)
                .get();
    }

    // Get all documents in a collection
    public Task<QuerySnapshot> getCollection(
            String collection) {

        return firestore
                .collection(collection)
                .get();
    }

    // Update selected fields
    public Task<Void> updateDocument(
            String collection,
            String documentId,
            Map<String, Object> updates) {

        return firestore
                .collection(collection)
                .document(documentId)
                .update(updates);
    }

    // Delete a document
    public Task<Void> deleteDocument(
            String collection,
            String documentId) {

        return firestore
                .collection(collection)
                .document(documentId)
                .delete();
    }
}