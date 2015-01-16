package com.worldventures.dreamtrips.core;

import com.google.gson.JsonObject;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.AuthApi;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;
import com.worldventures.dreamtrips.core.api.WorldVenturesApi;
import com.worldventures.dreamtrips.core.model.Image;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.response.ListPhotoResponse;

import java.io.File;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

public class DataManager {

    @Inject
    protected DreamTripsApi dreamTripsApi;

    @Inject
    protected AuthApi authApi;

    @Inject
    protected WorldVenturesApi worldVenturesApi;

    @Inject
    protected SharedServicesApi sharedServicesApi;

    @Inject
    protected SessionManager sessionManager;

    public DataManager(Injector injector) {
        injector.inject(this);
    }

    public void uploadAvatar(File image, Result<Image> result) {
        dreamTripsApi.uploadAvatar(new TypedFile("image/*", image), convert(result));
    }

    public void getMemberPhotos(Result<ListPhotoResponse> resultCallback) {
        dreamTripsApi.getUserPhotos(convert(resultCallback));
    }

    public void getYouShouldBeHerePhotos(Result<ListPhotoResponse> result) {
        result.response(new ListPhotoResponse(), null);
    }

    public void getMyPhotos( Result<ListPhotoResponse> resultCallback) {
        dreamTripsApi.getMyPhotos(sessionManager.getCurrentUser().getId(), convert(resultCallback));
    }

    public void getSession(String username, String password, Result<Session> resultCallback) {
        dreamTripsApi.login(username, password, convert(resultCallback));
    }

    public void getToken(String username, String password, Result<JsonObject> resultCallback) {
        worldVenturesApi.getToken(username, password, convert(resultCallback));
    }

    public void getWebSiteDocumentsByCountry(Result<JsonObject> resultCallback) {
        sharedServicesApi.getWebSiteDocumentsByCountry(convert(resultCallback));
    }

    public void flagPhoto(SessionManager sessionManager, int photoId, String nameOfReason, Result<JsonObject> result) {
        Callback<JsonObject> callback = convert(result);
        dreamTripsApi.flagPhoto(photoId, nameOfReason, callback);
    }

    public void likePhoto(SessionManager sessionManager, int photoId, Result<JsonObject> result) {
        Callback<JsonObject> callback = convert(result);
        dreamTripsApi.likePhoto(photoId, callback);
    }

    public void unlikePhoto(SessionManager sessionManager, int photoId, Result<JsonObject> result) {
        Callback<JsonObject> callback = convert(result);
        dreamTripsApi.unlikePhoto(photoId, callback);
    }


    private <T> Callback<T> convert(Result<T> result) {
        return new Callback<T>() {
            @Override
            public void success(T t, retrofit.client.Response r) {
                result.response(t, null);
            }

            @Override
            public void failure(RetrofitError error) {
                result.response(null, error);
            }
        };
    }

    public static interface Result<T> {
        void response(T t, Exception e);
    }
}
