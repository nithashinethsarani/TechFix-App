package com.example.techfix_app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TechFixDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "techfix.db";
    private static final int DATABASE_VERSION = 1;

    // ---- Table names ----
    public static final String TABLE_BRANCHES = "branches";
    public static final String TABLE_TECHNICIANS = "technicians";
    public static final String TABLE_INVENTORY = "inventory";
    public static final String TABLE_REPAIR_IMAGES = "repair_images";

    // ---- Branches columns ----
    public static final String COL_BRANCH_ID = "branch_id";
    public static final String COL_BRANCH_NAME = "name";
    public static final String COL_BRANCH_ADDRESS = "address";
    public static final String COL_BRANCH_CITY = "city";
    public static final String COL_BRANCH_PHONE = "phone";

    // ---- Technicians columns ----
    public static final String COL_TECH_ID = "technician_id";
    public static final String COL_TECH_NAME = "name";
    public static final String COL_TECH_SPECIALIZATION = "specialization";
    public static final String COL_TECH_PHONE = "phone";
    public static final String COL_TECH_BRANCH_ID = "branch_id";

    // ---- Inventory columns ----
    public static final String COL_ITEM_ID = "item_id";
    public static final String COL_ITEM_NAME = "item_name";
    public static final String COL_ITEM_QUANTITY = "quantity";
    public static final String COL_ITEM_PRICE = "price";
    public static final String COL_ITEM_BRANCH_ID = "branch_id";

    // ---- Repair images columns ----
    public static final String COL_IMAGE_ID = "image_id";
    public static final String COL_IMAGE_BRANCH_ID = "branch_id";
    public static final String COL_IMAGE_CATEGORY = "device_category";
    public static final String COL_IMAGE_PATH = "image_path";
    public static final String COL_IMAGE_CAPTION = "caption";
    public static final String COL_IMAGE_TIMESTAMP = "timestamp";

    public TechFixDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_BRANCHES + " (" +
                COL_BRANCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BRANCH_NAME + " TEXT, " +
                COL_BRANCH_ADDRESS + " TEXT, " +
                COL_BRANCH_CITY + " TEXT, " +
                COL_BRANCH_PHONE + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_TECHNICIANS + " (" +
                COL_TECH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TECH_NAME + " TEXT, " +
                COL_TECH_SPECIALIZATION + " TEXT, " +
                COL_TECH_PHONE + " TEXT, " +
                COL_TECH_BRANCH_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_TECH_BRANCH_ID + ") REFERENCES " +
                TABLE_BRANCHES + "(" + COL_BRANCH_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_INVENTORY + " (" +
                COL_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ITEM_NAME + " TEXT, " +
                COL_ITEM_QUANTITY + " INTEGER, " +
                COL_ITEM_PRICE + " REAL, " +
                COL_ITEM_BRANCH_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_ITEM_BRANCH_ID + ") REFERENCES " +
                TABLE_BRANCHES + "(" + COL_BRANCH_ID + "))");

        db.execSQL("CREATE TABLE " + TABLE_REPAIR_IMAGES + " (" +
                COL_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_IMAGE_BRANCH_ID + " INTEGER, " +
                COL_IMAGE_CATEGORY + " TEXT, " +
                COL_IMAGE_PATH + " TEXT, " +
                COL_IMAGE_CAPTION + " TEXT, " +
                COL_IMAGE_TIMESTAMP + " INTEGER, " +
                "FOREIGN KEY(" + COL_IMAGE_BRANCH_ID + ") REFERENCES " +
                TABLE_BRANCHES + "(" + COL_BRANCH_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPAIR_IMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TECHNICIANS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCHES);
        onCreate(db);
    }
}