package com.laverne.servicediscover.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.laverne.servicediscover.Adapter.LibraryRecyclerViewAdapter;
import com.laverne.servicediscover.Model.Library;
import com.laverne.servicediscover.NetworkConnection.NetworkConnection;
import com.laverne.servicediscover.R;
import com.laverne.servicediscover.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import okhttp3.internal.platform.android.UtilKt;

public class LibraryFragment extends Fragment {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    private Spinner sortSpinner;
    private ArrayAdapter<String> sortSpinnerAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LibraryRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Library> libraries;
    private List<Library> librariesWithCurrentDist;
    private NetworkConnection networkConnection;
    private Geocoder geocoder;
    private TextView errorTextView;

    private String address;
    private double[] homeLatLng;
    private double[] currentLatLng = null;

    private boolean enableGetLocation = false;
    private boolean hasCurrentLocation = false;
    private boolean isSortedByHomeAddress = true;

    public LibraryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.library_fragment, container, false);

        sortSpinner = view.findViewById(R.id.library_sort_spinner);
        recyclerView = view.findViewById(R.id.library_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.library_swipe_refresh_layout);
        errorTextView = view.findViewById(R.id.library_error_tv);

        setUpSwipeToUpdate();
        networkConnection = new NetworkConnection();
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        getUserCurrentLocation();
        getUserHomeLocation();
        configureSortSpinner();

        //new GetAllLibaryTask().execute();
        libraries = new ArrayList<>();
        libraries.add(new Library("Carnegie Library", "7 Shepparson Ave, Carnegie VIC 3163", "www.google.com", "0478742887", -37.887440, 145.058280, 0, 0));
        libraries.add(new Library("My home", "900 Dandenong Rd, Caulfield East VIC 3145", "www.google.com", "0478742887", -37.877010, 145.044266, 0, 0));
        initRecyclerView();
        sortByHomeAddress(false);

        return view;
    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            enableGetLocation = true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            enableGetLocation = true;
        } else {
            enableGetLocation = false;
        }
    }

    private void getUserCurrentLocation() {
        // create location request
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        checkLocationPermission();
        if (enableGetLocation) {
            LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(this);
                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                        double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                        double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                        currentLatLng = new double[] {latitude, longitude};
                        Log.i("locationCurrent", String.valueOf(currentLatLng[0]) + ", " + String.valueOf(currentLatLng[1]));
                    }
                }
            }, Looper.getMainLooper());
        }
    }


    private void initRecyclerView() {
        adapter = new LibraryRecyclerViewAdapter(libraries, isSortedByHomeAddress);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
        recyclerView.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }


    private class GetAllLibaryTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return networkConnection.getAllLibraries();
        }

        @Override
        protected void onPostExecute(String result) {
            libraries = new ArrayList<>();

            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() == 0) {
                        errorTextView.setText("Oops, something went wrong...");
                    } else {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (i == jsonArray.length() - 1) {
                                //isLastMemoir = true;
                            }
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String libName = jsonObject.getString("libName");
                            String libAddress = jsonObject.getString("libAddress");
                            String libPhoneNo = jsonObject.getString("libPhoneNo");
                            String libWebsite = jsonObject.getString("libWebsite");
                            double libLatitude = jsonObject.getDouble("libLatitude");
                            double libLongitude = jsonObject.getDouble("libLongitude");
                            float homeDistance;
                            float currentDistance = 0;
                            if (homeLatLng.length == 0) {
                                homeDistance = 0;
                            } else {
                                // distance from user's home address
                                float[] homeDist = new float[2];
                                Location.distanceBetween(homeLatLng[0], homeLatLng[1], libLatitude, libLongitude, homeDist);
                                homeDistance = homeDist[0];
                            }

                            Library library = new Library(libName,libAddress, libWebsite, libPhoneNo, libLatitude, libLongitude, homeDistance, currentDistance);
                            libraries.add(library);
                        }
                        sortByHomeAddress(false);
                        initRecyclerView();
                    }
                } catch (JSONException e) {
                    Utilities.showAlertDialogwithOkButton(getActivity(), "Error", "Something went wrong, please try again later.");
                    e.printStackTrace();
                }
            } else {
                Utilities.showAlertDialogwithOkButton(getActivity(), "Error", "Something went wrong, please try again later.");
            }
        }
    }


    private String getUserAddress() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        String userAddress = sharedPref.getString("address", "");
        Log.i("home", userAddress);
        return userAddress;
    }


    private void getUserHomeLocation() {
        address = getUserAddress();

        double homeLatitude = 0;
        double homeLongitude = 0;

        List<Address> userAddr = null;
        try {
            userAddr = geocoder.getFromLocationName(address, 1);

            if (userAddr == null || userAddr.size() == 0) {
                Utilities.showAlertDialogwithOkButton(getActivity(), "Error", "We cannot calculate the distance for you.\n Please check your home address in Setting!");
                return;
            }

            Address addr = userAddr.get(0);
            homeLatitude = addr.getLatitude();
            homeLongitude= addr.getLongitude();


        } catch (IOException e) {
            e.printStackTrace();
            Log.i("error", e.getMessage().toString());
            Utilities.showAlertDialogwithOkButton(getActivity(), "Error", "We cannot calculate the distance for you.\n Please check your home address in Setting!");
        }

        if (homeLatitude != 0 && homeLongitude != 0) {
            homeLatLng = new double[] {homeLatitude, homeLongitude};
        }
    }


    private  void configureSortSpinner() {
        String[] options = new String[] {"Distance from home", "Distance from current location"};

        final List<String> sortList = new ArrayList<String>(Arrays.asList(options));

        sortSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, sortList);
        sortSpinner.setAdapter(sortSpinnerAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // sort by distance from home
                        sortByHomeAddress(false);
                        if (!isSortedByHomeAddress) {
                            isSortedByHomeAddress = true;
                            resetRecyclerViewAdapter(true);
                        }
                        adapter.notifyDataSetChanged();
                        layoutManager.scrollToPosition(0);
                        break;
                    case 1:
                        // sort by distance from current location
                        if (isSortedByHomeAddress) {
                            isSortedByHomeAddress = false;
                            resetRecyclerViewAdapter(false);
                        }
                        sortByCurrentLocation();
                        adapter.notifyDataSetChanged();
                        layoutManager.scrollToPosition(0);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void resetRecyclerViewAdapter(boolean isSortedByHomeAddress) {
        adapter = new LibraryRecyclerViewAdapter(libraries, isSortedByHomeAddress);
        recyclerView.setAdapter(adapter);
    }


    private void sortByHomeAddress(boolean forRefresh) {
        if (forRefresh) {
            // if user has update his address in setting
            if (getUserAddress() != address) {
                getUserHomeLocation();
                for (int i = 0; i < libraries.size(); i++) {
                    if (homeLatLng.length == 0) {
                        libraries.get(i).setHomeDistance(0);
                    } else {
                        // distance from user's home address
                        float[] homeDist = new float[2];
                        Location.distanceBetween(homeLatLng[0], homeLatLng[1], libraries.get(i).getLatitude(), libraries.get(i).getLongitude(), homeDist);
                        libraries.get(i).setHomeDistance(homeDist[0]);
                    }
                }
            }
        }
        Collections.sort(libraries, new Comparator<Library>() {
            @Override
            public int compare(Library o1, Library o2) {
                return Float.compare(o1.getHomeDistance(), o2.getHomeDistance());
            }
        });
    }


    private void sortByCurrentLocation() {
        if (!hasCurrentLocation) {
            getUserCurrentLocation();
            if (enableGetLocation) {
                for (int i = 0; i < libraries.size(); i++) {
                    // distance from user current location
                    float[] distance = new float[2];
                    Location.distanceBetween(currentLatLng[0], currentLatLng[1], libraries.get(i).getLatitude(), libraries.get(i).getLongitude(), distance);
                    libraries.get(i).setCurrentDistance(distance[0]);
                    Log.i("CurrentDistance", String.valueOf(i) + ": " + String.valueOf(distance[0]));
                }
                hasCurrentLocation = true;
            }
        }

        Collections.sort(libraries, new Comparator<Library>() {
            @Override
            public int compare(Library o1, Library o2) {
                return Float.compare(o1.getCurrentDistance(), o2.getCurrentDistance());
            }
        });
    }

    private void setUpSwipeToUpdate() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isSortedByHomeAddress) {
                    sortByHomeAddress(true);
                } else {
                    hasCurrentLocation = false;
                    sortByCurrentLocation();
                }
                adapter.notifyDataSetChanged();
                layoutManager.scrollToPosition(0);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
