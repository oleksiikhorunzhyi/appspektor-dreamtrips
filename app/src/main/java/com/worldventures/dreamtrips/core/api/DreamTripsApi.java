package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketBasePostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketOrderModel;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketStatusItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.Suggestion;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;
import com.worldventures.dreamtrips.modules.common.model.Session;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Friend;
import com.worldventures.dreamtrips.modules.membership.api.InviteBody;
import com.worldventures.dreamtrips.modules.membership.model.History;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;
import com.worldventures.dreamtrips.modules.trips.model.TripDetails;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.video.model.Video;

import org.json.JSONObject;

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

    public static final String TYPE_MEMBER = "DTAPP";
    public static final String TYPE_MEMBER_360 = "DTAPP360";

    @FormUrlEncoded
    @POST("/api/sessions")
    Session login(@Field("username") String username, @Field("password") String password);

    @POST("/api/profile/avatar")
    @Multipart
    User uploadAvatar(@Part("avatar") TypedFile image);

    @GET("/api/profile")
    User getProfile();

    @GET("/api/trips")
    List<TripModel> getTrips();

    @GET("/api/regions")
    List<RegionModel> getRegions();

    @GET("/api/activities")
    List<ActivityModel> getActivities();

    @GET("/api/photos")
    ArrayList<Photo> getUserPhotos(@Query("per_page") int perPage, @Query("page") int page);

    @GET("/api/users/{id}/photos")
    ArrayList<Photo> getMyPhotos(@Path("id") int currentUserId, @Query("per_page") int query, @Query("page") int page);

    @GET("/api/inspirations?random_seed=1")
    ArrayList<Inspiration> getInspirationsPhotos(@Query("per_page") int perPage, @Query("page") int page, @Query("random_seed") double randomSeed);

    @GET("/api/ysbh_photos")
    ArrayList<Photo> getYouShouldBeHerePhotos(@Query("per_page") int perPage, @Query("page") int page);

    @GET("/api/success_stories")
    ArrayList<SuccessStory> getSuccessStores();

    @FormUrlEncoded
    @POST("/api/photos/{id}/flags")
    JsonObject flagPhoto(@Path("id") String photoId, @Field("reason") String nameOfReason);

    @DELETE("/api/photos/{id}")
    JsonObject deletePhoto(@Path("id") String photoId);

    @POST("/api/photos/{id}/like")
    JsonObject likePhoto(@Path("id") String photoId);

    @DELETE("/api/photos/{id}/like")
    JsonObject unlikePhoto(@Path("id") String photoId);

    @POST("/api/success_stories/{id}/like")
    JsonObject likeSS(@Path("id") int photoId);

    @DELETE("/api/success_stories/{id}/like")
    JsonObject unlikeSS(@Path("id") int photoId);

    @POST("/api/trips/{id}/like")
    JsonObject likeTrip(@Path("id") String photoId);

    @DELETE("/api/trips/{id}/like")
    JsonObject unlikeTrio(@Path("id") String photoId);

    @POST("/api/photos")
    Photo uploadTripPhoto(@Body ImageUploadTask uploadTask);

    @POST("/api/bucket_list_items/{id}/photos")
    BucketPhoto uploadBucketPhoto(@Path("id") int bucketId, @Body BucketPhoto bucketPhoto);

    @GET("/api/trips/{id}")
    TripDetails getDetails(@Path("id") String tripId);

    @POST("/api/bucket_list_items")
    BucketItem createItem(@Body BucketBasePostItem bucketItem);

    @PATCH("/api/bucket_list_items/{id}")
    BucketItem completeItem(@Path("id") int id, @Body BucketStatusItem bucketPostItem);

    @PATCH("/api/bucket_list_items/{id}")
    BucketItem updateItem(@Path("id") int id, @Body BucketBasePostItem bucketPostItem);

    @DELETE("/api/bucket_list_items/{id}")
    JsonObject deleteItem(@Path("id") int id);

    @GET("/api/bucket_list_items")
    ArrayList<BucketItem> getBucketList(@Query("type") String type);

    @DELETE("/api/bucket_list_items/{id}/photos/{photo_id}")
    JsonObject deleteBucketPhoto(@Path("id") int id, @Path("photo_id") String photoId);

    @GET("/api/bucket_list/locations")
    ArrayList<PopularBucketItem> getPopularLocations();

    @GET("/api/bucket_list/activities")
    ArrayList<PopularBucketItem> getPopularActivities();

    @GET("/api/bucket_list/dinings")
    ArrayList<PopularBucketItem> getPopularDining();

    @PUT("/api/bucket_list_items/{id}/position")
    JsonObject changeOrder(@Path("id") int id, @Body BucketOrderModel item);

    @GET("/api/categories")
    ArrayList<CategoryItem> getCategories();

    @GET("/api/location_suggestions")
    ArrayList<Suggestion> getLocationSuggestions(@Query("name") String name);

    @GET("/api/activity_suggestions")
    ArrayList<Suggestion> getActivitySuggestions(@Query("name") String name);

    @GET("/api/dining_suggestions")
    ArrayList<Suggestion> getDiningSuggestions(@Query("name") String name);

    @GET("/api/location_suggestions/popular")
    ArrayList<PopularBucketItem> getLocationPopularSuggestions(@Query("name") String name);

    @GET("/api/activity_suggestions/popular")
    ArrayList<PopularBucketItem> getActivityPopularSuggestions(@Query("name") String name);

    @GET("/api/dining_suggestions/popular")
    ArrayList<PopularBucketItem> getDiningPopularSuggestions(@Query("name") String name);

    @GET("/api/invitations/templates")
    ArrayList<InviteTemplate> getInviteTemplates();

    @GET("/api/invitations")
    ArrayList<History> getInvitations();

    @POST("/api/invitations")
    JSONObject sendInvitations(@Body InviteBody body);

    @FormUrlEncoded
    @POST("/api/invitations/templates/{id}")
    InviteTemplate getFilledInviteTemplate(@Path("id") int id, @Field("message") String message);

    @FormUrlEncoded
    @POST("/api/invitations/filled_templates")
    InviteTemplate createInviteTemplate(@Field("template_id") int id,
                                        @Field("message") String message,
                                        @Field("cover_photo_url") String photoUrl);

    @GET("/api/invitations/filled_templates/{id} ")
    InviteTemplate getFilledInviteTemplate(@Path("id") int id);

    @GET("/api/locales")
    ArrayList<AvailableLocale> getLocales();

    @GET("/api/member_videos/")
    ArrayList<Video> getVideos(@Query("type") String type);

    @GET("/api/flag_reasons")
    ArrayList<Flag> getFlags();

    @GET("/api/social/friends")
    ArrayList<Friend> getFriends(@Query("group") String group, @Query("offset") int offset, @Query("limit") int limit);
}
