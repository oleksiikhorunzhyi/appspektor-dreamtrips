package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.net.Uri;
import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.UploadBucketPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketAddPhotoClickEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoAsCoverRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoDeleteRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoFullscreenRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoReuploadRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadCancelRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.util.BucketItemInfoUtil;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.IBucketPhotoView;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.events.UploadStatusChanged;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class BucketDetailsBasePresenter<V extends BucketDetailsBasePresenter.View> extends Presenter<V> {

    @Inject
    BucketItemManager bucketItemManager;

    @Inject
    SnappyRepository db;

    protected BucketTabsPresenter.BucketType type;
    protected int bucketItemId;

    protected BucketItem bucketItem;

    protected PickImageDelegate.ImagePickCallback captureImageCallback = chosenImage -> {
        if (chosenImage != null) {
            handlePhotoPick(Uri.parse(chosenImage[0].getFilePathOriginal()), "camera");
        }
    };
    protected PickImageDelegate.ImagePickCallback chooseImageCallback = chosenImage -> {
        if (chosenImage != null) {
            handlePhotoPick(Uri.parse(chosenImage[0].getFilePathOriginal()), "album");
        }
    };
    protected PickImageDelegate.ImagePickCallback mulitImageCallback = chosenImages -> {
        for (ChosenImage image : chosenImages) {
            handlePhotoPick(Uri.parse(image.getFilePathOriginal()), "album");
        }
    };
    protected PickImageDelegate.ImagePickCallback fbCallback = chosenImage -> {
        if (chosenImage != null) {
            handlePhotoPick(Uri.parse(chosenImage[0].getFilePathOriginal()), "facebook");
        }
    };

    public BucketDetailsBasePresenter(Bundle bundle) {
        super();
        type = (BucketTabsPresenter.BucketType)
                bundle.getSerializable(BucketActivity.EXTRA_TYPE);
        bucketItemId = bundle.getInt(BucketActivity.EXTRA_ITEM);
    }

    @Override
    public void takeView(V view) {
        super.takeView(view);
        view.updatePhotos();

        bucketItem = bucketItemManager.getBucketItem(type, bucketItemId);
        bucketItemManager.setDreamSpiceManager(dreamSpiceManager);

        syncUI();
    }

    public void onEventMainThread(BucketItemUpdatedEvent event) {
        bucketItem = bucketItemManager.getBucketItem(type, bucketItemId);
        syncUI();
    }

    protected void syncUI() {
        view.setTitle(bucketItem.getName());
        view.setDescription(bucketItem.getDescription());
        view.setStatus(bucketItem.isDone());
        view.setPeople(bucketItem.getFriends());
        view.setTags(bucketItem.getBucketTags());
        view.setTime(BucketItemInfoUtil.getTime(context, bucketItem));

        List<BucketPhoto> photos = bucketItem.getPhotos();
        if (photos != null) {
            Collections.sort(photos, (lhs, rhs) -> rhs.getId() - lhs.getId());
            view.getBucketPhotosView().setImages(photos);
        }

        List<UploadTask> tasks = db.getUploadTasksForId(String.valueOf(bucketItem.getId()));
        Collections.reverse(tasks);

        view.getBucketPhotosView().addImages(tasks);
    }


    //////////////////////////////
    ///////// Photo upload staff
    //////////////////////////////

    public PickImageDelegate.ImagePickCallback getGalleryChooseCallback() {
        return chooseImageCallback;
    }

    public PickImageDelegate.ImagePickCallback getPhotoChooseCallback() {
        return captureImageCallback;
    }

    public PickImageDelegate.ImagePickCallback getFbCallback() {
        return fbCallback;
    }

    public PickImageDelegate.ImagePickCallback getMultiSelectPickCallback() {
        return mulitImageCallback;
    }


    public void onEvent(BucketAddPhotoClickEvent event) {
        eventBus.cancelEventDelivery(event);
        view.getBucketPhotosView().showAddPhotoDialog(false);
    }

    public void onEvent(BucketPhotoUploadCancelRequestEvent event) {
        photoUploadingSpiceManager.cancelUploading(event.getModelObject());

        db.removeUploadTask(event.getModelObject());
        view.getBucketPhotosView().deleteImage(event.getModelObject());
    }

    public void onCoverClicked() {
        if (!bucketItem.getPhotos().isEmpty())
            openFullScreen(Queryable.from(bucketItem.getPhotos()).first());
    }

    public void onEvent(BucketPhotoFullscreenRequestEvent event) {
        openFullScreen(event.getPhoto());
    }

    public void openFullScreen(BucketPhoto selectedPhoto) {
        if (!view.getBucketPhotosView().getImages().isEmpty()) {
            List<IFullScreenObject> photos = new ArrayList<>();
            if (bucketItem.getCoverPhoto() != null) {
                Queryable.from(bucketItem.getPhotos()).forEachR(photo ->
                        photo.setIsCover(bucketItem.getCoverPhoto().getId() == photo.getId()));
            }
            photos.addAll(bucketItem.getPhotos());
            db.savePhotoEntityList(Type.BUCKET_PHOTOS, photos);
            this.activityRouter.openFullScreenPhoto(photos.indexOf(selectedPhoto), Type.BUCKET_PHOTOS);
        }
    }

    public void onEvent(BucketPhotoAsCoverRequestEvent event) {
        if (bucketItem.getPhotos().contains(event.getPhoto())) {
            eventBus.cancelEventDelivery(event);
            saveCover(event.getPhoto().getId());
        }
    }

    private void saveCover(int coverID) {
        bucketItemManager.updateBucketItemCoverId(bucketItem, coverID, this);
    }

    public void onEvent(BucketPhotoDeleteRequestEvent event) {
        eventBus.cancelEventDelivery(event);
        bucketItemManager.deleteBucketItemPhoto(event.getPhoto(),
                bucketItem, jsonObject -> deleted(event.getPhoto()), this);
    }

    protected void deleted(BucketPhoto bucketPhoto) {
        view.getBucketPhotosView().deleteImage(bucketPhoto);
    }

    ////////////////////////////////////////
    /////// Photo upload
    ////////////////////////////////////////

    private void handlePhotoPick(Uri uri, String type) {
        UploadTask task = new UploadTask();
        task.setStatus(UploadTask.Status.IN_PROGRESS);
        task.setFilePath(uri.toString());
        task.setType(type);
        task.setLinkedItemId(String.valueOf(bucketItemId));

        copyFileIfNeeded(task);
    }

    private void copyFileIfNeeded(UploadTask task) {
        doRequest(new CopyFileCommand(context, task.getFilePath()), filePath -> upload(task, filePath));
    }

    private void upload(UploadTask uploadTask, String filePath) {
        Timber.d("Starting bucket photo upload");
        uploadTask.setFilePath(filePath);

        view.getBucketPhotosView().addImage(uploadTask);

        startUpload(uploadTask);
    }

    private void startUpload(UploadTask uploadTask) {
        TrackingHelper.bucketPhotoAction(TrackingHelper.ACTION_BUCKET_PHOTO_UPLOAD_START,
                uploadTask.getType(), bucketItem.getType());
        photoUploadingSpiceManager.uploadPhotoToS3(uploadTask);
    }

    public void onEventMainThread(UploadStatusChanged event) {
        UploadTask uploadTask = view.getBucketPhotosView()
                .getBucketPhotoUploadTask(event.getUploadTask().getFilePath());

        if (uploadTask != null) {
            uploadTask.changed(event.getUploadTask());
            view.getBucketPhotosView().itemChanged(uploadTask);

            if (uploadTask.getStatus().equals(UploadTask.Status.COMPLETED))
                addPhotoToBucketItem(uploadTask);
        }
    }

    public void onEvent(BucketPhotoReuploadRequestEvent event) {
        eventBus.cancelEventDelivery(event);

        event.getTask().setStatus(UploadTask.Status.IN_PROGRESS);
        view.getBucketPhotosView().itemChanged(event.getTask());

        startUpload(event.getTask());
    }

    private void addPhotoToBucketItem(UploadTask task) {
        doRequest(new UploadBucketPhotoCommand(bucketItemId, task),
                photo -> photoAdded(task, photo),
                spiceException -> photoUploadError(task));
    }

    private void photoAdded(UploadTask bucketPhotoUploadTask, BucketPhoto bucketPhoto) {
        view.getBucketPhotosView().replace(bucketPhotoUploadTask, bucketPhoto);
        db.removeUploadTask(bucketPhotoUploadTask);

        bucketItemManager.updateBucketItemWithPhoto(bucketItem, bucketPhoto);
    }

    private void photoUploadError(UploadTask task) {
        UploadTask bucketPhotoUploadTask = view.getBucketPhotosView()
                .getBucketPhotoUploadTask(task.getFilePath());
        bucketPhotoUploadTask.setStatus(UploadTask.Status.FAILED);
        view.getBucketPhotosView().itemChanged(bucketPhotoUploadTask);
    }

    public interface View extends Presenter.View {
        void setTitle(String title);

        void setDescription(String description);

        void setTime(String time);

        void setPeople(String people);

        void setTags(String tags);

        void setStatus(boolean isCompleted);

        void done();

        void updatePhotos();

        IBucketPhotoView getBucketPhotosView();
    }
}
