package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketBasePostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketOrderModel;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketStatusItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.Suggestion;
import com.worldventures.dreamtrips.modules.common.model.Session;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;
import com.worldventures.dreamtrips.modules.trips.model.TripDetails;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
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
    public List<TripModel> getTrips();

    @GET("/api/regions")
    public List<RegionModel> getRegions();

    @GET("/api/activities")
    public List<ActivityModel> getActivities();

    @GET("/api/photos")
    public ArrayList<Photo> getUserPhotos(@Query("per_page") int perPage, @Query("page") int page);

    @GET("/api/users/{id}/photos")
    public ArrayList<Photo> getMyPhotos(@Path("id") int currentUserId, @Query("per_page") int query, @Query("page") int page);

    @GET("/api/inspirations")
    public ArrayList<Inspiration> getInspirationsPhotos(@Query("per_page") int perPage, @Query("page") int page);

    @GET("/api/ysbh_photos")
    public ArrayList<Photo> getYouShouldBeHerePhotos(@Query("per_page") int perPage, @Query("page") int page);

    @GET("/api/success_stories")
    public ArrayList<SuccessStory> getSuccessStores();

    @FormUrlEncoded
    @POST("/api/photos/{id}/flags")
    public JsonObject flagPhoto(@Path("id") String photoId, @Field("reason") String nameOfReason);

    @DELETE("/api/photos/{id}")
    public JsonObject deletePhoto(@Path("id") String photoId);

    @POST("/api/photos/{id}/like")
    public JsonObject likePhoto(@Path("id") String photoId);

    @DELETE("/api/photos/{id}/like")
    public JsonObject unlikePhoto(@Path("id") String photoId);

    @POST("/api/success_stories/{id}/like")
    public JsonObject likeSS(@Path("id") int photoId);

    @DELETE("/api/success_stories/{id}/like")
    public JsonObject unlikeSS(@Path("id") int photoId);

    @POST("/api/trips/{id}/like")
    public JsonObject likeTrip(@Path("id") String photoId);

    @DELETE("/api/trips/{id}/like")
    public JsonObject unlikeTrio(@Path("id") String photoId);

    @POST("/api/photos")
    public Photo uploadTripPhoto(@Body ImageUploadTask uploadTask);

    @POST("/api/bucket_list_items/{id}/photos")
    public BucketPhoto uploadBucketPhoto(@Path("id") int bucketId, @Body BucketPhoto bucketPhoto);

    @GET("/api/trips/{id}/details")
    public TripDetails getDetails(@Path("id") String tripId);

    @POST("/api/bucket_list_items")
    public BucketItem createItem(@Body BucketBasePostItem bucketItem);

    @PATCH("/api/bucket_list_items/{id}")
    public BucketItem completeItem(@Path("id") int id, @Body BucketStatusItem bucketPostItem);

    @PATCH("/api/bucket_list_items/{id}")
    public BucketItem updateItem(@Path("id") int id, @Body BucketPostItem bucketPostItem);

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

    @GET("/api/categories")
    public ArrayList<CategoryItem> getCategories();

    @GET("/api/location_suggestions")
    public ArrayList<Suggestion> getLocationSuggestions(@Query("name") String name);

    @GET("/api/activity_suggestions")
    public ArrayList<Suggestion> getActivitySuggestions(@Query("name") String name);
}
