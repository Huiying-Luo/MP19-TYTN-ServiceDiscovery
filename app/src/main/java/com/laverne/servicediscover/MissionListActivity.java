package com.laverne.servicediscover;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.laverne.servicediscover.Adapter.MissionListRecyclerViewAdapter;
import com.laverne.servicediscover.Entity.Mission;
import com.laverne.servicediscover.ViewModel.MissionViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MissionListActivity extends AppCompatActivity implements MissionListRecyclerViewAdapter.OnMissionListener {

    private static final int REQUEST_SCHOOL_FILTER_CODE = 1011;
    private static final int REQUEST_MUSEUM_FILTER_CODE = 1022;

    private TextView titleTextView;
    private RecyclerView recyclerView;
    private Spinner filterSpinner;
    private ArrayAdapter<String> filterSpinnerAdapter;
    private MissionListRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private MissionViewModel missionViewModel;
    private int category;
    private List<Mission> missionList;
    private Intent intent;
    private FloatingActionButton fab;
    private ArrayList<String> selectedMuseumTypes;
    private ArrayList<String> selectedSchoolTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_list);
        setTitle("Add New Mission");

        intent = getIntent();
        category = intent.getIntExtra("category", 4);

        String[] categories = {"Library", "School", "Park", "Museum", "Mission"};
        titleTextView = findViewById(R.id.add_mission_title);
        titleTextView.setText("Select a " + categories[category] + " :");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.add_mission_rv);

        initializeViewModel();
        initializeRecyclerView();

        getAllMissionsByCategoryFromRoomDatabase();


    }


    // Get Selected Museum/School Types
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MUSEUM_FILTER_CODE) {
            if (resultCode == RESULT_OK) {
                selectedMuseumTypes = data.getStringArrayListExtra("selectedChipData");
                adapter.filterByMuseumTypes(selectedMuseumTypes);
            }
        } else if (requestCode == REQUEST_SCHOOL_FILTER_CODE) {
            if (resultCode == RESULT_OK) {
                selectedSchoolTypes = data.getStringArrayListExtra("selectedChipData");
                adapter.filterBySchoolTypes(selectedSchoolTypes);
            }
        }
    }

    // Back button in action bar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Animatoo.animateSlideRight(this);
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
        missionList = new ArrayList<>();

        new ChangeMissionsDistanceAsyncTask().execute();
    }


    private void calculateDistances() {
        SharedPreferences sharedPref = getSharedPreferences("User", MODE_PRIVATE);
        double latitude = sharedPref.getFloat("latitude", 0);
        double longitude = sharedPref.getFloat("longitude", 0);
        for (int i = 0; i < missionList.size(); i++) {
            // distance from user location
            float[] distance = new float[2];
            Mission mission = missionList.get(i);
            Location.distanceBetween(latitude, longitude, mission.getLatitude(), mission.getLongitude(), distance);
            mission.setDistance(distance[0]);
            missionViewModel.updateMission(mission);
        }
    }

    private class ChangeMissionsDistanceAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            missionList.addAll(missionViewModel.getAllNotAddedMissionsListByCategory(category));
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
/*
            // Education need spinner for filter
            if (category == 1) {
                filterSpinner = findViewById(R.id.mission_filter_spinner);
                filterSpinner.setVisibility(View.VISIBLE);
                configureFilterSpinner();
            }
 */
            // Education & Museum has floating action button for filter
            if (category == 3 || category == 1) {
                configureFloatingActionButton();
            }

            calculateDistances();
            missionList.removeAll(missionList);
            missionViewModel.getAllNotAddedMissionsByCategory(category).observe(MissionListActivity.this, new Observer<List<Mission>>() {
                @Override
                public void onChanged(List<Mission> missions) {
                    missionList = missions;
                    adapter.setMissionList(missions);
                }

            });
        }
    }


    private void configureFloatingActionButton() {
        fab = findViewById(R.id.mission_fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MissionListActivity.this, FilterActivity.class);
                if (category == 3) {
                    intent.putExtra("filterType", 1);
                    intent.putStringArrayListExtra("selectedTypes", selectedMuseumTypes);
                    startActivityForResult(intent, REQUEST_MUSEUM_FILTER_CODE);
                } else {
                    intent.putExtra("filterType", 0);
                    intent.putStringArrayListExtra("selectedTypes", selectedSchoolTypes);
                    startActivityForResult(intent, REQUEST_SCHOOL_FILTER_CODE);
                }
            }
        });
    }

/*
    private void configureFilterSpinner() {
        String[] options = new String[]{"All", "Primary School", "Secondary School", "Special School", "Adult English Program"};
        final List<String> filterList = new ArrayList<String>(Arrays.asList(options));
        filterSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, filterList);

        filterSpinner.setAdapter(filterSpinnerAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.filterBySchoolType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

*/
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
                Animatoo.animateInAndOut(MissionListActivity.this);
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