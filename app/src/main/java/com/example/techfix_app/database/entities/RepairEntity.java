package com.example.techfix_app.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "local_repairs")
public class RepairEntity {
    @PrimaryKey
    @NonNull
    public String repairId;
    public String status;
    public double totalAmount;

    public RepairEntity(@NonNull String repairId, String status, double totalAmount) {
        this.repairId = repairId;
        this.status = status;
        this.totalAmount = totalAmount;
    }
}