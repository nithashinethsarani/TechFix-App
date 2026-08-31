package com.example.techfix_app.activities.services;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.adapters.ServiceAdapter;
import com.example.techfix_app.models.Service;

import java.util.ArrayList;
import java.util.List;

public class ServicesActivity extends AppCompatActivity {

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

        // TODO: Replace loadDummyServices() with Member 1's FirestoreManager call
        // once it's ready, e.g. firestoreManager.getAllServices(callback)
        fullServiceList = loadDummyServices();
        displayedList = new ArrayList<>(fullServiceList);

        recyclerServices.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ServiceAdapter(displayedList, service -> {
            // TODO: open ServiceDetailsActivity and pass service id
            Intent intent = new Intent(ServicesActivity.this, ServiceDetailsActivity.class);
            intent.putExtra("service_id", service.getId());
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

    // Temporary dummy data - remove once Firestore data is connected
    private List<Service> loadDummyServices() {
        List<Service> list = new ArrayList<>();
        list.add(new Service("svc_001", "Computer", "Software Installation", 1500, "OS reinstall and driver setup", true));
        list.add(new Service("svc_002", "Mobile", "Screen Replacement", 8000, "Original screen replacement", true));
        list.add(new Service("svc_003", "Computer", "Hardware Upgrade (RAM)", 3500, "RAM upgrade service", true));
        list.add(new Service("svc_004", "Mobile", "Battery Replacement", 4000, "Battery replacement", false));
        list.add(new Service("svc_005", "Computer", "Virus Removal", 1200, "Full malware scan and cleanup", true));
        return list;
    }
}