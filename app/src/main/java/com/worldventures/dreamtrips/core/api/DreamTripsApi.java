package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.model.Activity;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.Region;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.model.TripDetails;
import com.worldventures.dreamtrips.core.model.User;

import org.json.JSONObject;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

public interface DreamTripsApi {

    @FormUrlEncoded
    @POST("/api/sessions")
    public void login(@Field("username") String username, @Field("password") String password, Callback<Session> callback);

    @POST("/api/profile/avatar")
    @Multipart
    public void uploadAvatar(@Part("avatar") TypedFile image, Callback<User.Avatar> callback);

    @GET("/api/photos")
    public List<Photo> getUserPhotos();

    @GET("/api/trips")
    public List<Trip> getTrips();

    @GET("/api/regions")
    public List<Region> getRegions();

    @GET("/api/users/{id}/photos")
    public List<Photo> getMyPhotos(@Path("id") int currentUserId);

    @GET("/api/inspirations")
    public List<Photo> getInspirationsPhotos();

    @GET("/api/ysbh_photos")
    public List<Photo> getYouShoulBeHerePhotos();

    @FormUrlEncoded
    @POST("/api/photos/{id}/flags")
    public void flagPhoto(@Path("id") int photoId, @Field("reason") String nameOfReason, Callback<JsonObject> callback);

    @POST("/api/photos/{id}/like")
    public void likePhoto(@Path("id") int photoId, Callback<JsonObject> callback);

    @DELETE("/api/photos/{id}/like")
    public void unlikePhoto(@Path("id") int photoId, Callback<JsonObject> callback);

    @POST("/api/photos")
    @Multipart
    public void postPhoto(@Body Photo photo);

    @POST("/api/trips/{id}/like")
    public void likeTrip(@Path("id") int photoId, Callback<JsonObject> callback);

    @DELETE("/api/trips/{id}/like")
    public void unlikeTrio(@Path("id") int photoId, Callback<JsonObject> callback);

    @GET("/api/trips/{id}/details")
    public void getDetails(@Path("id") int tripId, Callback<TripDetails> callback);

    @GET("/api/activities")
    public List<Activity> getActivities();

}
