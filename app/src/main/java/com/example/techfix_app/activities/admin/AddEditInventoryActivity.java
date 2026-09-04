package com.example.techfix_app.activities.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.Branch;
import com.example.techfix_app.models.InventoryItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEditInventoryActivity extends AppCompatActivity {

    private EditText etItemName;
    private EditText etQuantity;
    private EditText etPrice;
    private Spinner spinnerBranch;
    private Button btnSave;

    private FirestoreManager firestoreManager;

    private String itemId = null;

    private final List<Branch> branchList = new ArrayList<>();

    private ArrayAdapter<String> branchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_inventory);

        etItemName = findViewById(R.id.etItemName);
        etQuantity = findViewById(R.id.etQuantity);
        etPrice = findViewById(R.id.etPrice);
        spinnerBranch = findViewById(R.id.spinnerInventoryBranch);
        btnSave = findViewById(R.id.btnSaveInventory);

        firestoreManager = new FirestoreManager();

        if (getIntent().hasExtra("itemId")) {

            itemId = getIntent().getStringExtra("itemId");

            if (itemId != null && !itemId.isEmpty()) {
                loadItemData();
            }
        }
        loadBranchesToSpinner();

        btnSave.setOnClickListener(v -> saveItem());
    }


    //Load all branches from Firestore

    private void loadBranchesToSpinner() {

        firestoreManager.getAllBranches(
                new FirestoreManager.OnBranchesLoadedListener() {

                    @Override
                    public void onSuccess(List<Branch> branches) {

                        branchList.clear();
                        branchList.addAll(branches);

                        List<String> branchNames = new ArrayList<>();

                        for (Branch branch : branchList) {
                            branchNames.add(branch.getName());
                        }

                        branchAdapter = new ArrayAdapter<>(
                                AddEditInventoryActivity.this,
                                android.R.layout.simple_spinner_item,
                                branchNames
                        );

                        branchAdapter.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item
                        );

                        spinnerBranch.setAdapter(branchAdapter);

                        // If editing, select the item's branch
                        if (itemId != null) {
                            selectItemBranch();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                        Toast.makeText(
                                AddEditInventoryActivity.this,
                                "Failed to load branches: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


     // Load a inventory item from Firestore.

    private void loadItemData() {

        if (itemId == null || itemId.isEmpty()) {
            return;
        }

        firestoreManager.getDocument("inventory", itemId)
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        InventoryItem item =
                                documentSnapshot.toObject(InventoryItem.class);

                        if (item != null) {

                            item.setItemId(documentSnapshot.getId());

                            etItemName.setText(item.getItemName());
                            etQuantity.setText(
                                    String.valueOf(item.getQuantity())
                            );
                            etPrice.setText(
                                    String.valueOf(item.getPrice())
                            );

                            selectedItemBranchId = item.getBranchId();
                        }

                    } else {

                        Toast.makeText(
                                this,
                                "Inventory item not found",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load item: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private String selectedItemBranchId = null;


     //Select the branch belonging to the inventory item when editing.
    private void selectItemBranch() {

        if (selectedItemBranchId == null) {
            return;
        }

        for (int i = 0; i < branchList.size(); i++) {

            String branchId = branchList.get(i).getBranchId();

            if (selectedItemBranchId.equals(branchId)) {

                spinnerBranch.setSelection(i);
                break;
            }
        }
    }


    //Validate and save the inventory item.
    private void saveItem() {

        String name = etItemName.getText()
                .toString()
                .trim();

        String quantityText = etQuantity.getText()
                .toString()
                .trim();

        String priceText = etPrice.getText()
                .toString()
                .trim();

        // Basic validation
        if (name.isEmpty()
                || quantityText.isEmpty()
                || priceText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Make sure branches loaded
        if (branchList.isEmpty()) {

            Toast.makeText(
                    this,
                    "No branches available. Add a branch first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int quantity;
        double price;

        try {

            quantity = Integer.parseInt(quantityText);
            price = Double.parseDouble(priceText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid quantity and price",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Validate quantity
        if (quantity < 0) {

            Toast.makeText(
                    this,
                    "Quantity cannot be negative",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Validate price
        if (price < 0) {

            Toast.makeText(
                    this,
                    "Price cannot be negative",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int selectedPosition =
                spinnerBranch.getSelectedItemPosition();

        if (selectedPosition < 0
                || selectedPosition >= branchList.size()) {

            Toast.makeText(
                    this,
                    "Please select a branch",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Branch selectedBranch =
                branchList.get(selectedPosition);

        // Create inventory object
        InventoryItem item = new InventoryItem();

        item.setItemId(itemId);
        item.setItemName(name);
        item.setQuantity(quantity);
        item.setPrice(price);
        item.setBranchId(selectedBranch.getBranchId());

        // Add or update
        if (itemId == null || itemId.isEmpty()) {

            addInventoryItem(item);

        } else {

            updateInventoryItem(item);
        }
    }


    //Add a new item to Firestore.
    private void addInventoryItem(InventoryItem item) {

        firestoreManager.addInventoryItem(item)
                .addOnSuccessListener(documentReference -> {

                    String generatedItemId =
                            documentReference.getId();

                    // Store Firestore document ID inside itemId
                    Map<String, Object> updates =
                            new HashMap<>();

                    updates.put("itemId", generatedItemId);

                    firestoreManager.updateInventoryItem(
                            generatedItemId,
                            updates
                    ).addOnSuccessListener(unused -> {

                        Toast.makeText(
                                this,
                                "Inventory item added successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();

                    }).addOnFailureListener(e -> {

                        Toast.makeText(
                                this,
                                "Item added, but ID could not be saved: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to add inventory item: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


     //Update an inventory item.
    private void updateInventoryItem(InventoryItem item) {

        if (itemId == null || itemId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Invalid inventory item ID",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        firestoreManager.setInventoryItem(
                itemId,
                item
        ).addOnSuccessListener(unused -> {

            Toast.makeText(
                    this,
                    "Inventory item updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        }).addOnFailureListener(e -> {

            Toast.makeText(
                    this,
                    "Failed to update inventory item: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        });
    }
}