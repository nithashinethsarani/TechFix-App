package com.example.techfix_app.firebase;

import com.example.techfix_app.models.User;
import com.example.techfix_app.models.InventoryItem;
import com.example.techfix_app.models.Technician;
import com.example.techfix_app.models.Branch;
import com.example.techfix_app.models.Service;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirestoreManager {

    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;

    public FirestoreManager() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
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

    // Fetch user profile data by UID
    public void getUser(String uid, OnUserLoadedListener listener) {
        firestore
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        listener.onSuccess(user);
                    } else {
                        listener.onFailure(new Exception("User record not found"));
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }



    // Callback interface for user loading
    public interface OnUserLoadedListener {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public interface OnRoleLoadedListener {
        void onSuccess(String role);
        void onFailure(Exception e);
    }
    public void getCurrentUserRole(OnRoleLoadedListener listener) {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            listener.onFailure(new Exception("User not logged in"));
            return;
        }

        String userId = currentUser.getUid();

        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        listener.onSuccess(role != null ? role : "Customer");
                    } else {
                        listener.onSuccess("Customer");
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }


    // BASIC FIRESTORE METHODS

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



    // SERVICES

    // Get all services from Firestore
    public void getAllServices(
            OnServicesLoadedListener listener) {

        firestore
                .collection("services")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<Service> serviceList =
                            new ArrayList<>();

                    for (DocumentSnapshot document :
                            queryDocumentSnapshots.getDocuments()) {

                        Service service =
                                document.toObject(Service.class);

                        if (service != null) {

                            service.setId(
                                    document.getId()
                            );

                            serviceList.add(service);
                        }
                    }

                    listener.onSuccess(serviceList);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public Task<Void> addService(Service service) {

        DocumentReference document =
                firestore.collection("services").document();

        service.setId(document.getId());

        return document.set(service);
    }

    public Task<Void> setService(
            String serviceId,
            Service service) {

        service.setId(serviceId);

        return firestore
                .collection("services")
                .document(serviceId)
                .set(service);
    }


    // Callback interface for services
    public interface OnServicesLoadedListener {

        void onSuccess(List<Service> services);

        void onFailure(Exception e);
    }



    // BRANCHES

    // Get all branches from Firestore
    public void getAllBranches(
            OnBranchesLoadedListener listener) {

        firestore
                .collection("branches")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<Branch> branchList = new ArrayList<>();

                    for (DocumentSnapshot document :
                            queryDocumentSnapshots.getDocuments()) {

                        Branch branch =
                                document.toObject(Branch.class);

                        if (branch != null) {
                            branchList.add(branch);
                        }
                    }

                    listener.onSuccess(branchList);
                })
                .addOnFailureListener(listener::onFailure);
    }


    // Callback interface for branches
    public interface OnBranchesLoadedListener {

        void onSuccess(List<Branch> branches);

        void onFailure(Exception e);
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