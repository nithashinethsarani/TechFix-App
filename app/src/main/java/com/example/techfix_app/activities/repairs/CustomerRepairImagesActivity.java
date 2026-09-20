
package com.example.techfix_app.activities.repairs;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.techfix_app.R;
import com.example.techfix_app.database.RepairImageDatabaseHelper;
import com.example.techfix_app.models.RepairImage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomerRepairImagesActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvEmpty;
    private LinearLayout imageContainer;

    private RepairImageDatabaseHelper dbHelper;
    private String repairId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_repair_images);

        repairId = getIntent().getStringExtra("repairId");

        if (repairId == null || repairId.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Invalid repair ID",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        tvTitle = findViewById(R.id.tvRepairImagesTitle);
        tvEmpty = findViewById(R.id.tvNoRepairImages);
        imageContainer = findViewById(R.id.repairImagesContainer);

        tvTitle.setText("Repair Images");

        dbHelper = new RepairImageDatabaseHelper(this);

        loadImages();
    }

    private void loadImages() {
        List<RepairImage> images =
                dbHelper.getImagesByRepairId(repairId);

        imageContainer.removeAllViews();

        if (images == null || images.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("No images are available for this repair.");
            return;
        }

        tvEmpty.setVisibility(View.GONE);

        for (RepairImage image : images) {
            addImageItem(image);
        }
    }

    private void addImageItem(RepairImage repairImage) {
        View itemView = getLayoutInflater().inflate(
                R.layout.item_customer_repair_image,
                imageContainer,
                false
        );

        ImageView imageView = itemView.findViewById(R.id.imgRepairImage);
        TextView tvCategory = itemView.findViewById(R.id.tvImageCategory);
        TextView tvCaption = itemView.findViewById(R.id.tvImageCaption);
        TextView tvDate = itemView.findViewById(R.id.tvImageDate);
        TextView tvMissing = itemView.findViewById(R.id.tvImageMissing);

        String path = repairImage.getImagePath();
        File imageFile = path == null ? null : new File(path);

        if (imageFile != null && imageFile.exists()) {
            imageView.setImageBitmap(
                    BitmapFactory.decodeFile(imageFile.getAbsolutePath())
            );

            imageView.setVisibility(View.VISIBLE);
            tvMissing.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.GONE);
            tvMissing.setVisibility(View.VISIBLE);
            tvMissing.setText("Image file is no longer available.");
        }

        tvCategory.setText(
                "Device category: " + safe(repairImage.getDeviceCategory())
        );

        String caption = repairImage.getCaption();

        if (caption == null || caption.trim().isEmpty()) {
            tvCaption.setVisibility(View.GONE);
        } else {
            tvCaption.setVisibility(View.VISIBLE);
            tvCaption.setText("Caption: " + caption);
        }

        tvDate.setText(
                "Added: " + formatTimestamp(repairImage.getTimestamp())
        );

        imageContainer.addView(itemView);
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
        );

        return format.format(new Date(timestamp));
    }

    private String safe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Not available";
        }

        return value;
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }

        super.onDestroy();
    }
}