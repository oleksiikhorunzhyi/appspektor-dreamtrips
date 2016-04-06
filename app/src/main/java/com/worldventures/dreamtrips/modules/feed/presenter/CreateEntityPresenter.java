package com.worldventures.dreamtrips.modules.feed.presenter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.PhotoUploadSubscriber;
import com.worldventures.dreamtrips.core.api.UploadPurpose;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.Position;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.feed.api.CreatePostCommand;
import com.worldventures.dreamtrips.modules.feed.api.UploadPhotosCommand;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoEntity;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import timber.log.Timber;

public abstract class CreateEntityPresenter<V extends CreateEntityPresenter.View> extends ActionEntityPresenter<V> {

    public static final int MAX_PHOTOS_COUNT = 15;

    @State
    ArrayList<PhotoCreationItem> cachedCreationItems = new ArrayList<>();
    @State
    Location location;

    @Inject
    MediaPickerManager mediaPickerManager;

    PhotoUploadSubscriber photoUploadSubscriber;
    private Subscription mediaSubscription;

    @Override
    public void takeView(V view) {
        if (cachedCreationItems == null) cachedCreationItems = new ArrayList<>();
        //
        super.takeView(view);
        photoUploadSubscriber = new PhotoUploadSubscriber();
        photoUploadingManager.getTaskChangingObservable(UploadPurpose.TRIP_IMAGE).subscribe(photoUploadSubscriber);
        photoUploadSubscriber.afterEach(uploadTask -> {
            PhotoCreationItem task = Queryable.from(cachedCreationItems).firstOrDefault(cachedTask -> cachedTask.getId() == uploadTask.getId());
            if (task != null) {
                task.setStatus(uploadTask.getStatus());
                task.setOriginUrl(uploadTask.getOriginUrl());
                view.updateItem(cachedCreationItems.indexOf(task));

                if (task.getStatus() == UploadTask.Status.COMPLETED) {
                    ConnectableObservable<Bitmap> observable = ImageUtils.getBitmap(context, Uri.parse(task.getOriginUrl()), 0, 0).publish();

                    requestFaceRecognition(observable).subscribe(suggestions -> {
                        task.setSuggestions(suggestions);
                        view.updateItem(cachedCreationItems.indexOf(task));
                    });

                    observable.subscribe(bitmap -> {
                        task.setWidth(bitmap.getWidth());
                        task.setHeight(bitmap.getHeight());
                    });

                    observable.connect();
                }
            }
            //
            invalidateDynamicViews();
        });
        Queryable.from(photoUploadingManager.getUploadTasks(UploadPurpose.TRIP_IMAGE)).forEachR(photoUploadSubscriber::onNext);
        //
        mediaSubscription = mediaPickerManager.toObservable()
                .filter(attachment -> attachment.requestId == getMediaRequestId())
                .subscribe(this::attachImages);
    }

    protected Observable<List<PhotoTag>> requestFaceRecognition(Observable<Bitmap> observable) {
        return observable.compose(bitmapObservable -> ImageUtils.getRecognizedFaces(context, bitmapObservable))
                .doOnError(throwable -> Timber.e(throwable, ""))
                .flatMap(Observable::from)
                .toSortedList(Position.SORT_BY_POSITION);
    }


    public abstract int getMediaRequestId();

    @Override
    public void dropView() {
        super.dropView();
        if (!photoUploadSubscriber.isUnsubscribed()) photoUploadSubscriber.unsubscribe();
        //
        if (!mediaSubscription.isUnsubscribed()) mediaSubscription.unsubscribe();
    }

    @Override
    protected void updateUi() {
        super.updateUi();
        //
        if (!isCachedUploadTaskEmpty()) view.attachPhotos(cachedCreationItems);
        //
        invalidateDynamicViews();
    }

    @Override
    protected boolean isChanged() {
        return !isCachedTextEmpty()
                || (cachedCreationItems.size() > 0 && isEntitiesReadyToPost());
    }

    @Override
    public void post() {
        if (!isCachedTextEmpty() && isCachedUploadTaskEmpty()) {
            createPost(null);
        } else {
            CreatePhotoEntity createPhotoEntity = new CreatePhotoEntity();
            Queryable.from(cachedCreationItems).forEachR(item -> createPhotoEntity
                    .addPhoto(new CreatePhotoEntity.PhotoEntity.Builder()
                            .originUrl(item.getOriginUrl())
                            .title(item.getTitle())
                            .width(item.getWidth())
                            .height(item.getHeight())
                            .date(Calendar.getInstance().getTime())
                            .coordinates(new CreatePhotoEntity.Coordinates(location.getLat(), location.getLng()))
                            .locationName(location.getName())
                            .tags(item.getCachedAddedPhotoTags())
                            .build()));
            if (!createPhotoEntity.isEmpty()) {
                doRequest(new UploadPhotosCommand(createPhotoEntity), this::createPost);
            }
        }
    }

