package com.laverne.servicediscover.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.laverne.servicediscover.DAO.MissionDAO;
import com.laverne.servicediscover.Database.MissionDatabase;
import com.laverne.servicediscover.Entity.Mission;

import java.util.List;

public class MissionRepository {
    private MissionDAO dao;
    private LiveData<List<Mission>> allMissions;
    private Mission mission;

    public MissionRepository(Application application) {
        MissionDatabase db = MissionDatabase.getInstance(application);
        dao = db.missionDAO();
    }


    public LiveData<List<Mission>> getAllMissionsByCategory(final String category) {
        allMissions = dao.getAllMissionByCategory(category);
        return allMissions;
    }

    public LiveData<List<Mission>> getAllMissionsByStatus(final int status) {
        allMissions = dao.getAllMissionByStatus(status);
        return allMissions;
    }

    public List<Mission> getAllMissions() {

        return dao.getAllMissions();
    }


    public void insert(final Mission mission) {
        MissionDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.insert(mission);
            }
        });
    }

    public void insertAll(final Mission... missions) {
        MissionDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.insertAll(missions);
            }
        });
    }


    public void delete(final Mission mission) {
        MissionDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.delete(mission);
            }
        });
    }


    public void deleteAll() {
        MissionDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.deleteAll();
            }
        });
    }


}
