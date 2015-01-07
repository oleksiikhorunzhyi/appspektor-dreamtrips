package com.worldventures.dreamtrips.core.api;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.model.Image;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.response.ListPhotoResponse;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

public interface DreamTripsApi {
    String DEFAULT_URL = BuildConfig.DreamTripsApi;

    @FormUrlEncoded
    @POST("/api/sessions")
    public void getSession(@Field("username") String username, @Field("password") String password, Callback<Session> callback);

    @POST("/api/profile/avatar")
    @Multipart
    public void uploadAvatar(@Header("Authorization") String authorization, @Part("avatar") TypedFile image, Callback<Image> callback);

    @GET("/api/photos")
    public void getUserPhotos(@Header("Authorization") String authorization, Callback<ListPhotoResponse> callback);

    @GET("/api/users/{id}/photos")
    public void getMyPhotos(@Header("Authorization") String authorization, @Path("id") int currentUserId, Callback<ListPhotoResponse> callback);
}
