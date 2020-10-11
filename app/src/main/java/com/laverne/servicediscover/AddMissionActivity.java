package com.laverne.servicediscover;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class AddMissionActivity extends AppCompatActivity {

    private CardView libraryBtn;
    private CardView educationBtn;
    private CardView parkBtn;
    private CardView museumBtn;
    private Button resetBtn;

    private static final int REQUEST_INTRO = 1001;


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
        Animatoo.animateSlideRight(this);
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
                goToNextScreen(0);
            }
        });

        educationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextScreen(1);
            }
        });

        parkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextScreen(2);
            }
        });

        museumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextScreen(3);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a alert dialog
                AlertDialog.Builder alert = new AlertDialog.Builder(AddMissionActivity.this);
                alert.setTitle("Warning");
                alert.setMessage("Reset the preferences will also \nclear all the missions in your list\n and provide new mission recommendation.\n Are you sure to reset?");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AddMissionActivity.this, IntroductionActivity.class);
                        startActivityForResult(intent, REQUEST_INTRO);

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
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INTRO) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    private void goToNextScreen(int category) {
        Intent intent = new Intent(AddMissionActivity.this, MissionListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);

       Animatoo.animateSlideLeft(this);
    }


}