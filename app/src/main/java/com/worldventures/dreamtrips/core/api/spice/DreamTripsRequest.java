package com.worldventures.dreamtrips.core.api.spice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.mobileconnectors.s3.transfermanager.model.UploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.common.collect.Collections2;
import com.google.gson.JsonObject;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Activity;
import com.worldventures.dreamtrips.core.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.core.model.Inspiration;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.Region;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.SuccessStory;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.core.model.TripDetails;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.model.bucket.BucketItem;
import com.worldventures.dreamtrips.core.model.bucket.BucketPostItem;
import com.worldventures.dreamtrips.core.model.bucket.PopularBucketItem;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.uploader.Constants;
import com.worldventures.dreamtrips.core.uploader.UploadingFileManager;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.utils.busevents.PhotoUploadFailedEvent;
import com.worldventures.dreamtrips.utils.busevents.PhotoUploadFinished;
import com.worldventures.dreamtrips.utils.busevents.PhotoUploadStarted;
import com.worldventures.dreamtrips.utils.busevents.UploadProgressUpdateEvent;
import com.worldventures.dreamtrips.view.fragment.BucketTabsFragment;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.mime.TypedFile;

public abstract class DreamTripsRequest<T> extends RetrofitSpiceRequest<T, DreamTripsApi> {
    private static final long DELTA = 30 * 60 * 1000;
    private static final long DELTA_BUCKET = 3 * 60 * 1000;

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

    public static class AddBucketItem extends DreamTripsRequest<BucketItem> {
        private BucketPostItem bucketPostItem;

        public AddBucketItem(BucketPostItem bucketPostItem) {
            super(BucketItem.class);
            this.bucketPostItem = bucketPostItem;
        }

        @Override
        public BucketItem loadDataFromNetwork() {
            return getService().createItem(bucketPostItem);
        }
    }

    public static class MarkBucketItem extends DreamTripsRequest<BucketItem> {
        private BucketPostItem bucketPostItem;
        private int id;

        public MarkBucketItem(int id, BucketPostItem bucketPostItem) {
            super(BucketItem.class);
            this.bucketPostItem = bucketPostItem;
            this.id = id;
        }

        @Override
        public BucketItem loadDataFromNetwork() {
            return getService().markItem(id, bucketPostItem);
        }
    }

    public static class DeleteBucketItem extends DreamTripsRequest<JsonObject> {
        private int id;

        public DeleteBucketItem(int id) {
            super(JsonObject.class);
            this.id = id;
        }

        @Override
        public JsonObject loadDataFromNetwork() {
            return getService().deleteItem(id);
        }
    }


    public static class GetBucketList extends DreamTripsRequest<ArrayList<BucketItem>> {

        private BucketTabsFragment.Type type;
        private boolean fromNetwork;
        private SnappyRepository snappyRepository;
        private Prefs prefs;

        public GetBucketList(Prefs prefs, SnappyRepository snappyRepository, BucketTabsFragment.Type type, boolean fromNetwork) {
            super((Class<ArrayList<BucketItem>>) new ArrayList<BucketItem>().getClass());
            this.fromNetwork = fromNetwork;
            this.type = type;
            this.snappyRepository = snappyRepository;
            this.prefs = prefs;
        }

        @Override
        public ArrayList<BucketItem> loadDataFromNetwork() throws Exception {
            ArrayList<BucketItem> resultList = new ArrayList<>();

            if (needUpdate() || fromNetwork) {
                ArrayList<BucketItem> list = getService().getBucketList();

                ArrayList<BucketItem> activtyList = new ArrayList<>();
                ArrayList<BucketItem> locationList = new ArrayList<>();

                activtyList.addAll(Collections2.filter(list,
                        (bucketItem) -> bucketItem.getType().equalsIgnoreCase(BucketTabsFragment.Type.ACTIVITIES.getName())));
                locationList.addAll(Collections2.filter(list,
                        (bucketItem) -> bucketItem.getType().equalsIgnoreCase(BucketTabsFragment.Type.LOCATIONS.getName())));

                snappyRepository.saveBucketList(activtyList, BucketTabsFragment.Type.ACTIVITIES.name());
                snappyRepository.saveBucketList(locationList, BucketTabsFragment.Type.LOCATIONS.name());

                resultList.addAll(Collections2.filter(list,
                        (bucketItem) -> bucketItem.getType().equalsIgnoreCase(type.getName())));
            } else {
                resultList.addAll(snappyRepository.readBucketList(type.name()));
            }

            return resultList;
        }

