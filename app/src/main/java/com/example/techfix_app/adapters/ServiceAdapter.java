package com.example.techfix_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.models.Service;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> serviceList;
    private OnServiceClickListener listener;

    // Interface to handle item click (used to open ServiceDetailsActivity)
    public interface OnServiceClickListener {
        void onServiceClick(Service service);
    }

    public ServiceAdapter(List<Service> serviceList, OnServiceClickListener listener) {
        this.serviceList = serviceList;
        this.listener = listener;
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

        holder.tvServiceName.setText(service.getServiceName());
        holder.tvDeviceCategory.setText(service.getDeviceCategory());
        holder.tvPrice.setText(String.format("Rs. %.2f", service.getPrice()));

        if (service.isAvailable()) {
            holder.tvAvailability.setText("Available");
            holder.tvAvailability.setTextColor(0xFF2E7D32); // green
        } else {
            holder.tvAvailability.setText("Unavailable");
            holder.tvAvailability.setTextColor(0xFFC62828); // red
        }

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

    // Call this when filtering/searching to refresh the list
    public void updateList(List<Service> newList) {
        this.serviceList = newList;
        notifyDataSetChanged();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvDeviceCategory, tvPrice, tvAvailability;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvDeviceCategory = itemView.findViewById(R.id.tvDeviceCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
        }
    }
}