package com.example.techfix_app.activities.admin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.techfix_app.R;
import com.example.techfix_app.database.RepairImageDatabaseHelper;
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.models.Branch;
import com.example.techfix_app.models.RepairImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UploadRepairImageActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;

    private ImageView imgPreview;
    private EditText etCaption;
    private Spinner spinnerBranch, spinnerCategory;
    private Button btnCapture, btnUpload;

    private RepairImageDatabaseHelper dbHelper;
    private FirestoreManager firestoreManager;

    private Uri photoUri;
    private String currentPhotoPath;
    private List<Branch> branchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_repair_image);

        imgPreview = findViewById(R.id.imgPreview);
        etCaption = findViewById(R.id.etCaption);
        spinnerBranch = findViewById(R.id.spinnerUploadBranch);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnCapture = findViewById(R.id.btnCapture);
        btnUpload = findViewById(R.id.btnUpload);

        dbHelper = new RepairImageDatabaseHelper(this);
        firestoreManager = new FirestoreManager();

        setupCategorySpinner();
        loadBranchesFromFirestore();

        btnCapture.setOnClickListener(v -> checkCameraPermissionAndOpen());
        btnUpload.setOnClickListener(v -> saveImage());
    }

    private void setupCategorySpinner() {
        String[] categories = {"Laptop", "Mobile Phone", "Desktop", "Tablet", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void loadBranchesFromFirestore() {
        firestoreManager.getAllBranches(new FirestoreManager.OnBranchesLoadedListener() {
            @Override
            public void onSuccess(List<Branch> branches) {
                branchList.clear();
                branchList.addAll(branches);

                List<String> names = new ArrayList<>();
                for (Branch b : branchList) {
                    names.add(b.getName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(UploadRepairImageActivity.this,
                        android.R.layout.simple_spinner_item, names);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBranch.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(UploadRepairImageActivity.this,
                        "Failed to load branches: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "Error creating file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile
            );

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            // Grant URI permissions for security on newer Android versions
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            try {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            } catch (Exception e) {
                Toast.makeText(this, "No camera application found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "REPAIR_" + timeStamp;
        File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            imgPreview.setImageURI(photoUri);
        }
    }

    private void saveImage() {
        if (photoUri == null || currentPhotoPath == null) {
            Toast.makeText(this, "Please capture an image first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (branchList.isEmpty()) {
            Toast.makeText(this, "No branches available", Toast.LENGTH_SHORT).show();
            return;
        }

        String caption = etCaption.getText().toString().trim();
        String deviceCategory = spinnerCategory.getSelectedItem().toString();
        Branch selectedBranch = branchList.get(spinnerBranch.getSelectedItemPosition());

        int branchId = 0;
        try {
            // Parses numerical branchId if Branch model stores branchId as String
            branchId = Integer.parseInt(selectedBranch.getBranchId());
        } catch (NumberFormatException e) {
            // Uses position + 1 if branchId string is non-numeric
            branchId = spinnerBranch.getSelectedItemPosition() + 1;
        }

        // Instantiates RepairImage using updated constructor
        RepairImage repairImage = new RepairImage(
                branchId,
                deviceCategory,
                currentPhotoPath,
                caption,
                System.currentTimeMillis()
        );

        long newId = dbHelper.addImage(repairImage);
        if (newId != -1) {
            Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }
}