package com.laverne.servicediscover.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;
import java.util.Locale;

import okhttp3.internal.platform.android.UtilKt;

public class LibraryFragment extends Fragment {

    private Spinner sortSpinner;
    private ArrayAdapter<String> sortSpinnerAdapter;
    private RecyclerView recyclerView;
    private LibraryRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Library> libraries;
    private NetworkConnection networkConnection;
    private Geocoder geocoder;
    private TextView errorTextView;

    private double[] homeLatLng;

    public LibraryFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.library_fragment, container, false);

        sortSpinner = view.findViewById(R.id.library_sort_spinner);
        recyclerView = view.findViewById(R.id.library_recycler_view);
        errorTextView = view.findViewById(R.id.library_error_tv);

        networkConnection = new NetworkConnection();
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        getUserHomeLocation();
        initRecyclerView();
        configureSortSpinner();

        return view;
    }


    private void initRecyclerView() {
        libraries = new ArrayList<>();
        adapter = new LibraryRecyclerViewAdapter(libraries);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
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


                        }
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


    private void getUserHomeLocation() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        String address = sharedPref.getString("address", "");

        double homeLatitude = 0;
        double homeLongitude = 0;

        List<Address> userAddr = null;
        try {
            userAddr = geocoder.getFromLocationName(address, 1);

            if (userAddr == null || userAddr.size() == 0) {
                return;
        }

            Address addr = userAddr.get(0);
            homeLatitude = addr.getLatitude();
            homeLongitude= addr.getLongitude();


        } catch (IOException e) {
            e.printStackTrace();
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
                        break;
                    case 1:
                        // sort by distance from current location
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void sortByHome() {

    }


    private void sortByCurrentLocation() {

    }
}
