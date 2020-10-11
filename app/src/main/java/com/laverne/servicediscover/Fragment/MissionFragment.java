package com.laverne.servicediscover.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.laverne.servicediscover.Adapter.ViewPagerAdapter;
import com.laverne.servicediscover.R;

public class MissionFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int pageIndex = 8888;
    private FloatingActionButton fab;


    public MissionFragment() {}
    public MissionFragment(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.mission_fragment, container, false);

        fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        tabLayout = view.findViewById(R.id.tabbar);
        viewPager = view.findViewById(R.id.viewpager);

        getTabs();

        if (pageIndex == 0 || pageIndex == 1) {
            selectPage();
        }

        return view;
    }


    private void getTabs() {

        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), 0);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPagerAdapter.addFragment(new InProgressMissionFragment(), "IN PROGRESS");
                viewPagerAdapter.addFragment(new CompletedMissionFragment(), "COMPLETED");

                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }


    private void selectPage () {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(pageIndex, true);
                tabLayout.setScrollPosition(pageIndex, 0f, true);
            }
        });
    }
}
