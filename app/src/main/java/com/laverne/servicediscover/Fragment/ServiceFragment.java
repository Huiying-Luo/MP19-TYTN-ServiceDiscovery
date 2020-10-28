package com.laverne.servicediscover.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.laverne.servicediscover.Adapter.ServiceRecyclerViewAdapter;
import com.laverne.servicediscover.FilterActivity;
import com.laverne.servicediscover.MapActivity;
import com.laverne.servicediscover.Model.Service;
import com.laverne.servicediscover.NetworkConnection.NetworkConnection;
import com.laverne.servicediscover.R;
import com.laverne.servicediscover.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class ServiceFragment extends Fragment implements ServiceRecyclerViewAdapter.OnServiceListener {

    private static final int REQUEST_SCHOOL_FILTER_CODE = 101;
    private static final int REQUEST_MUSEUM_FILTER_CODE = 102;
    private static final String TAG = "ServiceFragment";
    private int category;

    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private Spinner filterSpinner;
    private TextView filterTitleTextView;
    private ArrayAdapter<String> filterSpinnerAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ServiceRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Service> services;
    private List<Service> allServices;
    private ArrayList<String> selectedMuseumTypes;
    private ArrayList<String> selectedSchoolTypes;
    private NetworkConnection networkConnection;
    private Geocoder geocoder;
    private TextView errorTextView;
    private ProgressBar progressBar;
    private FloatingActionButton fab;

    private double[] currentLatLng = new double[]{0, 0};

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;

    public ServiceFragment(int category) {
        this.category = category;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.service_fragment, container, false);

        configureView(view);
        /*
        // 1 = Education
        if (category == 1) {
            filterTitleTextView.setVisibility(View.VISIBLE);
            configureFilterSpinner();
        } */

        // Education & Museum has floating action button for filter
        if (category == 1 || category == 3) {
            configureFloatingActionButton();
        }

        geocoder = new Geocoder(getContext(), Locale.getDefault());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        services = new ArrayList<>();
        allServices = new ArrayList<>();
        initRecyclerView();
        setUpSwipeToUpdate();

        networkConnection = new NetworkConnection();

        getLastLocation(false);

        new GetAllServicesTask().execute();

        return view;
    }

    // Get Selected Museum Types
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MUSEUM_FILTER_CODE) {
            if (resultCode == RESULT_OK) {
                selectedMuseumTypes = data.getStringArrayListExtra("selectedChipData");
                filterMuseums();
            }
        } else if (requestCode == REQUEST_SCHOOL_FILTER_CODE) {
            if (resultCode == RESULT_OK) {
                selectedSchoolTypes = data.getStringArrayListExtra("selectedChipData");
                filterSchools();
            }
        }
    }


    private void configureFloatingActionButton() {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FilterActivity.class);
                if (category == 3) {
                    intent.putExtra("filterType", 1);
                    intent.putStringArrayListExtra("selectedTypes", selectedMuseumTypes);
                    startActivityForResult(intent, REQUEST_MUSEUM_FILTER_CODE);
                } else {
                    intent.putExtra("filterType", 0);
                    intent.putStringArrayListExtra("selectedTypes", selectedSchoolTypes);
                    startActivityForResult(intent, REQUEST_SCHOOL_FILTER_CODE);
                }
            }
        });
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


    private void configureView(View view) {
        fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.library_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.library_swipe_refresh_layout);
        errorTextView = view.findViewById(R.id.library_error_tv);
        progressBar = view.findViewById(R.id.library_progress_bar);
        filterTitleTextView = view.findViewById(R.id.filter_title);
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
        adapter = new ServiceRecyclerViewAdapter(services, this);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
        recyclerView.setAdapter(adapter);
        //registerCallbackForPhoneCall();
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onServiceClick(int position) {
        Intent intent = new Intent(getActivity(), MapActivity.class);
        Service service = services.get(position);
        intent.putExtra("name",service.getName());
        intent.putExtra("latitude", service.getLatitude());
        intent.putExtra("longitude", service.getLongitude());
        intent.putExtra("type", 1);
        intent.putExtra("category", category);
        intent.putExtra("address", service.getAddress());

        if (service.getPhoneNo() != null) {
            intent.putExtra("phoneNo", service.getPhoneNo());
        }
        if (service.getWebsite() != null) {
            intent.putExtra("website", service.getWebsite());
        }
        if (category == 1) {
            intent.putExtra("schoolType", service.getSchoolType());
        }
        if (category == 3) {
            intent.putExtra("museumDescription", service.getMuseumDescription());
            intent.putExtra("museumType", service.getMuseumType());
        }
        startActivity(intent);
    }


    private class GetAllServicesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            switch (category) {
                case 0:
                    return networkConnection.getSerivces(0);
                case 1:
                    return networkConnection.getSerivces(1);
                case 2:
                    return networkConnection.getSerivces(2);
                case 3:
                    return networkConnection.getSerivces(3);
                default:
                    return "";
            }
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
                            String serviceName = jsonObject.getString("name");
                            String serviceAddress = jsonObject.getString("address");

                            double serviceLat = jsonObject.getDouble("latitude");
                            double serviceLong = jsonObject.getDouble("longitude");


                            float currentDistance;
                            if (currentLatLng[0] == 0 && currentLatLng[1] == 0) {
                                currentDistance = 0;
                            } else {
                                // distance from user current location
                                float[] distance = new float[2];
                                Location.distanceBetween(currentLatLng[0], currentLatLng[1], serviceLat, serviceLong, distance);
                                currentDistance = distance[0];
                                Log.i("CurrentDistance", String.valueOf(i) + ": " + String.valueOf(distance[0]));
                            }

                            Service service = new Service(serviceName, serviceAddress, serviceLat, serviceLong, currentDistance, category);
                            // park do not has website and phone number
                            // 2 = park
                            if (category != 2) {
                                String serviceWebsite = jsonObject.getString("website");
                                service.setWebsite(serviceWebsite);
                                // museums do not has phone number
                                if (category != 3) {
                                    String servicePhoneNo = jsonObject.getString("phone");
                                    service.setPhoneNo(servicePhoneNo);
                                }
                            }
                            // if it is education service, set the school type
                            // 1 = Education
                            if (category == 1) {
                                String serviceSchoolType = jsonObject.getString("type");
                                switch (serviceSchoolType) {
                                    case "Primary":
                                        service.setSchoolType(0);
                                        break;
                                    case "Secondary":
                                        service.setSchoolType(1);
                                        break;
                                    case "Pri/Sec":
                                        service.setSchoolType(2);
                                        break;
                                    case "Special":
                                        service.setSchoolType(3);
                                        break;
                                    case "Adult English Program":
                                        service.setSchoolType(4);
                                        break;
                                }
                                allServices.add(service);
                            }

                            // if it is museum, set the museum type and description
                            if (category == 3) {
                                service.setMuseumType(jsonObject.getString("type"));
                                service.setMuseumDescription(jsonObject.getString("description"));
                                allServices.add(service);
                            }
                            services.add(service);
                        }

                        sortByCurrentLocation(false);
                        // remove the progress bar
                        progressBar.setVisibility(View.GONE);
                        adapter.updateList(services);
                        setUpLayoutAnimation(recyclerView);
                        // If Screen for Museum, make floating filter button visible
                        if (category == 3 || category == 1) {
                            fab.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (JSONException e) {
                    Utilities.showAlertDialogwithOkButton(getActivity(), "Error", "Something went wrong, please try again later.");
                    e.printStackTrace();
                }
            } else {
                Utilities.showAlertDialogwithOkButton(getActivity(), "Network Error", "No Internet Connection, please check your network and try again.");
            }
        }
    }

