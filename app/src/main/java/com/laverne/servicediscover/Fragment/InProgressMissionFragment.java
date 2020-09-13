package com.laverne.servicediscover.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.laverne.servicediscover.Adapter.InProgressMissionRecyclerViewAdapter;
import com.laverne.servicediscover.AddMissionActivity;
import com.laverne.servicediscover.Entity.Mission;
import com.laverne.servicediscover.IntroductionActivity;
import com.laverne.servicediscover.QAddressActivity;
import com.laverne.servicediscover.R;
import com.laverne.servicediscover.Utilities;
import com.laverne.servicediscover.ViewModel.MissionViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class InProgressMissionFragment extends Fragment {

    private Button addMissionButton;
    private RecyclerView recyclerView;

    private InProgressMissionRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private MissionViewModel missionViewModel;

    private List<Mission> missionList = new ArrayList<>();

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    public InProgressMissionFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.inprogress_mission_fragment, container, false);

        recyclerView = view.findViewById(R.id.inprogress_mission_rv);
        addMissionButton = view.findViewById(R.id.btn_addMission);

        initializeViewModel();
        initializeRecyclerView();
        setupAddMissionButton();

        getAllInProgressMissionsFromRoomDatabase();

        return view;
    }


    private void setupAddMissionButton() {
        // Check whether the user is first time to use
        final Boolean needQuestionnaire = getActivity().getSharedPreferences("App", MODE_PRIVATE).getBoolean("needQuestionnaire", true);
        addMissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (needQuestionnaire) {
                    Intent intent = new Intent(getActivity(), IntroductionActivity.class);
                    startActivity(intent);
                } else {
                    new CheckMissionsAsyncTask().execute();
                }
            }
        });
    }


    private class CheckMissionsAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            return missionViewModel.getAllNotAddedMissions().size();
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                // create a alert dialog
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Congrats!");
                alert.setMessage("You have added all the missions we recommended to you!\n Do you want to reset the preferences and get a new mission list?");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), IntroductionActivity.class);
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.create().show();
            } else {
                Intent intent = new Intent(getActivity(), AddMissionActivity.class);
                startActivity(intent);
            }
        }
    }


    private void initializeRecyclerView() {
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InProgressMissionRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
    }

    private void initializeViewModel() {
        missionViewModel = new ViewModelProvider(this).get(MissionViewModel.class);
        missionViewModel.initializeVars(getActivity().getApplication());
    }


    private void getAllInProgressMissionsFromRoomDatabase() {

        new ChangeMissionsDistanceAsyncTask().execute();
    }


    private class ChangeMissionsDistanceAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            missionList = missionViewModel.getAllInProgressMissions();
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            calculateDistances();

            missionViewModel.getAllMissionsByStatus(1).observe(getViewLifecycleOwner(), new Observer<List<Mission>>() {
                @Override
                public void onChanged(List<Mission> missions) {
                    adapter.setMissionList(missions);
                }

            });
        }
    }


    @SuppressLint("MissingPermission")
    private void calculateDistances() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(locationRequest, new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();


                    for(int i = 0; i < missionList.size(); i++) {
                        // distance from user location
                        float[] distance = new float[2];
                        Mission mission = missionList.get(i);
                        Location.distanceBetween(latitude, longitude, mission.getLatitude(),mission.getLongitude(), distance);
                        mission.setDistance(distance[0]);
                        missionViewModel.updateMission(mission);
                    }
                } else {
                    // show error alert
                    Utilities.showAlertDialogwithOkButton(getActivity(), "Error", "Something went wrong! Fail to get your current location.");
                    for(int i = 0; i < missionList.size(); i++) {
                        Mission mission = missionList.get(i);
                        mission.setDistance(0);
                        missionViewModel.updateMission(mission);
                    }
                }
            }
        }, Looper.getMainLooper());
    }

}
