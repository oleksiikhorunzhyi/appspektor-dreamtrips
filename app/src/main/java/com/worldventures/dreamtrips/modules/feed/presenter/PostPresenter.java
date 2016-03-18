package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.core.api.PhotoUploadSubscriber;
import com.worldventures.dreamtrips.core.api.UploadPurpose;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.NewPostCommand;
import com.worldventures.dreamtrips.modules.feed.event.AttachPhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.model.CachedPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.tripsimages.api.AddTripPhotoCommand;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class PostPresenter extends Presenter<PostPresenter.View> {

    public static final int REQUESTER_ID = -2;

    @Inject
    SnappyRepository snapper;

    @State
    CachedPostEntity cachedPostEntity;
    private PhotoUploadSubscriber photoUploadSubscriber;

    public PostPresenter() {
        priorityEventBus = 1;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        photoUploadSubscriber = PhotoUploadSubscriber.bind(view, photoUploadingManager.getTaskChangingObservable(UploadPurpose.TRIP_IMAGE));
        photoUploadSubscriber.afterEach(uploadTask -> {
            if (cachedPostEntity != null && cachedPostEntity.getUploadTask().getId() == uploadTask.getId()) {
                cachedPostEntity.getUploadTask().setStatus(uploadTask.getStatus());
                processUploadTask();
            }
        });
        if (cachedPostEntity == null) {
            cachedPostEntity = new CachedPostEntity();
        } else {
            Queryable.from(photoUploadingManager.getUploadTasks(UploadPurpose.TRIP_IMAGE)).forEachR(photoUploadSubscriber::onNext);
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

    public void cancelClicked() {
        if (TextUtils.isEmpty(cachedPostEntity.getText()) &&
                cachedPostEntity.getUploadTask() == null) {
            view.cancel();
        } else {
            view.showCancelationDialog();
        }
    }

    public void post() {
        if (cachedPostEntity.getUploadTask() != null && UploadTask.Status.COMPLETED.equals(cachedPostEntity.getUploadTask().getStatus())) {
            cachedPostEntity.getUploadTask().setTitle(cachedPostEntity.getText());
            cachedPostEntity.getUploadTask().setShotAt(Calendar.getInstance().getTime());

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

    protected void processPost(FeedEntity feedEntity) {
        eventBus.post(new FeedItemAddedEvent(FeedItem.create(feedEntity, getAccount())));
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

    ////////////////////////////////////////
    /////// Photo picking

    public void onProgressClicked() {
        if (cachedPostEntity.getUploadTask().getStatus().equals(UploadTask.Status.FAILED)) {
            startUpload(cachedPostEntity.getUploadTask());
        }
    }


    public void removeImage() {
        cachedPostEntity.setUploadTask(null);
        enablePostButton();
        view.attachPhoto(null);
        view.enableImagePicker();
    }

    ////////////////////////////////////////

    public void onEvent(AttachPhotoEvent event) {
        if (view.isVisibleOnScreen() && event.getRequestType() != -1) {
            eventBus.cancelEventDelivery(event);
            pickImage(event.getRequestType());
        }
    }

    public void pickImage(int requestType) {
        eventBus.post(new ImagePickRequestEvent(requestType, REQUESTER_ID));
    }

    public void onEvent(ImagePickedEvent event) {
        if (view.isVisibleOnScreen() && event.getRequesterID() == REQUESTER_ID) {
            eventBus.cancelEventDelivery(event);
            eventBus.removeStickyEvent(event);

            attachImages(Queryable.from(event.getImages()).toList());
        }
    }

    public void attachImages(List<ChosenImage> photos) {
        if (photos.size() == 0) {
            return;
        }

        view.disableImagePicker();

        String fileThumbnail = photos.get(0).getFileThumbnail();
        imageSelected(Uri.parse(fileThumbnail).toString());
    }

    private void imageSelected(String filePath) {
        if (view != null) {
            UploadTask imageUploadTask = new UploadTask();
            imageUploadTask.setFilePath(filePath);
            cachedPostEntity.setUploadTask(imageUploadTask);
            view.attachPhoto(Uri.parse(filePath));
            doRequest(new CopyFileCommand(context, cachedPostEntity.getUploadTask().getFilePath()), s -> {
                imageUploadTask.setFilePath(s);
                imageUploadTask.setStatus(UploadTask.Status.STARTED);
                startUpload(imageUploadTask);
            });
        }
    }

    public interface View extends RxView {

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
