package com.example.techfix_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.techfix_app.models.RepairHistory;

import java.util.ArrayList;
import java.util.List;

public class RepairHistoryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TechFixLocal.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_REPAIR_HISTORY = "repair_history";
    public static final String COLUMN_REPAIR_ID = "repairId";
    public static final String COLUMN_APPOINTMENT_ID = "appointmentId";
    public static final String COLUMN_DEVICE_NAME = "deviceName";
    public static final String COLUMN_SERVICE_NAME = "serviceName";
    public static final String COLUMN_FINAL_PRICE = "finalPrice";
    public static final String COLUMN_COMPLETED_DATE = "completedDate";
    public static final String COLUMN_STATUS = "status";

    public RepairHistoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_REPAIR_HISTORY + " ("
                + COLUMN_REPAIR_ID + " TEXT PRIMARY KEY, "
                + COLUMN_APPOINTMENT_ID + " TEXT, "
                + COLUMN_DEVICE_NAME + " TEXT, "
                + COLUMN_SERVICE_NAME + " TEXT, "
                + COLUMN_FINAL_PRICE + " REAL, "
                + COLUMN_COMPLETED_DATE + " TEXT, "
                + COLUMN_STATUS + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPAIR_HISTORY);
        onCreate(db);
    }

    // Insert or update repair record
    public void saveRepairHistory(RepairHistory repair) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_REPAIR_ID, repair.getRepairId());
        values.put(COLUMN_APPOINTMENT_ID, repair.getAppointmentId());
        values.put(COLUMN_DEVICE_NAME, repair.getDeviceName());
        values.put(COLUMN_SERVICE_NAME, repair.getServiceName());
        values.put(COLUMN_FINAL_PRICE, repair.getFinalPrice());
        values.put(COLUMN_COMPLETED_DATE, repair.getCompletedDate());
        values.put(COLUMN_STATUS, repair.getStatus());

        // stops creating duplicate records
        db.insertWithOnConflict(
                TABLE_REPAIR_HISTORY,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
        db.close();
    }

    // Retrieve all local repair history records
    public List<RepairHistory> getAllRepairHistory() {
        List<RepairHistory> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_REPAIR_HISTORY + " ORDER BY " + COLUMN_COMPLETED_DATE + " DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                RepairHistory item = new RepairHistory();
                item.setRepairId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REPAIR_ID)));
                item.setAppointmentId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPOINTMENT_ID)));
                item.setDeviceName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEVICE_NAME)));
                item.setServiceName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_NAME)));
                item.setFinalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FINAL_PRICE)));
                item.setCompletedDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED_DATE)));
                item.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));

                historyList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return historyList;
    }
}