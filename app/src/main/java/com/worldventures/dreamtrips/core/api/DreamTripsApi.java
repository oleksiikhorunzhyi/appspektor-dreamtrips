package com.worldventures.dreamtrips.core.api;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.model.Image;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.response.ListPhotoResponse;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

public interface DreamTripsApi {

    @FormUrlEncoded
    @POST("/api/sessions")
    public void login(@Field("username") String username, @Field("password") String password, Callback<Session> callback);

    @POST("/api/profile/avatar")
    @Multipart
    public void uploadAvatar(@Part("avatar") TypedFile image, Callback<Image> callback);

    @GET("/api/photos")
    public void getUserPhotos(Callback<ListPhotoResponse> callback);

    @GET("/api/users/{id}/photos")
    public void getMyPhotos(@Path("id") int currentUserId, Callback<ListPhotoResponse> callback);

    @FormUrlEncoded
    @POST("/api/photos/{id}/flags")
    public void flagPhoto(@Path("id") int photoId, @Field("reason") String nameOfReason, Callback<JsonObject> callback);

    @POST("/api/photos/{id}/like")
    public void likePhoto(@Path("id") int photoId, Callback<JsonObject> callback);

    @DELETE("/api/photos/{id}/like")
    public void unlikePhoto(@Path("id") int photoId, Callback<JsonObject> callback);
}
