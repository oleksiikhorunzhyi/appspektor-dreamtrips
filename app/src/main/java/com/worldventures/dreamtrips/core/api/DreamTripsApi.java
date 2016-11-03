package com.worldventures.dreamtrips.core.api;

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

public interface DreamTripsApi {

   @POST("/api/photos")
   ArrayList<Photo> uploadPhotos(@Body CreatePhotoEntity entity);

   @POST("/api/social/posts")
   TextualPost createPhotoPost(@Body CreatePhotoPostEntity createPhotoPostEntity);

   @PUT("/api/social/posts/{uid}")
   TextualPost editPost(@Path("uid") String uid, @Body CreatePhotoPostEntity createPhotoPostEntity);

   @DELETE("/api/social/posts/{uid}")
   Void deletePost(@Path("uid") String uid);

   @GET("/api/{uid}")
   FeedEntityHolder getFeedEntity(@Path("uid") String uid);
}
