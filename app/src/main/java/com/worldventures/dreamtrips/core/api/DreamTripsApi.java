package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketOrderModel;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.common.model.Session;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.trips.model.Activity;
import com.worldventures.dreamtrips.modules.trips.model.Region;
import com.worldventures.dreamtrips.modules.trips.model.Trip;
import com.worldventures.dreamtrips.modules.trips.model.TripDetails;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.ImageUploadTask;

import java.util.ArrayList;
import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.PUT;
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

    @GET("/api/trips")
    public List<Trip> getTrips();

    @GET("/api/regions")
    public List<Region> getRegions();

    @GET("/api/activities")
    public List<Activity> getActivities();

    @GET("/api/photos")
    public ArrayList<Photo> getUserPhotos(@Query("per_page") int perPage, @Query("page") int page);

    @GET("/api/users/{id}/photos")
    public ArrayList<Photo> getMyPhotos(@Path("id") int currentUserId, @Query("per_page") int Query, @Query("page") int page);

    @GET("/api/inspirations")
    public ArrayList<Inspiration> getInspirationsPhotos(@Query("per_page") int perPage, @Query("page") int page);

    @GET("/api/ysbh_photos")
    public ArrayList<Photo> getYouShouldBeHerePhotos(@Query("per_page") int perPage, @Query("page") int page);

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

    @POST("/api/success_stories/{id}/like")
    public JsonObject likeSS(@Path("id") int photoId);

    @DELETE("/api/success_stories/{id}/like")
    public JsonObject unlikeSS(@Path("id") int photoId);

    @POST("/api/trips/{id}/like")
    public JsonObject likeTrip(@Path("id") int photoId);

    @DELETE("/api/trips/{id}/like")
    public JsonObject unlikeTrio(@Path("id") int photoId);

    @POST("/api/photos")
    public Photo uploadTripPhoto(@Body ImageUploadTask uploadTask);

    @GET("/api/trips/{id}/details")
    public TripDetails getDetails(@Path("id") int tripId);

    @POST("/api/bucket_list_items")
    public BucketItem createItem(@Body BucketPostItem bucketItem);

    @PUT("/api/bucket_list_items/{id}/status")
    public BucketItem markItem(@Path("id") int id, @Body BucketPostItem bucketItem);

    @DELETE("/api/bucket_list_items/{id}")
    public JsonObject deleteItem(@Path("id") int id);

    @GET("/api/bucket_list_items")
    public ArrayList<BucketItem> getBucketList(@Query("type") String type);

    @GET("/api/bucket_list/locations")
    public ArrayList<PopularBucketItem> getPopularLocations();

    @GET("/api/bucket_list/activities")
    public ArrayList<PopularBucketItem> getPopularActivities();

    @PUT("/api/bucket_list_items/{id}/position")
    public JsonObject changeOrder(@Path("id") int id, @Body BucketOrderModel item);

}
