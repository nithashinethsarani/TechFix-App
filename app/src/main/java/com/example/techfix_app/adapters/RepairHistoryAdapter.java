package com.example.techfix_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.models.RepairHistory;

import java.util.List;
import java.util.Locale;

public class RepairHistoryAdapter extends RecyclerView.Adapter<RepairHistoryAdapter.ViewHolder> {

    private final List<RepairHistory> repairHistoryList;

    public RepairHistoryAdapter(List<RepairHistory> repairHistoryList) {
        this.repairHistoryList = repairHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_repair_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RepairHistory item = repairHistoryList.get(position);

        holder.textDeviceName.setText(item.getDeviceName() != null ? item.getDeviceName() : "Unknown Device");
        holder.textServiceName.setText(item.getServiceName() != null ? item.getServiceName() : "General Repair");

        // Format price to 2 decimal places
        holder.textPrice.setText(String.format(Locale.getDefault(), "Rs. %.2f", item.getFinalPrice()));

        // Format date or default fallback
        holder.textCompletedDate.setText(item.getCompletedDate() != null ? item.getCompletedDate() : "N/A");

        // Display status
        holder.textStatus.setText(item.getStatus() != null ? item.getStatus() : "Completed");
    }

    @Override
    public int getItemCount() {
        return repairHistoryList != null ? repairHistoryList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textDeviceName;
        TextView textServiceName;
        TextView textPrice;
        TextView textCompletedDate;
        TextView textStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDeviceName = itemView.findViewById(R.id.textDeviceName);
            textServiceName = itemView.findViewById(R.id.textServiceName);
            textPrice = itemView.findViewById(R.id.textPrice);
            textCompletedDate = itemView.findViewById(R.id.textCompletedDate);
            textStatus = itemView.findViewById(R.id.textStatus);
        }
    }
}