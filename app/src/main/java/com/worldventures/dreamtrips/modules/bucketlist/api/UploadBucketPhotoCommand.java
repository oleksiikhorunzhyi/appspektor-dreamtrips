package com.worldventures.dreamtrips.modules.bucketlist.api;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.api.MediaSpiceManager;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadCancelEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadCancelRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadFailedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadFinishEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadStarted;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.api.S3ImageUploader;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class UploadBucketPhotoCommand extends DreamTripsRequest<BucketPhoto> {

    @Inject
    @Global
    EventBus eventBus;
    @Inject
    BucketItemManager bucketItemManager;
    @Inject
    MediaSpiceManager mediaSpiceManager;
    @Inject
    SnappyRepository db;
    @ForApplication
    @Inject
    Context context;

    protected S3ImageUploader s3uploader = new S3ImageUploader();

    protected BucketPhotoUploadTask photoUploadTask;

    private BucketItem bucketItem;
    private BucketTabsPresenter.BucketType bucketType;

    public UploadBucketPhotoCommand(BucketPhotoUploadTask photoUploadTask, BucketItem bucketItem, BucketTabsPresenter.BucketType type, Injector injector) {
        super(BucketPhoto.class);

        this.photoUploadTask = photoUploadTask;
        this.bucketItem = bucketItem;
        this.bucketType = type;

        injector.inject(this);
        injector.inject(s3uploader);

        db.saveBucketPhotoTask(photoUploadTask);

        if (!mediaSpiceManager.isStarted()) {
            mediaSpiceManager.start(context);
        }
    }

    @Override
    public BucketPhoto loadDataFromNetwork() {
        eventBus.register(this);
        try {
            eventBus.post(new BucketPhotoUploadStarted(photoUploadTask));

            String fileUri = photoUploadTask.getFilePath();
            long taskId = photoUploadTask.getTaskId();

            String urlFromUploadResult = s3uploader.uploadImageToS3(fileUri, String.valueOf(taskId));

            BucketPhoto uploadObject = getUploadObject(urlFromUploadResult);

            BucketPhoto photo = null;
            if (isCancelled()) {
                eventBus.post(new BucketPhotoUploadCancelEvent(photoUploadTask));
            } else {
                photo = getService().uploadBucketPhoto(photoUploadTask.getBucketId(), uploadObject);
            }
            eventBus.post(new BucketPhotoUploadFinishEvent(photoUploadTask, photo));
            db.removeBucketPhotoTask(photoUploadTask);

            updateBucketItem(bucketItem, photo);
            return photo;
        } catch (Exception e) {
            Timber.e(e, "Can't load from network");
            photoUploadTask.setFailed(true);
            db.saveBucketPhotoTask(photoUploadTask);
            eventBus.post(new BucketPhotoUploadFailedEvent(photoUploadTask.getTaskId()));
        }

        eventBus.unregister(this);
        return null;
    }

    private void updateBucketItem(BucketItem updatedItem, BucketPhoto photo) {
        List<BucketItem> items = bucketItemManager.getBucketItems(bucketType);
        bucketItem.getPhotos().add(photo);

        int oldPosition = items.indexOf(updatedItem);
        BucketItem oldItem = items.get(oldPosition);
        int newPosition = (oldItem.isDone() && !updatedItem.isDone()) ? 0 : oldPosition;
        items.remove(oldPosition);
        items.add(newPosition, updatedItem);

        bucketItemManager.saveBucketItems(items, bucketType);
        eventBus.post(new BucketItemUpdatedEvent(updatedItem));
    }

    private BucketPhoto getUploadObject(String urlFromUploadResult) {
        BucketPhoto bucketPhoto = new BucketPhoto();
        bucketPhoto.setOriginUrl(urlFromUploadResult);
        return bucketPhoto;
    }

    public void onEvent(BucketPhotoUploadCancelRequestEvent event) {
        if (event.getModelObject().equals(photoUploadTask)) {
            eventBus.cancelEventDelivery(event);
        }
    }

}