    private void createPost(List<Photo> photos) {
        CreatePhotoPostEntity createPhotoPostEntity = new CreatePhotoPostEntity();
        createPhotoPostEntity.setDescription(cachedText);
        createPhotoPostEntity.setLocation(location);
        if (photos != null)
            Queryable.from(photos).forEachR(photo -> createPhotoPostEntity
                    .addAttachment(new CreatePhotoPostEntity.Attachment(photo.getUid())));
        doRequest(new CreatePostCommand(createPhotoPostEntity), this::processPostSuccess);
    }

    @Override
    protected void processPostSuccess(FeedEntity feedEntity) {
        super.processPostSuccess(feedEntity);
        eventBus.post(new FeedItemAddedEvent(FeedItem.create(feedEntity, getAccount())));
    }

    public int getRemainingPhotosCount() {
        return MAX_PHOTOS_COUNT - cachedCreationItems.size();
    }

    protected void processTagUploadSuccess(FeedEntity feedEntity) {
        super.processTagUploadSuccess(feedEntity); //Firstly update tags, then notify everyone else
        eventBus.post(new FeedItemAddedEvent(FeedItem.create(feedEntity, getAccount())));
    }

    @Override
    protected void invalidateDynamicViews() {
        super.invalidateDynamicViews();
        if (isChanged()) {
            view.enableButton();
        } else {
            view.disableButton();
        }
    }

    @Override
    public Location getLocation() {
        if (location == null) location = new Location();
        return location;
    }

    @Override
    public void updateLocation(Location location) {
        this.location = location;
    }

    public boolean removeImage(PhotoCreationItem item) {
        boolean removed = cachedCreationItems.remove(item);
        if (removed) {
            invalidateDynamicViews();
            view.enableImagePicker();
        }
        return removed;
    }

    public void attachImages(MediaAttachment mediaAttachment) {
        if (mediaAttachment.chosenImages == null || mediaAttachment.chosenImages.size() == 0) {
            return;
        }

        if (cachedCreationItems.size() + mediaAttachment.chosenImages.size() == MAX_PHOTOS_COUNT)
            view.disableImagePicker();

        imageSelected(mediaAttachment);
    }

    private void imageSelected(MediaAttachment mediaAttachment) {
        if (view != null) {
            List<PhotoCreationItem> newImages = new ArrayList<>();
            Queryable.from(mediaAttachment.chosenImages).forEachR(photo -> {
                PhotoCreationItem item = new PhotoCreationItem();
                item.setFilePath(photo.getThumbnailPath());
                item.setStatus(UploadTask.Status.STARTED);
                String type = "";
                switch (mediaAttachment.type) {
                    case PickImageDelegate.CAPTURE_PICTURE:
                        type = "camera";
                        break;
                    case PickImageDelegate.PICK_PICTURE:
                        type = "album";
                        break;
                    case PickImageDelegate.FACEBOOK:
                        type = "facebook";
                        break;
                }
                item.setMediaAttachmentType(type);
                newImages.add(item);
            });
            cachedCreationItems.addAll(newImages);
            view.attachPhotos(newImages);
            //
            Queryable.from(newImages).forEachR(this::startUpload);
        }
    }

    protected boolean isCachedUploadTaskEmpty() {
        return cachedCreationItems.size() == 0;
    }

    protected boolean isCachedTextEmpty() {
        return TextUtils.isEmpty(cachedText);
    }

    protected boolean isEntitiesReadyToPost() {
        return Queryable.from(cachedCreationItems).firstOrDefault(item -> item.getStatus() != UploadTask.Status.COMPLETED) == null;
    }

    @Override
    public void onTagSelected(long requestId, ArrayList<PhotoTag> photoTags, ArrayList<PhotoTag> removedTags) {
        PhotoCreationItem item = Queryable.from(cachedCreationItems).firstOrDefault(element -> element.getId() == requestId);

        if (item != null) {
            item.getCachedAddedPhotoTags().removeAll(photoTags);
            item.getCachedAddedPhotoTags().addAll(photoTags);
            item.getCachedAddedPhotoTags().removeAll(removedTags);

            item.getCachedRemovedPhotoTags().removeAll(removedTags);
            item.getCachedRemovedPhotoTags().addAll(removedTags);
            //if view ==null state will be updated on attach view.
            if (view != null) {
                view.updateItem(cachedCreationItems.indexOf(item));
            }
        }
    }

    ////////////////////////////////////////
    /////// Photo upload
    ////////////////////////////////////////

    public void startUpload(PhotoCreationItem item) {
        UploadTask uploadTask = item.toUploadTask();
        long upload = photoUploadingManager.upload(uploadTask, UploadPurpose.TRIP_IMAGE);
        uploadTask.setId(upload);
        item.setId(upload);
    }

    public interface View extends ActionEntityPresenter.View {

        void updateItem(int position);

        void enableImagePicker();

        void disableImagePicker();
    }
}
