package com.worldventures.dreamtrips.core.uploader;

import com.worldventures.dreamtrips.core.model.Photo;

import retrofit.http.Body;
import retrofit.http.POST;

public interface UploadingAPI {

    @POST("/api/photos")
    public Photo uploadTripPhoto(@Body ImageUploadTask uploadTask);
}
