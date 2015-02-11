package com.worldventures.dreamtrips.core.api;

import com.google.common.base.Supplier;
import com.google.gson.JsonObject;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Annotations.Private;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.model.Activity;
import com.worldventures.dreamtrips.core.model.Inspiration;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.Region;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.model.TripDetails;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.session.AppSessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

import org.apache.http.HttpStatus;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

public class DreamTripsApiProxy implements DreamTripsApi {

    @Inject
    @Private
    DreamTripsApi dreamTripsApi;

    @Inject
    WorldVenturesApi worldVenturesApi;

    @Inject
    AppSessionHolder appSessionHolder;

    @Inject
    S3Api s3Api;

    @Inject
    @Global
    EventBus eventBus;
    @Inject
    LoginHelper loginHelper;

    public DreamTripsApiProxy(Injector injector) {
        injector.inject(this);
    }

    @Override
    public void login(@Field("username") String username, @Field("password") String password, Callback<Session> callback) {
        dreamTripsApi.login(username, password, callback);
    }

    @Override
    public Session login(@Field("username") String username, @Field("password") String password) {
        return dreamTripsApi.login(username, password);
    }

    @Override
    public void uploadAvatar(@Part("avatar") TypedFile image, Callback<User> callback) {
        runApiMethodAsync(callback, proxyCallback -> dreamTripsApi.uploadAvatar(image, proxyCallback));
    }


    @Override
    public List<Trip> getTrips() {
        return runApiMethodSync(dreamTripsApi::getTrips);
    }

    @Override
    public List<Region> getRegions() {
        return runApiMethodSync(dreamTripsApi::getRegions);
    }

    @Override
    public void getUserPhotos(@Path("per_page") int perPage, @Path("page") int page, Callback<List<Photo>> callback) {
        runApiMethodAsync(callback, proxyCallback -> dreamTripsApi.getUserPhotos(perPage, page, proxyCallback));
    }

    @Override
    public void getMyPhotos(@Path("id") int currentUserId, @Path("per_page") int perPage, @Path("page") int page, Callback<List<Photo>> callback) {
        runApiMethodAsync(callback, proxyCallback -> dreamTripsApi.getMyPhotos(currentUserId, perPage, page, proxyCallback));
    }

    @Override
    public void getInspirationsPhotos(@Path("per_page") int perPage, @Path("page") int page, Callback<List<Inspiration>> callback) {
        runApiMethodAsync(callback, proxyCallback -> dreamTripsApi.getInspirationsPhotos(perPage, page, proxyCallback));
    }

    @Override
    public void getYouShoulBeHerePhotos(@Path("per_page") int perPage, @Path("page") int page, Callback<List<Photo>> callback) {
        runApiMethodAsync(callback, proxyCallback -> dreamTripsApi.getYouShoulBeHerePhotos(perPage, page, proxyCallback));
    }

    @Override
    public void flagPhoto(@Path("id") int photoId, @Field("reason") String nameOfReason, Callback<JsonObject> callback) {
        runApiMethodAsync(callback, proxyCallback -> dreamTripsApi.flagPhoto(photoId, nameOfReason, proxyCallback));
    }

    @Override
    public void deletePhoto(@Path("id") int photoId, Callback<JsonObject> callback) {
        runApiMethodAsync(callback, proxyCallback -> dreamTripsApi.deletePhoto(photoId, proxyCallback));
    }

    @Override
    public void likePhoto(@Path("id") int photoId, Callback<JsonObject> callback) {
        runApiMethodAsync(callback, proxyCallback -> dreamTripsApi.likePhoto(photoId, proxyCallback));
    }

    @Override
    public void unlikePhoto(@Path("id") int photoId, Callback<JsonObject> callback) {
        runApiMethodAsync(callback, proxyCallback -> dreamTripsApi.unlikePhoto(photoId, proxyCallback));
    }

    @Override
    public void postPhoto(@Body Photo photo) {
        dreamTripsApi.postPhoto(photo);
    }

    @Override
    public void likeTrip(@Path("id") int photoId, Callback<JsonObject> callback) {
        runApiMethodAsync(callback, proxyCallback -> dreamTripsApi.likeTrip(photoId, proxyCallback));
    }

    @Override
    public void unlikeTrio(@Path("id") int photoId, Callback<JsonObject> callback) {
        runApiMethodAsync(callback, proxyCallback -> dreamTripsApi.unlikeTrio(photoId, proxyCallback));
    }

    @Override
    public void getDetails(@Path("id") int tripId, Callback<TripDetails> callback) {
        runApiMethodAsync(callback, proxyCallback -> dreamTripsApi.getDetails(tripId, proxyCallback));
    }

    @Override
    public List<Activity> getActivities() {
        return runApiMethodSync(dreamTripsApi::getActivities);
    }


    private <T> T runApiMethodSync(Supplier<T> e) {
        try {
            return e.get();
        } catch (RetrofitError error) {
            if (isLoginError(error) && isCredentialExist()) {
                String username = appSessionHolder.get().get().getUsername();
                String userPassword = appSessionHolder.get().get().getUserPassword();
                if (loginHelper.loginSync(username, userPassword)) return e.get();
            }
            return null;
        }
    }

    private <T> void runApiMethodAsync(Callback<T> callback, Executor<T> executor) {
        Callback<T> proxy = new Callback<T>() {
            @Override
            public void success(T t, Response response) {
                callback.success(t, response);
            }

            @Override
            public void failure(RetrofitError error) {
                if (isLoginError(error) && isCredentialExist()) {
                    String username = appSessionHolder.get().get().getUsername();
                    String userPassword = appSessionHolder.get().get().getUserPassword();
                    loginHelper.login(executor, callback, username, userPassword);

                } else {
                    callback.failure(error);
                }
            }
        };
        executor.execute(proxy);
    }


    private boolean isCredentialExist() {
        UserSession userSession = appSessionHolder.get().get();
        return userSession.getUsername() != null && userSession.getUserPassword() != null;
    }

    private boolean isLoginError(RetrofitError error) {
        return error.getResponse() != null && error.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED;
    }


}
