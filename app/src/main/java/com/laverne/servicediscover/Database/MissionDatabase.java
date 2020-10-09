package com.laverne.servicediscover.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.laverne.servicediscover.DAO.MissionDAO;
import com.laverne.servicediscover.Entity.Mission;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Mission.class}, version = 2, exportSchema = false)
public  abstract  class MissionDatabase extends RoomDatabase {

    public abstract MissionDAO missionDAO();

    private static MissionDatabase INSTANCE;

    //we create an ExecutorService with a fixed thread pool so we can later run database operations asynchronously on a background thread.
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public static synchronized  MissionDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MissionDatabase.class, "MissionDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
