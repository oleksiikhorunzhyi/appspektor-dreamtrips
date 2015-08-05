package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.AmazonDelegate;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.NewPostCommand;
import com.worldventures.dreamtrips.modules.feed.event.PostCreatedEvent;
import com.worldventures.dreamtrips.modules.feed.model.Post;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;

import java.io.File;
import java.net.URISyntaxException;

import javax.inject.Inject;

public class PostPresenter extends Presenter<PostPresenter.View> implements TransferListener {

    private Post post;

    @ForApplication
    @Inject
    Injector injector;

    @Inject
    SnappyRepository snapper;

    @Inject
    AmazonDelegate amazonDelegate;

    protected ImagePickCallback selectImageCallback = (fragment, image, error) -> {
        if (error != null) {
            view.informUser(error);
        } else {
            Uri uri = Uri.fromFile(new File(image.getFileThumbnail()));
            handlePhotoPick(uri);
        }
    };
    protected ImagePickCallback fbCallback = (fragment, image, error) -> {
        if (error != null) {
            view.informUser(error);
        } else {
            Uri uri = Uri.parse(image.getFilePathOriginal());
            handlePhotoPick(uri);
        }
    };

    @Override
    public void takeView(View view) {
        super.takeView(view);

        post = snapper.getPost();
        if (post == null) {
            post = new Post();
            savePost();
        }

        view.setName(getAccount().getFullName());
        view.setAvatar(getAccount().getAvatar().getThumb());

        updateUi();
    }

    private void updateUi() {
        view.setText(post.getText());

        if (post.getImageUploadTask() != null) {
            view.attachPhoto(Uri.parse(post.getImageUploadTask().getFileUri()));

            if (TextUtils.isEmpty(post.getImageUploadTask().getOriginUrl()))
                view.showProgress();

            if (post.getImageUploadTask().isFailed()) {
                view.imageError();
            }
        }

        setTransferListenereIfNeeded();

        enablePostButton();
    }

    public void cancel() {
        if (post.getImageUploadTask() != null)
            amazonDelegate.cancel(post.getImageUploadTask().getAmazonTaskId());
        fragmentCompass.removePost();
        deletePost();
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
        if (post.getImageUploadTask() != null
                && !TextUtils.isEmpty(post.getImageUploadTask().getOriginUrl())) {
            post.getImageUploadTask().setTitle(post.getText());
            doRequest(new AddTripPhotoCommand(post.getImageUploadTask()), this::processPhoto);
        } else if (!TextUtils.isEmpty(post.getText())) {
            doRequest(new NewPostCommand(post.getText()), this::processPost);
        }
    }

    private void enablePostButton() {
        if (!TextUtils.isEmpty(post.getText()) ||
                (post.getImageUploadTask() != null &&
                        !TextUtils.isEmpty(post.getImageUploadTask().getOriginUrl()))) {
            view.enableButton();
        } else {
            view.disableButton();
        }
    }

    private void processPhoto(Photo photo) {
        cancel();
    }

    private void processPost(TextualPost post) {
        eventBus.post(new PostCreatedEvent(post));
        cancel();
    }

    public void postInputChanged(String input) {
        post.setText(input);
        enablePostButton();
    }


    private void setTransferListenereIfNeeded() {
        if (post.getImageUploadTask() != null &&
                TextUtils.isEmpty(post.getImageUploadTask().getOriginUrl())) {
            TransferObserver transferObserver = amazonDelegate.getTransferById(post.getImageUploadTask().getAmazonTaskId());
            if (transferObserver != null)
                initListener(transferObserver);
        }
    }

    private void initListener(TransferObserver transferObserver) {
        transferObserver.setTransferListener(this);
    }

    ////////////////////////////////////////
    /////// Photo upload
    ////////////////////////////////////////

    public ImagePickCallback provideSelectImageCallback() {
        return selectImageCallback;
    }

    public ImagePickCallback provideFbCallback() {
        return fbCallback;
    }

    private void handlePhotoPick(Uri uri) {
        if (view != null) {
            ImageUploadTask imageUploadTask = new ImageUploadTask();
            imageUploadTask.setFileUri(uri.toString());
            post.setImageUploadTask(imageUploadTask);
            uploadPhoto();
        }
    }

    private void uploadPhoto() {
        view.showProgress();
        doRequest(new CopyFileCommand(context, post.getImageUploadTask().getFileUri()), filePath -> {
            try {
                post.getImageUploadTask().setFileUri(filePath);
                view.attachPhoto(Uri.parse(filePath));
                initListener(amazonDelegate.uploadTripPhoto(context, post.getImageUploadTask()));
            } catch (URISyntaxException e) {
                photoFailed();
            }
        });
    }

    public void restartPhotoUpload() {
        if (post.getImageUploadTask().isFailed()) {
            try {
                post.getImageUploadTask().setFailed(false);
                view.showProgress();
                amazonDelegate.uploadTripPhoto(context, post.getImageUploadTask());
            } catch (URISyntaxException e) {
                photoFailed();
            }
        }
    }

    private void photoUploaded(String url) {
        view.hideProgress();
        post.getImageUploadTask().setOriginUrl(url);
        enablePostButton();
    }

    private void photoFailed() {
        post.getImageUploadTask().setFailed(true);
        view.imageError();
    }

    public void setPidType(int pidType) {
        post.setPidType(pidType);
    }

    public int getPidType() {
        return post.getPidType();
    }

    public void removeImage() {
        if (post.getImageUploadTask() != null)
            amazonDelegate.cancel(post.getImageUploadTask().getAmazonTaskId());
        post.setImageUploadTask(null);
        enablePostButton();
        view.attachPhoto(null);
    }

    @Override
    public void onStateChanged(int id, TransferState state) {
        switch (state) {
            case COMPLETED:
                if (post != null && post.getImageUploadTask() != null)
                    photoUploaded(post.getImageUploadTask().getAmazonResultUrl());
                break;
            case IN_PROGRESS:
                if (view != null)
                    view.showProgress();
                break;
        }
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        if (view != null)
            view.showProgress();
    }

    @Override
    public void onError(int id, Exception ex) {
        photoFailed();
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
    }
}
