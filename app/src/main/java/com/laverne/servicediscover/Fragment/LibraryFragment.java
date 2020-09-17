package com.laverne.servicediscover.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.laverne.servicediscover.Adapter.LibraryRecyclerViewAdapter;
import com.laverne.servicediscover.Model.Library;
import com.laverne.servicediscover.NetworkConnection.NetworkConnection;
import com.laverne.servicediscover.QAddressActivity;
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

public class LibraryFragment extends Fragment {

    private static final int REQUEST_CODE_CALL_PERMISSION = 2;
    private static final String TAG = "LibraryFragment";
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private Spinner sortSpinner;
    private ArrayAdapter<String> sortSpinnerAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LibraryRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Library> libraries;
    private NetworkConnection networkConnection;
    private Geocoder geocoder;
    private TextView errorTextView;
    private ProgressBar progressBar;

    private String callingNumber;

    private String address;
    private double[] homeLatLng = null;
    private double[] currentLatLng = new double[]{0, 0};

    //private boolean isSortedByHomeAddress = false;

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    public LibraryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.library_fragment, container, false);

        configureView(view);

        geocoder = new Geocoder(getContext(), Locale.getDefault());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        libraries = new ArrayList<>();
        initRecyclerView();
        setUpSwipeToUpdate();

        networkConnection = new NetworkConnection();

        getLastLocation(false);
        //configureSortSpinner();

        new GetAllLibaryTask().execute();


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    // Set up search Bar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }

        if (searchView != null) {
            //searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    return false;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    /*    @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_search:
                    return false;
                default:
                    break;
            }
            searchView.setOnQueryTextListener(queryTextListener);
            return super.onOptionsItemSelected(item);
        }
    */
    private void configureView(View view) {
        //sortSpinner = view.findViewById(R.id.library_sort_spinner);
        recyclerView = view.findViewById(R.id.library_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.library_swipe_refresh_layout);
        errorTextView = view.findViewById(R.id.library_error_tv);
        progressBar = view.findViewById(R.id.library_progress_bar);
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation(final boolean forRefresh) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(locationRequest, new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(this);
                if (locationResult != null && locationResult.getLocations().size() > 0) {
                    // We have location
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    currentLatLng[0] = latitude;
                    currentLatLng[1] = longitude;
                    if (forRefresh) {
                        sortByCurrentLocation(true);
                    }
                } else {
                    // show error alert
                    Utilities.showAlertDialogwithOkButton(getActivity(), "Error", "Something went wrong! Fail to get your current location.");
                }
            }
        }, Looper.getMainLooper());
    }


    private void initRecyclerView() {
        adapter = new LibraryRecyclerViewAdapter(libraries);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
        recyclerView.setAdapter(adapter);
        registerCallbackForPhoneCall();
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
                            String libName = jsonObject.getString("name");
                            String libAddress = jsonObject.getString("address");
                            String libPhoneNo = jsonObject.getString("phone");
                            String libWebsite = jsonObject.getString("website");
                            double libLatitude = jsonObject.getDouble("latitude");
                            double libLongitude = jsonObject.getDouble("longitude");
                            float homeDistance = 0;
                            float currentDistance;
                            if (currentLatLng[0] == 0 && currentLatLng[1] == 0) {
                                currentDistance = 0;
                            } else {
                                // distance from user current location
                                float[] distance = new float[2];
                                Location.distanceBetween(currentLatLng[0], currentLatLng[1], libLatitude, libLongitude, distance);
                                currentDistance = distance[0];
                                Log.i("CurrentDistance", String.valueOf(i) + ": " + String.valueOf(distance[0]));
                            }

                            Library library = new Library(libName, libAddress, libWebsite, libPhoneNo, libLatitude, libLongitude, homeDistance, currentDistance);
                            libraries.add(library);
                        }
                        sortByCurrentLocation(false);
                        // remove the progress bar
                        progressBar.setVisibility(View.GONE);
                        adapter.updateList(libraries);
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
        if (address.length() == 0) {
            // show an alert dialog
            alertForHomeAddress("You have not set  your home address, please go to Settings to enter your address.");
            sortSpinner.setSelection(0);
            return;
        }

        double homeLatitude = 0;
        double homeLongitude = 0;

        List<Address> userAddr = null;
        try {
            userAddr = geocoder.getFromLocationName(address, 1);

            if (userAddr == null || userAddr.size() == 0) {
                alertForHomeAddress("We cannot calculate the distance for you.\n Please check your home address in Setting");
                sortSpinner.setSelection(0);
                return;
            }

            Address addr = userAddr.get(0);
            homeLatitude = addr.getLatitude();
            homeLongitude = addr.getLongitude();


        } catch (IOException e) {
            e.printStackTrace();
            Log.i("error", e.getMessage().toString());
            alertForHomeAddress("We cannot calculate the distance for you.\n Please check your home address in Setting");
            sortSpinner.setSelection(0);
        }

        homeLatLng = new double[]{homeLatitude, homeLongitude};

    }


    private void alertForHomeAddress(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Sorry")
                .setMessage(message)
                .setPositiveButton("Go to Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, new SettingFragment());
                        fragmentTransaction.commit();
                        NavigationView navigationView = getActivity().findViewById(R.id.navigationView);
                        navigationView.setCheckedItem(R.id.library);
                        getActivity().setTitle("Library");
                    }
                })
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

