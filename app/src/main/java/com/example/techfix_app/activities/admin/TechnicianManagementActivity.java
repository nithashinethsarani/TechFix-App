package com.example.techfix_app.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.techfix_app.R;
import com.example.techfix_app.adapters.TechnicianAdapter;
import com.example.techfix_app.database.TechnicianDAO;
import com.example.techfix_app.models.Technician;

import java.util.ArrayList;
import java.util.List;

public class TechnicianManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TechnicianAdapter adapter;
    private List<Technician> technicianList = new ArrayList<>();
    private TechnicianDAO technicianDAO;
    private View fabAddTechnician;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_management);

        recyclerView = findViewById(R.id.recyclerViewTechnicians);
        fabAddTechnician = findViewById(R.id.fabAddTechnician);
        technicianDAO = new TechnicianDAO(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TechnicianAdapter(technicianList, new TechnicianAdapter.OnTechnicianClickListener() {
            @Override
            public void onEditClick(Technician technician) {
                Intent intent = new Intent(TechnicianManagementActivity.this, AddEditTechnicianActivity.class);
                intent.putExtra("technicianId", technician.getTechnicianId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Technician technician) {
                boolean success = technicianDAO.deleteTechnician(technician.getTechnicianId());
                if (success) {
                    Toast.makeText(TechnicianManagementActivity.this, "Technician deleted", Toast.LENGTH_SHORT).show();
                    loadTechnicians();
                } else {
                    Toast.makeText(TechnicianManagementActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setAdapter(adapter);

        fabAddTechnician.setOnClickListener(v ->
                startActivity(new Intent(TechnicianManagementActivity.this, AddEditTechnicianActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTechnicians();
    }

    private void loadTechnicians() {
        List<Technician> list = technicianDAO.getAllTechnicians();
        technicianList.clear();
        technicianList.addAll(list);
        adapter.notifyDataSetChanged();
    }
}