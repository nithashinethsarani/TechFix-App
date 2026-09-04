package com.example.techfix_app.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.adapters.ServiceAdapter;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.Service;

import java.util.ArrayList;
import java.util.List;

public class ServiceManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServiceAdapter adapter;

    private final List<Service> serviceList = new ArrayList<>();

    private FirestoreManager firestoreManager;

    private View fabAddService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_management);

        recyclerView = findViewById(R.id.recyclerViewServicesManagement);
        fabAddService = findViewById(R.id.fabAddService);

        firestoreManager = new FirestoreManager();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ServiceAdapter(
                serviceList,
                new ServiceAdapter.OnServiceClickListener() {

                    @Override
                    public void onServiceClick(Service service) {
                        openAddEditService(service.getId());
                    }

                    @Override
                    public void onEditClick(Service service) {
                        openAddEditService(service.getId());
                    }

                    @Override
                    public void onDeleteClick(Service service) {
                        deleteService(service);
                    }
                }
        );

        recyclerView.setAdapter(adapter);

        fabAddService.setOnClickListener(v -> openAddEditService(null));
    }

    private void openAddEditService(String serviceId) {
        Intent intent = new Intent(ServiceManagementActivity.this, AddEditServiceActivity.class);
        if (serviceId != null) {
            intent.putExtra("serviceId", serviceId);
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadServices();
    }

    private void loadServices() {
        firestoreManager.getAllServices(
                new FirestoreManager.OnServicesLoadedListener() {

                    @Override
                    public void onSuccess(List<Service> services) {
                        serviceList.clear();
                        if (services != null) {
                            serviceList.addAll(services);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(
                                ServiceManagementActivity.this,
                                "Failed to load services: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void deleteService(Service service) {
        String serviceId = service.getId();

        if (serviceId == null || serviceId.isEmpty()) {
            Toast.makeText(this, "Invalid service ID", Toast.LENGTH_SHORT).show();
            return;
        }

        firestoreManager.deleteDocument("services", serviceId)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(
                            ServiceManagementActivity.this,
                            "Service deleted successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    serviceList.remove(service);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(
                        ServiceManagementActivity.this,
                        "Delete failed: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
    }
}