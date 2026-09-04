package com.example.techfix_app.activities.services;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class ServicesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewServices;
    private ProgressBar progressBar;
    private TextView tvNoServices;

    private FirestoreManager firestoreManager;
    private ServiceAdapter adapter;
    private String userRole = "user";

    private final List<Service> serviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        recyclerViewServices = findViewById(R.id.recyclerViewServices);
        progressBar = findViewById(R.id.progressBar);
        tvNoServices = findViewById(R.id.tvNoServices);

        firestoreManager = new FirestoreManager();

        recyclerViewServices.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ServiceAdapter(
                serviceList,
                new ServiceAdapter.OnServiceClickListener() {
                    @Override
                    public void onServiceClick(Service service) {
                        Intent intent = new Intent(
                                ServicesActivity.this,
                                ServiceDetailsActivity.class
                        );
                        intent.putExtra("serviceId", service.getId());
                        startActivity(intent);
                    }

                    @Override
                    public void onEditClick(Service service) {}

                    @Override
                    public void onDeleteClick(Service service) {}
                }
        );


        // Attached adapter and closed onCreate method
        recyclerViewServices.setAdapter(adapter);
        fetchUserRole();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadServices();
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    private void fetchUserRole() {
        firestoreManager.getCurrentUserRole(new FirestoreManager.OnRoleLoadedListener() {
            @Override
            public void onSuccess(String userRole) {
                adapter.setUserRole(userRole);
            }

            @Override
            public void onFailure(Exception e) {
                adapter.setUserRole("user");
            }
        });
    }

    private void loadServices() {

        progressBar.setVisibility(View.VISIBLE);
        tvNoServices.setVisibility(View.GONE);

        firestoreManager.getAllServices(
                new FirestoreManager.OnServicesLoadedListener() {

                    @Override
                    public void onSuccess(List<Service> services) {

                        progressBar.setVisibility(View.GONE);

                        serviceList.clear();
                        serviceList.addAll(services);

                        adapter.notifyDataSetChanged();

                        if (serviceList.isEmpty()) {
                            tvNoServices.setVisibility(View.VISIBLE);
                        } else {
                            tvNoServices.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(
                                ServicesActivity.this,
                                "Failed to load services: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
}