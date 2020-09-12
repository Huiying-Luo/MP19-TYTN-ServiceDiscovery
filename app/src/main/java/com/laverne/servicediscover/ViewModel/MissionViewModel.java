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

    public LiveData<List<Mission>> getAllMissionsByCategory(String category) {
        return mRepository.getAllMissionsByCategory(category);
    }

    public LiveData<List<Mission>> getAllMissionsByStatus(int status) {
        return mRepository.getAllMissionsByStatus(status);
    }

    public List<Mission> getAllMission() {
        return mRepository.getAllMissions();
    }

    public void insert(Mission mission) {
        mRepository.insert(mission);
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
