package com.laverne.servicediscover;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.laverne.servicediscover.Adapter.MissionListRecyclerViewAdapter;
import com.laverne.servicediscover.Entity.Mission;
import com.laverne.servicediscover.ViewModel.MissionViewModel;

import java.util.ArrayList;
import java.util.List;

public class MissionListActivity extends AppCompatActivity implements MissionListRecyclerViewAdapter.OnMissionListener {

    private RecyclerView recyclerView;

    private MissionListRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private MissionViewModel missionViewModel;
    private String category;
    private List<Mission> missionList;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_list);
        setTitle("Add New Mission");

        intent = getIntent();
        category = intent.getStringExtra("category");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.add_mission_rv);

        initializeViewModel();
        initializeRecyclerView();
        missionList = new ArrayList<>();
        getAllMissionsByCategoryFromRoomDatabase();
    }

    // Back button in action bar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    private void initializeRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MissionListRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, 0));
    }


    private void initializeViewModel() {
        missionViewModel = new ViewModelProvider(this).get(MissionViewModel.class);
        missionViewModel.initializeVars(this.getApplication());

    }


    private void getAllMissionsByCategoryFromRoomDatabase() {

        missionViewModel.getAllNotAddedMissionsByCategory(category).observe(this, new Observer<List<Mission>>() {
            @Override
            public void onChanged(List<Mission> missions) {
                missionList = missions;
                adapter.setMissionList(missions);
            }
        });
    }


    @Override
    public void onMissionClick(final int position) {

        // create a alert dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(MissionListActivity.this);
        alert.setTitle("Add Mission");
        alert.setMessage("Are you sure to add " + missionList.get(position).getName() + " into your mission list?");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("AddMission", missionList.get(position).getName());
                Mission mission = missionList.get(position);
                mission.setStatus(1);
                missionViewModel.updateMission(mission);
                Intent intent = new Intent(MissionListActivity.this, MainActivity.class);
                intent.putExtra("goToMission", true);
                startActivity(intent);
                finish();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create().show();
    }
}