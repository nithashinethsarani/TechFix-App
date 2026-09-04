package com.example.techfix_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.techfix_app.models.InventoryItem;

import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    private TechFixDatabaseHelper dbHelper;

    public InventoryDAO(Context context) {
        dbHelper = new TechFixDatabaseHelper(context);
    }

    public long addItem(InventoryItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TechFixDatabaseHelper.COL_ITEM_NAME, item.getItemName());
        values.put(TechFixDatabaseHelper.COL_ITEM_QUANTITY, item.getQuantity());
        values.put(TechFixDatabaseHelper.COL_ITEM_PRICE, item.getPrice());
        values.put(TechFixDatabaseHelper.COL_ITEM_BRANCH_ID, item.getBranchId());

        long id = db.insert(TechFixDatabaseHelper.TABLE_INVENTORY, null, values);
        db.close();
        return id;
    }

    public boolean updateItem(InventoryItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TechFixDatabaseHelper.COL_ITEM_NAME, item.getItemName());
        values.put(TechFixDatabaseHelper.COL_ITEM_QUANTITY, item.getQuantity());
        values.put(TechFixDatabaseHelper.COL_ITEM_PRICE, item.getPrice());
        values.put(TechFixDatabaseHelper.COL_ITEM_BRANCH_ID, item.getBranchId());

        int rows = db.update(TechFixDatabaseHelper.TABLE_INVENTORY, values,
                TechFixDatabaseHelper.COL_ITEM_ID + "=?",
                new String[]{String.valueOf(item.getItemId())});
        db.close();
        return rows > 0;
    }

    public boolean deleteItem(int itemId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(TechFixDatabaseHelper.TABLE_INVENTORY,
                TechFixDatabaseHelper.COL_ITEM_ID + "=?",
                new String[]{String.valueOf(itemId)});
        db.close();
        return rows > 0;
    }

    public InventoryItem getItemById(int itemId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TechFixDatabaseHelper.TABLE_INVENTORY, null,
                TechFixDatabaseHelper.COL_ITEM_ID + "=?",
                new String[]{String.valueOf(itemId)}, null, null, null);

        InventoryItem item = null;
        if (cursor.moveToFirst()) {
            item = cursorToItem(cursor);
        }
        cursor.close();
        db.close();
        return item;
    }

    public List<InventoryItem> getAllItems() {
        List<InventoryItem> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TechFixDatabaseHelper.TABLE_INVENTORY, null,
                null, null, null, null, TechFixDatabaseHelper.COL_ITEM_NAME + " ASC");

        while (cursor.moveToNext()) {
            list.add(cursorToItem(cursor));
        }
        cursor.close();
        db.close();
        return list;
    }

    private InventoryItem cursorToItem(Cursor cursor) {
        InventoryItem item = new InventoryItem();
        item.setItemId(cursor.getInt(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_ITEM_ID)));
        item.setItemName(cursor.getString(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_ITEM_NAME)));
        item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_ITEM_QUANTITY)));
        item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_ITEM_PRICE)));
        item.setBranchId(cursor.getInt(cursor.getColumnIndexOrThrow(TechFixDatabaseHelper.COL_ITEM_BRANCH_ID)));
        return item;
    }
}