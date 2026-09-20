
package com.example.techfix_app.activities.admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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

    private ImageView imgPreview;
    private EditText etCaption;
    private Spinner spinnerBranch;
    private Spinner spinnerCategory;
    private Button btnCapture;
    private Button btnUpload;

    private RepairImageDatabaseHelper dbHelper;
    private FirestoreManager firestoreManager;

    private Uri photoUri;
    private String currentPhotoPath;

    private String repairId;
    private String repairBranchId;
    private String repairDeviceCategory;

    private final List<Branch> branchList = new ArrayList<>();

    private ArrayAdapter<String> branchAdapter;

    private final String[] categories = {
            "Laptop",
            "Mobile Phone",
            "Desktop",
            "Tablet",
            "Other"
    };

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (granted) {
                            openCamera();
                        } else {
                            Toast.makeText(
                                    this,
                                    "Camera permission is required",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            if (photoUri != null && currentPhotoPath != null) {
                                imgPreview.setImageURI(photoUri);
                            } else {
                                Toast.makeText(
                                        this,
                                        "Could not load captured image",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        } else {
                            deleteTemporaryPhoto();

                            Toast.makeText(
                                    this,
                                    "Image capture cancelled",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_repair_image);

        repairId = getIntent().getStringExtra("repairId");
        repairBranchId = getIntent().getStringExtra("branchId");
        repairDeviceCategory = getIntent().getStringExtra("deviceCategory");

        if (repairId == null || repairId.trim().isEmpty()) {
            Toast.makeText(this, "Invalid repair ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();

        dbHelper = new RepairImageDatabaseHelper(this);
        firestoreManager = new FirestoreManager();

        setupCategorySpinner();
        setupBranchSpinner();
        loadBranchesFromFirestore();

        btnCapture.setOnClickListener(v -> checkCameraPermissionAndOpen());
        btnUpload.setOnClickListener(v -> saveImage());
    }

    private void initializeViews() {
        imgPreview = findViewById(R.id.imgPreview);
        etCaption = findViewById(R.id.etCaption);
        spinnerBranch = findViewById(R.id.spinnerUploadBranch);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnCapture = findViewById(R.id.btnCapture);
        btnUpload = findViewById(R.id.btnUpload);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerCategory.setAdapter(adapter);

        if (repairDeviceCategory != null) {
            int position = adapter.getPosition(repairDeviceCategory);

            if (position >= 0) {
                spinnerCategory.setSelection(position);
            }
        }
    }

    private void setupBranchSpinner() {
        branchAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>()
        );

        branchAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerBranch.setAdapter(branchAdapter);
    }

    private void loadBranchesFromFirestore() {
        firestoreManager.getAllBranches(
                new FirestoreManager.OnBranchesLoadedListener() {
                    @Override
                    public void onSuccess(List<Branch> branches) {
                        branchList.clear();

                        if (branches != null) {
                            branchList.addAll(branches);
                        }

                        List<String> branchNames = new ArrayList<>();

                        for (Branch branch : branchList) {
                            branchNames.add(
                                    branch.getName() == null
                                            ? "Unnamed branch"
                                            : branch.getName()
                            );
                        }

                        branchAdapter.clear();
                        branchAdapter.addAll(branchNames);
                        branchAdapter.notifyDataSetChanged();

                        selectRepairBranch();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(
                                UploadRepairImageActivity.this,
                                "Failed to load branches: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void selectRepairBranch() {
        if (repairBranchId == null) {
            return;
        }

        for (int i = 0; i < branchList.size(); i++) {
            String branchId = branchList.get(i).getBranchId();

            if (repairBranchId.equals(branchId)) {
                spinnerBranch.setSelection(i);
                return;
            }
        }
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(
                    this,
                    "No camera application found",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        try {
            File photoFile = createImageFile();

            photoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    photoFile
            );

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            cameraIntent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            );

            cameraLauncher.launch(cameraIntent);

        } catch (IOException e) {
            Toast.makeText(
                    this,
                    "Could not create image file: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
        ).format(new Date());

        File storageDir = getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
        );

        if (storageDir == null) {
            throw new IOException("Picture storage is unavailable");
        }

        if (!storageDir.exists() && !storageDir.mkdirs()) {
            throw new IOException("Could not create picture folder");
        }

        File image = File.createTempFile(
                "REPAIR_" + timestamp + "_",
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void saveImage() {
        if (photoUri == null || currentPhotoPath == null) {
            Toast.makeText(
                    this,
                    "Please capture an image first",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        File imageFile = new File(currentPhotoPath);

        if (!imageFile.exists() || imageFile.length() == 0) {
            Toast.makeText(
                    this,
                    "Captured image file is missing or empty",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (branchList.isEmpty()
                || spinnerBranch.getSelectedItemPosition() < 0) {
            Toast.makeText(
                    this,
                    "Please select a valid branch",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Branch selectedBranch = branchList.get(
                spinnerBranch.getSelectedItemPosition()
        );

        String branchId = selectedBranch.getBranchId();

        if (branchId == null || branchId.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Selected branch has an invalid ID",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String caption = etCaption.getText().toString().trim();
        String deviceCategory = spinnerCategory.getSelectedItem().toString();

        RepairImage repairImage = new RepairImage(
                repairId,
                branchId,
                deviceCategory,
                currentPhotoPath,
                caption,
                System.currentTimeMillis()
        );

        btnUpload.setEnabled(false);

        long newId = dbHelper.addImage(repairImage);

        btnUpload.setEnabled(true);

        if (newId != -1) {
            Toast.makeText(
                    this,
                    "Repair image saved locally",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        } else {
            Toast.makeText(
                    this,
                    "Failed to save image",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void deleteTemporaryPhoto() {
        if (currentPhotoPath != null) {
            File file = new File(currentPhotoPath);

            if (file.exists()) {
                file.delete();
            }
        }

        photoUri = null;
        currentPhotoPath = null;
        imgPreview.setImageDrawable(null);
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }

        super.onDestroy();
    }
}