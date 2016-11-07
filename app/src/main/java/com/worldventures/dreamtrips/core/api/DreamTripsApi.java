package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface DreamTripsApi {

   @POST("/api/photos")
   ArrayList<Photo> uploadPhotos(@Body CreatePhotoEntity entity);

   @GET("/api/{uid}")
   FeedEntityHolder getFeedEntity(@Path("uid") String uid);
}
