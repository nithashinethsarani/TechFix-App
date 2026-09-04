package com.example.techfix_app.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.adapters.InventoryAdapter;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.InventoryItem;

import java.util.ArrayList;
import java.util.List;

public class InventoryManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;

    private List<InventoryItem> itemList = new ArrayList<>();

    private FirestoreManager firestoreManager;

    private View fabAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_management);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewInventory);
        fabAddItem = findViewById(R.id.fabAddInventory);

        // Initialize Firestore
        firestoreManager = new FirestoreManager();

        // RecyclerView setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new InventoryAdapter(
                itemList,
                new InventoryAdapter.OnItemClickListener() {

                    @Override
                    public void onEditClick(InventoryItem item) {

                        Intent intent = new Intent(
                                InventoryManagementActivity.this,
                                AddEditInventoryActivity.class
                        );

                        intent.putExtra(
                                "itemId",
                                item.getItemId()
                        );

                        startActivity(intent);
                    }

                    @Override
                    public void onDeleteClick(InventoryItem item) {

                        deleteInventoryItem(item);
                    }
                }
        );

        recyclerView.setAdapter(adapter);

        // Add inventory item
        fabAddItem.setOnClickListener(v -> {

            Intent intent = new Intent(
                    InventoryManagementActivity.this,
                    AddEditInventoryActivity.class
            );

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reload inventory whenever returning to this screen
        loadInventory();
    }

    /**
     * Load all inventory items from Firestore
     */
    private void loadInventory() {

        firestoreManager.getAllInventory()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    itemList.clear();

                    for (com.google.firebase.firestore.DocumentSnapshot document
                            : queryDocumentSnapshots.getDocuments()) {

                        InventoryItem item =
                                document.toObject(InventoryItem.class);

                        if (item != null) {

                            // Make sure the Firestore document ID
                            // is stored in the model
                            if (item.getItemId() == null ||
                                    item.getItemId().isEmpty()) {

                                item.setItemId(document.getId());
                            }

                            itemList.add(item);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            InventoryManagementActivity.this,
                            "Failed to load inventory: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    //Delete an inventory item from Firestore
    private void deleteInventoryItem(InventoryItem item) {

        String itemId = item.getItemId();

        if (itemId == null || itemId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Invalid inventory item ID",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        firestoreManager.deleteDocument(
                "inventory",
                itemId
        ).addOnSuccessListener(unused -> {

            Toast.makeText(
                    InventoryManagementActivity.this,
                    "Item deleted successfully",
                    Toast.LENGTH_SHORT
            ).show();

            // Remove from current list immediately
            itemList.remove(item);
            adapter.notifyDataSetChanged();

        }).addOnFailureListener(e -> {

            Toast.makeText(
                    InventoryManagementActivity.this,
                    "Delete failed: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        });
    }
}