package com.example.techfix_app.activities.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.InventoryItem;
import com.example.techfix_app.models.Service;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddEditServiceActivity extends AppCompatActivity {

    private EditText etServiceName;
    private EditText etServiceDescription;
    private EditText etServicePrice;

    private Spinner spinnerServiceCategory;

    private LinearLayout layoutInventoryItems;

    private Button btnSaveService;

    private FirestoreManager firestoreManager;

    private String serviceId;
    private boolean isEditMode = false;

    private final List<InventoryItem> inventoryList =
            new ArrayList<>();

    private final List<String> selectedInventoryIds =
            new ArrayList<>();

    private final List<CheckBox> inventoryCheckBoxes =
            new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_edit_service
        );

        etServiceName =
                findViewById(R.id.etServiceName);

        etServiceDescription =
                findViewById(R.id.etServiceDescription);

        etServicePrice =
                findViewById(R.id.etServicePrice);

        spinnerServiceCategory =
                findViewById(R.id.spinnerServiceCategory);

        layoutInventoryItems =
                findViewById(R.id.layoutInventoryItems);

        btnSaveService =
                findViewById(R.id.btnSaveService);

        firestoreManager =
                new FirestoreManager();

        setupCategorySpinner();

        serviceId =
                getIntent().getStringExtra("serviceId");

        if (serviceId != null && !serviceId.isEmpty()) {

            isEditMode = true;

            loadService();
        }

        loadInventory();

        btnSaveService.setOnClickListener(
                v -> saveService()
        );
    }


    private void setupCategorySpinner() {

        String[] categories = {
                "Mobile",
                "Laptop",
                "Desktop",
                "Tablet",
                "Other"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categories
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerServiceCategory.setAdapter(adapter);
    }


    private void loadService() {

        firestoreManager
                .getDocument(
                        "services",
                        serviceId
                )
                .addOnSuccessListener(
                        documentSnapshot -> {

                            if (!documentSnapshot.exists()) {

                                Toast.makeText(
                                        this,
                                        "Service not found",
                                        Toast.LENGTH_SHORT
                                ).show();

                                finish();
                                return;
                            }

                            Service service =
                                    documentSnapshot.toObject(
                                            Service.class
                                    );

                            if (service == null) {

                                Toast.makeText(
                                        this,
                                        "Unable to load service",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            etServiceName.setText(
                                    service.getName()
                            );

                            etServiceDescription.setText(
                                    service.getDescription()
                            );

                            etServicePrice.setText(
                                    String.valueOf(
                                            service.getPrice()
                                    )
                            );

                            setCategory(
                                    service.getDeviceCategory()
                            );

                            selectedInventoryIds.clear();

                            if (service.getInventoryItemIds()
                                    != null) {

                                selectedInventoryIds.addAll(
                                        service.getInventoryItemIds()
                                );
                            }

                            displayInventoryItems();
                        }
                )
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                "Failed to load service: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }


    private void loadInventory() {

        firestoreManager
                .getAllInventory()
                .addOnSuccessListener(
                        querySnapshot -> {

                            inventoryList.clear();

                            for (
                                    DocumentSnapshot document :
                                    querySnapshot.getDocuments()
                            ) {

                                InventoryItem item =
                                        document.toObject(
                                                InventoryItem.class
                                        );

                                if (item != null) {

                                    if (item.getItemId() == null
                                            || item.getItemId().isEmpty()) {

                                        item.setItemId(
                                                document.getId()
                                        );
                                    }

                                    inventoryList.add(item);
                                }
                            }

                            displayInventoryItems();
                        }
                )
                .addOnFailureListener(
                        e -> Toast.makeText(
                                this,
                                "Failed to load inventory: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }


    private void displayInventoryItems() {

        if (layoutInventoryItems == null) {
            return;
        }

        layoutInventoryItems.removeAllViews();

        inventoryCheckBoxes.clear();

        for (InventoryItem item : inventoryList) {

            CheckBox checkBox =
                    new CheckBox(this);

            String text =
                    item.getItemName()
                            + "  |  Stock: "
                            + item.getQuantity();

            checkBox.setText(text);

            checkBox.setTextSize(16);

            checkBox.setPadding(
                    8,
                    8,
                    8,
                    8
            );

            if (selectedInventoryIds.contains(
                    item.getItemId()
            )) {

                checkBox.setChecked(true);
            }

            layoutInventoryItems.addView(
                    checkBox
            );

            inventoryCheckBoxes.add(
                    checkBox
            );
        }
    }


    private void setCategory(String category) {

        if (category == null) {
            return;
        }

        for (int i = 0;
             i < spinnerServiceCategory
                     .getCount();
             i++) {

            String value =
                    spinnerServiceCategory
                            .getItemAtPosition(i)
                            .toString();

            if (value.equalsIgnoreCase(category)) {

                spinnerServiceCategory
                        .setSelection(i);

                break;
            }
        }
    }


    private List<String> getSelectedInventoryIds() {

        List<String> selectedIds =
                new ArrayList<>();

        for (int i = 0;
             i < inventoryCheckBoxes.size();
             i++) {

            CheckBox checkBox =
                    inventoryCheckBoxes.get(i);

            if (checkBox.isChecked()) {

                InventoryItem item =
                        inventoryList.get(i);

                selectedIds.add(
                        item.getItemId()
                );
            }
        }

        return selectedIds;
    }


    private void saveService() {

        String name =
                etServiceName.getText()
                        .toString()
                        .trim();

        String description =
                etServiceDescription.getText()
                        .toString()
                        .trim();

        String priceText =
                etServicePrice.getText()
                        .toString()
                        .trim();

        String category =
                spinnerServiceCategory
                        .getSelectedItem()
                        .toString();


        if (name.isEmpty()) {

            etServiceName.setError(
                    "Enter service name"
            );

            etServiceName.requestFocus();

            return;
        }


        if (priceText.isEmpty()) {

            etServicePrice.setError(
                    "Enter service price"
            );

            etServicePrice.requestFocus();

            return;
        }


        double price;

        try {

            price =
                    Double.parseDouble(priceText);

        } catch (NumberFormatException e) {

            etServicePrice.setError(
                    "Enter a valid price"
            );

            etServicePrice.requestFocus();

            return;
        }


        if (price < 0) {

            etServicePrice.setError(
                    "Price cannot be negative"
            );

            etServicePrice.requestFocus();

            return;
        }


        List<String> selectedIds =
                getSelectedInventoryIds();


        Service service =
                new Service();

        service.setName(name);

        service.setDescription(
                description
        );

        service.setDeviceCategory(
                category
        );

        service.setPrice(
                price
        );

        service.setInventoryItemIds(
                selectedIds
        );


        btnSaveService.setEnabled(false);


        if (isEditMode) {

            firestoreManager
                    .setService(
                            serviceId,
                            service
                    )
                    .addOnSuccessListener(
                            unused -> {

                                Toast.makeText(
                                        this,
                                        "Service updated successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                finish();
                            }
                    )
                    .addOnFailureListener(
                            e -> {

                                btnSaveService
                                        .setEnabled(true);

                                Toast.makeText(
                                        this,
                                        "Failed to update service: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                    );

        } else {

            firestoreManager
                    .addService(service)
                    .addOnSuccessListener(
                            unused -> {

                                Toast.makeText(
                                        this,
                                        "Service added successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                finish();
                            }
                    )
                    .addOnFailureListener(
                            e -> {

                                btnSaveService
                                        .setEnabled(true);

                                Toast.makeText(
                                        this,
                                        "Failed to add service: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                    );
        }
    }
}