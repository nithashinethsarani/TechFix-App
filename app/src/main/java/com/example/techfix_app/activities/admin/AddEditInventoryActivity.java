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
import java.util.List;

public class AddEditInventoryActivity extends AppCompatActivity {

    private EditText etItemName, etQuantity, etPrice;
    private Spinner spinnerBranch;
    private Button btnSave;
    private FirestoreManager firestoreManager;
    private String itemId = null;
    private List<Branch> branchList = new ArrayList<>();

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

        loadBranchesToSpinner();

        if (getIntent().hasExtra("itemId")) {
            itemId = getIntent().getStringExtra("itemId");
            loadItemData();
        }

        btnSave.setOnClickListener(v -> saveItem());
    }

    private void loadBranchesToSpinner() {
        firestoreManager.getAllBranches(list -> {
            branchList.clear();
            branchList.addAll(list);
            List<String> names = new ArrayList<>();
            for (Branch b : branchList) names.add(b.getName());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBranch.setAdapter(adapter);
        });
    }

    private void loadItemData() {
        firestoreManager.getInventoryItemById(itemId, item -> {
            etItemName.setText(item.getItemName());
            etQuantity.setText(String.valueOf(item.getQuantity()));
            etPrice.setText(String.valueOf(item.getPrice()));

            for (int i = 0; i < branchList.size(); i++) {
                if (branchList.get(i).getBranchId().equals(item.getBranchId())) {
                    spinnerBranch.setSelection(i);
                    break;
                }
            }
        });
    }

    private void saveItem() {
        String name = etItemName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (branchList.isEmpty()) {
            Toast.makeText(this, "No branches available. Add a branch first.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(qtyStr);
        double price = Double.parseDouble(priceStr);
        Branch selectedBranch = branchList.get(spinnerBranch.getSelectedItemPosition());

        InventoryItem item = new InventoryItem();
        item.setItemName(name);
        item.setQuantity(quantity);
        item.setPrice(price);
        item.setBranchId(selectedBranch.getBranchId());

        if (itemId == null) {
            firestoreManager.addInventoryItem(item, success -> {
                if (success) {
                    Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            item.setItemId(itemId);
            firestoreManager.updateInventoryItem(item, success -> {
                if (success) {
                    Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update item", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}