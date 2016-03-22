package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.core.api.PhotoUploadSubscriber;
import com.worldventures.dreamtrips.core.api.UploadPurpose;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.feed.api.NewPostCommand;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoTagsBundle;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.Calendar;
import java.util.List;

public class CreateEntityPresenter<V extends CreateEntityPresenter.View> extends ActionEntityPresenter<V> {

    public static final int REQUESTER_ID = -2;

    UploadTask cachedUploadTask;
    PhotoUploadSubscriber photoUploadSubscriber;

    @Override
    public void takeView(V view) {
        super.takeView(view);
        photoUploadSubscriber = new PhotoUploadSubscriber();
        photoUploadingManager.getTaskChangingObservable(UploadPurpose.TRIP_IMAGE).subscribe(photoUploadSubscriber);
        photoUploadSubscriber.afterEach(uploadTask -> {
            if (cachedUploadTask != null && cachedUploadTask.getId() == uploadTask.getId()) {
                cachedUploadTask.setStatus(uploadTask.getStatus());
                processUploadTask();
            }
        });
        Queryable.from(photoUploadingManager.getUploadTasks(UploadPurpose.TRIP_IMAGE)).forEachR(photoUploadSubscriber::onNext);
    }

    @Override
    public void dropView() {
        super.dropView();
        photoUploadSubscriber.unsubscribe();
    }

    @Override
    protected void updateUi() {
        super.updateUi();

        if (!isCachedUploadTaskEmpty()) {
            view.attachPhoto(Uri.parse(cachedUploadTask.getFilePath()));

            if (!cachedUploadTask.getStatus().equals(UploadTask.Status.COMPLETED))
                view.showProgress();

            if (cachedUploadTask.getStatus().equals(UploadTask.Status.FAILED)) {
                view.imageError();
            }
        }

        invalidateDynamicViews();
    }


    @Override
    protected boolean isChanged() {
        return !TextUtils.isEmpty(cachedText)
                || (cachedUploadTask != null && cachedUploadTask.getStatus().equals(UploadTask.Status.COMPLETED))
                || !cachedAddedPhotoTags.isEmpty() || !cachedRemovedPhotoTags.isEmpty();
    }

    @Override
    public void post() {
        if (!isCachedUploadTaskEmpty() && UploadTask.Status.COMPLETED.equals(cachedUploadTask.getStatus())) {
            cachedUploadTask.setTitle(cachedText);
            cachedUploadTask.setShotAt(Calendar.getInstance().getTime());

            doRequest(new AddTripPhotoCommand(cachedUploadTask), this::processPostSuccess, spiceException -> {
                handleError(spiceException);
                view.onPostError();
            });
        } else if (!isCachedTextEmpty() && isCachedUploadTaskEmpty()) {
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

    protected void processTagUploadSuccess(FeedEntity feedEntity) {
        eventBus.post(new FeedItemAddedEvent(FeedItem.create(feedEntity, getAccount())));
        super.processTagUploadSuccess(feedEntity);
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
        Location location = new Location();
        if (cachedUploadTask != null) {
            location.setLat(cachedUploadTask.getLatitude());
            location.setLng(cachedUploadTask.getLongitude());
            location.setName(cachedUploadTask.getLocationName());
        }
        return location;
    }

    @Override
    public void updateLocation(Location location) {
        cachedUploadTask.setLocationName(location.getName());
        cachedUploadTask.setLongitude((float) location.getLng());
        cachedUploadTask.setLatitude((float) location.getLat());
    }

    public void attachImages(List<ChosenImage> photos, int requestType) {
        if (photos.size() == 0 || (!isCachedUploadTaskEmpty() && cachedUploadTask.getStatus() == UploadTask.Status.COMPLETED)) {
            return;
        }

        String fileThumbnail = photos.get(0).getFileThumbnail();
        imageSelected(Uri.parse(fileThumbnail).toString(), requestType);
    }

    private void imageSelected(String filePath, int requestType) {
        if (view != null) {
            UploadTask imageUploadTask = new UploadTask();
            imageUploadTask.setFilePath(filePath);
            imageUploadTask.setStatus(UploadTask.Status.STARTED);
            String type = "";
            switch (requestType) {
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
            //
            cachedUploadTask = imageUploadTask;
            view.attachPhoto(Uri.parse(filePath));
            doRequest(new CopyFileCommand(context, cachedUploadTask.getFilePath()), s -> {
                imageUploadTask.setFilePath(s);
                startUpload(imageUploadTask);
            });
        }
    }

    public void onProgressClicked() {
        if (cachedUploadTask.getStatus().equals(UploadTask.Status.FAILED)) {
            startUpload(cachedUploadTask);
        }
    }

    protected EditPhotoTagsBundle.PhotoEntity getImageForTagging() {
        return new EditPhotoTagsBundle.PhotoEntity(cachedUploadTask.getOriginUrl(), cachedUploadTask.getFilePath());
    }

    protected boolean isCachedUploadTaskEmpty() {
        return cachedUploadTask == null;
    }

    protected boolean isCachedTextEmpty() {
        return TextUtils.isEmpty(cachedText);
    }

    public void invalidateAddTagBtn() {
        boolean isViewShown = cachedUploadTask != null &&
                cachedUploadTask.getStatus() == UploadTask.Status.COMPLETED;
        boolean someTagSets = !cachedAddedPhotoTags.isEmpty();
        if (view != null) {
            view.redrawTagButton(isViewShown, someTagSets);
        }
    }


    ////////////////////////////////////////
    /////// Photo upload
    ////////////////////////////////////////

    private void startUpload(UploadTask uploadTask) {
        view.showProgress();
        long upload = photoUploadingManager.upload(uploadTask, UploadPurpose.TRIP_IMAGE);
        cachedUploadTask.setId(upload);
    }

    private void processUploadTask() {
        if (!isCachedUploadTaskEmpty()) {
            switch (cachedUploadTask.getStatus()) {
                case STARTED:
                    photoInProgress();
                    break;
                case FAILED:
                    photoFailed();
                    break;
                case COMPLETED:
                    photoCompleted();
                    break;
            }
        }
    }

    private void photoInProgress() {
        if (view != null) {
            view.showProgress();
            invalidateDynamicViews();
        }
    }

    protected void photoCompleted() {
        if (view != null) {
            view.hideProgress();
            invalidateDynamicViews();
        }
    }

    private void photoFailed() {
        if (view != null) {
            view.imageError();
            invalidateDynamicViews();
        }
    }

    public interface View extends ActionEntityPresenter.View {

        void showProgress();

        void hideProgress();

        void imageError();
    }
}
