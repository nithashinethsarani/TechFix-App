package com.example.techfix_app.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface RepairDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveRepair(RepairEntity repair);

    @Query("SELECT * FROM local_repairs WHERE repairId = :id")
    RepairEntity getLocalRepair(String id);
}