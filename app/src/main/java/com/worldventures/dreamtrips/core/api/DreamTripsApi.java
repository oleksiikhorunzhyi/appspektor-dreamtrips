package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.Trip;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

public interface DreamTripsApi {
    String DEFAULT_URL = "http://54.201.166.182:8000";

    @FormUrlEncoded
    @POST("/api/sessions")
    public void getSession(@Field("username") String username, @Field("password") String password, Callback<Session> callback);

    @GET("/trips")
    public void trips(Callback<List<Trip>> callback);
}
