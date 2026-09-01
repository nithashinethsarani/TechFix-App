package com.example.techfix_app.activities.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.database.BranchDAO;
import com.example.techfix_app.database.TechnicianDAO;
import com.example.techfix_app.models.Branch;
import com.example.techfix_app.models.Technician;

import java.util.ArrayList;
import java.util.List;

public class AddEditTechnicianActivity extends AppCompatActivity {

    private EditText etName, etSpecialization, etPhone;
    private Spinner spinnerBranch;
    private Button btnSave;
    private TechnicianDAO technicianDAO;
    private BranchDAO branchDAO;
    private int technicianId = -1;
    private List<Branch> branchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_technician);

        etName = findViewById(R.id.etTechName);
        etSpecialization = findViewById(R.id.etSpecialization);
        etPhone = findViewById(R.id.etTechPhone);
        spinnerBranch = findViewById(R.id.spinnerBranch);
        btnSave = findViewById(R.id.btnSaveTechnician);

        technicianDAO = new TechnicianDAO(this);
        branchDAO = new BranchDAO(this);

        loadBranchesToSpinner();

        if (getIntent().hasExtra("technicianId")) {
            technicianId = getIntent().getIntExtra("technicianId", -1);
            loadTechnicianData();
        }

        btnSave.setOnClickListener(v -> saveTechnician());
    }

    private void loadBranchesToSpinner() {
        branchList.clear();
        branchList.addAll(branchDAO.getAllBranches());

        List<String> names = new ArrayList<>();
        for (Branch b : branchList) names.add(b.getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(adapter);
    }

    private void loadTechnicianData() {
        Technician technician = technicianDAO.getTechnicianById(technicianId);
        if (technician == null) return;

        etName.setText(technician.getName());
        etSpecialization.setText(technician.getSpecialization());
        etPhone.setText(technician.getPhone());

        for (int i = 0; i < branchList.size(); i++) {
            if (branchList.get(i).getBranchId() == technician.getBranchId()) {
                spinnerBranch.setSelection(i);
                break;
            }
        }
    }

    private void saveTechnician() {
        String name = etName.getText().toString().trim();
        String specialization = etSpecialization.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty() || specialization.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (branchList.isEmpty()) {
            Toast.makeText(this, "No branches available. Add a branch first.", Toast.LENGTH_SHORT).show();
            return;
        }

        Branch selectedBranch = branchList.get(spinnerBranch.getSelectedItemPosition());

        Technician technician = new Technician();
        technician.setName(name);
        technician.setSpecialization(specialization);
        technician.setPhone(phone);
        technician.setBranchId(selectedBranch.getBranchId());

        if (technicianId == -1) {
            long newId = technicianDAO.addTechnician(technician);
            if (newId != -1) {
                Toast.makeText(this, "Technician added", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add technician", Toast.LENGTH_SHORT).show();
            }
        } else {
            technician.setTechnicianId(technicianId);
            boolean success = technicianDAO.updateTechnician(technician);
            if (success) {
                Toast.makeText(this, "Technician updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update technician", Toast.LENGTH_SHORT).show();
            }
        }
    }
}