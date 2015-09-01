package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
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

import io.techery.scalablecropp.library.Crop;

public class PostPresenter extends Presenter<PostPresenter.View> implements TransferListener {

    public static final int REQUESTER_ID = -2;

    private CachedPostEntity post;

    @Inject
    SnappyRepository snapper;

    @Override
    public void takeView(View view) {
        super.takeView(view);

        post = snapper.getPost();

        if (post == null) {
            post = new CachedPostEntity();
            savePost();
        } else if (!TextUtils.isEmpty(post.getFilePath())) {
            post.setUploadTask(snapper.getUploadTask(post.getFilePath()));
            if (post.getUploadTask() != null) {
                TransferObserver transferObserver =
                        photoUploadingSpiceManager.getTransferById(post.getUploadTask().getAmazonTaskId());
                onStateChanged(transferObserver.getId(), transferObserver.getState());
                transferObserver.setTransferListener(this);
            }
        }

        view.setName(getAccount().getFullName());
        view.setAvatar(getAccount().getAvatar().getThumb());

        updateUi();
    }

    private void updateUi() {
        view.setText(post.getText());

        if (post.getUploadTask() != null && post.getUploadTask().getStatus() != null) {
            view.attachPhoto(Uri.parse(post.getUploadTask().getFilePath()));

            if (!post.getUploadTask().getStatus().equals(UploadTask.Status.COMPLETED))
                view.showProgress();

            if (post.getUploadTask().getStatus().equals(UploadTask.Status.FAILED)) {
                view.imageError();
            }
        }

        enablePostButton();
    }

    public void cancel() {
        cancelUpload();
        deletePost();
    }

    public void cancelClicked() {
        if (view != null)
            if (TextUtils.isEmpty(post.getText()) && post.getUploadTask() == null) {
                view.cancel();
            } else {
                view.showCancelationDialog();
            }
    }

    private void savePost() {
        snapper.savePost(post);
    }

    private void deletePost() {
        post = null;
        snapper.removePost();
    }

    @Override
    public void dropView() {
        super.dropView();
        if (post != null) {
            savePost();
        }
    }

    public void post() {
        if (post.getUploadTask() != null && UploadTask.Status.COMPLETED.equals(post.getUploadTask().getStatus())) {
            post.getUploadTask().setTitle(post.getText());
            doRequest(new AddTripPhotoCommand(post.getUploadTask()), this::processPost, spiceException -> {
                PostPresenter.super.handleError(spiceException);
                view.onPostError();
            });
        } else if (!TextUtils.isEmpty(post.getText()) && post.getUploadTask() == null) {
            doRequest(new NewPostCommand(post.getText()), this::processPost, spiceException -> {
                PostPresenter.super.handleError(spiceException);
                view.onPostError();
            });
        }
    }

    private void enablePostButton() {
        if ((!TextUtils.isEmpty(post.getText()) && post.getUploadTask() == null) ||
                (post.getUploadTask() != null &&
                        post.getUploadTask().getStatus().equals(UploadTask.Status.COMPLETED))) {
            view.enableButton();
        } else {
            view.disableButton();
        }
    }

    private void processPost(IFeedObject iFeedObject) {
        eventBus.post(new FeedItemAddedEvent(BaseEventModel.create(iFeedObject, getAccount())));
        view.cancel();
        view = null;
    }

    public void postInputChanged(String input) {
        post.setText(input);
        enablePostButton();
    }

    ////////////////////////////////////////
    /////// Photo upload
    ////////////////////////////////////////

    private void savePhotoIfNeeded() {
        doRequest(new CopyFileCommand(context, post.getUploadTask().getFilePath()), this::uploadPhoto);
    }

    private void uploadPhoto(String filePath) {
        post.setFilePath(filePath);
        post.getUploadTask().setFilePath(filePath);
        view.attachPhoto(Uri.parse(filePath));
        startUpload(post.getUploadTask());
    }

    private void startUpload(UploadTask uploadTask) {
        view.showProgress();
        TransferObserver transferObserver = photoUploadingSpiceManager.upload(post.getUploadTask());
        uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));

        snapper.saveUploadTask(uploadTask);
        transferObserver.setTransferListener(this);

    }

    @Override
    public void onStateChanged(int id, TransferState state) {
        if (view != null && post.getUploadTask() != null) {
            if (state.equals(TransferState.COMPLETED)) {
                post.getUploadTask().setStatus(UploadTask.Status.COMPLETED);
                post.getUploadTask().setOriginUrl
                        (photoUploadingSpiceManager.getResultUrl(post.getUploadTask()));
            } else if (state.equals(TransferState.FAILED)) {
                post.getUploadTask().setStatus(UploadTask.Status.FAILED);
            }

            processUploadTask();
        }
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
    }

    @Override
    public void onError(int id, Exception ex) {
        post.getUploadTask().setStatus(UploadTask.Status.FAILED);
        processUploadTask();
    }

    private void processUploadTask() {
        if (post != null && post.getUploadTask() != null) {
            snapper.saveUploadTask(post.getUploadTask());

            switch (post.getUploadTask().getStatus()) {
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
        view.showProgress();
        enablePostButton();
    }

    private void photoCompleted() {
        view.hideProgress();
        enablePostButton();
    }

    private void photoFailed() {
        view.imageError();
        enablePostButton();
    }

    public void onProgressClicked() {
        if (post.getUploadTask().getStatus().equals(UploadTask.Status.FAILED)) {
            startUpload(post.getUploadTask());
        }
    }

    public void removeImage() {
        cancelUpload();
        post.setUploadTask(null);
        enablePostButton();
        view.attachPhoto(null);
        view.enableImagePicker();
    }

    private void cancelUpload() {
        if (post.getUploadTask() != null)
            photoUploadingSpiceManager.cancelUploading(post.getUploadTask());
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
            post.setUploadTask(imageUploadTask);
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
    }
}
