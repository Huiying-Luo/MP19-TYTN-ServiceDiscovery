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
    private List<Mission> missionList;
    private Mission mission;


    public MissionRepository(Application application) {
        MissionDatabase db = MissionDatabase.getInstance(application);
        dao = db.missionDAO();
    }


    public LiveData<List<Mission>> getAllNotAddedMissionsByCategory(final int category) {
        allMissions = dao.getAllNotAddedMissionsByCategory(category);
        return allMissions;
    }


    public LiveData<List<Mission>> getAllMissionsByStatus(final int status) {
        allMissions = dao.getAllMissionByStatus(status);
        return allMissions;
    }


    public List<Mission> getAllNotAddedMissions() {
        return missionList = dao.getAllNotAddedMissions();
    }

    public List<Mission> getAllNotAddedMissionsListByCategory(int category) {
        return missionList = dao.getAllNotAddedMissionsListByCategory(category);
    }


    public List<Mission> getAllInProgressMissions() {
        return dao.getAllInProgressMissions();
    }


    public List<Mission> getAllCompletedMissions() {
        return missionList = dao.getAllCompletedMissions();
    }


    public Mission findMissionByID(int id) {
        return dao.findMissionByID(id);
    }


    public Mission findMission(int category, String name, String address, double latitude, double longitude) {
        return dao.findMission(category, name, address, latitude, longitude);
    }


    public void setMission(Mission mission) {
        this.mission = mission;
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


    public void updateMission(final Mission mission) {
        MissionDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.updateMission(mission);
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
