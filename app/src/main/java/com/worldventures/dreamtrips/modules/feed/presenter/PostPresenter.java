package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.NewPostCommand;
import com.worldventures.dreamtrips.modules.feed.event.PostCreatedEvent;
import com.worldventures.dreamtrips.modules.feed.model.Post;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.UploadingFileManager;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;

import java.io.File;
import java.util.Locale;

import javax.inject.Inject;

public class PostPresenter extends Presenter<PostPresenter.View> implements TransferListener {

    private Post post;

    @ForApplication
    @Inject
    Injector injector;

    @Inject
    SnappyRepository snapper;

    @Inject
    TransferUtility transferUtility;

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

    public void removeImage() {
        if (post.getImageUploadTask() != null)
            transferUtility.cancel(post.getImageUploadTask().getAmazonTaskId());
        post.setImageUploadTask(null);
        enablePostButton();
        view.attachPhoto(null);
    }

    public void cancel() {
        if (post.getImageUploadTask() != null)
            transferUtility.cancel(post.getImageUploadTask().getAmazonTaskId());
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

    public void restartPhotoUpload() {
        if (post.getImageUploadTask().isFailed()) {
            uploadPhoto();
        }
    }

    private void handlePhotoPick(Uri uri) {
        view.attachPhoto(uri);
        ImageUploadTask imageUploadTask = new ImageUploadTask();
        imageUploadTask.setFileUri(uri.toString());
        post.setImageUploadTask(imageUploadTask);
        uploadPhoto();
    }

    private void uploadPhoto() {
        view.showProgress();
        File file = UploadingFileManager.copyFileIfNeed(post.getImageUploadTask().getFileUri(), context);
        String bucketName = BuildConfig.BUCKET_NAME.toLowerCase(Locale.US);
        String key = BuildConfig.BUCKET_ROOT_PATH + file.getName();
        post.setImageUploadUrl(key);
        TransferObserver transferObserver = transferUtility.upload(bucketName, key, file);
        post.getImageUploadTask().setAmazonTaskId(transferObserver.getId());
        initListener(transferObserver);
    }

    private void setTransferListenereIfNeeded() {
        if (post.getImageUploadTask() != null &&
                TextUtils.isEmpty(post.getImageUploadTask().getOriginUrl())) {
            TransferObserver transferObserver = transferUtility.getTransferById(post.getImageUploadTask().getAmazonTaskId());
            if (transferObserver != null)
                initListener(transferObserver);
        }
    }

    private void initListener(TransferObserver transferObserver) {
        transferObserver.setTransferListener(this);
    }

    @Override
    public void onStateChanged(int id, TransferState state) {
        switch (state) {
            case COMPLETED:
                photoUploaded("https://" + BuildConfig.BUCKET_NAME.toLowerCase(Locale.US)
                        + ".s3.amazonaws.com/" + post.getImageUploadUrl());
                break;
        }
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
    }

    @Override
    public void onError(int id, Exception ex) {
        photoFailed();
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

    public ImagePickCallback provideSelectImageCallback() {
        return selectImageCallback;
    }

    public ImagePickCallback provideFbCallback() {
        return fbCallback;
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
