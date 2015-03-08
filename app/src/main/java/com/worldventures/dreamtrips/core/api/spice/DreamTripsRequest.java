package com.worldventures.dreamtrips.core.api.spice;

import android.content.Context;
import android.util.Log;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.mobileconnectors.s3.transfermanager.model.UploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.gson.JsonObject;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.core.model.Inspiration;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.SuccessStory;
import com.worldventures.dreamtrips.core.model.TripDetails;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.repository.Repository;
import com.worldventures.dreamtrips.core.uploader.Constants;
import com.worldventures.dreamtrips.core.uploader.UploadingFileManager;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.utils.busevents.PhotoUploadFinished;
import com.worldventures.dreamtrips.utils.busevents.PhotoUploadStarted;
import com.worldventures.dreamtrips.utils.busevents.UploadProgressUpdateEvent;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;
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
            Log.i("LoadNext", "per page: " + perPage + "; page:" + page);

            return getService().getInspirationsPhotos(perPage, page);
        }
    }

    public static class GetMyPhotos extends DreamTripsRequest<ArrayList<IFullScreenAvailableObject>> {

        private Context context;
        private int currentUserId;
        int perPage;
        int page;

        public GetMyPhotos(Context context, int currentUserId, int perPage, int page) {
            super((Class<ArrayList<IFullScreenAvailableObject>>) new ArrayList<IFullScreenAvailableObject>().getClass());
            this.context = context;
            this.currentUserId = currentUserId;
            this.perPage = perPage;
            this.page = page;
        }

        @Override
        public ArrayList<IFullScreenAvailableObject> loadDataFromNetwork() throws Exception {
            ArrayList<Photo> myPhotos = getService().getMyPhotos(currentUserId, perPage, page);
            List<ImageUploadTask> uploadTasks = getUploadTasks();
            ArrayList<IFullScreenAvailableObject> result = new ArrayList<>();
            result.addAll(ImageUploadTask.from(uploadTasks));
            result.addAll(myPhotos);
            return result;
        }

        private List<ImageUploadTask> getUploadTasks() {
            Repository<ImageUploadTask> repository = new Repository<>(Realm.getInstance(context), ImageUploadTask.class);
            RealmResults<ImageUploadTask> all = repository.query().findAll();
            List<ImageUploadTask> list = Arrays.asList(all.toArray(new ImageUploadTask[all.size()]));
            Collections.reverse(ImageUploadTask.copy(list));

            return list;
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

    public static class GetSuccessStores extends DreamTripsRequest<ArrayList<SuccessStory>> {

        public GetSuccessStores() {
            super((Class<ArrayList<SuccessStory>>) new ArrayList<SuccessStory>().getClass());
        }

        @Override
        public ArrayList<SuccessStory> loadDataFromNetwork() throws Exception {
        //    ArrayList<SuccessStory> successStores = getService().getSuccessStores();

            ArrayList<SuccessStory> successStores = new ArrayList<>();
            successStores.add(new SuccessStory());
            successStores.add(new SuccessStory());
            successStores.add(new SuccessStory());
            successStores.add(new SuccessStory());
            successStores.add(new SuccessStory());
            successStores.add(new SuccessStory());
            return successStores;
        }
    }

    public static class UploadTripPhoto extends DreamTripsRequest<Photo> {

        @Inject
        transient TransferManager transferManager;
        @Inject
        transient UploadingFileManager uploadingFileManager;
        @Inject
        @Global
        transient EventBus eventBus;
        @Inject
        transient Context context;

        transient double byteTransferred;
        transient int lastPercent;

        private ImageUploadTask uploadTask;

        public UploadTripPhoto(ImageUploadTask uploadTask) {
            super(Photo.class);
            this.uploadTask = uploadTask;
        }

        @Override
        public Photo loadDataFromNetwork() throws Exception {
            eventBus.post(new PhotoUploadStarted(uploadTask));

            File file = this.uploadingFileManager.copyFileIfNeed(uploadTask.getFileUri());

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("");
            Upload uploadHandler = transferManager.upload(
                    Constants.BUCKET_NAME.toLowerCase(Locale.US),
                    Constants.BUCKET_ROOT_PATH + file.getName(),
                    new FileInputStream(file), metadata
            );

            ProgressListener progressListener = progressEvent -> {
                byteTransferred += progressEvent.getBytesTransferred();
                double l = byteTransferred / file.length() * 100;
                if (l > lastPercent + 5 || l > 99) {
                    lastPercent = (int) l;

                    eventBus.post(new UploadProgressUpdateEvent(uploadTask.getTaskId(), (int) l));
                }
            };

            uploadHandler.addProgressListener(progressListener);

            UploadResult uploadResult = uploadHandler.waitForUploadResult();

            uploadTask.setOriginUrl(getURLFromUploadResult(uploadResult));

            eventBus.post(new UploadProgressUpdateEvent(uploadTask.getTaskId(), 100));

            Repository<ImageUploadTask> repository = new Repository<ImageUploadTask>(Realm.getInstance(context), ImageUploadTask.class);
            ImageUploadTask cursor = repository.query().equalTo("taskId", uploadTask.getTaskId()).findFirst();
            repository.remove(cursor);

            Photo photo = getService().uploadTripPhoto(uploadTask);
            photo.setTaskId(uploadTask.getTaskId());
            eventBus.post(new PhotoUploadFinished(photo));
            return photo;
        }

        private String getURLFromUploadResult(UploadResult uploadResult) {
            return "https://" + uploadResult.getBucketName() + ".s3.amazonaws.com/" + uploadResult.getKey();
        }
    }


}
