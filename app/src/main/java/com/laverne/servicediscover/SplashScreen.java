package com.laverne.servicediscover;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private static int DELAY_TIME = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Animation topAnim, bottomAnim;
        ImageView logo, appName;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        logo = findViewById(R.id.logo);
        appName = findViewById(R.id.app_name);

        logo.setAnimation(topAnim);
        appName.setAnimation(bottomAnim);

        // Check whether the user is first time to use
        final Boolean isFirstRun = getSharedPreferences("App", MODE_PRIVATE).getBoolean("isFirstRun", true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (isFirstRun) {
                    intent = new Intent(SplashScreen.this, IntroductionActivity.class);
                    setUpSharedPref(false);
                } else {
                    intent = new Intent(SplashScreen.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, DELAY_TIME);

    }

    private void setUpSharedPref(boolean isFirstRun) {
        SharedPreferences sharedPref = this.getSharedPreferences("App", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putBoolean("isFirstRun", isFirstRun);
        spEditor.apply();
    }
}