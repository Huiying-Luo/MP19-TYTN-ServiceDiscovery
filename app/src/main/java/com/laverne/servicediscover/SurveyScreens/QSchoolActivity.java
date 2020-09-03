package com.laverne.servicediscover.SurveyScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.laverne.servicediscover.R;

public class QSchoolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_school);

        final CheckBox primarySchool = findViewById(R.id.checkBoxPrimary);
        final CheckBox secondarySchool = findViewById(R.id.checkBoxSecondary);
        final CheckBox other = findViewById(R.id.checkBoxNone);

        final TextView errorTextView = findViewById(R.id.question_school_error);

        Button button = findViewById(R.id.question_school_nextButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (primarySchool.isChecked()) {
                    setUserDetail("needPrimarySchool", true);
                } else {
                    setUserDetail("needPrimarySchool", false);
                }

                if (secondarySchool.isChecked()) {
                    setUserDetail("needSecondarySchool", true);
                } else {
                    setUserDetail("needSecondarySchool", false);
                }

                if (!other.isChecked() && !primarySchool.isChecked() && !secondarySchool.isChecked()) {
                    errorTextView.setText("*Please at least select one!");
                } else {
                    Intent intent = new Intent(QSchoolActivity.this, QEnglishActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void setUserDetail(String schoolType, Boolean isNeed) {
        SharedPreferences sharedPref = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putBoolean(schoolType, isNeed);
        spEditor.apply();
    }



}