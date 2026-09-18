package com.example.techfix_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.Repair;

import java.util.List;
import java.util.Locale;

public class AdminRepairAdapter
        extends RecyclerView.Adapter<AdminRepairAdapter.RepairViewHolder> {

    public interface OnRepairClickListener {
        void onRepairClick(Repair repair);
    }

    private final List<Repair> repairList;
    private final OnRepairClickListener listener;

    public AdminRepairAdapter(
            List<Repair> repairList,
            OnRepairClickListener listener) {

        this.repairList = repairList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RepairViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_admin_repair,
                        parent,
                        false
                );

        return new RepairViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RepairViewHolder holder,
            int position) {

        Repair repair = repairList.get(position);

        FirestoreManager firestoreManager = new FirestoreManager();

        firestoreManager.getAppointmentUserName(repair.getAppointmentId(), new FirestoreManager.OnUserNameLoadedListener() {
            @Override
            public void onLoaded(String userName) {
                holder.tvRepair.setText(
                        "Repair For: " + userName
                );
            }

            @Override
            public void onError(Exception e) {
                holder.tvRepair.setText(
                        "Repair ID: " + repair.getRepairId()
                );
            }
        });

        holder.tvDevice.setText(
                "Device : "+repair.getDeviceName()
        );

        holder.tvCategory.setText(
                "Category: "+repair.getDeviceCategory()
        );

        holder.tvStatus.setText(
                "Status: " + formatStatus(repair.getStatus())
        );

        holder.tvPrice.setText(
                String.format(
                        Locale.getDefault(),
                        "Final Price: Rs. %.2f",
                        repair.getFinalPrice()
                )
        );

        holder.itemView.setOnClickListener(
                v -> listener.onRepairClick(repair)
        );
    }

    @Override
    public int getItemCount() {
        return repairList.size();
    }

    private String formatStatus(String status) {

        if (status == null || status.isEmpty()) {
            return "Unknown";
        }

        return status
                .replace("_", " ")
                .substring(0, 1)
                .toUpperCase()
                + status.replace("_", " ").substring(1);
    }

    static class RepairViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvRepair;
        TextView tvDevice;
        TextView tvCategory;
        TextView tvStatus;
        TextView tvPrice;

        public RepairViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRepair =
                    itemView.findViewById(R.id.tvRepair);

            tvDevice =
                    itemView.findViewById(R.id.tvDevice);

            tvCategory =
                    itemView.findViewById(R.id.tvCategory);

            tvStatus =
                    itemView.findViewById(R.id.tvStatus);

            tvPrice =
                    itemView.findViewById(R.id.tvPrice);
        }
    }
}