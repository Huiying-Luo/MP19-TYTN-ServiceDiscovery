package com.laverne.servicediscover.Fragment;

import android.os.AsyncTask;
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
import androidx.lifecycle.ViewModelProvider;


import com.google.android.material.navigation.NavigationView;
import com.laverne.servicediscover.R;
import com.laverne.servicediscover.ViewModel.MissionViewModel;

public class HomeFragment extends Fragment {

    private TextView completedTextView, inprogressTextView, viewBtn;
    private CardView libraryBtn, parkBtn, educationBtn, museumBtn, inprogressBtn, completeBtn;
    private LinearLayout publicServiceLayout;
    private NavigationView navigationView;

    private MissionViewModel missionViewModel;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        configureUI(view);

        // initialize view model
        missionViewModel = new ViewModelProvider(this).get(MissionViewModel.class);
        missionViewModel.initializeVars(getActivity().getApplication());

        new CheckInProgressMissionsAsyncTask().execute();
        new CheckCompletedMissionsAsyncTask().execute();

        setUpButtons();

        return view;
    }


    private void setUpButtons() {
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMissionScreen(0);
            }
        });

        libraryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ServiceFragment(0));
                navigationView.setCheckedItem(R.id.library);
                getActivity().setTitle("Library");
            }
        });

        educationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ServiceFragment(1));
                navigationView.setCheckedItem(R.id.education);
                getActivity().setTitle("Education");
            }
        });


        parkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ServiceFragment(2));
                navigationView.setCheckedItem(R.id.park);
                getActivity().setTitle("Park");
            }
        });


        museumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ServiceFragment(3));
                navigationView.setCheckedItem(R.id.museum);
                getActivity().setTitle("Museum");
            }
        });



        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMissionScreen(1);
            }
        });

        inprogressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMissionScreen(0);
            }
        });
    }


    private void goToMissionScreen(int pageIndex) {
        replaceFragment(new MissionFragment(pageIndex));
        navigationView.setCheckedItem(R.id.mission);
        getActivity().setTitle("My Mission");
    }


    private void configureUI(View view) {
        completedTextView = view.findViewById(R.id.completed_number);
        inprogressTextView = view.findViewById(R.id.inprogress_number);
        viewBtn = view.findViewById(R.id.home_view_btn);
        libraryBtn = view.findViewById(R.id.cardView_library);
        parkBtn = view.findViewById(R.id.cardView_park);
        educationBtn = view.findViewById(R.id.cardView_edu);
        museumBtn = view.findViewById(R.id.cardView_museum);
        publicServiceLayout = view.findViewById(R.id.services_layout);
        navigationView = getActivity().findViewById(R.id.navigationView);
        completeBtn = view.findViewById(R.id.cardView_completed);
        inprogressBtn = view.findViewById(R.id.cardView_inprogress);
    }


    private class CheckCompletedMissionsAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            return missionViewModel.getAllCompletedMissions().size();
        }

        @Override
        protected void onPostExecute(Integer result) {
            completedTextView.setText(String.valueOf(result));
        }
    }

    private class CheckInProgressMissionsAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            return missionViewModel.getAllInProgressMissions().size();
        }

        @Override
        protected void onPostExecute(Integer result) {
            inprogressTextView.setText(String.valueOf(result));
        }
    }



    private void replaceFragment(Fragment nextFragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, nextFragment);
        fragmentTransaction.commit();
    }


}
