package com.laverne.servicediscover;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    private Chip chipArt, chipAutomotive, chipAviation, chipEthnic, chipHistory, chipFarm,
            chipFashion, chipForestry, chipIndustry, chipLiterary, chipMaritime, chipMedia,
            chipMilitary, chipMusic, chipOpenAir, chipPrison, chipRailway, chipSports, chipTechnology;

    private ArrayList<Chip> chips;

    private Button applyBtn;
    private ToggleButton toggleButton;
    private ArrayList<String> selectedChipData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

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
            if (selectedMuseumTypes.size() == 19) {
                toggleButton.setChecked(true);
            }
            for (int i = 0; i < chips.size(); i++) {
                if (selectedMuseumTypes.contains(chips.get(i).getText().toString())) {
                    chips.get(i).setChecked(true);
                }
            }
        } else {
            // default select all
            toggleButton.setChecked(true);
        }
    }


    private void configureView() {
        chips = new ArrayList<>();
        chipArt = findViewById(R.id.chipArt);
        chips.add(chipArt);
        chipAutomotive = findViewById(R.id.chipAutomotive);
        chips.add(chipAutomotive);
        chipAviation = findViewById(R.id.chipAviation);
        chips.add(chipAviation);
        chipEthnic = findViewById(R.id.chipEthnic);
        chips.add(chipEthnic);
        chipHistory = findViewById(R.id.chipHistory);
        chips.add(chipHistory);
        chipFarm = findViewById(R.id.chipFarm);
        chips.add(chipFarm);
        chipFashion = findViewById(R.id.chipFashion);
        chips.add(chipFashion);
        chipForestry = findViewById(R.id.chipForestry);
        chips.add(chipForestry);
        chipIndustry = findViewById(R.id.chipIndustry);
        chips.add(chipIndustry);
        chipLiterary = findViewById(R.id.chipLiterary);
        chips.add(chipLiterary);
        chipMaritime = findViewById(R.id.chipMaritime);
        chips.add(chipMaritime);
        chipMedia = findViewById(R.id.chipMedia);
        chips.add(chipMedia);
        chipMilitary = findViewById(R.id.chipMilitary);
        chips.add(chipMilitary);
        chipMusic = findViewById(R.id.chipMusic);
        chips.add(chipMusic);
        chipOpenAir = findViewById(R.id.chipOpenAir);
        chips.add(chipOpenAir);
        chipPrison = findViewById(R.id.chipPrison);
        chips.add(chipPrison);
        chipRailway = findViewById(R.id.chipRailway);
        chips.add(chipRailway);
        chipSports = findViewById(R.id.chipSports);
        chips.add(chipSports);
        chipTechnology = findViewById(R.id.chipTechnology);
        chips.add(chipTechnology);

        applyBtn = findViewById(R.id.filter_apply_btn);
        toggleButton = findViewById(R.id.filter_toggleButton);
    }


    private void setUpButton() {
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = getIntent();
                returnIntent.putStringArrayListExtra("selectedChipData", selectedChipData);
                setResult(RESULT_OK, returnIntent);
                finish();
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
}