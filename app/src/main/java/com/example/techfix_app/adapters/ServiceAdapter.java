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

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> serviceList;
    private OnServiceClickListener listener;
    private String userRole = "user";

    public interface OnServiceClickListener {
        void onServiceClick(Service service);
        void onEditClick(Service service);
        void onDeleteClick(Service service);
    }

    public ServiceAdapter(List<Service> serviceList, OnServiceClickListener listener) {
        this.serviceList = serviceList;
        this.listener = listener;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);

        holder.tvServiceName.setText(service.getName());
        holder.tvDeviceCategory.setText(service.getDeviceCategory());
        holder.tvPrice.setText(String.format("Rs. %.2f", service.getPrice()));

        if ("Mobile".equalsIgnoreCase(service.getDeviceCategory())) {
            holder.ivCategoryIcon.setImageResource(R.drawable.ic_mobile);
        } else {
            holder.ivCategoryIcon.setImageResource(R.drawable.ic_computer);
        }

        boolean isAvailable = "Available".equalsIgnoreCase(service.getAvailability());

        if (isAvailable) {
            holder.tvAvailability.setText("● Available");
            holder.tvAvailability.setTextColor(0xFF4CAF50);
        } else {
            holder.tvAvailability.setText("● Unavailable");
            holder.tvAvailability.setTextColor(0xFFE53935);
        }

        // Show edit/delete icons only for admin users
        boolean isAdmin = "admin".equalsIgnoreCase(userRole);
        holder.btnEditService.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        holder.btnDeleteService.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        holder.btnEditService.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(service);
            }
        });

        holder.btnDeleteService.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(service);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServiceClick(service);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public void updateList(List<Service> newList) {
        this.serviceList = newList;
        notifyDataSetChanged();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryIcon, btnEditService, btnDeleteService;
        TextView tvServiceName, tvDeviceCategory, tvPrice, tvAvailability;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvDeviceCategory = itemView.findViewById(R.id.tvDeviceCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
            btnEditService = itemView.findViewById(R.id.btnEditService);
            btnDeleteService = itemView.findViewById(R.id.btnDeleteService);
        }
    }
}