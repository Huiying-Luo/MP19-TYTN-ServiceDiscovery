package com.laverne.servicediscover.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.laverne.servicediscover.Adapter.CompletedMissionRecyclerViewAdapter;
import com.laverne.servicediscover.Adapter.InProgressMissionRecyclerViewAdapter;
import com.laverne.servicediscover.Entity.Mission;
import com.laverne.servicediscover.R;
import com.laverne.servicediscover.ViewModel.MissionViewModel;

import java.util.List;

public class CompletedMissionFragment extends Fragment {

    private RecyclerView recyclerView;

    private CompletedMissionRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private MissionViewModel missionViewModel;

    private List<Mission> missionList;

    public CompletedMissionFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.completed_mission_fragment, container, false);

        recyclerView = view.findViewById(R.id.completed_mission_rv);

        initializeRecyclerView();

        getAllCompletedMissionsFromRoomDatabase();
        return view;
    }



    private void initializeRecyclerView() {
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CompletedMissionRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
    }


    private void getAllCompletedMissionsFromRoomDatabase() {
        // initialize view model
        missionViewModel = new ViewModelProvider(this).get(MissionViewModel.class);
        missionViewModel.initializeVars(getActivity().getApplication());

        missionViewModel.getAllMissionsByStatus(2).observe(getViewLifecycleOwner(), new Observer<List<Mission>>() {
            @Override
            public void onChanged(List<Mission> missions) {
                adapter.setMissionList(missions);
            }
        });


    }

}