/*
    private void configureSortSpinner() {
        String[] options = new String[]{"Distance from current location", "Distance from home"};

        final List<String> sortList = new ArrayList<String>(Arrays.asList(options));

        sortSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, sortList);
        sortSpinner.setAdapter(sortSpinnerAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // sort by distance from current location
                        if (isSortedByHomeAddress) {
                            isSortedByHomeAddress = false;
                            resetRecyclerViewAdapter(false);
                        }
                        sortByCurrentLocation(false);
                        break;
                    case 1:
                        // sort by distance from home
                        getUserHomeLocation();
                        if (homeLatLng != null) {
                            sortByHomeAddress();
                            if (!isSortedByHomeAddress) {
                                isSortedByHomeAddress = true;
                                resetRecyclerViewAdapter(true);
                            }
                            adapter.notifyDataSetChanged();
                            layoutManager.scrollToPosition(0);
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
*/

    private void resetRecyclerViewAdapter(boolean isSortedByHomeAddress) {
        adapter = new LibraryRecyclerViewAdapter(libraries);
        recyclerView.setAdapter(adapter);
        registerCallbackForPhoneCall();
    }


    private void sortByHomeAddress() {
        for (int i = 0; i < libraries.size(); i++) {
            // distance from user's home address
            float[] homeDist = new float[2];
            Location.distanceBetween(homeLatLng[0], homeLatLng[1], libraries.get(i).getLatitude(), libraries.get(i).getLongitude(), homeDist);
            libraries.get(i).setHomeDistance(homeDist[0]);
        }

        Collections.sort(libraries, new Comparator<Library>() {
            @Override
            public int compare(Library o1, Library o2) {
                return Float.compare(o1.getHomeDistance(), o2.getHomeDistance());
            }
        });
        adapter.updateList(libraries);
        layoutManager.scrollToPosition(0);
    }


    private void sortByCurrentLocation(boolean forRefresh) {
        if (forRefresh) {
            // Update location
            for (int i = 0; i < libraries.size(); i++) {
                // distance from user current location
                float[] distance = new float[2];
                Location.distanceBetween(currentLatLng[0], currentLatLng[1], libraries.get(i).getLatitude(), libraries.get(i).getLongitude(), distance);
                libraries.get(i).setCurrentDistance(distance[0]);
                Log.i("CurrentDistance", String.valueOf(i) + ": " + String.valueOf(distance[0]));
            }
        }

        // Sorting
        Collections.sort(libraries, new Comparator<Library>() {
            @Override
            public int compare(Library o1, Library o2) {
                return Float.compare(o1.getCurrentDistance(), o2.getCurrentDistance());
            }
        });
        swipeRefreshLayout.setRefreshing(false);
        adapter.updateList(libraries);
        layoutManager.scrollToPosition(0);

    }


    private void setUpSwipeToUpdate() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLastLocation(true);


            }
        });
    }


    private void goToApplicationSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    private void registerCallbackForPhoneCall() {
        adapter.setPhoneCallListener(new LibraryRecyclerViewAdapter.OnPhoneCallListener() {
            @Override
            public void onPhoneCallClick(String phoneNo) {
                makePhoneCall(phoneNo);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                makePhoneCall(callingNumber);
            }
        }
    }


    private void makePhoneCall(final String phoneNo) {
        callingNumber = phoneNo;
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {
                // This block here means PERMANENTLY DENIED PERMISSION (Don't ask again)
                new AlertDialog.Builder(getActivity())
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
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_CALL_PERMISSION);
            }

        } else {
            new AlertDialog.Builder(getActivity())
                    .setMessage("Do you want to call the number:\n" + phoneNo)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phoneNo));
                            getActivity().startActivity(callIntent);
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

}
