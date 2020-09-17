package com.laverne.servicediscover;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.laverne.servicediscover.DirectionHelpers.FetchURL;
import com.laverne.servicediscover.DirectionHelpers.TaskLoadedCallback;
import com.laverne.servicediscover.Entity.Mission;
import com.laverne.servicediscover.ViewModel.MissionViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MissionMapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private Button getDirectionBtn, completeMissionBtn;
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private MissionViewModel missionViewModel;

    private LatLng missionLocation, userLocation;
    private String missionName;
    private int uid;

    private String[] dialogList;

    private Polyline currentPolyine;

    private static final String GOOGLE_MAP_KEY = "AIzaSyCEADqWbiBEunIO0gjudx7NLK8OC8ccTI4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_map);

        setTitle("Map");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getDirectionBtn = findViewById(R.id.get_direction_btn);
        completeMissionBtn = findViewById(R.id.complete_mission_btn);


        initializeViewModel();

        Intent intent = getIntent();
        double missionLat = intent.getDoubleExtra("latitude", 0);
        double missionLng = intent.getDoubleExtra("longitude", 0);
        missionLocation = new LatLng(missionLat, missionLng);
        missionName = intent.getStringExtra("name");
        uid = intent.getIntExtra("id", 0);

        getCurrentLocation();

        setupGetDirectionButton();
        setupCompleteMissionButton();
    }


    private void displayGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);

        // add mission location marker
        mMap.addMarker(new MarkerOptions().position(missionLocation).title(missionName));
        mMap.addMarker(new MarkerOptions().position(userLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Your Location"));

       zoomToCenter();
    }

/** Citation: https://stackoverflow.com/questions/38264483/google-map-zoom-in-between-latlng-bounds **/

    private void zoomToCenter() {
        float[] distance = new float[2];
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, missionLocation.latitude, missionLocation.longitude, distance);
        double radius = distance[0] / 2;

        double scale = radius / 150;
        float zoomLevel =  ((int) (16 - Math.log(scale) / Math.log(2)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel));
    }


    private void setupGetDirectionButton() {
        getDirectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog();
            }
        });
    }

    private void displayDialog() {
        dialogList = new String[]{"driving", "walking", "bicycling"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MissionMapActivity.this);
        mBuilder.setTitle("Choose a travel mode");
        mBuilder.setSingleChoiceItems(dialogList, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String directionMode = dialogList[which];
                getDirection(directionMode);
                dialog.dismiss();
            }
        });
        mBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }


    // travel modes: driving, walking and bicycling
    private void getDirection(String directionMode) {
        if (userLocation != null && missionLocation != null) {
            String url = getUrl(userLocation, missionLocation, directionMode);
            new FetchURL(MissionMapActivity.this).execute(url, directionMode);
        } else {
            Utilities.showAlertDialogwithOkButton(MissionMapActivity.this, "Error", "Something went wrong! Fail to get the direction.");
        }

    }


    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" +  GOOGLE_MAP_KEY;
        return url;
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(MissionMapActivity.this).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    userLocation = new LatLng(latitude, longitude);
                    displayGoogleMap();



                } else {
                    // show error alert
                    Utilities.showAlertDialogwithOkButton(MissionMapActivity.this, "Error", "Something went wrong! Fail to get your current location.");
                }
            }
        }, Looper.getMainLooper());
    }

    private void initializeViewModel() {
        // initialize view model
        missionViewModel = new ViewModelProvider(this).get(MissionViewModel.class);
        missionViewModel.initializeVars(getApplication());
    }


    private void setupCompleteMissionButton() {
        completeMissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uid != 0) {
                    new FindMissionsAsyncTask().execute();

                }
            }
        });
    }

    // Back button in action bar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private class FindMissionsAsyncTask extends AsyncTask<String, Void, Mission> {

        @Override
        protected Mission doInBackground(String... strings) {
            return missionViewModel.findMissionByID(uid);
        }

        @Override
        protected void onPostExecute(Mission mission) {
            if (mission != null) {
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String date = dateFormat.format(currentTime);
                mission.setCompleteDate(date);
                mission.setStatus(2);
                missionViewModel.updateMission(mission);

                showAlertAndMove();

            } else {

                Log.i("Mission", "null");
            }
        }
    }

    private void showAlertAndMove() {
        // create a alert dialog
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(MissionMapActivity.this);
        alert.setTitle("Congrats!");
        alert.setMessage("You have known more one service!!!");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MissionMapActivity.this, MainActivity.class);
                intent.putExtra("goToCompletedMission", true);
                startActivity(intent);
                finish();
            }
        });
        alert.create().show();
    }




    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyine != null) {
            currentPolyine.remove();
        }
        currentPolyine = mMap.addPolyline((PolylineOptions) values[0]);
    }
}