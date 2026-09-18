package com.example.techfix_app.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.adapters.AdminAppointmentAdapter;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.Appointment;

import java.util.ArrayList;
import java.util.List;

public class AppointmentManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAppointments;
    private ProgressBar progressBar;
    private TextView tvNoAppointments;

    private AdminAppointmentAdapter adapter;
    private final List<Appointment> appointmentList = new ArrayList<>();

    private FirestoreManager firestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_management);

        recyclerViewAppointments = findViewById(R.id.recyclerViewAppointments);
        progressBar = findViewById(R.id.progressBar);
        tvNoAppointments = findViewById(R.id.tvNoAppointments);

        firestoreManager = new FirestoreManager();

        recyclerViewAppointments.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new AdminAppointmentAdapter(
                appointmentList,
                appointment -> {
                    Intent intent = new Intent(
                            AppointmentManagementActivity.this,
                            AppointmentDetailsActivity.class
                    );

                    intent.putExtra(
                            "appointmentId",
                            appointment.getAppointmentId()
                    );

                    startActivity(intent);
                }
        );

        recyclerViewAppointments.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments();
    }

    private void loadAppointments() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoAppointments.setVisibility(View.GONE);

        firestoreManager.getAllAppointments()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    appointmentList.clear();

                    for (com.google.firebase.firestore.DocumentSnapshot document
                            : queryDocumentSnapshots.getDocuments()) {

                        Appointment appointment =
                                document.toObject(Appointment.class);

                        if (appointment != null) {

                            if (appointment.getAppointmentId() == null ||
                                    appointment.getAppointmentId().isEmpty()) {

                                appointment.setAppointmentId(
                                        document.getId()
                                );
                            }

                            appointmentList.add(appointment);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);

                    if (appointmentList.isEmpty()) {
                        tvNoAppointments.setVisibility(View.VISIBLE);
                    }

                })
                .addOnFailureListener(e -> {

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(
                            AppointmentManagementActivity.this,
                            "Failed to load appointments: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}