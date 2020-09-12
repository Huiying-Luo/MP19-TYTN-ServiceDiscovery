package com.laverne.servicediscover.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.laverne.servicediscover.AddMissionActivity;
import com.laverne.servicediscover.R;

public class InProgressMissionFragment extends Fragment {

    private Button addMissionButton;

    public InProgressMissionFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.inprogress_mission_fragment, container, false);


       addMissionButton = view.findViewById(R.id.btn_addMission);
       addMissionButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(), AddMissionActivity.class);
               startActivity(intent);
           }
       });

        return view;
    }

}
