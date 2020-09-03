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

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class HomeFragment extends Fragment implements MaterialIntroListener {

    private TextView statusTextView;
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

        libraryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new LibraryFragment());
                navigationView.setCheckedItem(R.id.library);
                getActivity().setTitle("Library");
            }
        });

        showIntro(statusTextView, "status", "This show the status of your weekly mission!", Focus.ALL, ShapeType.CIRCLE);

        return view;
    }


    private void configureUI(View view) {
        statusTextView = view.findViewById(R.id.home_status);
        percentageTextView = view.findViewById(R.id.home_percentage);
        missionCardView = view.findViewById(R.id.cardView_mission);
        libraryBtn = view.findViewById(R.id.cardView_library);
        healthBtn = view.findViewById(R.id.cardView_health);
        educationBtn = view.findViewById(R.id.cardView_edu);
        workBtn = view.findViewById(R.id.cardView_work);
        publicServiceLayout = view.findViewById(R.id.services_layout);
        navigationView = getActivity().findViewById(R.id.navigationView);
    }

    // Check whether the user is first time to use

    private void showIntro(View view, String id, String text, Focus focusType, ShapeType shapeType) {
        new MaterialIntroView.Builder(getActivity())
                .enableDotAnimation(false)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(focusType)
                .setDelayMillis(200)
                .enableFadeAnimation(true)
                .performClick(true)
                .setInfoText(text)
                .setShape(shapeType)
                .setTarget(view)
                .setInfoTextSize(19)
                .setUsageId(id) //THIS SHOULD BE UNIQUE ID
                .setListener(this)
                .show();
    }

    @Override
    public void onUserClicked(String materialIntroViewId) {
        switch (materialIntroViewId) {
            case "status":
                showIntro(percentageTextView, "percentage", "This show your percentage of your completed tasks in this week", Focus.ALL, ShapeType.CIRCLE);
                break;
            case "percentage":
                showIntro(missionCardView, "mission","You can click here to check you weekly mission detail.", Focus.ALL, ShapeType.RECTANGLE);
                break;
            case "mission":
                showIntro(publicServiceLayout, "services","There are 4 categories of public services you can check out!", Focus.NORMAL, ShapeType.CIRCLE);
                break;
            default:
                break;
        }
    }


    private void replaceFragment(Fragment nextFragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, nextFragment);
        fragmentTransaction.commit();
    }


}
