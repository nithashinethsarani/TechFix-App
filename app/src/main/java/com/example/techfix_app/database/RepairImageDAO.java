package com.example.techfix_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.techfix_app.models.RepairImage;

import java.util.ArrayList;
import java.util.List;

public class RepairImageDAO {

    private TechFixDatabaseHelper dbHelper;

    public RepairImageDAO(Context context) {
        dbHelper = new TechFixDatabaseHelper(context);
    }

    public long addImage(RepairImage image) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TechFixDatabaseHelper.COL_IMAGE_BRANCH_ID, image.getBranchId());
        values.put(TechFixDatabaseHelper.COL_IMAGE_CATEGORY, image.getDeviceCategory());
        values.put(TechFixDatabaseHelper.COL_IMAGE_PATH, image.getImagePath());
        values.put(TechFixDatabaseHelper.COL_IMAGE_CAPTION, image.getCaption());
        values.put(TechFixDatabaseHelper.COL_IMAGE_TIMESTAMP, image.getTimestamp());

        long id = db.insert(TechFixDatabaseHelper.TABLE_REPAIR_IMAGES, null, values);
        db.close();
        return id;
    }

    public List<RepairImage> getAllImages() {
        List<RepairImage> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TechFixDatabaseHelper.TABLE_REPAIR_IMAGES, null,
                null, null, null, null, TechFixDatabaseHelper.COL_IMAGE_TIMESTAMP + " DESC");

        while (cursor.moveToNext()) {
            list.add(cursorToImage(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<RepairImage> getImagesByCategory(String category) {
        List<RepairImage> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TechFixDatabaseHelper.TABLE_REPAIR_IMAGES, null,
                TechFixDatabaseHelper.COL_IMAGE_CATEGORY + "=?",
                new String[]{category}, null, null,
                TechFixDatabaseHelper.COL_IMAGE_TIMESTAMP + " DESC");

        while (cursor.moveToNext()) {
            list.add(cursorToImage(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    private RepairImage cursorToImage(Cursor cursor) {
        RepairImage image = new RepairImage();
        image.setImageId(cursor.getInt(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_IMAGE_ID)));
        image.setBranchId(cursor.getInt(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_IMAGE_BRANCH_ID)));
        image.setDeviceCategory(cursor.getString(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_IMAGE_CATEGORY)));
        image.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_IMAGE_PATH)));
        image.setCaption(cursor.getString(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_IMAGE_CAPTION)));
        image.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_IMAGE_TIMESTAMP)));
        return image;
    }
}