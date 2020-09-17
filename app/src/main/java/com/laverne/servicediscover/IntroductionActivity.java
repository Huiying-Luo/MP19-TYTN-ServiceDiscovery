package com.laverne.servicediscover;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class IntroductionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        Button startButton = findViewById(R.id.intro_startButton);
        Button cancelButton = findViewById(R.id.intro_cancelButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent returnIntent = getIntent();
                setResult(RESULT_OK, returnIntent);

                Intent intent = new Intent(IntroductionActivity.this, QKidsActivity.class);
                startActivity(intent);

                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(IntroductionActivity.this, MainActivity.class);
                intent.putExtra("goToMission", true);
                startActivity(intent);

                 */
                finish();
            }
        });
    }
}