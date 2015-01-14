package com.worldventures.dreamtrips.core;

import com.google.gson.JsonObject;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.AuthApi;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;
import com.worldventures.dreamtrips.core.api.WorldVenturesApi;
import com.worldventures.dreamtrips.core.model.Image;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.model.response.ListPhotoResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;
import timber.log.Timber;

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

    public void getMemberPhotos(Result<ListPhotoResponse> response) {
        Callback<ListPhotoResponse> callback = convert(response);
        dreamTripsApi.getUserPhotos(callback);
    }

    public void getYouShouldBeHerePhotos(Result<ListPhotoResponse> result) {
        result.response(new ListPhotoResponse(), null);
    }

    public void getMyPhotos( Result<ListPhotoResponse> response) {
        Callback<ListPhotoResponse> callback = convert(response);
        dreamTripsApi.getMyPhotos(sessionManager.getCurrentUser().getId(), callback);
    }

    public void getSession(String username, String password, Result<Session> result) {
        Callback<Session> callback = convert(result);
        dreamTripsApi.login(username, password, callback);
    }

    public void getToken(String username, String password, Result<JsonObject> result) {
        Callback<JsonObject> callback = convert(result);
        worldVenturesApi.getToken(username, password, callback);
    }

    public void getWebSiteDocumentsByCountry(Result<JsonObject> result) {
        Callback<JsonObject> callback = convert(result);
        sharedServicesApi.getWebSiteDocumentsByCountry(callback);
    }

    private <T> Callback<T> convert(Result<T> result) {
        return new Callback<T>() {
            @Override
            public void success(T t, retrofit.client.Response r) {
                result.response(t, null);
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    Timber.d(error.toString());
                    if (error.getResponse().getBody() != null) {
                        String errorString = convertStreamToString(error.getResponse().getBody().in());
                        Timber.d(errorString);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                result.response(null, error);
            }
        };
    }

    private String convertStreamToString(java.io.InputStream in) throws IOException {
        InputStreamReader is = new InputStreamReader(in);
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();

            while (read != null) {
                sb.append(read);
                read = br.readLine();
            }
        } catch (Exception e) {
            Timber.e(e, "error during stream convert");
        }

        return sb.toString();
    }

    public static interface Result<T> {
        void response(T t, Exception e);
    }
}
