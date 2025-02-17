package com.laverne.servicediscover.ViewModel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.laverne.servicediscover.Entity.Mission;
import com.laverne.servicediscover.Repository.MissionRepository;

import java.util.List;

public class MissionViewModel extends ViewModel {

    private MissionRepository mRepository;
    private MutableLiveData<List<Mission>> allMissions;


    public MissionViewModel() {
        allMissions = new MutableLiveData<>();
    }


    public void initializeVars(Application application) {
        mRepository = new MissionRepository(application);
    }


    public LiveData<List<Mission>> getAllNotAddedMissionsByCategory(int category) {
        return mRepository.getAllNotAddedMissionsByCategory(category);
    }


    public LiveData<List<Mission>> getAllMissionsByStatus(int status) {
        return mRepository.getAllMissionsByStatus(status);
    }


    public List<Mission> getAllNotAddedMissions() {
        return mRepository.getAllNotAddedMissions();
    }


    public List<Mission> getAllNotAddedMissionsListByCategory(int category) {
        return mRepository.getAllNotAddedMissionsListByCategory(category);
    }


    public List<Mission> getAllInProgressMissions() {
        return mRepository.getAllInProgressMissions();
    }


    public List<Mission> getAllCompletedMissions() {
        return mRepository.getAllCompletedMissions();
    }


    public Mission findMissionByID(int id) {
        return mRepository.findMissionByID(id);
    }


    public Mission findMission(int category, String name, String address, double latitude, double longitude) {
        return mRepository.findMission(category, name, address, latitude, longitude);
    }


    public void insert(Mission mission) {
        mRepository.insert(mission);
    }


    public void updateMission(Mission mission) {
        mRepository.updateMission(mission);
    }


    public void insertAll(Mission... missions) {
        mRepository.insertAll(missions);
    }


    public void delete(Mission mission) {
        mRepository.delete(mission);
    }


    public void deleteAll() {
        mRepository.deleteAll();
    }



}
