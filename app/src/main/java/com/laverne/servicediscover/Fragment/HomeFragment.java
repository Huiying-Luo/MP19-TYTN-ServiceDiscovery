package com.laverne.servicediscover.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.navigation.NavigationView;
import com.laverne.servicediscover.R;

public class HomeFragment extends Fragment {

    private TextView completedTextView;
    private TextView inprogressTextView;
    private TextView viewBtn;
    private TextView percentageTextView;
    private CardView missionCardView;

    private CardView libraryBtn;
    private CardView healthBtn;
    private CardView educationBtn;
    private CardView workBtn;
    private LinearLayout publicServiceLayout;
    private NavigationView navigationView;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        configureUI(view);

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new MissionFragment());
                navigationView.setCheckedItem(R.id.mission);
                getActivity().setTitle("My Mission");
            }
        });

        libraryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new LibraryFragment());
                navigationView.setCheckedItem(R.id.library);
                getActivity().setTitle("Library");
            }
        });


        return view;
    }


    private void configureUI(View view) {
        completedTextView = view.findViewById(R.id.completed_number);
        inprogressTextView = view.findViewById(R.id.inprogress_number);
        viewBtn = view.findViewById(R.id.home_view_btn);
        libraryBtn = view.findViewById(R.id.cardView_library);
        healthBtn = view.findViewById(R.id.cardView_health);
        educationBtn = view.findViewById(R.id.cardView_edu);
        workBtn = view.findViewById(R.id.cardView_work);
        publicServiceLayout = view.findViewById(R.id.services_layout);
        navigationView = getActivity().findViewById(R.id.navigationView);
    }




    private void replaceFragment(Fragment nextFragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, nextFragment);
        fragmentTransaction.commit();
    }


}
