package com.example.techfix_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.models.Appointment;

import java.util.List;

public class AdminAppointmentAdapter
        extends RecyclerView.Adapter<AdminAppointmentAdapter.AppointmentViewHolder> {

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    private final List<Appointment> appointmentList;
    private final OnAppointmentClickListener listener;

    public AdminAppointmentAdapter(
            List<Appointment> appointmentList,
            OnAppointmentClickListener listener) {

        this.appointmentList = appointmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_admin_appointment,
                        parent,
                        false
                );

        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull AppointmentViewHolder holder,
            int position) {

        Appointment appointment = appointmentList.get(position);

        holder.tvAppointment.setText(
                "Appointment For: " + safe(appointment.getCustomerName())
        );

        holder.tvCustomerName.setText(
                "Customer Name: " +safe(appointment.getCustomerName())
        );

        holder.tvDevice.setText(
                "Device: " + safe(appointment.getDeviceName())
        );

        holder.tvCategory.setText(
                "Category: " + safe(appointment.getDeviceCategory())
        );

        holder.tvDateTime.setText(
                "Date: " + safe(appointment.getAppointmentDate())
                        + "  |  Time: "
                        + safe(appointment.getAppointmentTime())
        );

        holder.tvStatus.setText(
                "Status: " + safe(appointment.getStatus())
        );

        holder.itemView.setOnClickListener(v ->
                listener.onAppointmentClick(appointment)
        );
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    private String safe(String value) {
        return value == null || value.isEmpty()
                ? "N/A"
                : value;
    }

    static class AppointmentViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvAppointment;
        TextView tvCustomerName;
        TextView tvDevice;
        TextView tvCategory;
        TextView tvDateTime;
        TextView tvStatus;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAppointment =
                    itemView.findViewById(R.id.tvAppointment);

            tvCustomerName =
                    itemView.findViewById(R.id.tvCustomerName);

            tvDevice =
                    itemView.findViewById(R.id.tvDevice);

            tvCategory =
                    itemView.findViewById(R.id.tvCategory);

            tvDateTime =
                    itemView.findViewById(R.id.tvDateTime);

            tvStatus =
                    itemView.findViewById(R.id.tvStatus);
        }
    }
}