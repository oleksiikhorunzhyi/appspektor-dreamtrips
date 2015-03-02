package com.worldventures.dreamtrips.core.api.spice;

import com.google.gson.JsonObject;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Inspiration;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.TripDetails;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;

import java.util.ArrayList;

import retrofit.mime.TypedFile;

public abstract class DreamTripsRequest<T> extends RetrofitSpiceRequest<T, DreamTripsApi> {
    public DreamTripsRequest(Class<T> clazz) {
        super(clazz, DreamTripsApi.class);
    }

    public static class UploadAvatarRequest extends DreamTripsRequest<User> {
        private retrofit.mime.TypedFile type;

        public UploadAvatarRequest(TypedFile type) {
            super(User.class);
            this.type = type;
        }

        @Override
        public User loadDataFromNetwork() throws Exception {
            return getService().uploadAvatar(type);
        }
    }

    public static class Login extends DreamTripsRequest<Session> {

        private String username;
        private String password;

        public Login(String username, String password) {
            super(Session.class);
            this.username = username;
            this.password = password;
        }

        @Override
        public Session loadDataFromNetwork() throws Exception {
            return getService().login(username, password);
        }
    }


    public static class GetUserPhotos extends DreamTripsRequest<ArrayList<Photo>> {

        int perPage;
        int page;

        public GetUserPhotos(int perPage, int page) {
            super((Class<ArrayList<Photo>>) new ArrayList<Photo>().getClass());
            this.perPage = perPage;
            this.page = page;
        }

        @Override
        public ArrayList<Photo> loadDataFromNetwork() throws Exception {
            return getService().getUserPhotos(perPage, page);
        }
    }

    public static class GetYSBHPhotos extends DreamTripsRequest<ArrayList<Photo>> {

        int perPage;
        int page;

        public GetYSBHPhotos(int perPage, int page) {
            super((Class<ArrayList<Photo>>) new ArrayList<Photo>().getClass());
            this.perPage = perPage;
            this.page = page;
        }

        @Override
        public ArrayList<Photo> loadDataFromNetwork() throws Exception {
            return getService().getYouShoulBeHerePhotos(perPage, page);
        }
    }

    public static class GetInspireMePhotos extends DreamTripsRequest<ArrayList<Inspiration>> {

        int perPage;
        int page;

        public GetInspireMePhotos(int perPage, int page) {
            super((Class<ArrayList<Inspiration>>) new ArrayList<Inspiration>().getClass());
            this.perPage = perPage;
            this.page = page;
        }

        @Override
        public ArrayList<Inspiration> loadDataFromNetwork() throws Exception {
            return getService().getInspirationsPhotos(perPage, page);
        }
    }

    public static class GetMyPhotos extends DreamTripsRequest<ArrayList<Photo>> {

        private int currentUserId;
        int perPage;
        int page;

        public GetMyPhotos(int currentUserId, int perPage, int page) {
            super((Class<ArrayList<Photo>>) new ArrayList<Photo>().getClass());
            this.currentUserId = currentUserId;
            this.perPage = perPage;
            this.page = page;
        }

        @Override
        public ArrayList<Photo> loadDataFromNetwork() throws Exception {
            return getService().getMyPhotos(currentUserId, perPage, page);
        }
    }

    public static class FlagPhoto extends DreamTripsRequest<JsonObject> {

        private String reason;
        private int photoId;

        public FlagPhoto(int photoId, String reason) {
            super(JsonObject.class);
            this.reason = reason;
            this.photoId = photoId;
        }

        @Override
        public JsonObject loadDataFromNetwork() throws Exception {
            return getService().flagPhoto(photoId, reason);
        }
    }

    public static class DeletePhoto extends DreamTripsRequest<JsonObject> {

        private int photoId;

        public DeletePhoto(int photoId) {
            super(JsonObject.class);
            this.photoId = photoId;
        }

        @Override
        public JsonObject loadDataFromNetwork() throws Exception {
            return getService().deletePhoto(photoId);
        }
    }

    public static class LikePhoto extends DreamTripsRequest<JsonObject> {

        private int photoId;

        public LikePhoto(int photoId) {
            super(JsonObject.class);
            this.photoId = photoId;
        }

        @Override
        public JsonObject loadDataFromNetwork() throws Exception {
            return getService().likePhoto(photoId);
        }
    }

    public static class UnlikePhoto extends DreamTripsRequest<JsonObject> {

        private int photoId;

        public UnlikePhoto(int photoId) {
            super(JsonObject.class);
            this.photoId = photoId;
        }

        @Override
        public JsonObject loadDataFromNetwork() throws Exception {
            return getService().unlikePhoto(photoId);
        }
    }

    public static class LikeTrip extends DreamTripsRequest<JsonObject> {

        private int photoId;

        public LikeTrip(int photoId) {
            super(JsonObject.class);
            this.photoId = photoId;
        }

        @Override
        public JsonObject loadDataFromNetwork() throws Exception {
            return getService().likeTrip(photoId);
        }
    }

    public static class UnlikeTrip extends DreamTripsRequest<JsonObject> {

        private int photoId;

        public UnlikeTrip(int photoId) {
            super(JsonObject.class);
            this.photoId = photoId;
        }

        @Override
        public JsonObject loadDataFromNetwork() throws Exception {
            return getService().unlikeTrio(photoId);
        }
    }

    public static class GetDetails extends DreamTripsRequest<TripDetails> {

        private int tripId;

        public GetDetails(int tripId) {
            super(TripDetails.class);
            this.tripId = tripId;
        }

        @Override
        public TripDetails loadDataFromNetwork() throws Exception {
            return getService().getDetails(tripId);
        }
    }

    public static class UploadTripPhoto extends DreamTripsRequest<Photo> {


        private ImageUploadTask uploadTask;

        public UploadTripPhoto(ImageUploadTask uploadTask) {
            super(Photo.class);
            this.uploadTask = uploadTask;
        }

        @Override
        public Photo loadDataFromNetwork() throws Exception {
            return getService().uploadTripPhoto(uploadTask);
        }
    }


}
