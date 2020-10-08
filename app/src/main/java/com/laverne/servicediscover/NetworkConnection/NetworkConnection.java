package com.laverne.servicediscover.NetworkConnection;

import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkConnection {

    //public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String BASE_URL = "https://safe-everglades-39383.herokuapp.com/api/";

    private OkHttpClient client = null;
    private String result;
    //private int resultCode;

    public NetworkConnection() {
        client = new OkHttpClient();
    }

    public String getAllLibraries() {
        final String methodPath = "libraries";

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("networkError", e.getMessage().toString());
        }
        return result;
    }

    public String getAllSchools() {
        return result;
    }


    public String getAllParks() {
        return result;
    }


    public String getAllMuseums() {
        return result;
    }
}
