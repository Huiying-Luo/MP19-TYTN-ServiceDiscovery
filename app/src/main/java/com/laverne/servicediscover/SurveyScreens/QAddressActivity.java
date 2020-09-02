package com.laverne.servicediscover.SurveyScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

        //configureEditText();

        addressTIL = findViewById(R.id.til_address);
        addressEditText = findViewById(R.id.et_address);
        addressEditText.addTextChangedListener(new addressEditTextWatcher());
        postcodeTIL = findViewById(R.id.til_postcode);
        postcodeEditText = findViewById(R.id.et_postcode);
        postcodeEditText.addTextChangedListener(new postcodeEditTextWatcher());
        Button button = findViewById(R.id.question_address_button);
        Button skipBtn = findViewById(R.id.button_skip);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addressEditText.getText().toString();
                String postcode = postcodeEditText.getText().toString();

                if (validateAddress(address) && validatePostcode(postcode)) {
                    setUserAddress(address + " " + "VIC" + " " + postcode);

                    Intent intent = new Intent(QAddressActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserAddress("");
                Intent intent = new Intent(QAddressActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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

    private void configureEditText() {
        postcodeEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) QAddressActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(postcodeEditText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    private class addressEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            addressTIL.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    private class postcodeEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            postcodeTIL.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}