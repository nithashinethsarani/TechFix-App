package com.example.techfix_app.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.Technician;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TechnicianAdapter
        extends RecyclerView.Adapter<TechnicianAdapter.TechnicianViewHolder> {

    private final List<Technician> technicianList;
    private final OnTechnicianClickListener listener;

    private final FirestoreManager firestoreManager;
    private final Map<String, String> branchNameCache = new HashMap<>();

    public interface OnTechnicianClickListener {
        void onEditClick(Technician technician);
        void onDeleteClick(Technician technician);
    }

    public TechnicianAdapter(
            List<Technician> technicianList,
            OnTechnicianClickListener listener
    ) {
        this.technicianList = technicianList;
        this.listener = listener;
        this.firestoreManager = new FirestoreManager();
    }

    @NonNull
    @Override
    public TechnicianViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_technician,
                        parent,
                        false
                );

        return new TechnicianViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TechnicianViewHolder holder,
            int position
    ) {
        Technician technician = technicianList.get(position);

        holder.tvName.setText(technician.getName());
        holder.tvSpecialization.setText(
                technician.getSpecialization()
        );

        // Load and display the branch name.
        loadBranchName(
                technician.getBranchId(),
                holder.tvBranchName
        );

        holder.tvPhone.setText(technician.getPhone());

        // Availability
        if (Boolean.TRUE.equals(technician.getIsAvailable())) {
            holder.tvIsAvailable.setText("Available");
            holder.tvIsAvailable.setTextColor(Color.GREEN);
        } else {
            holder.tvIsAvailable.setText("Not Available");
            holder.tvIsAvailable.setTextColor(Color.RED);
        }

        // Edit button
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(technician);
            }
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(technician);
            }
        });
    }

    private void loadBranchName(
            String branchId,
            TextView branchTextView
    ) {
        if (branchId == null || branchId.trim().isEmpty()) {
            branchTextView.setText("No branch assigned");
            branchTextView.setTag(null);
            return;
        }

        // Store the current branch ID to prevent recycled rows
        // from displaying the wrong branch name.
        branchTextView.setTag(branchId);

        // Display cached branch name if available.
        if (branchNameCache.containsKey(branchId)) {
            branchTextView.setText(
                    branchNameCache.get(branchId)
            );
            return;
        }

        branchTextView.setText("Loading branch...");

        firestoreManager.getBranchById(branchId)
                .addOnSuccessListener(documentSnapshot -> {

                    String branchName = getBranchName(documentSnapshot);

                    if (branchName == null) {
                        branchName = "Branch unavailable";
                    } else {
                        branchNameCache.put(branchId, branchName);
                    }

                    // Only update if this TextView still represents
                    // the same branch.
                    if (branchId.equals(branchTextView.getTag())) {
                        branchTextView.setText(branchName);
                    }
                })
                .addOnFailureListener(e -> {
                    if (branchId.equals(branchTextView.getTag())) {
                        branchTextView.setText(
                                "Failed to load branch"
                        );
                    }
                });
    }

    private String getBranchName(
            DocumentSnapshot documentSnapshot
    ) {
        if (documentSnapshot == null || !documentSnapshot.exists()) {
            return null;
        }

        return documentSnapshot.getString("name");
    }

    @Override
    public int getItemCount() {
        return technicianList.size();
    }

    static class TechnicianViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvSpecialization;
        TextView tvBranchName;
        TextView tvPhone;
        TextView tvIsAvailable;

        View btnEdit;
        View btnDelete;

        public TechnicianViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            tvName = itemView.findViewById(
                    R.id.tvTechnicianName
            );

            tvSpecialization = itemView.findViewById(
                    R.id.tvSpecialization
            );

            tvBranchName = itemView.findViewById(
                    R.id.tvBranchName
            );

            tvPhone = itemView.findViewById(
                    R.id.tvPhone
            );

            tvIsAvailable = itemView.findViewById(
                    R.id.tvIsAvailable
            );

            btnEdit = itemView.findViewById(
                    R.id.btnEdit
            );

            btnDelete = itemView.findViewById(
                    R.id.btnDelete
            );
        }
    }
}