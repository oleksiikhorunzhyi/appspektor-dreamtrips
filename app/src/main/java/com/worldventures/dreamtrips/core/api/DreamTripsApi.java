package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.Trip;

import org.json.JSONObject;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

public interface DreamTripsApi {
    String DEFAULT_URL = "http://private-5690-dreamtrips.apiary-mock.com";

    @FormUrlEncoded
    @POST("/api/sessions")
    public void getSession(@Field("username") String username, @Field("password") String password, Callback<Session> callback);

    @GET("/trips")
    public void trips(Callback<List<Trip>> callback);
}
