package com.lambent.lambentremote.common;

import com.jakewharton.retrofit.Ok3Client;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit.RestAdapter;

public class RetrofitServicesFactory {

    private static OkHttpClient okHttpClient;

    public static <T> T newService(Class<T> retrofitServiceClass, String hostAddress, int port) {
        Ok3Client client = new Ok3Client(getOkHttpClient());
        String endpoint = generateEndpoint(hostAddress, port);

        RestAdapter.Builder adapterBuilder = new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(endpoint)
                .setLogLevel(RestAdapter.LogLevel.BASIC);

        return adapterBuilder.build().create(retrofitServiceClass);
    }

    private static String generateEndpoint(String hostAddress, int port) {
        return "http://" + hostAddress + ":" + port;
    }

    private static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = generateOkHttpClient();
        }

        return okHttpClient;
    }

    private static OkHttpClient generateOkHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }
}
