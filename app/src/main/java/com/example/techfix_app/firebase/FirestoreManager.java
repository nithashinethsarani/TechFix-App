package com.example.techfix_app.firebase;

import com.example.techfix_app.models.User;
import com.example.techfix_app.models.InventoryItem;
import com.example.techfix_app.models.Technician;

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

    // USER

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


    // GENERIC FIRESTORE METHODS

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
    // INVENTORY

    // Get all inventory items
    public Task<QuerySnapshot> getAllInventory() {

        return firestore
                .collection("inventory")
                .get();
    }

    // Add a new inventory item
    public Task<DocumentReference> addInventoryItem(
            InventoryItem item) {

        return firestore
                .collection("inventory")
                .add(item);
    }

    // Add or replace inventory item using a specific document ID
    public Task<Void> setInventoryItem(
            String documentId,
            InventoryItem item) {

        return firestore
                .collection("inventory")
                .document(documentId)
                .set(item);
    }

    // Update selected inventory fields
    public Task<Void> updateInventoryItem(
            String documentId,
            Map<String, Object> updates) {

        return firestore
                .collection("inventory")
                .document(documentId)
                .update(updates);
    }

    // Delete an inventory item
    public Task<Void> deleteInventoryItem(
            String documentId) {

        return firestore
                .collection("inventory")
                .document(documentId)
                .delete();
    }

    // TECHNICIANS

    // Get all technicians
    public Task<QuerySnapshot> getAllTechnicians() {

        return firestore
                .collection("technicians")
                .get();
    }

    // Add a new technician
    public Task<DocumentReference> addTechnician(
            Technician technician) {

        return firestore
                .collection("technicians")
                .add(technician);
    }

    // Add or replace technician using a specific document ID
    public Task<Void> setTechnician(
            String documentId,
            Technician technician) {

        return firestore
                .collection("technicians")
                .document(documentId)
                .set(technician);
    }

    // Update selected technician fields
    public Task<Void> updateTechnician(
            String documentId,
            Map<String, Object> updates) {

        return firestore
                .collection("technicians")
                .document(documentId)
                .update(updates);
    }

    // Delete a technician
    public Task<Void> deleteTechnician(
            String documentId) {

        return firestore
                .collection("technicians")
                .document(documentId)
                .delete();
    }
}