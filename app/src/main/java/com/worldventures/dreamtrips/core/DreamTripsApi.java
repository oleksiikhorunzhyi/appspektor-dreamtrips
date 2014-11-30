package com.worldventures.dreamtrips.core;

import com.worldventures.dreamtrips.core.model.Trip;

import org.json.JSONObject;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

public interface DreamTripsApi {
    public static final String DEFAULT_URL = "http://private-5690-dreamtrips.apiary-mock.com";

    @FormUrlEncoded()
    @POST("/auth/token")
    public void token(@Field("username") String username, @Field("password") String password, Callback<JSONObject> callback);

    @GET("/trips")
    public void trips(Callback<List<Trip>> callback);

}
