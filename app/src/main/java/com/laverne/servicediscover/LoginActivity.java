package com.laverne.servicediscover;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout passwordTIL;
    EditText passwordEditText;
    Button button;

    private static final String HASH_VALUE = "db6ca3e074ca1bac38dc595943f89b93";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordTIL = findViewById(R.id.til_password);
        passwordEditText = findViewById(R.id.et_password);
        passwordEditText.addTextChangedListener(new passwordEditTextWatcher());
        configurePwdEditText();

        Button button = findViewById(R.id.login_button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwdEntered = passwordEditText.getText().toString();

                if (pwdEntered.isEmpty()) {
                    passwordTIL.setError("* Password cannot be empty!");
                } else {
                    try {
                        String pwdEnteredHash = Utilities.getHash(pwdEntered);
                        if (pwdEnteredHash.equals(HASH_VALUE)) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            passwordTIL.setError("* Wrong Password");
                        }

                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void configurePwdEditText() {
        passwordEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) LoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    private class passwordEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            passwordTIL.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}