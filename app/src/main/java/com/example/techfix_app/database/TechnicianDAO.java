package com.example.techfix_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.techfix_app.models.Technician;

import java.util.ArrayList;
import java.util.List;

public class TechnicianDAO {

    private TechFixDatabaseHelper dbHelper;

    public TechnicianDAO(Context context) {
        dbHelper = new TechFixDatabaseHelper(context);
    }

    public long addTechnician(Technician t) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TechFixDatabaseHelper.COL_TECH_NAME, t.getName());
        values.put(TechFixDatabaseHelper.COL_TECH_SPECIALIZATION, t.getSpecialization());
        values.put(TechFixDatabaseHelper.COL_TECH_PHONE, t.getPhone());
        values.put(TechFixDatabaseHelper.COL_TECH_BRANCH_ID, t.getBranchId());

        long id = db.insert(TechFixDatabaseHelper.TABLE_TECHNICIANS, null, values);
        db.close();
        return id;
    }

    public boolean updateTechnician(Technician t) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TechFixDatabaseHelper.COL_TECH_NAME, t.getName());
        values.put(TechFixDatabaseHelper.COL_TECH_SPECIALIZATION, t.getSpecialization());
        values.put(TechFixDatabaseHelper.COL_TECH_PHONE, t.getPhone());
        values.put(TechFixDatabaseHelper.COL_TECH_BRANCH_ID, t.getBranchId());

        int rows = db.update(TechFixDatabaseHelper.TABLE_TECHNICIANS, values,
                TechFixDatabaseHelper.COL_TECH_ID + "=?",
                new String[]{String.valueOf(t.getTechnicianId())});
        db.close();
        return rows > 0;
    }

    public boolean deleteTechnician(int technicianId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(TechFixDatabaseHelper.TABLE_TECHNICIANS,
                TechFixDatabaseHelper.COL_TECH_ID + "=?",
                new String[]{String.valueOf(technicianId)});
        db.close();
        return rows > 0;
    }

    public Technician getTechnicianById(int technicianId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TechFixDatabaseHelper.TABLE_TECHNICIANS, null,
                TechFixDatabaseHelper.COL_TECH_ID + "=?",
                new String[]{String.valueOf(technicianId)}, null, null, null);

        Technician t = null;
        if (cursor.moveToFirst()) {
            t = cursorToTechnician(cursor);
        }
        cursor.close();
        db.close();
        return t;
    }

    public List<Technician> getAllTechnicians() {
        List<Technician> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TechFixDatabaseHelper.TABLE_TECHNICIANS, null,
                null, null, null, null, TechFixDatabaseHelper.COL_TECH_NAME + " ASC");

        while (cursor.moveToNext()) {
            list.add(cursorToTechnician(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    private Technician cursorToTechnician(Cursor cursor) {
        Technician t = new Technician();
        t.setTechnicianId(cursor.getInt(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_TECH_ID)));
        t.setName(cursor.getString(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_TECH_NAME)));
        t.setSpecialization(cursor.getString(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_TECH_SPECIALIZATION)));
        t.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_TECH_PHONE)));
        t.setBranchId(cursor.getInt(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_TECH_BRANCH_ID)));
        return t;
    }
}