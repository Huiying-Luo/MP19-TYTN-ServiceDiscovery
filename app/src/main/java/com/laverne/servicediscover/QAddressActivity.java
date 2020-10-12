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

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
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

    private static final String TAG = "QAddressActivity";

    private TextView titleTextView;
    private TextInputLayout postcodeTIL;
    private EditText postcodeEditText;
    private TextView generateMsg;
    private ProgressBar missionProgressBar;
    private Button finishButton;
    private Button useCurrentLocationButton;
    private Button cancelButton;

    private boolean isValid = false;
    private boolean hasUseCurrentLocation = false;
    private boolean currentLocationBtnClick = false;
    private boolean needPrimarySchool, needSecondarySchool, needSpecialSchool, needEnglishSchool;

    private double userLatitude = 0;
    private double userLongitude = 0;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Geocoder geocoder;

    private NetworkConnection networkConnection;
    private MissionViewModel missionViewModel;

    private ResultReceiver resultReceiver;

    private ArrayList<String> selectedMuseumTypes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_address);

        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        getUserPref();
    }


    private void configureUI() {
        titleTextView = findViewById(R.id.question_address_title);
        generateMsg = findViewById(R.id.generate_msg);
        missionProgressBar = findViewById(R.id.mission_progress_bar);
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
                String postcode = postcodeEditText.getText().toString();

                if (validatePostcode(postcode)) {
                    convertAddressToCoordinate(postcode);
                    if (isValid) {
                        generateMissions();
                    } else {
                        Utilities.showAlertDialogwithOkButton(QAddressActivity.this, "Invalid Address", "Please enter a valid postcode or use current location.");
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
                currentLocationBtnClick = true;
                getLastLocation();
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
                    convertCoordinateToAddress(location);

                    missionProgressBar.setVisibility(View.VISIBLE);
                    finishButton.setClickable(false);
                    hasUseCurrentLocation = true;

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
                //String address = resultData.getString(Constants.RESULT_DATA_KEY_ONE);
                String postcode = resultData.getString(Constants.RESULT_DATA_KEY_TWO);
                //addressEditText.setText(address);
                if (!(postcodeEditText.getText().toString().equals(postcode))) {
                    postcodeEditText.setText(postcode);
                }

            } else {
                Log.i("ConvertAddressError", resultData.getString(Constants.RESULT_DATA_KEY_ONE));
            }
            currentLocationBtnClick = false;
            missionProgressBar.setVisibility(View.GONE);
            finishButton.setClickable(true);
        }
    }


    private void convertCoordinateToAddress(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);

    }


    private void convertAddressToCoordinate(String postcode) {
        List<Address> addressList = null;
        String location = postcode + ", Australia";
        Log.i("Address", location);
        try {
            addressList = geocoder.getFromLocationName(location, 1);

            if (addressList == null || addressList.size() == 0) {
                isValid = false;
                Utilities.showAlertDialogwithOkButton(QAddressActivity.this, "Incorrect Address", "We can not find this postcode.");
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


    private void setLocation(double latitude, double longitude) {
        userLatitude = latitude;
        userLongitude = longitude;
        SharedPreferences sharedPref = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putFloat("latitude", (float) latitude);
        spEditor.putFloat("longitude", (float) longitude);
        spEditor.apply();
    }


    private void generateMissions() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        new GetAllMissionsTask().execute();
        titleTextView.setVisibility(View.GONE);
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

    private class GetAllMissionsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return "[" + networkConnection.getSerivces(0) + "," + networkConnection.getSerivces(1) + "," + networkConnection.getSerivces(2) + "," + networkConnection.getSerivces(3) + "]";
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
                            JSONArray serviceArray = jsonArray.getJSONArray(i);
                            for (int j = 0; j < serviceArray.length(); j++) {
                                JSONObject jsonObject = serviceArray.getJSONObject(j);
                                String serviceName = jsonObject.getString("name");
                                String serviceAddress = jsonObject.getString("address");
                                double serviceLat = jsonObject.getDouble("latitude");
                                double serviceLong = jsonObject.getDouble("longitude");
                                float currentDistance;
                                if (userLatitude == 0 && userLongitude == 0) {
                                    currentDistance = 0;
                                } else {
                                    // distance from user location
                                    float[] distance = new float[2];
                                    Location.distanceBetween(userLatitude, userLongitude, serviceLat, serviceLong, distance);
                                    currentDistance = distance[0];
                                    Log.i("CurrentDistance", String.valueOf(i) + ": " + String.valueOf(distance[0]));

                                    // Museum Special Recommendation
                                    if (i == 3) {
                                        String museumType = jsonObject.getString("type");
                                        Mission mission = new Mission(serviceName, serviceAddress, serviceLat, serviceLong, i, 0);
                                        mission.setMuseumDescription(jsonObject.getString("description"));
                                        Mission checkedMission = checkMuseumPref(currentDistance, museumType, mission);
                                        if (checkedMission != null) {
                                            missionViewModel.insert(checkedMission);
                                        }
                                    } else if (currentDistance < 8000) {
                                        Mission mission = new Mission(serviceName, serviceAddress, serviceLat, serviceLong, i, 0);
                                        // Education services should check the school type
                                        if (i == 1) {
                                            Mission checkedMission = checkSchoolPref(jsonObject.getString("type"), mission);
                                            if (checkedMission != null) {
                                                missionViewModel.insert(checkedMission);
                                            }
                                        } else {
                                            missionViewModel.insert(mission);
                                        }
                                    }
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


    private void getUserPref() {
        SharedPreferences sharedPref = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        needPrimarySchool = sharedPref.getBoolean("needPrimarySchool", false);
        needSecondarySchool = sharedPref.getBoolean("needSecondarySchool", false);
        needSpecialSchool = sharedPref.getBoolean("needSpecialSchool", false);
        needEnglishSchool = sharedPref.getBoolean("needEnglishSchool", false);

        // museum types
        selectedMuseumTypes = new ArrayList<>();
        int size = sharedPref.getInt("museumTypeSize", 0);
        for (int i = 0; i < size; i++) {
            selectedMuseumTypes.add(sharedPref.getString("museumType" + i, ""));
        }
    }


    private Mission checkSchoolPref(String schoolType, Mission mission) {
        switch (schoolType) {
            case "Primary":
                if (needPrimarySchool) {
                    mission.setSchoolType(0);
                    return mission;
                }
                break;
            case "Secondary":
                if (needSecondarySchool) {
                    mission.setSchoolType(1);
                    return mission;
                }
                break;
            case "Pri/Sec":
                if (needPrimarySchool || needSecondarySchool) {
                    mission.setSchoolType(2);
                    return mission;
                }
                break;
            case "Special":
                if (needSpecialSchool) {
                    mission.setSchoolType(3);
                    return mission;
                }
                break;
            case "Adult English Program":
                if (needEnglishSchool) {
                    mission.setSchoolType(4);
                    return mission;
                }
                break;
        }
        return null;
    }


    private Mission checkMuseumPref(double distance, String museumType, Mission mission) {
        if (selectedMuseumTypes.contains(museumType)) {
            if (!museumType.equals("Art") || !museumType.equals("History")) {
                mission.setMuseumType(museumType);
                return mission;
            } else {
                if (distance < 300000) {
                    mission.setMuseumType(museumType);
                    return mission;
                }
            }

        } else if (distance < 100000) {
            mission.setMuseumType(museumType);
            return mission;
        }
        return null;
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


    private class postcodeEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            postcodeTIL.setErrorEnabled(false);
            if (!currentLocationBtnClick) {
                hasUseCurrentLocation = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    // Back button in action bar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Animatoo.animateSlideRight(this);
        return true;
    }
}