package com.example.techfix_app.firebase;

import com.example.techfix_app.models.Appointment;
import com.example.techfix_app.models.User;
import com.example.techfix_app.models.InventoryItem;
import com.example.techfix_app.models.Technician;
import com.example.techfix_app.models.Branch;
import com.example.techfix_app.models.Service;
import com.example.techfix_app.models.Repair;

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

    public interface OnUserNameLoadedListener {
        void onLoaded(String userName);
        void onError(Exception e);
    }
    public void getAppointmentUserName(String appointmentId, OnUserNameLoadedListener listener) {
        firestore.collection("appointments").document(appointmentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("customerName");
                        listener.onLoaded(userName != null ? userName : "Unknown User");
                    } else {
                        listener.onLoaded("Customer Name not found");
                    }
                })
                .addOnFailureListener(e -> listener.onError(e));
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


    public Task<DocumentSnapshot> getService(String serviceId) {
        return firestore.collection("services")
                .document(serviceId)
                .get();
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

    public Task<DocumentSnapshot> getInventoryItem(String itemDocumentId) {
        return firestore.collection("inventory")
                .document(itemDocumentId)
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

    public Task<DocumentSnapshot> getTechnicianById(String technicianId) {
        return firestore.collection("technicians").document(technicianId).get();
    }

    public Task<QuerySnapshot> getAvailableTechniciansByBranch(
            String branchId) {

        return firestore.collection("technicians")
                .whereEqualTo("branchId", branchId)
                .whereEqualTo("isAvailable", true)
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



    //APPOINTMENTS
    // Get all appointments
    public Task<QuerySnapshot> getAllAppointments() {

        return firestore
                .collection("appointments")
                .get();
    }

    //get appointment
    public Task<DocumentSnapshot> getAppointment(
            String appointmentId) {

        return firestore.collection("appointments")
                .document(appointmentId)
                .get();
    }

    //get a customer's appointments
    public Task<QuerySnapshot> getCustomerAppointments(
            String customerId) {

        return firestore.collection("appointments")
                .whereEqualTo("customerId", customerId)
                .get();
    }



    // Add a new appointment
    public Task<DocumentReference> addAppointment(
            Appointment appointment) {

        return firestore
                .collection("appointments")
                .add(appointment);
    }


    // Add or replace appointment using a specific document ID
    public Task<Void> setAppontment(
            String documentId,
            Appointment appointment) {

        return firestore
                .collection("appointments")
                .document(documentId)
                .set(appointment);
    }


    // Update selected appointment fields
    public Task<Void> updateAppointment(
            String documentId,
            Map<String, Object> updates) {

        return firestore
                .collection("appointments")
                .document(documentId)
                .update(updates);
    }


    // Delete a appointment
    public Task<Void> deleteAppointment(
            String documentId) {

        return firestore
                .collection("appointments")
                .document(documentId)
                .delete();
    }

    // REPAIRS HISTORY

    public interface OnCompletedRepairsLoadedListener {
        void onSuccess(List<DocumentSnapshot> repairDocuments);
        void onFailure(Exception e);
    }

    // Fetch completed or ready repairs for the customer
    public void getCompletedRepairsForCustomer(String customerId, OnCompletedRepairsLoadedListener listener) {
        firestore.collection("repairs")
                .whereEqualTo("customerId", customerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> completedRepairs = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String status = doc.getString("status");
                        if ("Completed".equalsIgnoreCase(status) || "Ready for Collection".equalsIgnoreCase(status)) {
                            completedRepairs.add(doc);
                        }
                    }
                    listener.onSuccess(completedRepairs);
                })
                .addOnFailureListener(listener::onFailure);
    }

    // REPAIRS
    public Task<DocumentReference> createRepair(Repair repair) {

        DocumentReference document =
                firestore.collection("repairs").document();

        repair.setRepairId(document.getId());

        return document.set(repair)
                .continueWith(task -> document);
    }

    public Task<QuerySnapshot> getAllRepairs() {
        return firestore.collection("repairs").get();
    }

    public Task<QuerySnapshot> getActiveRepairs() {
        return firestore.collection("repairs")
                .whereIn(
                        "status",
                        java.util.Arrays.asList(
                                "received",
                                "diagnosing",
                                "waiting_for_parts",
                                "repairing",
                                "ready_for_collection"
                        )
                )
                .get();
    }

    public Task<DocumentSnapshot> getRepair(String repairId) {
        return firestore.collection("repairs")
                .document(repairId)
                .get();
    }

    public Task<QuerySnapshot> getRepairByAppointmentId(String appointmentId) {
        return firestore.collection("repairs")
                .whereEqualTo("appointmentId", appointmentId)
                .get();
    }

    public Task<Void> updateRepair(
            String repairId,
            Map<String, Object> updates) {

        return firestore.collection("repairs")
                .document(repairId)
                .update(updates);
    }

    public Task<Void> deleteRepair(String repairId) {
        return firestore.collection("repairs")
                .document(repairId)
                .delete();
    }

    public Task<QuerySnapshot> getCustomerRepairs(String customerId) {
        return firestore.collection("repairs")
                .whereEqualTo("customerId", customerId)
                .get();
    }

    public Task<QuerySnapshot> getRepairsByTechnician(
            String technicianId) {

        return firestore.collection("repairs")
                .whereEqualTo("technicianId", technicianId)
                .get();
    }


    public interface OnServiceStockCheckedListener {
        void onResult(boolean available, String message);
    }


    // used for checking service inventory item availability
    public void checkServiceStockAtBranch(
            String serviceId,
            String branchId,
            OnServiceStockCheckedListener listener) {

        if (serviceId == null || serviceId.trim().isEmpty()
                || branchId == null || branchId.trim().isEmpty()) {
            listener.onResult(false, "Please select a service and branch.");
            return;
        }

        getService(serviceId)
                .addOnSuccessListener(serviceDoc -> {
                    if (!serviceDoc.exists()) {
                        listener.onResult(false, "Service not found.");
                        return;
                    }

                    List<String> requiredItemIds =
                            (List<String>) serviceDoc.get("inventoryItemIds");

                    if (requiredItemIds == null || requiredItemIds.isEmpty()) {
                        listener.onResult(
                                false,
                                "This service has no required inventory parts configured."
                        );
                        return;
                    }

                    getAllInventory()
                            .addOnSuccessListener(inventoryDocs -> {
                                List<String> unavailableItems =
                                        new ArrayList<>();

                                for (String requiredId : requiredItemIds) {
                                    boolean foundInBranch = false;

                                    for (DocumentSnapshot inventoryDoc :
                                            inventoryDocs.getDocuments()) {

                                        String inventoryBranchId =
                                                inventoryDoc.getString("branchId");

                                        String inventoryItemId =
                                                inventoryDoc.getString("itemId");

                                        boolean sameBranch =
                                                branchId.equals(inventoryBranchId);

                                        boolean sameItem =
                                                requiredId.equals(inventoryDoc.getId())
                                                        || requiredId.equals(inventoryItemId);

                                        if (sameBranch && sameItem) {
                                            foundInBranch = true;

                                            Long quantity =
                                                    inventoryDoc.getLong("quantity");

                                            if (quantity == null || quantity <= 0) {
                                                String itemName =
                                                        inventoryDoc.getString("itemName");

                                                unavailableItems.add(
                                                        itemName != null
                                                                ? itemName
                                                                : requiredId
                                                );
                                            }

                                            break;
                                        }
                                    }

                                    if (!foundInBranch) {
                                        unavailableItems.add(requiredId);
                                    }
                                }

                                if (unavailableItems.isEmpty()) {
                                    listener.onResult(
                                            true,
                                            "All required parts are available."
                                    );
                                } else {
                                    listener.onResult(
                                            false,
                                            "This service is unavailable at the selected branch. "
                                                    + "Unavailable parts: "
                                                    + android.text.TextUtils.join(
                                                    ", ",
                                                    unavailableItems
                                            )
                                    );
                                }
                            })
                            .addOnFailureListener(e ->
                                    listener.onResult(
                                            false,
                                            "Could not check branch inventory: "
                                                    + e.getMessage()
                                    )
                            );
                })
                .addOnFailureListener(e ->
                        listener.onResult(
                                false,
                                "Could not load service: " + e.getMessage()
                        )
                );
    }

}