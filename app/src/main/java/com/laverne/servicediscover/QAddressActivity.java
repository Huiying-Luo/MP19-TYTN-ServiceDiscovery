package com.laverne.servicediscover;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputLayout;
import com.laverne.servicediscover.Entity.Mission;
import com.laverne.servicediscover.NetworkConnection.NetworkConnection;
import com.laverne.servicediscover.ViewModel.MissionViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class QAddressActivity extends AppCompatActivity {

    private static int DELAY_TIME = 3200;
    private static final String TAG = "QAddressActivity";

    private TextView titleTextView;
    private TextInputLayout addressTIL;
    private TextInputLayout postcodeTIL;
    private EditText addressEditText;
    private EditText postcodeEditText;
    private TextView generateMsg;
    private ProgressBar missionProgressBar;
    private Button finishButton;
    private Button useCurrentLocationButton;
    private Button cancelButton;

    private boolean isValid = false;
    private boolean hasUseCurrentLocation = false;

    private double userLatitude = 0;
    private double userLongitude = 0;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Geocoder geocoder;

    private NetworkConnection networkConnection;
    private MissionViewModel missionViewModel;

    private ResultReceiver resultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_address);

        // use to convert user current address from coordinate to address
        resultReceiver = new AddressResultReceiver(new Handler());

        geocoder = new Geocoder(this, Locale.getDefault());

        networkConnection = new NetworkConnection();
        initializeViewModel();

        //configureEditText();
        configureUI();
        configureEditText();

        configureFinishButton();
        configureCancelButton();
        configureUseCurrentLocationButton();
    }


    private void configureUI() {
        titleTextView = findViewById(R.id.question_address_title);
        generateMsg = findViewById(R.id.generate_msg);
        missionProgressBar = findViewById(R.id.mission_progress_bar);
        addressTIL = findViewById(R.id.til_address);
        addressEditText = findViewById(R.id.et_address);
        addressEditText.addTextChangedListener(new addressEditTextWatcher());
        postcodeTIL = findViewById(R.id.til_postcode);
        postcodeEditText = findViewById(R.id.et_postcode);
        postcodeEditText.addTextChangedListener(new postcodeEditTextWatcher());
        finishButton = findViewById(R.id.question_address_finish_button);
        useCurrentLocationButton = findViewById(R.id.question_address_location_button);
        cancelButton = findViewById(R.id.iquestion_address_cancel_button);
    }


    private void configureFinishButton() {
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hasUseCurrentLocation && isValid) {
                    generateMissions();
                    return;
                }
                String address = addressEditText.getText().toString();
                String postcode = postcodeEditText.getText().toString();

                if (validateAddress(address) && validatePostcode(postcode)) {
                    convertAddressToCoordinate(address, postcode);
                    if (isValid) {
                        generateMissions();
                    } else {
                        Utilities.showAlertDialogwithOkButton(QAddressActivity.this, "Invaild Address", "Please enter a valid address or use current location.");
                        return;
                    }
                }

            }

        });
    }


    private void configureUseCurrentLocationButton() {
        useCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasUseCurrentLocation) {
                    addressEditText.setEnabled(true);
                    addressEditText.setText("");
                    postcodeEditText.setEnabled(true);
                    postcodeEditText.setText("");
                    addressTIL.setEndIconActivated(true);
                    postcodeTIL.setEndIconActivated(true);
                    useCurrentLocationButton.setText("Use Current Location");
                    hasUseCurrentLocation = false;
                } else {
                    getLastLocation();
                }
            }
        });
    }


    private void configureCancelButton() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QAddressActivity.this, MainActivity.class);
                intent.putExtra("goToMission", true);
                startActivity(intent);
                finish();
            }
        });
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(QAddressActivity.this).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    Log.i("CurrentLocation", String.valueOf(latitude) + ", " + String.valueOf(longitude));
                    setLocation(latitude, longitude);
                    isValid = true;

                    Location location = new Location("providerNA");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    convertCoordianteToAddress(location);

                    missionProgressBar.setVisibility(View.VISIBLE);

                    addressEditText.setEnabled(false);
                    postcodeEditText.setEnabled(false);
                    addressTIL.setEndIconActivated(false);
                    postcodeTIL.setEndIconActivated(false);
                    hasUseCurrentLocation = true;
                    useCurrentLocationButton.setText("Enter My Location");
                } else {
                    // show error alert
                    Utilities.showAlertDialogwithOkButton(QAddressActivity.this, "Error", "Something went wrong! Fail to get your current location.");
                    isValid = false;
                }
            }
        }, Looper.getMainLooper());
    }


    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == Constants.SUCCESS_RESULT) {
                String address = resultData.getString(Constants.RESULT_DATA_KEY_ONE);
                String postcode = resultData.getString(Constants.RESULT_DATA_KEY_TWO);
                addressEditText.setText(address);
                postcodeEditText.setText(postcode);
            } else {
                addressEditText.setText("Use current Location");
                postcodeEditText.setText("Use current Location");
                Log.i("ConvertAddressError", resultData.getString(Constants.RESULT_DATA_KEY_ONE));
            }
            missionProgressBar.setVisibility(View.GONE);
        }
    }


    private void convertCoordianteToAddress(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);

    }


    private void convertAddressToCoordinate(String address, String postcode) {
        List<Address> addressList = null;
        String location = address + " VIC " + postcode;
        Log.i("Address", location);
        try {
            addressList = geocoder.getFromLocationName(location, 1);

            if (addressList == null || addressList.size() == 0) {
                isValid = false;
                Utilities.showAlertDialogwithOkButton(QAddressActivity.this, "Incorrect Address", "We can not find this location, please check your address.");
                return;
            }

            Address validAddress = addressList.get(0);
            double latitude = validAddress.getLatitude();
            double longitude = validAddress.getLongitude();
            setLocation(latitude, longitude);
            isValid = true;

        } catch (IOException e) {
            isValid = false;
            Utilities.showAlertDialogwithOkButton(QAddressActivity.this, "Error", "Something went wrong! Please try again later.");
            e.printStackTrace();
        }

    }


    private void setLocation(double latitude, double longtitude) {
        userLatitude = latitude;
        userLongitude = longtitude;
        SharedPreferences sharedPref = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putFloat("latitude", (float) latitude);
        spEditor.putFloat("longitude", (float) longtitude);
        spEditor.apply();
    }


    private void generateMissions() {
        new GetAllLibaryTask().execute();
        titleTextView.setVisibility(View.GONE);
        addressTIL.setVisibility(View.GONE);
        addressEditText.setVisibility(View.GONE);
        postcodeTIL.setVisibility(View.GONE);
        postcodeEditText.setVisibility(View.GONE);
        finishButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        useCurrentLocationButton.setVisibility(View.GONE);
        generateMsg.setVisibility(View.VISIBLE);
        missionProgressBar.setVisibility(View.VISIBLE);
    }


    private void initializeViewModel() {
        missionViewModel = new ViewModelProvider(this).get(MissionViewModel.class);
        missionViewModel.initializeVars(this.getApplication());
    }

    private class GetAllLibaryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return networkConnection.getAllLibraries();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() == 0) {
                        showAlert();
                    } else {
                        missionViewModel.deleteAll();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String libName = jsonObject.getString("name");
                            String libAddress = jsonObject.getString("address");
                            String libPhoneNo = jsonObject.getString("phone");
                            String libWebsite = jsonObject.getString("website");
                            double libLatitude = jsonObject.getDouble("latitude");
                            double libLongitude = jsonObject.getDouble("longitude");
                            float currentDistance;
                            if (userLatitude == 0 && userLongitude == 0) {
                                currentDistance = 0;
                            } else {
                                // distance from user location
                                float[] distance = new float[2];
                                Location.distanceBetween(userLatitude, userLongitude, libLatitude, libLongitude, distance);
                                currentDistance = distance[0];
                                Log.i("CurrentDistance", String.valueOf(i) + ": " + String.valueOf(distance[0]));
                                if (currentDistance < 8000) {
                                    Mission mission = new Mission(libName, libAddress, libLatitude, libLongitude, "Library", 0);
                                    missionViewModel.insert(mission);

                                }
                            }
                        }

                        goToAddMissionScreen();
                    }
                } catch (JSONException e) {
                    showAlert();
                    e.printStackTrace();
                }
            } else {
                showAlert();
            }
        }
    }



    private void showAlert() {
        // create a alert dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(QAddressActivity.this);
        alert.setTitle("Error");
        alert.setMessage("Oops, something went wrong when connecting to the network. Please try again later.");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(QAddressActivity.this, MainActivity.class);
                intent.putExtra("goToMission", true);
                startActivity(intent);
                finish();
            }
        });
        alert.create().show();
    }


    private void goToAddMissionScreen() {
        SharedPreferences sharedPref = this.getSharedPreferences("App", MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putBoolean("needQuestionnaire", false);
        spEditor.apply();

        Intent intent = new Intent(QAddressActivity.this, AddMissionActivity.class);
        startActivity(intent);
        finish();
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

    private void configureEditText() {
        postcodeEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
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