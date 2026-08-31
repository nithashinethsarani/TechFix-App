package com.example.techfix_app.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.techfix_app.database.entities.RepairEntity;

@Dao
public interface RepairDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveRepair(RepairEntity repair);

    @Query("SELECT * FROM repairs WHERE repairId = :repairId LIMIT 1")
    RepairEntity getLocalRepair(String repairId);

    @Query("DELETE FROM repairs")
    void deleteAllRepairs();
}