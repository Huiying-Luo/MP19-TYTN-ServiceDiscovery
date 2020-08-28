package com.laverne.servicediscover.SurveyScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.laverne.servicediscover.MainActivity;
import com.laverne.servicediscover.R;
import com.laverne.servicediscover.SplashScreen;

public class QAddressActivity extends AppCompatActivity {

    private TextInputLayout addressTIL;
    private  TextInputLayout postcodeTIL;
    private EditText addressEditText;
    private EditText postcodeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_address);

        addressTIL = findViewById(R.id.til_address);
        addressEditText = findViewById(R.id.et_address);
        postcodeTIL = findViewById(R.id.til_postcode);
        postcodeEditText = findViewById(R.id.et_postcode);
        Button button = findViewById(R.id.question_address_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addressEditText.getText().toString();
                String postcode = postcodeEditText.getText().toString();

                if (validateAddress(address) && validatePostcode(postcode)) {
                    setUserAddress(address + ", " + postcode + " " + "VIC");

                    Intent intent = new Intent(QAddressActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean validateAddress(String address) {
        if (address.isEmpty()) {
            addressTIL.setError("*Field cannot be empty!");
            return false;
        }
        return true;
    }


    private boolean validatePostcode(String postcode) {
        if (postcode.isEmpty()) {
            postcodeTIL.setError("*Field cannot be empty!");
            return false;
        }
        if (postcode.length() != 4) {
            postcodeTIL.setError("*Invalid Postcode");
            return false;
        }

        return true;
    }

    private void setUserAddress(String address) {
        SharedPreferences sharedPref = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putString("address", address);
        spEditor.apply();
    }
}