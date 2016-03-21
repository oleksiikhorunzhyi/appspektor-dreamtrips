package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.modules.bucketlist.model.PhotoUploadResponse;

import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

public interface UploaderyApi {

    @POST("/upload")
    @Multipart
    PhotoUploadResponse uploadPhoto(@Part("photo") TypedFile image);
}
