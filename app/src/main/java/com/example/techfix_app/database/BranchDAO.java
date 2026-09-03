package com.example.techfix_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.techfix_app.models.Branch;

import java.util.ArrayList;
import java.util.List;

public class BranchDAO {

    private TechFixDatabaseHelper dbHelper;

    public BranchDAO(Context context) {
        dbHelper = new TechFixDatabaseHelper(context);
    }

    public long addBranch(Branch branch) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TechFixDatabaseHelper.COL_BRANCH_NAME, branch.getName());
        values.put(TechFixDatabaseHelper.COL_BRANCH_ADDRESS, branch.getAddress());
        values.put(TechFixDatabaseHelper.COL_BRANCH_CITY, branch.getCity());
        values.put(TechFixDatabaseHelper.COL_BRANCH_PHONE, branch.getPhone());

        long id = db.insert(TechFixDatabaseHelper.TABLE_BRANCHES, null, values);
        db.close();
        return id;
    }

    public boolean updateBranch(Branch branch) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TechFixDatabaseHelper.COL_BRANCH_NAME, branch.getName());
        values.put(TechFixDatabaseHelper.COL_BRANCH_ADDRESS, branch.getAddress());
        values.put(TechFixDatabaseHelper.COL_BRANCH_CITY, branch.getCity());
        values.put(TechFixDatabaseHelper.COL_BRANCH_PHONE, branch.getPhone());

        int rows = db.update(TechFixDatabaseHelper.TABLE_BRANCHES, values,
                TechFixDatabaseHelper.COL_BRANCH_ID + "=?",
                new String[]{String.valueOf(branch.getBranchId())});
        db.close();
        return rows > 0;
    }

    public boolean deleteBranch(int branchId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(TechFixDatabaseHelper.TABLE_BRANCHES,
                TechFixDatabaseHelper.COL_BRANCH_ID + "=?",
                new String[]{String.valueOf(branchId)});
        db.close();
        return rows > 0;
    }

    public Branch getBranchById(int branchId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TechFixDatabaseHelper.TABLE_BRANCHES, null,
                TechFixDatabaseHelper.COL_BRANCH_ID + "=?",
                new String[]{String.valueOf(branchId)}, null, null, null);

        Branch branch = null;
        if (cursor.moveToFirst()) {
            branch = cursorToBranch(cursor);
        }
        cursor.close();
        db.close();
        return branch;
    }

    public List<Branch> getAllBranches() {
        List<Branch> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TechFixDatabaseHelper.TABLE_BRANCHES, null,
                null, null, null, null, TechFixDatabaseHelper.COL_BRANCH_NAME + " ASC");

        while (cursor.moveToNext()) {
            list.add(cursorToBranch(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    private Branch cursorToBranch(Cursor cursor) {
        Branch branch = new Branch();
        branch.setBranchId(cursor.getInt(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_BRANCH_ID)));
        branch.setName(cursor.getString(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_BRANCH_NAME)));
        branch.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_BRANCH_ADDRESS)));
        branch.setCity(cursor.getString(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_BRANCH_CITY)));
        branch.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_BRANCH_PHONE)));
        return branch;
    }
}