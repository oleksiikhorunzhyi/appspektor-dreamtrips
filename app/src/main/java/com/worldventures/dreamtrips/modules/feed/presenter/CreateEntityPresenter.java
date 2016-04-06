package com.worldventures.dreamtrips.modules.feed.presenter;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.PhotoUploadSubscriber;
import com.worldventures.dreamtrips.core.api.UploadPurpose;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.feed.api.NewPostCommand;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.ArrayList;

import javax.inject.Inject;

import icepick.State;
import rx.Subscription;

public abstract class CreateEntityPresenter<V extends CreateEntityPresenter.View> extends ActionEntityPresenter<V> {

    @State
    ArrayList<UploadTask> cachedTasks;
    @State
    Location location;

    @Inject
    MediaPickerManager mediaPickerManager;

    PhotoUploadSubscriber photoUploadSubscriber;
    private Subscription mediaSubscription;

    @Override
    public void takeView(V view) {
        super.takeView(view);
        photoUploadSubscriber = new PhotoUploadSubscriber();
        photoUploadingManager.getTaskChangingObservable(UploadPurpose.TRIP_IMAGE).subscribe(photoUploadSubscriber);
        photoUploadSubscriber.afterEach(uploadTask -> {
            UploadTask task = Queryable.from(cachedTasks).first(cachedTask -> cachedTask.getId() == uploadTask.getId());
            if (task != null) {
                task.setStatus(uploadTask.getStatus());
                view.updateItem(cachedTasks.indexOf(task));
            }
        });
        Queryable.from(photoUploadingManager.getUploadTasks(UploadPurpose.TRIP_IMAGE)).forEachR(photoUploadSubscriber::onNext);
        //
        mediaSubscription = mediaPickerManager.toObservable()
                .filter(attachment -> attachment.requestId == getMediaRequestId())
                .subscribe(this::attachImages);
        if (cachedTasks == null) cachedTasks = new ArrayList<>();
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
        if (!isCachedUploadTaskEmpty()) view.attachPhotos(cachedTasks);
        //
        invalidateDynamicViews();
    }

    @Override
    protected boolean isChanged() {
        //TODO changes
//        return !TextUtils.isEmpty(cachedText)
//                || (cachedUploadTask != null && cachedUploadTask.getStatus().equals(UploadTask.Status.COMPLETED))
//                || !cachedAddedPhotoTags.isEmpty() || !cachedRemovedPhotoTags.isEmpty();
        return false;
    }

    @Override
    public void post() {
        //TODO post logic
//        if (!isCachedUploadTaskEmpty() && UploadTask.Status.COMPLETED.equals(cachedUploadTask.getStatus())) {
//            cachedUploadTask.setTitle(cachedText);
//            cachedUploadTask.setShotAt(Calendar.getInstance().getTime());
//
//            doRequest(new AddTripPhotoCommand(cachedUploadTask), this::processPhotoSuccess, spiceException -> {
//                handleError(spiceException);
//                view.onPostError();
//            });
//        } else if (!isCachedTextEmpty() && isCachedUploadTaskEmpty()) {
//            postTextualUpdate();
//        }
        if (!isCachedTextEmpty() && isCachedUploadTaskEmpty()) {
            postTextualUpdate();
        }
    }

    protected void postTextualUpdate() {
        doRequest(new NewPostCommand(cachedText),
                this::processPostSuccess, spiceException -> {
                    handleError(spiceException);
                    view.onPostError();
                });
    }

    @Override
    protected void processPostSuccess(FeedEntity feedEntity) {
        super.processPostSuccess(feedEntity);
        eventBus.post(new FeedItemAddedEvent(FeedItem.create(feedEntity, getAccount())));
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

    public int removeImage(UploadTask task) {
        int position = cachedTasks.indexOf(task);
        cachedTasks.remove(task);
        return position;
    }

    public void attachImages(MediaAttachment mediaAttachment) {
        if (mediaAttachment.chosenImages == null || mediaAttachment.chosenImages.size() == 0 || !isCachedUploadTaskEmpty()) {
            return;
        }

        imageSelected(mediaAttachment);
    }

    private void imageSelected(MediaAttachment mediaAttachment) {
        if (view != null) {
            Queryable.from(mediaAttachment.chosenImages).forEachR(photo -> {
                UploadTask imageUploadTask = new UploadTask();
                imageUploadTask.setFilePath(photo.getThumbnailPath());
                imageUploadTask.setStatus(UploadTask.Status.STARTED);
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
                imageUploadTask.setType(type);
                cachedTasks.add(imageUploadTask);
            });
            view.attachPhotos(cachedTasks);
            //
            Queryable.from(cachedTasks).forEachR(task -> {
                doRequest(new CopyFileCommand(context, task.getFilePath()), s -> {
                    task.setFilePath(s);
                    startUpload(task);
                });
            });
        }
    }

    protected boolean isCachedUploadTaskEmpty() {
        return cachedTasks != null && cachedTasks.size() == 0;
    }

    protected boolean isCachedTextEmpty() {
        return TextUtils.isEmpty(cachedText);
    }

    ////////////////////////////////////////
    /////// Photo upload
    ////////////////////////////////////////

    public void startUpload(UploadTask uploadTask) {
        long upload = photoUploadingManager.upload(uploadTask, UploadPurpose.TRIP_IMAGE);
        uploadTask.setId(upload);
    }

    public interface View extends ActionEntityPresenter.View {

        void updateItem(int position);
    }
}
