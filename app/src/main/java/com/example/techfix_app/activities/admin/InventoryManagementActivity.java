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
import com.example.techfix_app.database.InventoryDAO;
import com.example.techfix_app.models.InventoryItem;

import java.util.ArrayList;
import java.util.List;

public class InventoryManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<InventoryItem> itemList = new ArrayList<>();
    private InventoryDAO inventoryDAO;
    private View fabAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_management);

        recyclerView = findViewById(R.id.recyclerViewInventory);
        fabAddItem = findViewById(R.id.fabAddInventory);
        inventoryDAO = new InventoryDAO(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventoryAdapter(itemList, new InventoryAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(InventoryItem item) {
                Intent intent = new Intent(InventoryManagementActivity.this, AddEditInventoryActivity.class);
                intent.putExtra("itemId", item.getItemId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(InventoryItem item) {
                boolean success = inventoryDAO.deleteItem(item.getItemId());
                if (success) {
                    Toast.makeText(InventoryManagementActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                    loadInventory();
                } else {
                    Toast.makeText(InventoryManagementActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setAdapter(adapter);

        fabAddItem.setOnClickListener(v ->
                startActivity(new Intent(InventoryManagementActivity.this, AddEditInventoryActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInventory();
    }

    private void loadInventory() {
        List<InventoryItem> list = inventoryDAO.getAllItems();
        itemList.clear();
        itemList.addAll(list);
        adapter.notifyDataSetChanged();
    }
}