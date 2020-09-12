package com.laverne.servicediscover.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.laverne.servicediscover.Entity.Mission;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
@Dao
public interface MissionDAO {

    @Query("SELECT * FROM mission WHERE category = :category")
    LiveData<List<Mission>> getAllMissionByCategory(String category);

    @Query("SELECT * FROM mission WHERE status = :status")
    LiveData<List<Mission>> getAllMissionByStatus(int status);

    @Query("SELECT * FROM mission")
    List<Mission> getAllMissions();

    @Insert
    void insertAll(Mission... missions);

    @Insert
    long insert(Mission mission);

    @Delete
    void delete(Mission mission);

    @Query("DELETE FROM mission")
    void deleteAll();
}

