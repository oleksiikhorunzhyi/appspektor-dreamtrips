package com.worldventures.dreamtrips.core;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.api.AuthApi;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.WorldVenturesApi;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.utils.Logs;
import com.worldventures.dreamtrips.view.activity.Injector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;

public class DataManager {

    @Inject
    protected DreamTripsApi dreamTripsApi;

    @Inject
    protected AuthApi authApi;

    @Inject
    protected WorldVenturesApi worldVenturesApi;

    public DataManager(Injector injector) {
        injector.inject(this);
    }

    public void getTrips(Result<List<Trip>> result) {
        dreamTripsApi.trips(convert(result));
    }

    public void getSession(String username, String password, Result<Session> result) {

        Callback<Session> callback = convert(result);
        dreamTripsApi.getSession(username, password, callback);
    }

    public void getToken(String username, String password, Result<JsonObject> result) {
        Callback<JsonObject> callback = convert(result);
        worldVenturesApi.getToken(username, password, callback);
    }

    public void getWebSiteDocumentsByCountry(Result<JsonObject> result) {
        Callback<JsonObject> callback = convert(result);
        worldVenturesApi.getWebSiteDocumentsByCountry(callback);
    }

    static String convertStreamToString(java.io.InputStream in) throws IOException {
        InputStreamReader is = new InputStreamReader(in);
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();

        while (read != null) {
            sb.append(read);
            read = br.readLine();
        }

        return sb.toString();
    }

    private <T> Callback<T> convert(Result<T> result) {
        return new Callback<T>() {
            @Override
            public void success(T t, retrofit.client.Response r) {
                result.response(t, null);
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    if (error.getResponse().getBody() != null) {
                        String errorString = convertStreamToString(error.getResponse().getBody().in());
                        Logs.d(errorString);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                result.response(null, error);
            }
        };
    }

    public static interface Result<T> {
        void response(T t, Exception e);
    }
}
