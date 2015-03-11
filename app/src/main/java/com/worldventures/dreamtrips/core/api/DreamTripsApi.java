package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.model.Activity;
import com.worldventures.dreamtrips.core.model.Inspiration;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.Region;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.SuccessStory;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.model.TripDetails;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;

import java.util.ArrayList;
import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public interface DreamTripsApi {

    @FormUrlEncoded
    @POST("/api/sessions")
    public Session login(@Field("username") String username, @Field("password") String password);

    @POST("/api/profile/avatar")
    @Multipart
    public User uploadAvatar(@Part("avatar") TypedFile image);

    @GET("/api/trips")/*TODO*/
    public List<Trip> getTrips();

    @GET("/api/regions")/*TODO*/
    public List<Region> getRegions();

    @GET("/api/activities")/*TODO*/
    public List<Activity> getActivities();

    @GET("/api/photos")
    public ArrayList<Photo> getUserPhotos(@Query("per_page") int perPage, @Query("page") int page);

    @GET("/api/users/{id}/photos")
    public ArrayList<Photo> getMyPhotos(@Path("id") int currentUserId, @Query("per_page") int Query, @Query("page") int page);

    @GET("/api/inspirations")
    public ArrayList<Inspiration> getInspirationsPhotos(@Query("per_page") int perPage, @Query("page") int page);

    @GET("/api/ysbh_photos")
    public ArrayList<Photo> getYouShoulBeHerePhotos(@Query("per_page") int perPage, @Query("page") int page);

    @GET("/api/success_stories")
    public ArrayList<SuccessStory> getSuccessStores();

    @FormUrlEncoded
    @POST("/api/photos/{id}/flags")
    public JsonObject flagPhoto(@Path("id") int photoId, @Field("reason") String nameOfReason);

    @DELETE("/api/photos/{id}")
    public JsonObject deletePhoto(@Path("id") int photoId);

    @POST("/api/photos/{id}/like")
    public JsonObject likePhoto(@Path("id") int photoId);

    @DELETE("/api/photos/{id}/like")
    public JsonObject unlikePhoto(@Path("id") int photoId);

    @POST("/api/trips/{id}/like")
    public JsonObject likeTrip(@Path("id") int photoId);

    @DELETE("/api/trips/{id}/like")
    public JsonObject unlikeTrio(@Path("id") int photoId);

    @POST("/api/photos")
    public Photo uploadTripPhoto(@Body ImageUploadTask uploadTask);


    @GET("/api/trips/{id}/details")
    public TripDetails getDetails(@Path("id") int tripId);

}
