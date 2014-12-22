package com.worldventures.dreamtrips.core;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.DTApplication;
import com.worldventures.dreamtrips.core.api.AuthApi;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.WorldVenturesApi;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.Trip;

import java.util.List;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;

public class DataManager {

    private static final String BODY = "{\"__type\":\"UsernameAuthenticateRQ:#Rovia.Framework.Aut\n" +
            "h.WebDataContract\",\"TraceToken\":\"307e2dbe-7a40-4feea248-59a9cd5e56af\",\"Password\":\"guestpassword\",\"UserNam\n" +
            "e\":\"mdl\"}";
    @Inject
    protected DreamTripsApi dreamTripsApi;
    @Inject
    protected AuthApi authApi;
    @Inject
    protected WorldVenturesApi worldVenturesApi;

    public DataManager(DTApplication application) {
        application.inject(this);
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

    private <T> Callback<T> convert(Result<T> result) {
        return new Callback<T>() {
            @Override
            public void success(T t, retrofit.client.Response r) {
                result.response(t, null);
            }

            @Override
            public void failure(RetrofitError error) {
                result.response(null, error);
            }
        };
    }

    public static interface Result<T> {
        void response(T t, Exception e);
    }

}
