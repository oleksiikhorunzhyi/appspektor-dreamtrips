package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.NewPostCommand;
import com.worldventures.dreamtrips.modules.feed.event.PostCreatedEvent;
import com.worldventures.dreamtrips.modules.feed.model.Post;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.events.UploadStatusChanged;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;

import java.io.File;

import javax.inject.Inject;

public class PostPresenter extends Presenter<PostPresenter.View> {

    private Post post;

    @Inject
    SnappyRepository snapper;

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
        } else if (!TextUtils.isEmpty(post.getFilePath())) {
            post.setUploadTask(snapper.getUploadTask(post.getFilePath()));
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
        if (post.getUploadTask() != null &&
                post.getUploadTask().getStatus().equals(UploadTask.Status.COMPLETED)) {
            post.getUploadTask().setTitle(post.getText());
            doRequest(new AddTripPhotoCommand(post.getUploadTask()), this::processPhoto);
        } else if (!TextUtils.isEmpty(post.getText()) && post.getUploadTask() == null) {
            doRequest(new NewPostCommand(post.getText()), this::processPost);
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
            UploadTask imageUploadTask = new UploadTask();
            imageUploadTask.setFilePath(uri.toString());
            imageUploadTask.setStatus(UploadTask.Status.IN_PROGRESS);
            post.setUploadTask(imageUploadTask);
            savePhotoIfNeeded();
        }
    }

    private void savePhotoIfNeeded() {
        doRequest(new CopyFileCommand(context, post.getUploadTask().getFilePath()), this::uploadPhoto);
    }

    private void uploadPhoto(String filePath) {
        post.setFilePath(filePath);
        post.getUploadTask().setFilePath(filePath);
        view.attachPhoto(Uri.parse(filePath));
        startUpload();
    }

    private void startUpload() {
        view.showProgress();
        photoUploadingSpiceManager.uploadPhotoToS3(post.getUploadTask());
    }

    public void onEventMainThread(UploadStatusChanged event) {
        if (post.getUploadTask().equals(event.getUploadTask())) {
            post.setUploadTask(event.getUploadTask());
            processUploadTask();
        }
    }

    private void processUploadTask() {
        switch (post.getUploadTask().getStatus()) {
            case IN_PROGRESS:
                photoInProgress();
                break;
            case FAILED:
                photoFailed();
                break;
            case CANCELED:
                break;
            case COMPLETED:
                photoCompleted();
                break;
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

    public void setPidType(int pidType) {
        post.setPidType(pidType);
    }

    public int getPidType() {
        return post.getPidType();
    }

    public void onProgressClicked() {
        if (post.getUploadTask().getStatus().equals(UploadTask.Status.FAILED)) {
            startUpload();
        }
    }

    public void removeImage() {
        photoUploadingSpiceManager.cancelUploading(post.getUploadTask());
        post.setUploadTask(null);
        enablePostButton();
        view.attachPhoto(null);
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
