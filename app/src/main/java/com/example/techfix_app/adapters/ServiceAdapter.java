package com.example.techfix_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.models.Service;

import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private final List<Service> serviceList;
    private final OnServiceClickListener listener;
    private String userRole = "user";

    public interface OnServiceClickListener {
        void onServiceClick(Service service);
        void onEditClick(Service service);
        void onDeleteClick(Service service);
    }

    public ServiceAdapter(
            List<Service> serviceList,
            OnServiceClickListener listener) {
        this.serviceList = serviceList;
        this.listener = listener;
    }

    // Public setter so fetchUserRole() in Activities can resolve this method
    public void setUserRole(String userRole) {
        this.userRole = userRole != null ? userRole : "user";
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);

        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ServiceViewHolder holder,
            int position) {

        Service service = serviceList.get(position);

        holder.tvServiceName.setText(service.getName());

        if (service.getDeviceCategory() != null && !service.getDeviceCategory().isEmpty()) {
            holder.tvDeviceCategory.setText(service.getDeviceCategory());
            holder.tvDeviceCategory.setVisibility(View.VISIBLE);
        } else {
            holder.tvDeviceCategory.setVisibility(View.GONE);
        }

        holder.tvPrice.setText(
                String.format(
                        Locale.getDefault(),
                        "Rs. %.2f",
                        service.getPrice()
                )
        );

        boolean isAdmin = "admin".equalsIgnoreCase(userRole);

        if (holder.btnEdit != null) {
            holder.btnEdit.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            holder.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(service);
            });
        }

        if (holder.btnDelete != null) {
            holder.btnDelete.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(service);
            });
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onServiceClick(service);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList != null ? serviceList.size() : 0;
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCategoryIcon;
        TextView tvServiceName;
        TextView tvDeviceCategory;
        TextView tvAvailability;
        TextView tvPrice;
        View btnEdit, btnDelete;

        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvDeviceCategory = itemView.findViewById(R.id.tvDeviceCategory);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
            tvPrice = itemView.findViewById(R.id.tvPrice);

            btnEdit = itemView.findViewById(R.id.btnEditService);
            btnDelete = itemView.findViewById(R.id.btnDeleteService);
        }
    }
}