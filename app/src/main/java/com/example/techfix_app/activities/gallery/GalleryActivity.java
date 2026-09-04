package com.example.techfix_app.activities.gallery;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.adapters.GalleryAdapter;
import com.example.techfix_app.database.RepairImageDatabaseHelper;
import com.example.techfix_app.models.RepairImage;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private List<RepairImage> imageList = new ArrayList<>();
    private List<RepairImage> fullList = new ArrayList<>();
    private Spinner spinnerFilterCategory;
    private RepairImageDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerView = findViewById(R.id.recyclerViewGallery);
        spinnerFilterCategory = findViewById(R.id.spinnerFilterCategory);
        dbHelper = new RepairImageDatabaseHelper(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new GalleryAdapter(imageList);
        recyclerView.setAdapter(adapter);

        setupFilterSpinner();
        loadImages();
    }

    private void setupFilterSpinner() {
        String[] categories = {"All", "Laptop", "Mobile Phone", "Desktop", "Tablet", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterCategory.setAdapter(adapter);

        spinnerFilterCategory.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selected = categories[position];
                filterImages(selected);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });
    }

    private void loadImages() {
        List<RepairImage> list = dbHelper.getAllImages();
        fullList.clear();
        fullList.addAll(list);
        imageList.clear();
        imageList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    private void filterImages(String category) {
        imageList.clear();
        if ("All".equals(category)) {
            imageList.addAll(fullList);
        } else {
            for (RepairImage img : fullList) {
                if (img.getDeviceCategory() != null && img.getDeviceCategory().equalsIgnoreCase(category)) {
                    imageList.add(img);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}