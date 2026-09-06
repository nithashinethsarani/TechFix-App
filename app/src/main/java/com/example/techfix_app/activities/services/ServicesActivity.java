package com.example.techfix_app.activities.services;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
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

    private final List<Service> fullServiceList = new ArrayList<>();
    private final List<Service> displayedList = new ArrayList<>();

    private String currentCategory = "All";
    private String currentSearchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        recyclerViewServices = findViewById(R.id.recyclerViewServices);
        progressBar = findViewById(R.id.progressBar);
        tvNoServices = findViewById(R.id.tvNoServices);
        SearchView searchView = findViewById(R.id.searchView);
        Button btnAll = findViewById(R.id.btnAll);
        Button btnComputer = findViewById(R.id.btnComputer);
        Button btnMobile = findViewById(R.id.btnMobile);

        firestoreManager = new FirestoreManager();

        recyclerViewServices.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ServiceAdapter(
                displayedList,
                new ServiceAdapter.OnServiceClickListener() {
                    @Override
                    public void onServiceClick(Service service) {
                        Intent intent = new Intent(
                                ServicesActivity.this,
                                ServiceDetailsActivity.class
                        );

                        // Duplicate extra removal
                        intent.putExtra("service_id", service.getId());
                        intent.putExtra("service_name", service.getName());
                        intent.putExtra("device_category", service.getDeviceCategory());
                        intent.putExtra("price", service.getPrice());
                        intent.putExtra("description", service.getDescription());
                        intent.putExtra("available", service.getIsAvailable());
                        startActivity(intent);
                    }

                    @Override
                    public void onEditClick(Service service) {
                        // Admin edit functionality goes here
                    }

                    @Override
                    public void onDeleteClick(Service service) {
                        // Admin delete functionality goes here
                    }
                }
        );

        recyclerViewServices.setAdapter(adapter);
        fetchUserRole();

        // Category filter buttons
        btnAll.setOnClickListener(v -> {
            currentCategory = "All";
            applyFilters();
        });
        btnComputer.setOnClickListener(v -> {
            currentCategory = "Computer";
            applyFilters();
        });
        btnMobile.setOnClickListener(v -> {
            currentCategory = "Mobile";
            applyFilters();
        });

        // Search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchText = query;
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchText = newText;
                applyFilters();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadServices();
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
                        fullServiceList.clear();
                        fullServiceList.addAll(services);
                        applyFilters();
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

    // Filters fullServiceList by category + search text, updates adapter
    private void applyFilters() {
        List<Service> filtered = new ArrayList<>();

        for (Service s : fullServiceList) {
            boolean matchesCategory = currentCategory.equals("All")
                    || (s.getDeviceCategory() != null && s.getDeviceCategory().equalsIgnoreCase(currentCategory));
            boolean matchesSearch = currentSearchText.isEmpty()
                    || (s.getName() != null && s.getName().toLowerCase().contains(currentSearchText.toLowerCase()));

            if (matchesCategory && matchesSearch) {
                filtered.add(s);
            }
        }

        displayedList.clear();
        displayedList.addAll(filtered);
        adapter.notifyDataSetChanged();

        tvNoServices.setVisibility(displayedList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}