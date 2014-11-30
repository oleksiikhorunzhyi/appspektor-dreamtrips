package com.worldventures.dreamtrips.core;

import com.worldventures.dreamtrips.DTApplication;
import com.worldventures.dreamtrips.core.model.Trip;

import org.apache.http.HttpStatus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit.Callback;

public class DataManager {

    @Inject
    @Named("realService")
    protected DreamTripsApi api;

    public DataManager(DTApplication application) {
        application.inject(this);
    }

    public void getTrips(Callback<List<Trip>> callback) {
        api.trips(callback);
    }


    public void login(String username, String password, Callback<Integer> callback) {
        callback.success(HttpStatus.SC_OK, null);
    }
}
