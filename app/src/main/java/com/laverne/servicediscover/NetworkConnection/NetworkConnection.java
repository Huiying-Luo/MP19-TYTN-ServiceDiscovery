package com.laverne.servicediscover.NetworkConnection;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkConnection {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String BASE_URL = "";

    private OkHttpClient client = null;
    private String result;
    private int resultCode;

    public NetworkConnection() {
        client = new OkHttpClient();
    }

    public String getAllLibraries() {
        final String methodPath = "";

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
