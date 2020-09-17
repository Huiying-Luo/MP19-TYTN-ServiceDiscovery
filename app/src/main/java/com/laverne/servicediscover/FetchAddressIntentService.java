package com.laverne.servicediscover;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {

    private ResultReceiver resultReceiver;

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String errorMessage = "" ;
            resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
            Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
            if (location == null) {
                return;
            }

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                errorMessage = e.getMessage();
            }

            if (addressList == null || addressList.size() == 0) {
                deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage, "");
            } else {
                Address address = addressList.get(0);
                String street = address.getThoroughfare();
                String postcode = address.getPostalCode();
                deliverResultToReceiver(Constants.SUCCESS_RESULT, street, postcode);
            }
        }
    }


    private void deliverResultToReceiver(int resultCode, String address, String postcode) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY_ONE, address);
        bundle.putString(Constants.RESULT_DATA_KEY_TWO, postcode);
        resultReceiver.send(resultCode, bundle);
    }
}
