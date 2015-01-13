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
import com.worldventures.dreamtrips.utils.Logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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

    private User currentUser;

    public DataManager(Injector injector) {
        injector.inject(this);
    }

    public void uploadAvatar(SessionManager sessionManager, File image, Result<Image> result) {
        String token = getTokenForQuery(sessionManager);
        dreamTripsApi.uploadAvatar(token, new TypedFile("image/*", image), convert(result));
    }

    private String getTokenForQuery(SessionManager sessionManager) {
        return "Token token=" + sessionManager.getCurrentSession();
    }

    public void getMemberPhotos(SessionManager sessionManager, Result<ListPhotoResponse> response) {
        Callback<ListPhotoResponse> callback = convert(response);
        dreamTripsApi.getUserPhotos(getTokenForQuery(sessionManager), callback);
    }

    public void getYouShouldBeHerePhotos(SessionManager sessionManager, Result<ListPhotoResponse> result) {
        result.response(new ListPhotoResponse(), null);
    }

    public void getMyPhotos(SessionManager sessionManager, Result<ListPhotoResponse> response) {
        Callback<ListPhotoResponse> callback = convert(response);
        dreamTripsApi.getMyPhotos(getTokenForQuery(sessionManager), currentUser.getId(), callback);
    }

    public void getSession(String username, String password, Result<Session> result) {
        Callback<Session> callback = convert(result);
        dreamTripsApi.getSession(username, password, callback);
    }

    public void getToken(String username, String password, Result<JsonObject> result) {
        Callback<JsonObject> callback = convert(result);
        worldVenturesApi.getToken(username, password, callback);
    }

    public void getWebSiteDocumentsByCountry(Result<JsonObject> result) {
        Callback<JsonObject> callback = convert(result);
        sharedServicesApi.getWebSiteDocumentsByCountry(callback);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
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
                    Logs.d(error.toString());
                    if (error.getResponse().getBody() != null) {
                        String errorString = convertStreamToString(error.getResponse().getBody().in());
                        Logs.d(errorString);
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
            Logs.e(e);
        }

        return sb.toString();
    }

    public static interface Result<T> {
        void response(T t, Exception e);
    }
}
