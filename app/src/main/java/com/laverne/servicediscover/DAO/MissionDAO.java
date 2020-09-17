package com.laverne.servicediscover.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.laverne.servicediscover.Entity.Mission;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MissionDAO {

    @Query("SELECT * FROM mission WHERE status = :status ORDER BY distance")
    LiveData<List<Mission>> getAllMissionByStatus(int status);

    @Query("SELECT * FROM mission WHERE status = 0")
    List<Mission> getAllNotAddedMissions();

    @Query("SELECT * FROM mission WHERE status = 1")
    List<Mission> getAllInProgressMissions();

    @Query("SELECT * FROM mission WHERE status = 2")
    List<Mission> getAllCompletedMissions();

    @Query("SELECT * FROM mission WHERE status = 0 AND category = :category")
    LiveData<List<Mission>> getAllNotAddedMissionsByCategory(String category);

    @Query("SELECT * FROM mission WHERE uid = :id LIMIT 1")
    Mission findMissionByID(int id);


    @Insert
    void insertAll(Mission...missions);

    @Insert
    long insert(Mission mission);

    @Update(onConflict = REPLACE)
    void updateMission(Mission mission);

    @Delete
    void delete(Mission mission);

    @Query("DELETE FROM mission")
    void deleteAll();
}

