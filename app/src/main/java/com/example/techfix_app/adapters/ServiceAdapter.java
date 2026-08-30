package com.example.techfix_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.models.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> serviceList;
    private final List<Service> originalList;

    public ServiceAdapter(List<Service> serviceList) {
        this.serviceList = new ArrayList<>(serviceList);
        this.originalList = new ArrayList<>(serviceList);
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

        holder.tvServiceName.setText(service.getServiceName());
        holder.tvDeviceCategory.setText(service.getDeviceCategory());

        holder.tvPrice.setText(
                String.format(
                        Locale.getDefault(),
                        "Rs. %.2f",
                        service.getPrice()
                )
        );
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public void filterList(List<Service> filteredList) {
        serviceList = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }

    public void resetList() {
        serviceList = new ArrayList<>(originalList);
        notifyDataSetChanged();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {

        TextView tvServiceName;
        TextView tvDeviceCategory;
        TextView tvPrice;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvDeviceCategory = itemView.findViewById(R.id.tvDeviceCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}