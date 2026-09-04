package com.example.techfix_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.techfix_app.R;
import java.util.List;

public class SparePartAdapter extends RecyclerView.Adapter<SparePartAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(SpareItem item);
    }

    public static class SpareItem {
        public String name;
        public boolean isAvailable;

        public SpareItem(String name, boolean isAvailable) {
            this.name = name;
            this.isAvailable = isAvailable;
        }
    }

    private final List<SpareItem> spareList;
    private final OnItemClickListener listener;

    // Single Consolidated Constructor
    public SparePartAdapter(List<SpareItem> spareList, OnItemClickListener listener) {
        this.spareList = spareList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spare_part, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SpareItem item = spareList.get(position);
        holder.tvPartName.setText(item.name);

        if (item.isAvailable) {
            holder.tvPartStatus.setText("In Stock / Available");
            holder.tvPartStatus.setTextColor(0xFF008800); // Green
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
            });
        } else {
            holder.tvPartStatus.setText("Out of Stock / Unavailable");
            holder.tvPartStatus.setTextColor(0xFFFF0000); // Red
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
            });
        }
    }

    @Override
    public int getItemCount() {
        return spareList != null ? spareList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPartName, tvPartStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPartName = itemView.findViewById(R.id.tvPartName);
            tvPartStatus = itemView.findViewById(R.id.tvPartStatus);
        }
    }
}