/*
    private void configureFilterSpinner() {
        filterSpinner.setVisibility(View.VISIBLE);

        String[] options = new String[]{"All", "Primary School", "Secondary School", "Special School", "Adult English Program"};
        final List<String> filterList = new ArrayList<String>(Arrays.asList(options));
        filterSpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, filterList);

        filterSpinner.setAdapter(filterSpinnerAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterBySchoolType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
*/
/*
    private void filterBySchoolType(int position) {
        services.removeAll(services);
        if (position != 0) {
            for (int i = 0; i < allServices.size(); i++) {
                Service tempSerivce = allServices.get(i);
                // primary and secondary schools
                int schoolType = tempSerivce.getSchoolType();
                if (position == 1 || position == 2) {
                    if (schoolType == position - 1 || schoolType == 2) {
                        services.add(tempSerivce);
                    }
                } else {
                    // special and adult english schools
                    if (schoolType == position) {
                        services.add(tempSerivce);
                    }
                }
            }
        } else {
            services.addAll(allServices);
        }
        adapter.updateList(services);
        setUpLayoutAnimation(recyclerView);
        layoutManager.scrollToPosition(0);
    }
*/

    private void filterSchools() {
        services.removeAll(services);

        for (int i = 0; i < allServices.size(); i++) {
            int schoolType =  allServices.get(i).getSchoolType();
            boolean addToList = false;
            switch (schoolType) {
                case 0:
                    addToList = selectedSchoolTypes.contains("Primary School");
                    break;
                case 1:
                    addToList = selectedSchoolTypes.contains("Secondary School");
                    break;
                case 2:
                    addToList = selectedSchoolTypes.contains("Primary School") || selectedSchoolTypes.contains("Secondary School");
                    break;
                case 3:
                    addToList = selectedSchoolTypes.contains("Special School");
                    break;
                case 4:
                    addToList = selectedSchoolTypes.contains("Adult English Program");
                    break;
            }

            if (addToList) {
                services.add(allServices.get(i));
            }
        }

        adapter.updateList(services);
        setUpLayoutAnimation(recyclerView);
        layoutManager.scrollToPosition(0);
    }

    private void filterMuseums() {
        services.removeAll(services);

        for (int i = 0; i < allServices.size(); i++) {
            if (selectedMuseumTypes.contains(allServices.get(i).getMuseumType())) {
                services.add(allServices.get(i));
            }
        }

        adapter.updateList(services);
        setUpLayoutAnimation(recyclerView);
        layoutManager.scrollToPosition(0);
    }


    private void sortByCurrentLocation(boolean forRefresh) {
        if (forRefresh) {
            // Update location
            for (int i = 0; i < services.size(); i++) {
                // distance from user current location
                float[] distance = new float[2];
                Location.distanceBetween(currentLatLng[0], currentLatLng[1], services.get(i).getLatitude(), services.get(i).getLongitude(), distance);
                services.get(i).setCurrentDistance(distance[0]);
                Log.i("CurrentDistance", String.valueOf(i) + ": " + String.valueOf(distance[0]));
            }
        }

        // Sorting
        Collections.sort(services, new Comparator<Service>() {
            @Override
            public int compare(Service o1, Service o2) {
                return Float.compare(o1.getCurrentDistance(), o2.getCurrentDistance());
            }
        });
        swipeRefreshLayout.setRefreshing(false);
        adapter.updateList(services);
        setUpLayoutAnimation(recyclerView);
        layoutManager.scrollToPosition(0);
        // 1 = education
        if (category == 1 || category == 3) {
            // Sorting the school list (can be filtered)
            Collections.sort(allServices, new Comparator<Service>() {
                @Override
                public int compare(Service o1, Service o2) {
                    return Float.compare(o1.getCurrentDistance(), o2.getCurrentDistance());
                }
            });
        }
    }


    private void setUpSwipeToUpdate() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLastLocation(true);
            }
        });
    }

/*
    private void goToApplicationSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    private void registerCallbackForPhoneCall() {
        adapter.setPhoneCallListener(new ServiceRecyclerViewAdapter.OnPhoneCallListener() {
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

*/
    private void setUpLayoutAnimation(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_down_to_up);
        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
}