        private boolean needUpdate() throws ExecutionException, InterruptedException {
            long current = Calendar.getInstance().getTimeInMillis();
            return current - prefs.getLong(Prefs.LAST_SYNC_BUCKET) > DELTA_BUCKET && snappyRepository.isEmpty(SnappyRepository.BUCKET_LIST);
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

    public static class GetPopularLocation extends DreamTripsRequest<ArrayList<PopularBucketItem>> {

        private BucketTabsFragment.Type type;

        public GetPopularLocation(BucketTabsFragment.Type type) {
            super((Class<ArrayList<PopularBucketItem>>) new ArrayList<PopularBucketItem>().getClass());
            this.type = type;
        }

        @Override
        public ArrayList<PopularBucketItem> loadDataFromNetwork() throws Exception {
            if (type.equals(BucketTabsFragment.Type.LOCATIONS)) {
                return getService().getPopularLocations();
            } else {
                return getService().getPopularActivities();
            }
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


        @Inject
        SnappyRepository db;

        private int currentUserId;
        int perPage;
        int page;

        public GetMyPhotos(int currentUserId, int perPage, int page) {
            super((Class<ArrayList<IFullScreenAvailableObject>>) new ArrayList<IFullScreenAvailableObject>().getClass());
            this.currentUserId = currentUserId;
            this.perPage = perPage;
            this.page = page;
        }

        @Override
        public ArrayList<IFullScreenAvailableObject> loadDataFromNetwork() throws Exception {
            ArrayList<Photo> myPhotos = getService().getMyPhotos(currentUserId, perPage, page);
            List<ImageUploadTask> uploadTasks = getUploadTasks();
            ArrayList<IFullScreenAvailableObject> result = new ArrayList<>();
            result.addAll(uploadTasks);
            result.addAll(myPhotos);
            return result;
        }

        private List<ImageUploadTask> getUploadTasks() {
            List<ImageUploadTask> list = db.getAllImageUploadTask();
            Collections.reverse(list);
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
            ArrayList<SuccessStory> successStores = getService().getSuccessStores();

           /* ArrayList<SuccessStory> successStores = new ArrayList<>();
            successStores.add(new SuccessStory());
            successStores.add(new SuccessStory());
            successStores.add(new SuccessStory());
            successStores.add(new SuccessStory());
            successStores.add(new SuccessStory());
            successStores.add(new SuccessStory());*/
            return successStores;
        }
    }


    public static class LikeSS extends DreamTripsRequest<JsonObject> {
        private int ssId;

        public LikeSS(int ssId) {
            super(JsonObject.class);
            this.ssId = ssId;
        }

        @Override
        public JsonObject loadDataFromNetwork() throws Exception {
            return getService().likeSS(ssId);
        }
    }


    public static class UnlikeSS extends DreamTripsRequest<JsonObject> {

        private int ssId;

        public UnlikeSS(int ssId) {
            super(JsonObject.class);
            this.ssId = ssId;
        }

        @Override
        public JsonObject loadDataFromNetwork() throws Exception {
            return getService().unlikeSS(ssId);
        }
    }

    public static class GetRegionsRequest extends DreamTripsRequest<ArrayList<Region>> {

        private SnappyRepository db;

        public GetRegionsRequest(SnappyRepository snappyRepository) {
            super((Class<ArrayList<Region>>) new ArrayList<Region>().getClass());
            this.db = snappyRepository;
        }

        @Override
        public ArrayList<Region> loadDataFromNetwork() throws Exception {
            ArrayList<Region> data = new ArrayList<>();
            if (db.isEmpty(SnappyRepository.REGIONS)) {
                data.addAll(getService().getRegions());
                db.putList(data, SnappyRepository.REGIONS, Region.class);

            } else {
                data.addAll(db.readList(SnappyRepository.REGIONS, Region.class));
            }
            return data;
        }
    }


    public static class GetActivitiesRequest extends DreamTripsRequest<ArrayList<Activity>> {

        private SnappyRepository db;

        public GetActivitiesRequest(SnappyRepository snappyRepository) {
            super((Class<ArrayList<Activity>>) new ArrayList<Activity>().getClass());
            this.db = snappyRepository;
        }

        @Override
        public ArrayList<Activity> loadDataFromNetwork() throws Exception {
            ArrayList<Activity> data = new ArrayList<>();
            if (db.isEmpty(SnappyRepository.ACTIVITIES)) {
                data.addAll(getService().getActivities());
                db.putList(data, SnappyRepository.ACTIVITIES, Activity.class);

            } else {
                data.addAll(db.readList(SnappyRepository.ACTIVITIES, Activity.class));
            }
            return data;
        }
    }

    public static class GetTripsRequest extends DreamTripsRequest<ArrayList<Trip>> {

        private SnappyRepository db;
        private boolean fromApi;
        private Prefs prefs;

        public GetTripsRequest(SnappyRepository snappyRepository, Prefs prefs, boolean fromApi) {
            super((Class<ArrayList<Trip>>) new ArrayList<Trip>().getClass());
            this.fromApi = fromApi;
            this.prefs = prefs;
            this.db = snappyRepository;
        }

        @Override
        public ArrayList<Trip> loadDataFromNetwork() throws Exception {
            ArrayList<Trip> data = new ArrayList<>();
            try {
                if (needUpdate() || fromApi) {
                    this.fromApi = false;
                    data.addAll(getService().getTrips());
                    db.saveTrips(data);
                    prefs.put(Prefs.LAST_SYNC, Calendar.getInstance().getTimeInMillis());
                } else {
                    data.addAll(db.getTrips());
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return data;
        }

        private boolean needUpdate() throws ExecutionException, InterruptedException {
            long current = Calendar.getInstance().getTimeInMillis();
            return current - prefs.getLong(Prefs.LAST_SYNC) > DELTA && db.isEmpty(SnappyRepository.TRIP_KEY);
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

        @Inject
        SnappyRepository db;

        @Override
        public Photo loadDataFromNetwork() {
            try {
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
                        Log.v("Progress event", "send UploadProgressUpdateEvent:" + l);
                        eventBus.post(new UploadProgressUpdateEvent(uploadTask.getTaskId(), (int) l));
                    }
                };

                uploadHandler.addProgressListener(progressListener);

                UploadResult uploadResult = null;

                uploadResult = uploadHandler.waitForUploadResult();


                uploadTask.setOriginUrl(getURLFromUploadResult(uploadResult));

                eventBus.post(new UploadProgressUpdateEvent(uploadTask.getTaskId(), 100));

                db.removeImageUploadTask(uploadTask);

                Photo photo = getService().uploadTripPhoto(uploadTask);
                photo.setTaskId(uploadTask.getTaskId());
                eventBus.post(new PhotoUploadFinished(photo));
                return photo;
            } catch (Exception e) {
                eventBus.post(new PhotoUploadFailedEvent(uploadTask.getTaskId()));
            }
            return null;
        }

        private String getURLFromUploadResult(UploadResult uploadResult) {
            return "https://" + uploadResult.getBucketName() + ".s3.amazonaws.com/" + uploadResult.getKey();
        }
    }



}
