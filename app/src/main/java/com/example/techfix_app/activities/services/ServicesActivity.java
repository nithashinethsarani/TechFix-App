package com.example.techfix_app.activities.services;

import android.os.Bundle;
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

    private RecyclerView recyclerView;
    private SearchView searchView;

    private ServiceAdapter adapter;
    private List<Service> serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        // Connect Java variables to XML
        recyclerView = findViewById(R.id.recyclerViewServices);
        searchView = findViewById(R.id.searchView);

        // Setup RecyclerView
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Load temporary service data
        serviceList = getDummyServices();

        // Create adapter
        adapter = new ServiceAdapter(serviceList);

        // Attach adapter to RecyclerView
        recyclerView.setAdapter(adapter);

        // Setup search
        setupSearch();
    }

    private void setupSearch() {

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        filterServices(newText);

                        return true;
                    }
                }
        );
    }

    private void filterServices(String text) {

        List<Service> filteredList = new ArrayList<>();

        String searchText = text.toLowerCase().trim();

        for (Service service : serviceList) {

            String serviceName =
                    service.getServiceName().toLowerCase();

            String category =
                    service.getDeviceCategory().toLowerCase();

            if (serviceName.contains(searchText)
                    || category.contains(searchText)) {

                filteredList.add(service);
            }
        }

        adapter.filterList(filteredList);
    }

    // Temporary data for testing the UI
    private List<Service> getDummyServices() {

        List<Service> list = new ArrayList<>();

        list.add(new Service(
                "1",
                "Mobile Phone",
                "Screen Replacement",
                5000.00
        ));

        list.add(new Service(
                "2",
                "Mobile Phone",
                "Battery Replacement",
                3000.00
        ));

        list.add(new Service(
                "3",
                "Laptop",
                "Screen Replacement",
                12000.00
        ));

        list.add(new Service(
                "4",
                "Laptop",
                "Keyboard Replacement",
                6000.00
        ));

        list.add(new Service(
                "5",
                "Laptop",
                "RAM Upgrade",
                8000.00
        ));

        list.add(new Service(
                "6",
                "Mobile Phone",
                "Charging Port Repair",
                2500.00
        ));

        return list;
    }
}