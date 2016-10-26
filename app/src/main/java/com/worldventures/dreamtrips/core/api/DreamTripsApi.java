package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.Suggestion;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoEntity;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;

import retrofit.http.Body;
import retrofit.http.DELETE;
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

   @PUT("/api/social/posts/{uid}")
   TextualPost editPost(@Path("uid") String uid, @Body CreatePhotoPostEntity createPhotoPostEntity);

   @DELETE("/api/social/posts/{uid}")
   Void deletePost(@Path("uid") String uid);

   @GET("/api/{uid}")
   FeedEntityHolder getFeedEntity(@Path("uid") String uid);
}
