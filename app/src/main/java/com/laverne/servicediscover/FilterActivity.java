package com.laverne.servicediscover;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity {

    private Chip chipArt, chipAutomotive, chipAviation, chipEthnic, chipHistory, chipFarm,
                chipFashion, chipForestry, chipIndustry, chipLiterary, chipMaritime, chipMedia,
                chipMilitary, chipMusic, chipOpenAir, chipPrison, chipRailway, museumChipsports, chipTechnology,
                chipPrimarySchool, chipSecondarySchool, chipSpecialSchool, chipEnglish;

    private ChipGroup cgMuseum, cgSchool;

    private ArrayList<Chip> museumChips;
    private ArrayList<Chip> schoolChips;
    private Button applyBtn;
    private ToggleButton toggleButton;
    private ArrayList<String> selectedChipData;
    private int filterType;

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

        setUpButton();

        Intent intent = getIntent();
        filterType = intent.getIntExtra("filterType", 0);

        // set checked changed listener
        if (filterType == 0) {
            for (int i = 0; i < schoolChips.size(); i++) {
                schoolChips.get(i).setOnCheckedChangeListener(checkedChangeListener);
            }
        } else {
            for (int i = 0; i < museumChips.size(); i++) {
                museumChips.get(i).setOnCheckedChangeListener(checkedChangeListener);
            }
        }


        // filter for school
        if (filterType == 0) {
            cgSchool.setVisibility(View.VISIBLE);
            cgMuseum.setVisibility(View.GONE);
            ArrayList<String> selectedSchoolTypes = intent.getStringArrayListExtra("selectedTypes");
            if (selectedSchoolTypes != null) {
                if (selectedSchoolTypes.size() == 4) {
                    toggleButton.setChecked(true);
                }
                for (int i = 0; i < schoolChips.size(); i++) {
                    if (selectedSchoolTypes.contains(schoolChips.get(i).getText().toString())) {
                        schoolChips.get(i).setChecked(true);
                    }
                }
            } else {
                // default select all
                toggleButton.setChecked(true);
            }
            // filter for museum
        } else {
            cgMuseum.setVisibility(View.VISIBLE);
            cgSchool.setVisibility(View.GONE);
            ArrayList<String> selectedMuseumTypes = intent.getStringArrayListExtra("selectedTypes");
            if (selectedMuseumTypes != null) {
                if (selectedMuseumTypes.size() == 19) {
                    toggleButton.setChecked(true);
                }
                for (int i = 0; i < museumChips.size(); i++) {
                    if (selectedMuseumTypes.contains(museumChips.get(i).getText().toString())) {
                        museumChips.get(i).setChecked(true);
                    }
                }
            } else {
                // default select all
                toggleButton.setChecked(true);
            }
        }
    }


    private void configureView() {
        cgMuseum = findViewById(R.id.cgMuseum);
        cgSchool = findViewById(R.id.cgSchool);

        // museum chip group
        museumChips = new ArrayList<>();
        chipArt = findViewById(R.id.chipArt);
        museumChips.add(chipArt);
        chipAutomotive = findViewById(R.id.chipAutomotive);
        museumChips.add(chipAutomotive);
        chipAviation = findViewById(R.id.chipAviation);
        museumChips.add(chipAviation);
        chipEthnic = findViewById(R.id.chipEthnic);
        museumChips.add(chipEthnic);
        chipHistory = findViewById(R.id.chipHistory);
        museumChips.add(chipHistory);
        chipFarm = findViewById(R.id.chipFarm);
        museumChips.add(chipFarm);
        chipFashion = findViewById(R.id.chipFashion);
        museumChips.add(chipFashion);
        chipForestry = findViewById(R.id.chipForestry);
        museumChips.add(chipForestry);
        chipIndustry = findViewById(R.id.chipIndustry);
        museumChips.add(chipIndustry);
        chipLiterary = findViewById(R.id.chipLiterary);
        museumChips.add(chipLiterary);
        chipMaritime = findViewById(R.id.chipMaritime);
        museumChips.add(chipMaritime);
        chipMedia = findViewById(R.id.chipMedia);
        museumChips.add(chipMedia);
        chipMilitary = findViewById(R.id.chipMilitary);
        museumChips.add(chipMilitary);
        chipMusic = findViewById(R.id.chipMusic);
        museumChips.add(chipMusic);
        chipOpenAir = findViewById(R.id.chipOpenAir);
        museumChips.add(chipOpenAir);
        chipPrison = findViewById(R.id.chipPrison);
        museumChips.add(chipPrison);
        chipRailway = findViewById(R.id.chipRailway);
        museumChips.add(chipRailway);
        museumChipsports = findViewById(R.id.chipSports);
        museumChips.add(museumChipsports);
        chipTechnology = findViewById(R.id.chipTechnology);
        museumChips.add(chipTechnology);

        // school chips group
        schoolChips = new ArrayList<>();
        chipPrimarySchool = findViewById(R.id.chipPrimarySchool);
        schoolChips.add(chipPrimarySchool);
        chipSecondarySchool = findViewById(R.id.chipSecondarySchool);
        schoolChips.add(chipSecondarySchool);
        chipSpecialSchool = findViewById(R.id.chipSpecialSchool);
        schoolChips.add(chipSpecialSchool);
        chipEnglish = findViewById(R.id.chipEnglishProgram);
        schoolChips.add(chipEnglish);

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
                    if (filterType == 0) {
                        for(int i = 0; i < schoolChips.size(); i++) {
                            schoolChips.get(i).setChecked(true);
                        }
                    } else {
                        for(int i = 0; i < museumChips.size(); i++) {
                            museumChips.get(i).setChecked(true);
                        }
                    }

                } else {
                    if (filterType == 0) {
                        for(int i = 0; i < schoolChips.size(); i++) {
                            schoolChips.get(i).setChecked(false);
                        }
                    } else {
                        for(int i = 0; i < museumChips.size(); i++) {
                            museumChips.get(i).setChecked(false);
                        }
                    }

                }
            }
        });
    }
}