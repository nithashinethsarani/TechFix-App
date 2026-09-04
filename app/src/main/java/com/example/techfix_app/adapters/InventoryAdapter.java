package com.example.techfix_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.models.InventoryItem;

import java.util.List;

public class InventoryAdapter
        extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private final List<InventoryItem> itemList;
    private final OnItemClickListener listener;

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

    static class InventoryViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
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