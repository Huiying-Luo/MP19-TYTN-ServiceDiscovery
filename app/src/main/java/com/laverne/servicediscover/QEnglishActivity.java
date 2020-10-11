package com.laverne.servicediscover;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class QEnglishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_english);

        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button basicButton = findViewById(R.id.question_eglish_basic);
        Button mediumButton = findViewById(R.id.question_eglish_intermediate);
        Button advancedButton = findViewById(R.id.question_eglish_advanced);

        basicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnglishPref(true);
                nextActivity();
            }
        });

        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnglishPref(true);
                nextActivity();
            }
        });

        advancedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do not need english classes
                setEnglishPref(false);
                nextActivity();
            }
        });
    }

    private void nextActivity() {
        Intent intent = new Intent(QEnglishActivity.this, QMuseumActivity.class);
        startActivity(intent);
        Animatoo.animateSlideLeft(QEnglishActivity.this);
}

    private void setEnglishPref(boolean needEnglishSchool) {
        SharedPreferences sharedPref = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putBoolean("needEnglishSchool", needEnglishSchool);
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