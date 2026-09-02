package com.example.techfix_app.activities.services;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;
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

    private FirestoreManager firestoreManager;
    private RecyclerView recyclerServices;
    private ServiceAdapter adapter;
    private List<Service> fullServiceList;   // master list (all services)
    private List<Service> displayedList;     // currently filtered/shown list

    private String currentCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        recyclerServices = findViewById(R.id.recyclerServices);
        SearchView searchView = findViewById(R.id.searchView);
        Button btnAll = findViewById(R.id.btnAll);
        Button btnComputer = findViewById(R.id.btnComputer);
        Button btnMobile = findViewById(R.id.btnMobile);

        // FireStore database call
        fullServiceList = new ArrayList<>();
        displayedList = new ArrayList<>();

        firestoreManager = new FirestoreManager();

        loadServicesFromFirestore();

        recyclerServices.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ServiceAdapter(displayedList, service -> {
            Intent intent = new Intent(ServicesActivity.this, ServiceDetailsActivity.class);
            intent.putExtra("service_id", service.getId());
            intent.putExtra("service_name", service.getServiceName());
            intent.putExtra("device_category", service.getDeviceCategory());
            intent.putExtra("price", service.getPrice());
            intent.putExtra("description", service.getDescription());
            intent.putExtra("available", service.isAvailable());
            startActivity(intent);
        });
        recyclerServices.setAdapter(adapter);

        // Category filter buttons
        btnAll.setOnClickListener(v -> {
            currentCategory = "All";
            applyFilters(searchView.getQuery().toString());
        });
        btnComputer.setOnClickListener(v -> {
            currentCategory = "Computer";
            applyFilters(searchView.getQuery().toString());
        });
        btnMobile.setOnClickListener(v -> {
            currentCategory = "Mobile";
            applyFilters(searchView.getQuery().toString());
        });

        // Search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters(newText);
                return true;
            }
        });
    }

    // Filters fullServiceList by category + search text, updates adapter
    private void applyFilters(String searchText) {
        List<Service> filtered = new ArrayList<>();
        for (Service s : fullServiceList) {
            boolean matchesCategory = currentCategory.equals("All")
                    || s.getDeviceCategory().equalsIgnoreCase(currentCategory);
            boolean matchesSearch = searchText == null || searchText.isEmpty()
                    || s.getServiceName().toLowerCase().contains(searchText.toLowerCase());

            if (matchesCategory && matchesSearch) {
                filtered.add(s);
            }
        }
        displayedList = filtered;
        adapter.updateList(displayedList);
    }

    // fucntion to load services from firestore services collection
    private void loadServicesFromFirestore() {

        firestoreManager.getAllServices(
                new FirestoreManager.OnServicesLoadedListener() {

                    @Override
                    public void onSuccess(List<Service> services) {

                        fullServiceList = services;
                        displayedList = new ArrayList<>(services);

                        adapter.updateList(displayedList);
                    }

                    @Override
                    public void onFailure(Exception e) {

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