package com.example.techfix_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.models.Technician;

import java.util.List;

public class TechnicianAdapter extends RecyclerView.Adapter<TechnicianAdapter.TechnicianViewHolder> {

    private List<Technician> technicianList;
    private OnTechnicianClickListener listener;

    public interface OnTechnicianClickListener {
        void onEditClick(Technician technician);
        void onDeleteClick(Technician technician);
    }

    public TechnicianAdapter(List<Technician> technicianList, OnTechnicianClickListener listener) {
        this.technicianList = technicianList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TechnicianViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_technician, parent, false);
        return new TechnicianViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TechnicianViewHolder holder, int position) {
        Technician technician = technicianList.get(position);
        holder.tvName.setText(technician.getName());
        holder.tvSpecialization.setText(technician.getSpecialization());
        holder.tvPhone.setText(technician.getPhone());

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(technician));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(technician));
    }

    @Override
    public int getItemCount() {
        return technicianList.size();
    }

    static class TechnicianViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSpecialization, tvPhone;
        View btnEdit, btnDelete;

        public TechnicianViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTechnicianName);
            tvSpecialization = itemView.findViewById(R.id.tvSpecialization);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}