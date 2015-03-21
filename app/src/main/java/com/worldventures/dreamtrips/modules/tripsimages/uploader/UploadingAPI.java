package com.worldventures.dreamtrips.modules.tripsimages.uploader;

import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import retrofit.http.Body;
import retrofit.http.POST;

public interface UploadingAPI {

    @POST("/api/photos")
    public Photo uploadTripPhoto(@Body ImageUploadTask uploadTask);
}
