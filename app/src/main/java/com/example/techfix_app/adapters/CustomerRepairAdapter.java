package com.example.techfix_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CustomerRepairAdapter
        extends RecyclerView.Adapter<CustomerRepairAdapter.RepairViewHolder> {

    public interface OnRepairClickListener {
        void onRepairClick(String repairId);
    }

    private final List<DocumentSnapshot> repairs;
    private final OnRepairClickListener listener;

    public CustomerRepairAdapter(
            List<DocumentSnapshot> repairs,
            OnRepairClickListener listener) {

        this.repairs = repairs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RepairViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer_repair, parent, false);

        return new RepairViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RepairViewHolder holder,
            int position) {

        DocumentSnapshot repair = repairs.get(position);

        String repairId = repair.getId();

        String deviceName = repair.getString("deviceName");
        String status = repair.getString("status");

        if (deviceName == null || deviceName.trim().isEmpty()) {
            deviceName = "Device";
        }

        if (status == null || status.trim().isEmpty()) {
            status = "Not available";
        }

        holder.tvRepairDevice.setText(deviceName);
        holder.tvRepairId.setText("Repair ID: " + repairId);
        holder.tvRepairStatus.setText("Status: " + status);

        Number price = repair.getDouble("finalPrice");

        if (price == null) {
            price = repair.getLong("finalPrice");
        }

        if (price != null) {
            NumberFormat format =
                    NumberFormat.getNumberInstance(Locale.US);

            format.setMinimumFractionDigits(2);
            format.setMaximumFractionDigits(2);

            holder.tvRepairPrice.setText(
                    "Final Price: Rs. " + format.format(price.doubleValue())
            );
        } else {
            holder.tvRepairPrice.setText("Final Price: Not set");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRepairClick(repairId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return repairs.size();
    }

    static class RepairViewHolder extends RecyclerView.ViewHolder {

        TextView tvRepairDevice;
        TextView tvRepairId;
        TextView tvRepairStatus;
        TextView tvRepairPrice;

        public RepairViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRepairDevice = itemView.findViewById(R.id.tvRepairDevice);
            tvRepairId = itemView.findViewById(R.id.tvRepairId);
            tvRepairStatus = itemView.findViewById(R.id.tvRepairStatus);
            tvRepairPrice = itemView.findViewById(R.id.tvRepairPrice);
        }
    }
}