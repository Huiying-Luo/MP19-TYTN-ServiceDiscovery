package com.laverne.servicediscover;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class QKidsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_kids);

        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button yesButton = findViewById(R.id.Q1_yes_Button);
        Button noButton = findViewById(R.id.Q1_no_Button);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserDetail(true);
                // Go to school Page
                Intent intent = new Intent(QKidsActivity.this, QSchoolActivity.class);
                startActivity(intent);
                Animatoo.animateSlideLeft(QKidsActivity.this);
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserDetail(false);
                Intent intent = new Intent(QKidsActivity.this, QEnglishActivity.class);
                startActivity(intent);
                Animatoo.animateSlideLeft(QKidsActivity.this);
            }
        });
    }

    private void setUserDetail(Boolean isParent) {
        SharedPreferences sharedPref = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putBoolean("isParent", isParent);
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