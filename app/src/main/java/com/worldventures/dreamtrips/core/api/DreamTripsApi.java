package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.Suggestion;
import com.worldventures.dreamtrips.modules.common.model.DELETE_WITH_BODY;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoEntity;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.List;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface DreamTripsApi {

   @POST("/api/photos")
   ArrayList<Photo> uploadPhotos(@Body CreatePhotoEntity entity);

   @POST("/api/social/posts")
   TextualPost createPhotoPost(@Body CreatePhotoPostEntity createPhotoPostEntity);

   @GET("/api/bucket_list/locations")
   ArrayList<PopularBucketItem> getPopularLocations();

   @GET("/api/bucket_list/activities")
   ArrayList<PopularBucketItem> getPopularActivities();

   @GET("/api/bucket_list/dinings")
   ArrayList<PopularBucketItem> getPopularDining();

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

   @GET("/api/social/friends/requests")
   ArrayList<User> getRequests();

   @FormUrlEncoded
   @POST("/api/social/posts")
   TextualPost post(@Field("description") String description);

   @PUT("/api/social/posts/{uid}")
   TextualPost editPost(@Path("uid") String uid, @Body CreatePhotoPostEntity createPhotoPostEntity);

   @DELETE("/api/social/posts/{uid}")
   Void deletePost(@Path("uid") String uid);

   @FormUrlEncoded
   @POST("/api/social/circles/{circle_id}/users")
   Void addToGroup(@Path("circle_id") String groupId, @Field("user_ids[]") List<String> userIds);

   @FormUrlEncoded
   @DELETE_WITH_BODY("/api/social/circles/{circle_id}/users")
   Void deleteFromGroup(@Path("circle_id") String groupId, @Field("user_ids[]") List<String> userIds);

  @GET("/api/{uid}")
   FeedEntityHolder getFeedEntity(@Path("uid") String uid);
}
