package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.NewPostCommand;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.CachedPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddTripPhotoCommand;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.io.File;

import javax.inject.Inject;

import icepick.Icicle;

public class PostPresenter extends Presenter<PostPresenter.View> implements TransferListener {

    public static final int REQUESTER_ID = -2;

    @Inject
    SnappyRepository snapper;

    @Icicle
    CachedPostEntity cachedPostEntity;

    public PostPresenter() {
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (cachedPostEntity == null) {
            cachedPostEntity = new CachedPostEntity();
        } else if (!TextUtils.isEmpty(cachedPostEntity.getFilePath())) {
            cachedPostEntity.setUploadTask(snapper.getUploadTask(cachedPostEntity.getFilePath()));
            if (cachedPostEntity.getUploadTask() != null) {
                TransferObserver transferObserver =
                        photoUploadingSpiceManager.getTransferById(cachedPostEntity.getUploadTask().getAmazonTaskId());
                onStateChanged(transferObserver.getId(), transferObserver.getState());
                transferObserver.setTransferListener(this);
            }
        }

        view.setName(getAccount().getFullName());
        view.setAvatar(getAccount().getAvatar().getThumb());

        updateUi();
    }

    protected void updateUi() {
        view.setText(cachedPostEntity.getText());

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

    public void cancel() {
        cancelUpload();
    }

    public void cancelClicked() {
        if (view != null)
            if (TextUtils.isEmpty(cachedPostEntity.getText()) && cachedPostEntity.getUploadTask() == null) {
                view.cancel();
            } else {
                view.showCancelationDialog();
            }
    }

    public void post() {
        if (cachedPostEntity.getUploadTask() != null && UploadTask.Status.COMPLETED.equals(cachedPostEntity.getUploadTask().getStatus())) {
            cachedPostEntity.getUploadTask().setTitle(cachedPostEntity.getText());
            doRequest(new AddTripPhotoCommand(cachedPostEntity.getUploadTask()), this::processPost, spiceException -> {
                PostPresenter.super.handleError(spiceException);
                view.onPostError();
            });
        } else if (!TextUtils.isEmpty(cachedPostEntity.getText()) && cachedPostEntity.getUploadTask() == null) {
            postTextualUpdate();
        }
    }

    protected void postTextualUpdate() {
        doRequest(new NewPostCommand(cachedPostEntity.getText()),
                this::processPost, spiceException -> {
                    PostPresenter.super.handleError(spiceException);
                    view.onPostError();
                });
    }

    private void enablePostButton() {
        if ((!TextUtils.isEmpty(cachedPostEntity.getText()) && cachedPostEntity.getUploadTask() == null) ||
                (cachedPostEntity.getUploadTask() != null &&
                        cachedPostEntity.getUploadTask().getStatus().equals(UploadTask.Status.COMPLETED))) {
            view.enableButton();
        } else {
            view.disableButton();
        }
    }

    protected void processPost(IFeedObject iFeedObject) {
        if (cachedPostEntity.getUploadTask() != null)
            snapper.removeUploadTask(cachedPostEntity.getUploadTask());

        eventBus.post(new FeedItemAddedEvent(BaseEventModel.create(iFeedObject, getAccount())));
        view.cancel();
        view = null;
    }

    public void postInputChanged(String input) {
        cachedPostEntity.setText(input);
        enablePostButton();
    }

    ////////////////////////////////////////
    /////// Photo upload
    ////////////////////////////////////////

    private void savePhotoIfNeeded() {
        doRequest(new CopyFileCommand(context, cachedPostEntity.getUploadTask().getFilePath()), this::uploadPhoto);
    }

    private void uploadPhoto(String filePath) {
        cachedPostEntity.setFilePath(filePath);
        cachedPostEntity.getUploadTask().setFilePath(filePath);
        view.attachPhoto(Uri.parse(filePath));
        startUpload(cachedPostEntity.getUploadTask());
    }

    private void startUpload(UploadTask uploadTask) {
        view.showProgress();
        TransferObserver transferObserver = photoUploadingSpiceManager.upload(cachedPostEntity.getUploadTask());
        uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));

        snapper.saveUploadTask(uploadTask);
        transferObserver.setTransferListener(this);
    }

    @Override
    public void onStateChanged(int id, TransferState state) {
        if (view != null && cachedPostEntity.getUploadTask() != null) {
            if (state.equals(TransferState.COMPLETED)) {
                cachedPostEntity.getUploadTask().setStatus(UploadTask.Status.COMPLETED);
                cachedPostEntity.getUploadTask().setOriginUrl
                        (photoUploadingSpiceManager.getResultUrl(cachedPostEntity.getUploadTask()));
            } else if (state.equals(TransferState.FAILED)) {
                cachedPostEntity.getUploadTask().setStatus(UploadTask.Status.FAILED);
            }

            processUploadTask();
        }
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
    }

    @Override
    public void onError(int id, Exception ex) {
        cachedPostEntity.getUploadTask().setStatus(UploadTask.Status.FAILED);
        processUploadTask();
    }

    private void processUploadTask() {
        if (cachedPostEntity != null && cachedPostEntity.getUploadTask() != null) {
            snapper.saveUploadTask(cachedPostEntity.getUploadTask());

            switch (cachedPostEntity.getUploadTask().getStatus()) {
                case IN_PROGRESS:
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

    public void onProgressClicked() {
        if (cachedPostEntity.getUploadTask().getStatus().equals(UploadTask.Status.FAILED)) {
            startUpload(cachedPostEntity.getUploadTask());
        }
    }

    public void removeImage() {
        cancelUpload();
        cachedPostEntity.setUploadTask(null);
        enablePostButton();
        view.attachPhoto(null);
        view.enableImagePicker();
    }

    private void cancelUpload() {
        if (cachedPostEntity.getUploadTask() != null) {
            photoUploadingSpiceManager.cancelUploading(cachedPostEntity.getUploadTask());
            snapper.removeUploadTask(cachedPostEntity.getUploadTask());
            cachedPostEntity.setUploadTask(null);
        }
    }


    ////////////////////////////////////////
    /////// Photo picking
    ////////////////////////////////////////

    public void pickImage(int requestType) {
        eventBus.post(new ImagePickRequestEvent(requestType, REQUESTER_ID));
    }

    public void onEvent(ImagePickedEvent event) {
        if (event.getRequesterID() == REQUESTER_ID) {
            view.disableImagePicker();
            eventBus.removeStickyEvent(event);
            String fileThumbnail = event.getImages()[0].getFileThumbnail();
            if (ValidationUtils.isUrl(fileThumbnail)) {
                imageSelected(Uri.parse(fileThumbnail).toString());
            } else {
                imageSelected(Uri.fromFile(new File(fileThumbnail)).toString());
            }

        }
    }

    private void imageSelected(String filePath) {
        if (view != null) {
            UploadTask imageUploadTask = new UploadTask();
            imageUploadTask.setFilePath(filePath);
            imageUploadTask.setStatus(UploadTask.Status.IN_PROGRESS);
            cachedPostEntity.setUploadTask(imageUploadTask);
            savePhotoIfNeeded();
        }
    }

    public interface View extends Presenter.View {
        void setName(String userName);

        void setAvatar(String avatarUrl);

        void setText(String text);

        void enableButton();

        void disableButton();

        void attachPhoto(Uri uri);

        void showProgress();

        void hideProgress();

        void imageError();

        void enableImagePicker();

        void disableImagePicker();

        void cancel();

        void showCancelationDialog();

        void onPostError();

        void hidePhotoControl();
    }
}
