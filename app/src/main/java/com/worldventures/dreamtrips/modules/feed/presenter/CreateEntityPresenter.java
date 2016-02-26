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
import com.worldventures.dreamtrips.modules.tripsimages.api.AddTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.Calendar;
import java.util.List;

public class CreateEntityPresenter<V extends CreateEntityPresenter.View> extends ActionEntityPresenter<V> {

    public static final int REQUESTER_ID = -2;

    @Override
    public void takeView(V view) {
        super.takeView(view);
        PhotoUploadSubscriber photoUploadSubscriber = PhotoUploadSubscriber.bind(view, photoUploadingManager.getTaskChangingObservable(UploadPurpose.TRIP_IMAGE));
        photoUploadSubscriber.afterEach(uploadTask -> {
            if (cachedPostEntity != null && cachedPostEntity.getUploadTask().getId() == uploadTask.getId()) {
                cachedPostEntity.getUploadTask().setStatus(uploadTask.getStatus());
                processUploadTask();
            }
        });

        if (cachedPostEntity != null) {
            Queryable.from(photoUploadingManager.getUploadTasks(UploadPurpose.TRIP_IMAGE)).forEachR(photoUploadSubscriber::onNext);
        }
    }

    @Override
    protected void updateUi() {
        super.updateUi();

        if (cachedPostEntity.getUploadTask() != null && cachedPostEntity.getUploadTask().getStatus() != null) {
            view.attachPhoto(Uri.parse(cachedPostEntity.getUploadTask().getFilePath()));

            if (!cachedPostEntity.getUploadTask().getStatus().equals(UploadTask.Status.COMPLETED))
                view.showProgress();

            if (cachedPostEntity.getUploadTask().getStatus().equals(UploadTask.Status.FAILED)) {
                view.imageError();
            }
        }

        enablePostButton();
    }

    @Override
    protected boolean isChanged() {
        return !TextUtils.isEmpty(cachedPostEntity.getText()) ||
                cachedPostEntity.getUploadTask() != null;
    }

    @Override
    public void post() {
        if (cachedPostEntity.getUploadTask() != null && UploadTask.Status.COMPLETED.equals(cachedPostEntity.getUploadTask().getStatus())) {
            cachedPostEntity.getUploadTask().setTitle(cachedPostEntity.getText());
            cachedPostEntity.getUploadTask().setShotAt(Calendar.getInstance().getTime());

            doRequest(new AddTripPhotoCommand(cachedPostEntity.getUploadTask()), this::processPost, spiceException -> {
                handleError(spiceException);
                view.onPostError();
            });
        } else if (!TextUtils.isEmpty(cachedPostEntity.getText()) && cachedPostEntity.getUploadTask() == null) {
            postTextualUpdate();
        }
    }

    protected void postTextualUpdate() {
        doRequest(new NewPostCommand(cachedPostEntity.getText()),
                this::processPost, spiceException -> {
                    handleError(spiceException);
                    view.onPostError();
                });
    }

    protected void processPost(FeedEntity feedEntity) {
        eventBus.post(new FeedItemAddedEvent(FeedItem.create(feedEntity, getAccount())));
        view.cancel();
        view = null;
    }

    @Override
    protected void enablePostButton() {
        if ((!TextUtils.isEmpty(cachedPostEntity.getText()) && cachedPostEntity.getUploadTask() == null) ||
                (cachedPostEntity.getUploadTask() != null &&
                        cachedPostEntity.getUploadTask().getStatus().equals(UploadTask.Status.COMPLETED))) {
            view.enableButton();
        } else {
            view.disableButton();
        }
    }

    public void attachImages(List<ChosenImage> photos, int requestType) {
        if (photos.size() == 0
                || (cachedPostEntity.getUploadTask() != null
                && cachedPostEntity.getUploadTask().getStatus() == UploadTask.Status.COMPLETED)) {
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
            cachedPostEntity.setUploadTask(imageUploadTask);
            view.attachPhoto(Uri.parse(filePath));
            doRequest(new CopyFileCommand(context, cachedPostEntity.getUploadTask().getFilePath()), s -> {
                startUpload(imageUploadTask);
            });
        }
    }

    public void onProgressClicked() {
        if (cachedPostEntity.getUploadTask().getStatus().equals(UploadTask.Status.FAILED)) {
            startUpload(cachedPostEntity.getUploadTask());
        }
    }

    ////////////////////////////////////////
    /////// Photo upload
    ////////////////////////////////////////

    private void startUpload(UploadTask uploadTask) {
        view.showProgress();
        long upload = photoUploadingManager.upload(uploadTask, UploadPurpose.TRIP_IMAGE);
        cachedPostEntity.getUploadTask().setId(upload);
    }

    private void processUploadTask() {
        if (cachedPostEntity != null && cachedPostEntity.getUploadTask() != null) {
            switch (cachedPostEntity.getUploadTask().getStatus()) {
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
            enablePostButton();
        }
    }

    private void photoCompleted() {
        if (view != null) {
            view.hideProgress();
            enablePostButton();
        }
    }

    private void photoFailed() {
        if (view != null) {
            view.imageError();
            enablePostButton();
        }
    }

    public interface View extends ActionEntityPresenter.View {

        void showProgress();

        void hideProgress();

        void imageError();
    }
}
