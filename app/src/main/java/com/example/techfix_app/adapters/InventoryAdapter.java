package com.example.techfix_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.InventoryItem;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryAdapter
        extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private final List<InventoryItem> itemList;
    private final OnItemClickListener listener;
    private final FirestoreManager firestoreManager;
    private final Map<String, String> branchNameCache = new HashMap<>();

    public interface OnItemClickListener {

        void onEditClick(InventoryItem item);

        void onDeleteClick(InventoryItem item);
    }

    public InventoryAdapter(
            List<InventoryItem> itemList,
            OnItemClickListener listener
    ) {
        this.itemList = itemList;
        this.listener = listener;
        this.firestoreManager = new FirestoreManager();
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_inventory,
                        parent,
                        false
                );

        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull InventoryViewHolder holder,
            int position
    ) {

        InventoryItem item = itemList.get(position);

        // Item name
        holder.tvName.setText(
                item.getItemName()
        );

        // Branch name
        loadBranchName(item.getBranchId(), holder.tvBranchName);

        // Quantity
        holder.tvQuantity.setText(
                "Qty: " + item.getQuantity()
        );

        // Price
        holder.tvPrice.setText(
                "Rs. " + item.getPrice()
        );

        // Edit button
        holder.btnEdit.setOnClickListener(v -> {

            if (listener != null) {
                listener.onEditClick(item);
            }
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {

            if (listener != null) {
                listener.onDeleteClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void loadBranchName(
            String branchId,
            TextView branchTextView
    ) {
        if (branchTextView == null) {
            return;
        }

        if (branchId == null || branchId.trim().isEmpty()) {
            branchTextView.setText("No branch assigned");
            branchTextView.setTag(null);
            return;
        }

        // Store the current branch ID to prevent recycled rows from displaying the wrong branch name.
        branchTextView.setTag(branchId);

        // Display cached branch name if available.
        if (branchNameCache.containsKey(branchId)) {
            branchTextView.setText(
                    branchNameCache.get(branchId)
            );
            return;
        }

        branchTextView.setText("Loading branch...");

        firestoreManager.getBranchById(branchId)
                .addOnSuccessListener(documentSnapshot -> {

                    String branchName = getBranchName(documentSnapshot);

                    if (branchName == null) {
                        branchName = "Branch unavailable";
                    } else {
                        branchNameCache.put(branchId, branchName);
                    }

                    // Only update if this TextView still is the same branch.
                    if (branchId.equals(branchTextView.getTag())) {
                        branchTextView.setText(branchName);
                    }
                })
                .addOnFailureListener(e -> {
                    if (branchId.equals(branchTextView.getTag())) {
                        branchTextView.setText(
                                "Failed to load branch"
                        );
                    }
                });
    }

    private String getBranchName(DocumentSnapshot doc) {
        if (doc != null && doc.exists()) {
            String name = doc.getString("name");
            if (name == null || name.trim().isEmpty()) {
                name = doc.getString("branchName");
            }
            return name;
        }
        return null;
    }

    static class InventoryViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvBranchName;
        TextView tvQuantity;
        TextView tvPrice;

        View btnEdit;
        View btnDelete;

        public InventoryViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            tvName = itemView.findViewById(
                    R.id.tvItemName
            );

            tvBranchName = itemView.findViewById(
                    R.id.tvBranchName
            );

            tvQuantity = itemView.findViewById(
                    R.id.tvQuantity
            );

            tvPrice = itemView.findViewById(
                    R.id.tvPrice
            );

            btnEdit = itemView.findViewById(
                    R.id.btnEditItem
            );

            btnDelete = itemView.findViewById(
                    R.id.btnDeleteItem
            );
        }
    }
}