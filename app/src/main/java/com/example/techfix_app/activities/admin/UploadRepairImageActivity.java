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
import com.example.techfix_app.firebase.FirestoreManager;
import com.example.techfix_app.firebase.StorageManager;
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

    private FirestoreManager firestoreManager;
    private StorageManager storageManager;

    private Uri photoUri;          // uri for camera to save into
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

        firestoreManager = new FirestoreManager();
        storageManager = new StorageManager();

        setupCategorySpinner();
        loadBranchesToSpinner();

        btnCapture.setOnClickListener(v -> checkCameraPermissionAndOpen());
        btnUpload.setOnClickListener(v -> uploadImage());
    }

    private void setupCategorySpinner() {
        String[] categories = {"Laptop", "Mobile Phone", "Desktop", "Tablet", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void loadBranchesToSpinner() {
        firestoreManager.getAllBranches(list -> {
            branchList.clear();
            branchList.addAll(list);
            List<String> names = new ArrayList<>();
            for (Branch b : branchList) names.add(b.getName());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBranch.setAdapter(adapter);
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
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
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

    private void uploadImage() {
        if (photoUri == null) {
            Toast.makeText(this, "Please capture an image first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (branchList.isEmpty()) {
            Toast.makeText(this, "No branches available", Toast.LENGTH_SHORT).show();
            return;
        }

        String caption = etCaption.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        Branch selectedBranch = branchList.get(spinnerBranch.getSelectedItemPosition());

        btnUpload.setEnabled(false);
        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

        storageManager.uploadRepairImage(photoUri, downloadUrl -> {
            if (downloadUrl != null) {
                RepairImage repairImage = new RepairImage(
                        null,
                        selectedBranch.getBranchId(),
                        category,
                        downloadUrl,
                        caption,
                        System.currentTimeMillis()
                );

                firestoreManager.addRepairImage(repairImage, success -> {
                    btnUpload.setEnabled(true);
                    if (success) {
                        Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to save image data", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                btnUpload.setEnabled(true);
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}