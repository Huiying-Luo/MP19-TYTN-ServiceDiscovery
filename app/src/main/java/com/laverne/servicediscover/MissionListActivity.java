package com.laverne.servicediscover;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MissionListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_list);
        setTitle("Add New Mission");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Back button in action bar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}