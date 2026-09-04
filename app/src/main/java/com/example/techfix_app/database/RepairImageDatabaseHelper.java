package com.example.techfix_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.techfix_app.models.RepairImage;

import java.util.ArrayList;
import java.util.List;

public class RepairImageDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "techfix_repairs.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_REPAIR_IMAGES = "repair_images";
    public static final String COLUMN_ID = "image_id";
    public static final String COLUMN_BRANCH_ID = "branch_id";
    public static final String COLUMN_DEVICE_CATEGORY = "device_category";
    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_CAPTION = "caption";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public RepairImageDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_REPAIR_IMAGES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BRANCH_ID + " INTEGER, "
                + COLUMN_DEVICE_CATEGORY + " TEXT, "
                + COLUMN_IMAGE_PATH + " TEXT, "
                + COLUMN_CAPTION + " TEXT, "
                + COLUMN_TIMESTAMP + " INTEGER" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPAIR_IMAGES);
        onCreate(db);
    }

    public long addImage(RepairImage repairImage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BRANCH_ID, repairImage.getBranchId());
        values.put(COLUMN_DEVICE_CATEGORY, repairImage.getDeviceCategory());
        values.put(COLUMN_IMAGE_PATH, repairImage.getImagePath());
        values.put(COLUMN_CAPTION, repairImage.getCaption());
        values.put(COLUMN_TIMESTAMP, repairImage.getTimestamp());

        long result = db.insert(TABLE_REPAIR_IMAGES, null, values);
        db.close();
        return result;
    }

    public List<RepairImage> getAllImages() {
        List<RepairImage> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_REPAIR_IMAGES + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int imageId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                int branchId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BRANCH_ID));
                String deviceCategory = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEVICE_CATEGORY));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH));
                String caption = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAPTION));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));

                RepairImage repairImage = new RepairImage(branchId, deviceCategory, imagePath, caption, timestamp);
                repairImage.setImageId(imageId);

                list.add(repairImage);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }
}