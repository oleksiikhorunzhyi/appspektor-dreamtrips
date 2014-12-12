package com.worldventures.dreamtrips.core;

import com.worldventures.dreamtrips.DTApplication;
import com.worldventures.dreamtrips.core.api.AuthApi;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.WorldVenturesApi;
import com.worldventures.dreamtrips.core.model.Trip;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit.Callback;

public class DataManager {

    private static final String BODY = "{\"__type\":\"UsernameAuthenticateRQ:#Rovia.Framework.Aut\n" +
            "h.WebDataContract\",\"TraceToken\":\"307e2dbe-7a40-4feea248-59a9cd5e56af\",\"Password\":\"guestpassword\",\"UserNam\n" +
            "e\":\"mdl\"}";
    @Inject
    @Named("realService")
    protected DreamTripsApi dreamTripsApi;
    @Inject
    AuthApi authApi;
    @Inject
    WorldVenturesApi worldVenturesApi;

    public DataManager(DTApplication application) {
        application.inject(this);
    }

    public void getTrips(Callback<List<Trip>> callback) {
        dreamTripsApi.trips(callback);
    }


    public void login(String username, String password, Callback<Object> callback) {
        dreamTripsApi.sessions(username, password, callback);
        worldVenturesApi.token(username, password, callback);
        authApi.authenticateByUserName(BODY, callback);
    }
}
