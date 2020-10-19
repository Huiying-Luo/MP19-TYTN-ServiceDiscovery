package com.laverne.servicediscover;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private static final int REQUEST_CODE_CALL_PERMISSION = 2;

    private Button getDirectionBtn, completeMissionBtn, callBtn, websiteBtn, addMissionBtn;
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private MissionViewModel missionViewModel;

    private LatLng location, userLocation;
    private double latitude, longitude;
    private String locationName, address = "", phoneNo, website, museumDescription = "", museumType = "";
    private int uid, schoolType, serviceCategory;

    private String[] dialogList;

    private Polyline currentPolyline;

    private Mission existingMission;



    private static final String GOOGLE_MAP_KEY = "AIzaSyCEADqWbiBEunIO0gjudx7NLK8OC8ccTI4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setTitle("Map");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getDirectionBtn = findViewById(R.id.get_direction_btn);
        completeMissionBtn = findViewById(R.id.complete_mission_btn);
        callBtn = findViewById(R.id.call_btn);
        websiteBtn = findViewById(R.id.website_btn);
        addMissionBtn = findViewById(R.id.add_to_mission_btn);
        addMissionBtn.setVisibility(View.GONE);

        initializeViewModel();

        getCurrentLocation();

        Intent intent = getIntent();
        // 0 - Mission, 1 - Service
        int type = intent.getIntExtra("type", 0);
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
        location = new LatLng(latitude, longitude);
        locationName = intent.getStringExtra("name");

        if (type == 0) {

            uid = intent.getIntExtra("id", 0);

            completeMissionBtn.setVisibility(View.VISIBLE);
            callBtn.setVisibility(View.GONE);
            websiteBtn.setVisibility(View.GONE);
            addMissionBtn.setVisibility(View.GONE);

            setupCompleteMissionButton();

        } else {
            completeMissionBtn.setVisibility(View.GONE);

            serviceCategory = intent.getIntExtra("category", 0);
            schoolType = intent.getIntExtra("schoolType", 10);
            museumDescription = intent.getStringExtra("museumDescription");
            museumType = intent.getStringExtra("museumType");
            address = intent.getStringExtra("address");
            phoneNo = intent.getStringExtra("phoneNo");

            if (serviceCategory != 2) {
                websiteBtn.setVisibility(View.VISIBLE);
                // Disable the website button for Adult English Programs
                if (serviceCategory == 1 && schoolType == 4) {
                    websiteBtn.setVisibility(View.GONE);
                } else {
                    website = intent.getStringExtra("website");
                }
                setupWebsiteButton();

                // museum does not have phone number
                if (serviceCategory != 3) {
                    callBtn.setVisibility(View.VISIBLE);
                    setupCallButton();
                } else {
                    callBtn.setVisibility(View.GONE);
                }
            } else {
                // park does not have website and phone no
                callBtn.setVisibility(View.GONE);
                websiteBtn.setVisibility(View.GONE);
            }

            // check whether it has been added to mission list
            new CheckMissionAsyncTask().execute(new Integer[]{serviceCategory});
        }


        setupGetDirectionButton();
    }

    private void setupAddMissionButton(final boolean isExist) {

        addMissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a alert dialog
                android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(MapActivity.this);
                alert.setTitle("Add Mission");
                alert.setMessage("Are you sure to add " + locationName + " into your mission list?");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isExist) {
                            existingMission.setStatus(1);
                            missionViewModel.updateMission(existingMission);
                        } else {
                            Mission mission = new Mission(locationName, address, latitude, longitude, serviceCategory, 1);
                            if (serviceCategory == 1) {
                                mission.setSchoolType(schoolType);
                            } else if (serviceCategory == 3) {
                                mission.setMuseumDescription(museumDescription);
                                mission.setMuseumType(museumType);
                            }
                            missionViewModel.insert(mission);
                        }
                        Intent intent = new Intent(MapActivity.this, MainActivity.class);
                        intent.putExtra("goToMission", true);
                        startActivity(intent);
                        Animatoo.animateInAndOut(MapActivity.this);
                        finish();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.create().show();
            }
        });
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
        mMap.addMarker(new MarkerOptions().position(location).title(locationName));
        mMap.addMarker(new MarkerOptions().position(userLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Your Location"));

        zoomToCenter();
    }

    /**
     * Citation: https://stackoverflow.com/questions/38264483/google-map-zoom-in-between-latlng-bounds
     **/

    private void zoomToCenter() {
        float[] distance = new float[2];
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, location.latitude, location.longitude, distance);
        double radius = distance[0] / 2;

        double scale = radius / 150;
        float zoomLevel = ((int) (16 - Math.log(scale) / Math.log(2)));
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
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapActivity.this);
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
        if (userLocation != null && location != null) {
            String url = getUrl(userLocation, location, directionMode);
            new FetchURL(MapActivity.this).execute(url, directionMode);
        } else {
            Utilities.showAlertDialogwithOkButton(MapActivity.this, "Error", "Something went wrong! Fail to get the direction.");
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
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + GOOGLE_MAP_KEY;
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
                LocationServices.getFusedLocationProviderClient(MapActivity.this).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    userLocation = new LatLng(latitude, longitude);
                    displayGoogleMap();


                } else {
                    // show error alert
                    Utilities.showAlertDialogwithOkButton(MapActivity.this, "Error", "Something went wrong! Fail to get your current location.");
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
                    new FindMissionAsyncTask().execute();
                }
            }
        });
    }


    private void setupCallButton() {
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

    }


    private void setupWebsiteButton() {
        websiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open website
                Uri uri = Uri.parse(website);
                Intent launchWeb = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(launchWeb);
            }
        });
    }


    private class CheckMissionAsyncTask extends AsyncTask<Integer, Void, Mission> {

        @Override
        protected Mission doInBackground(Integer... categories) {
            return missionViewModel.findMission(categories[0], locationName, address, latitude, longitude);
        }

        @Override
        protected void onPostExecute(Mission mission) {
            existingMission = mission;
            if (mission != null && mission.getStatus() != 0) {
                // the service already in the mission list or already complete
                addMissionBtn.setVisibility(View.GONE);
            } else if (mission == null){
                addMissionBtn.setVisibility(View.VISIBLE);
                setupAddMissionButton(false);
            } else {
                addMissionBtn.setVisibility(View.VISIBLE);
                setupAddMissionButton(true);
            }
        }
    }

    // Back button in action bar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private class FindMissionAsyncTask extends AsyncTask<String, Void, Mission> {

        @Override
        protected Mission doInBackground(String... strings) {
            return missionViewModel.findMissionByID(uid);
        }

        @Override
        protected void onPostExecute(Mission mission) {
            if (mission != null) {
                showAlert(mission);
            } else {
                Utilities.showAlertDialogwithOkButton(MapActivity.this, "Error", "Something went wrong, please try again later.");
                Log.i("Mission", "null");
            }
        }
    }


    private void setMissionComplete(Mission mission) {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String date = dateFormat.format(currentTime);
        mission.setCompleteDate(date);
        mission.setStatus(2);
        missionViewModel.updateMission(mission);

    }


    private void makePhoneCall() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                // This block here means PERMANENTLY DENIED PERMISSION (Don't ask again)
                new android.app.AlertDialog.Builder(this)
                        .setMessage("Phone Number: " + phoneNo + "\nIf you want to make a phone call here, please go to Settings to enable the phone call permission.")
                        .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goToApplicationSettings();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_CALL_PERMISSION);
            }

        } else {
            new android.app.AlertDialog.Builder(this)
                    .setMessage("Do you want to call the number:\n" + phoneNo)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phoneNo));
                            startActivity(callIntent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                makePhoneCall();
            }
        }
    }


    private void goToApplicationSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    private void showAlert(final Mission mission) {
        // create a alert dialog
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(MapActivity.this);
        alert.setTitle("Conformation!");
        alert.setMessage("Are you sure you have found out about this service?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setMissionComplete(mission);

                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                intent.putExtra("goToCompletedMission", true);
                startActivity(intent);
                Animatoo.animateInAndOut(MapActivity.this);
                finish();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create().show();
    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}