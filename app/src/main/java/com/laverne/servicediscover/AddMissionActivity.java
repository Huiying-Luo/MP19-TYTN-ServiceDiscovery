package com.laverne.servicediscover;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddMissionActivity extends AppCompatActivity {

    private CardView libraryBtn;
    private CardView educationBtn;
    private CardView parkBtn;
    private CardView museumBtn;
    private Button resetBtn;

    private static final int REQUEST_CODE = 2002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mission);

        configureUI();
        setupButtons();

    }

    // Back button in action bar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void configureUI() {
        setTitle("Add New Mission");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        libraryBtn = findViewById(R.id.add_library_card);
        educationBtn = findViewById(R.id.add_education_card);
        parkBtn = findViewById(R.id.add_park_card);
        museumBtn = findViewById(R.id.add_museum_card);
        resetBtn = findViewById(R.id.reset_button);
    }


    private void setupButtons() {
        libraryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextScreen("Library");
            }
        });

        educationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextScreen("Education");
            }
        });

        parkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextScreen("Park");
            }
        });

        museumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextScreen("Museum");
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddMissionActivity.this, QKidsActivity.class);
                startActivity(intent);
        }
        });
    }


    private void goToNextScreen(String category) {
        Intent intent = new Intent(AddMissionActivity.this, MissionListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
        //startActivityForResult(intent, REQUEST_CODE);
    }


}