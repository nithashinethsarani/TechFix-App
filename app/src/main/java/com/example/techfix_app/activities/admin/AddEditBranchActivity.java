package com.example.techfix_app.activities.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.database.BranchDAO;
import com.example.techfix_app.models.Branch;

public class AddEditBranchActivity extends AppCompatActivity {

    private EditText etBranchName, etAddress, etCity, etPhone;
    private Button btnSave;
    private BranchDAO branchDAO;
    private int branchId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_branch);

        etBranchName = findViewById(R.id.etBranchName);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);

        branchDAO = new BranchDAO(this);

        if (getIntent().hasExtra("branchId")) {
            branchId = getIntent().getIntExtra("branchId", -1);
            loadBranchData();
        }

        btnSave.setOnClickListener(v -> saveBranch());
    }

    private void loadBranchData() {
        Branch branch = branchDAO.getBranchById(branchId);
        if (branch != null) {
            etBranchName.setText(branch.getName());
            etAddress.setText(branch.getAddress());
            etPhone.setText(branch.getPhone());
        }
    }

    private void saveBranch() {
        String name = etBranchName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Branch branch = new Branch();
        branch.setName(name);
        branch.setAddress(address);
        branch.setPhone(phone);

        if (branchId == -1) {
            long newId = branchDAO.addBranch(branch);
            if (newId != -1) {
                Toast.makeText(this, "Branch added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add branch", Toast.LENGTH_SHORT).show();
            }
        } else {
            branch.setBranchId(branchId);
            boolean success = branchDAO.updateBranch(branch);
            if (success) {
                Toast.makeText(this, "Branch updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update branch", Toast.LENGTH_SHORT).show();
            }
        }
    }
}