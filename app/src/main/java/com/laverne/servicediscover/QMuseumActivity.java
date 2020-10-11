package com.laverne.servicediscover;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class QMuseumActivity extends AppCompatActivity {

    private Chip chipArt, chipAutomotive, chipAviation, chipEthnic, chipHistory, chipFarm,
            chipFashion, chipForestry, chipIndustry, chipLiterary, chipMaritime, chipMedia,
            chipMilitary, chipMusic, chipOpenAir, chipPrison, chipRailway, chipSports, chipTechnology;

    private ArrayList<Chip> chips;

    private Button nextBtn;
    private ToggleButton toggleButton;
    private ArrayList<String> selectedChipData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_museum);

        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configureView();

        selectedChipData = new ArrayList<>();

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedChipData.add(buttonView.getText().toString());
                } else {
                    selectedChipData.remove(buttonView.getText().toString());
                }
            }
        };

        // set checked changed listener
        for (int i = 0; i < chips.size(); i++) {
            chips.get(i).setOnCheckedChangeListener(checkedChangeListener);
        }

        setUpButton();

        Intent intent = getIntent();
        ArrayList<String> selectedMuseumTypes = intent.getStringArrayListExtra("selectedMuseumTypes");
        if (selectedMuseumTypes != null) {
            for (int i = 0; i < chips.size(); i++) {
                if (selectedMuseumTypes.contains(chips.get(i).getText().toString())) {
                    chips.get(i).setChecked(true);
                }
            }
        }
    }


    private void configureView() {
        chips = new ArrayList<>();
        chipArt = findViewById(R.id.q_chipArt);
        chips.add(chipArt);
        chipAutomotive = findViewById(R.id.q_chipAutomotive);
        chips.add(chipAutomotive);
        chipAviation = findViewById(R.id.q_chipAviation);
        chips.add(chipAviation);
        chipEthnic = findViewById(R.id.q_chipEthnic);
        chips.add(chipEthnic);
        chipHistory = findViewById(R.id.q_chipHistory);
        chips.add(chipHistory);
        chipFarm = findViewById(R.id.q_chipFarm);
        chips.add(chipFarm);
        chipFashion = findViewById(R.id.q_chipFashion);
        chips.add(chipFashion);
        chipForestry = findViewById(R.id.q_chipForestry);
        chips.add(chipForestry);
        chipIndustry = findViewById(R.id.q_chipIndustry);
        chips.add(chipIndustry);
        chipLiterary = findViewById(R.id.q_chipLiterary);
        chips.add(chipLiterary);
        chipMaritime = findViewById(R.id.q_chipMaritime);
        chips.add(chipMaritime);
        chipMedia = findViewById(R.id.q_chipMedia);
        chips.add(chipMedia);
        chipMilitary = findViewById(R.id.q_chipMilitary);
        chips.add(chipMilitary);
        chipMusic = findViewById(R.id.q_chipMusic);
        chips.add(chipMusic);
        chipOpenAir = findViewById(R.id.q_chipOpenAir);
        chips.add(chipOpenAir);
        chipPrison = findViewById(R.id.q_chipPrison);
        chips.add(chipPrison);
        chipRailway = findViewById(R.id.q_chipRailway);
        chips.add(chipRailway);
        chipSports = findViewById(R.id.q_chipSports);
        chips.add(chipSports);
        chipTechnology = findViewById(R.id.q_chipTechnology);
        chips.add(chipTechnology);

        nextBtn = findViewById(R.id.question_museum_next_btn);
        toggleButton = findViewById(R.id.q_filter_toggleButton);
    }


    private void setUpButton() {
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMuseumPref();
                Intent intent = new Intent(QMuseumActivity.this, QAddressActivity.class);
                startActivity(intent);
                Animatoo.animateSlideLeft(QMuseumActivity.this);
            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for(int i = 0; i < chips.size(); i++) {
                        chips.get(i).setChecked(true);
                    }
                } else {
                    for(int i = 0; i < chips.size(); i++) {
                        chips.get(i).setChecked(false);
                    }
                }
            }
        });
    }


    private void setMuseumPref() {
        SharedPreferences sharedPref = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putInt("museumTypeSize", selectedChipData.size());
        for (int i = 0; i < selectedChipData.size(); i++) {
            spEditor.putString("museumType" + i, selectedChipData.get(i));
        }
        spEditor.apply();
    }


    // Back button in action bar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Animatoo.animateSlideRight(this);
        return true;
    }
}