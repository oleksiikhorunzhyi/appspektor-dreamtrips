package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.UploadProgressUpdateEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.NewPostCommand;
import com.worldventures.dreamtrips.modules.feed.api.UploadPostPhotoCommand;
import com.worldventures.dreamtrips.modules.feed.event.PostCreatedEvent;
import com.worldventures.dreamtrips.modules.feed.event.PostPhotoUploadFailed;
import com.worldventures.dreamtrips.modules.feed.event.PostPhotoUploadFinished;
import com.worldventures.dreamtrips.modules.feed.model.Post;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;

import java.io.File;
import java.util.UUID;

import javax.inject.Inject;

import icepick.Icicle;

public class PostPresenter extends Presenter<PostPresenter.View> {

    private Post post;

    @ForApplication
    @Inject
    Injector injector;

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
        }

        view.setName(getAccount().getFullName());
        view.setAvatar(getAccount().getAvatar().getThumb());

        updateUi();
    }

    private void updateUi() {
        view.setText(post.getText());

        if (post.getImageUploadTask() != null) {
            if (!TextUtils.isEmpty(post.getImageUploadTask().getOriginUrl())) {
                view.attachPhoto(Uri.parse(post.getImageUploadTask().getOriginUrl()));
            } else {
                view.attachPhoto(Uri.parse(post.getImageUploadTask().getFileUri()));
                view.showProgress();
                view.setProgress((int) post.getImageUploadTask().getProgress());
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

    public void onPhotoClick() {
        if (post.getImageUploadTask().isFailed()) {
            uploadPhoto();
        }
    }

    private void handlePhotoPick(Uri uri) {
        view.showProgress();
        view.attachPhoto(uri);
        ImageUploadTask imageUploadTask = new ImageUploadTask();
        imageUploadTask.setFileUri(uri.toString());
        imageUploadTask.setTaskId(UUID.randomUUID().toString());
        post.setImageUploadTask(imageUploadTask);
        uploadPhoto();
    }

    private void uploadPhoto() {
        photoUploadSpiceManager.uploadPostPhoto(post.getImageUploadTask());
    }

    public void onEventMainThread(PostPhotoUploadFinished event) {
        if (post.getImageUploadTask().getTaskId().equalsIgnoreCase(event.getTaskId())) {
            view.hideProgress();
            post.getImageUploadTask().setOriginUrl(event.getOriginUrl());
            enablePostButton();
        }
    }

    public void onEventMainThread(PostPhotoUploadFailed event) {
        if (post.getImageUploadTask().getTaskId().equalsIgnoreCase(event.getTaskId()))
            view.imageError();
    }

    public void onEventMainThread(UploadProgressUpdateEvent event) {
        if (post.getImageUploadTask().getTaskId().equalsIgnoreCase(event.getTaskId()))
            view.setProgress(event.getProgress());
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

        void setProgress(int progress);

        void hideProgress();

        void imageError();
    }
}
