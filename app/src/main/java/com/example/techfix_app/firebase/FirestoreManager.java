package com.example.techfix_app.firebase;

import com.example.techfix_app.models.Service;
import com.example.techfix_app.models.Branch;
import com.example.techfix_app.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirestoreManager {

    private final FirebaseFirestore db;

    public FirestoreManager() {
        db = FirebaseFirestore.getInstance();
    }

    // Save a customer/user profile
    public void saveUser(
            String uid,
            User user,
            OnCompleteListener<Void> listener) {

        db
                .collection("users")
                .document(uid)
                .set(user)
                .addOnCompleteListener(listener);
    }

    // Add a document with an automatically generated ID
    public Task<DocumentReference> addDocument(
            String collection,
            Map<String, Object> data) {

        return db
                .collection(collection)
                .add(data);
    }

    // Add or replace a document using a specific ID
    public Task<Void> setDocument(
            String collection,
            String documentId,
            Map<String, Object> data) {

        return db
                .collection(collection)
                .document(documentId)
                .set(data);
    }

    // Get a single document
    public Task<DocumentSnapshot> getDocument(
            String collection,
            String documentId) {

        return db
                .collection(collection)
                .document(documentId)
                .get();
    }

    // Get all documents in a collection
    public Task<QuerySnapshot> getCollection(
            String collection) {

        return db
                .collection(collection)
                .get();
    }

    // Update selected fields
    public Task<Void> updateDocument(
            String collection,
            String documentId,
            Map<String, Object> updates) {

        return db
                .collection(collection)
                .document(documentId)
                .update(updates);
    }

    // Delete a document
    public Task<Void> deleteDocument(
            String collection,
            String documentId) {

        return db
                .collection(collection)
                .document(documentId)
                .delete();
    }


    // SERVICES

    // Get all services from Firestore
    public void getAllServices(OnServicesLoadedListener listener) {

        db.collection("services")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<Service> serviceList = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                        String id = document.getId();

                        String serviceName =
                                document.getString("serviceName");

                        String deviceCategory =
                                document.getString("deviceCategory");

                        String description =
                                document.getString("description");

                        Double priceValue =
                                document.getDouble("price");

                        Boolean availableValue =
                                document.getBoolean("available");

                        double price =
                                priceValue != null ? priceValue : 0.0;

                        boolean available =
                                availableValue != null && availableValue;

                        Service service = new Service(
                                id,
                                deviceCategory != null ? deviceCategory : "",
                                serviceName != null ? serviceName : "",
                                price,
                                description != null ? description : "",
                                available
                        );

                        serviceList.add(service);
                    }

                    listener.onSuccess(serviceList);
                })
                .addOnFailureListener(listener::onFailure);
    }


    // Callback interface for service loading
    public interface OnServicesLoadedListener {

        void onSuccess(List<Service> services);

        void onFailure(Exception e);
    }


    // BRANCHES

    //get all branches from firestore
    public void getAllBranches(OnBranchesLoadedListener listener) {

        db.collection("branches")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    List<Branch> branchList = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                        String id = document.getId();

                        String name = document.getString("name");

                        Double latitudeValue = document.getDouble("latitude");
                        Double longitudeValue = document.getDouble("longitude");

                        double latitude =
                                latitudeValue != null ? latitudeValue : 0.0;

                        double longitude =
                                longitudeValue != null ? longitudeValue : 0.0;

                        Branch branch = new Branch(
                                id,
                                name != null ? name : "",
                                latitude,
                                longitude
                        );

                        branchList.add(branch);
                    }

                    listener.onSuccess(branchList);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public interface OnBranchesLoadedListener {

        void onSuccess(List<Branch> branches);

        void onFailure(Exception e);
    }
}