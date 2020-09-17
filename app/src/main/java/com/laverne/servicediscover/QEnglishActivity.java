package com.laverne.servicediscover;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class QEnglishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_english);

        Button basicButton = findViewById(R.id.question_eglish_basic);
        Button mediumButton = findViewById(R.id.question_eglish_intermediate);
        Button advancedButton = findViewById(R.id.question_eglish_advanced);

        basicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 0 = basic
                setEnglishLevel(0);
                nextActivity();
            }
        });

        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1 = intermediate
                setEnglishLevel(1);
                nextActivity();
            }
        });

        advancedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 2 = advanced
                setEnglishLevel(2);
                nextActivity();
            }
        });
    }

    private void nextActivity() {
        Intent intent = new Intent(QEnglishActivity.this, QAddressActivity.class);
        startActivity(intent);
        finish();
    }

    private void setEnglishLevel(int englishLevel) {
        SharedPreferences sharedPref = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putInt("EnglishLevel", englishLevel);
        spEditor.apply();
    }
}