package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.net.Uri;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
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
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class BucketDetailsBasePresenter<V extends BucketDetailsBasePresenter.View> extends Presenter<V>
        implements TransferListener {

    @Inject
    BucketItemManager bucketItemManager;

    @Inject
    protected SnappyRepository db;

    protected BucketItem.BucketType type;
    protected String bucketItemId;
    protected BucketItem bucketItem;

    public BucketDetailsBasePresenter(BucketBundle bundle) {
        super();
        type = bundle.getType();
        bucketItemId = bundle.getBucketItemUid();
    }

    @Override
    public void onResume() {
        super.onResume();

        getBucketItemManager().setDreamSpiceManager(dreamSpiceManager);
        restoreBucketItem();

        List<UploadTask> tasks = db.getUploadTasksForId(bucketItemId);

        if (!tasks.isEmpty()) {
            Collections.reverse(tasks);
            Queryable.from(tasks).forEachR(task -> {
                TransferObserver transferObserver = photoUploadingSpiceManager
                        .getTransferById(task.getAmazonTaskId());
                transferObserver.setTransferListener(this);
                onStateChanged(transferObserver.getId(), transferObserver.getState());
            });
        }

        syncUI(tasks);
    }

    private void restoreBucketItem() {
        bucketItem = getBucketItemManager().getBucketItem(type, bucketItemId);
    }

    public void onEventMainThread(BucketItemUpdatedEvent event) {
        restoreBucketItem();
        syncUI();
    }

    protected void syncUI() {
        syncUI(db.getUploadTasksForId(bucketItemId));
    }

    protected void syncUI(List<UploadTask> tasks) {
        if (bucketItem != null) {
            view.setTitle(bucketItem.getName());
            view.setDescription(bucketItem.getDescription());
            view.setStatus(bucketItem.isDone());
            view.setPeople(bucketItem.getFriends());
            view.setTags(bucketItem.getBucketTags());
            view.setTime(BucketItemInfoUtil.getTime(context, bucketItem));

            List<BucketPhoto> photos = bucketItem.getPhotos();
            if (photos != null && !photos.isEmpty()) {
                int coverIndex = Math.max(photos.indexOf(bucketItem.getCoverPhoto()), 0);
                Collections.swap(photos, coverIndex, 0);
                view.setImages(photos);
            }

            if (!tasks.isEmpty()) view.addImages(tasks);
        }
    }


    //////////////////////////////
    ///////// Photo processing
    //////////////////////////////

    public void onEvent(BucketAddPhotoClickEvent event) {
        if (view.isVisibleOnScreen()) {
            eventBus.cancelEventDelivery(event);
            view.showAddPhotoDialog();
        }
    }

    public void onEvent(BucketPhotoUploadCancelRequestEvent event) {
        photoUploadingSpiceManager.cancelUploading(event.getModelObject());

        db.removeUploadTask(event.getModelObject());
        view.deleteImage(event.getModelObject());
    }

    public void onCoverClicked() {
        if (!bucketItem.getPhotos().isEmpty())
            openFullScreen(Queryable.from(bucketItem.getPhotos()).first());
    }

    public void onEvent(BucketPhotoFullscreenRequestEvent event) {
        openFullScreen(event.getPhoto());
    }

    public void openFullScreen(int position) {
        openFullScreen(bucketItem.getPhotos().get(position));
    }

    public void openFullScreen(BucketPhoto selectedPhoto) {
        if ((bucketItem.getPhotos().contains(selectedPhoto))) {
            ArrayList<IFullScreenObject> photos = new ArrayList<>();
            if (bucketItem.getCoverPhoto() != null) {
                Queryable.from(bucketItem.getPhotos()).forEachR(photo ->
                        photo.setIsCover(photo.getFsId().equals(bucketItem.getCoverPhoto().getFsId())));
            }
            photos.addAll(bucketItem.getPhotos());

            FullScreenImagesBundle data = new FullScreenImagesBundle.Builder()
                    .position(photos.indexOf(selectedPhoto))
                    .type(TripImagesListFragment.Type.FIXED_LIST)
                    .fixedList(photos)
                    .foreign(bucketItem.getOwner().getId() != appSessionHolder.get().get().getUser().getId())
                    .build();

            view.openFullscreen(data);
        }
    }

    public void onEvent(BucketPhotoAsCoverRequestEvent event) {
        if (bucketItem.getPhotos().contains(event.getPhoto())) {
            eventBus.cancelEventDelivery(event);
            saveCover(event.getPhoto().getFsId());
        }
    }

    private void saveCover(String coverID) {
        getBucketItemManager().updateBucketItemCoverId(bucketItem, coverID, this);
    }

    public void onEvent(BucketPhotoDeleteRequestEvent event) {
        if (bucketItem.getPhotos().size() > 0 &&
                bucketItem.getPhotos().contains(event.getPhoto())) {
            eventBus.cancelEventDelivery(event);
            getBucketItemManager().deleteBucketItemPhoto(event.getPhoto(),
                    bucketItem, jsonObject -> deleted(event.getPhoto()), this);
        }
    }

    protected void deleted(BucketPhoto bucketPhoto) {
        view.deleteImage(bucketPhoto);
    }

    ////////////////////////////////////////
    /////// Photo upload
    ////////////////////////////////////////

    private void copyFileIfNeeded(UploadTask task) {
        doRequest(new CopyFileCommand(context, task.getFilePath()), filePath -> upload(task, filePath));
    }

    private void upload(UploadTask uploadTask, String filePath) {
        Timber.d("Starting bucket photo upload");
        uploadTask.setFilePath(filePath);

        view.addImage(uploadTask);

        startUpload(uploadTask);
    }

    private void startUpload(UploadTask uploadTask) {
        TrackingHelper.bucketPhotoAction(TrackingHelper.ACTION_BUCKET_PHOTO_UPLOAD_START,
                uploadTask.getType(), bucketItem.getType());

        TransferObserver transferObserver = photoUploadingSpiceManager.upload(uploadTask);

        uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));

        db.saveUploadTask(uploadTask);

        transferObserver.setTransferListener(this);
    }

    @Override
    public void onStateChanged(int id, TransferState state) {
        if (view != null) {
            UploadTask bucketPhotoUploadTask = view.getBucketPhotoUploadTask(String.valueOf(id));
            if (bucketPhotoUploadTask != null) {
                if (state.equals(TransferState.COMPLETED)
                        && !bucketPhotoUploadTask.getStatus().equals(UploadTask.Status.COMPLETED)) {
                    bucketPhotoUploadTask.setOriginUrl(photoUploadingSpiceManager
                            .getResultUrl(bucketPhotoUploadTask));
                    bucketPhotoUploadTask.setStatus(UploadTask.Status.COMPLETED);
                    addPhotoToBucketItem(bucketPhotoUploadTask);
                } else if (state.equals(TransferState.FAILED)
                        && !bucketPhotoUploadTask.getStatus().equals(UploadTask.Status.FAILED)) {
                    photoUploadError(bucketPhotoUploadTask);
                }
            }
        }
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
    }

    @Override
    public void onError(int id, Exception ex) {
        UploadTask bucketPhotoUploadTask = view.getBucketPhotoUploadTask(String.valueOf(id));
        if (bucketPhotoUploadTask != null)
            photoUploadError(bucketPhotoUploadTask);
    }

    private void addPhotoToBucketItem(UploadTask task) {
        doRequest(new UploadBucketPhotoCommand(bucketItemId, task),
                photo -> photoAdded(task, photo),
                spiceException -> onError(Integer.valueOf(task.getAmazonTaskId()), spiceException));
    }

    private void photoAdded(UploadTask bucketPhotoUploadTask, BucketPhoto bucketPhoto) {
        view.replace(bucketPhotoUploadTask, bucketPhoto);
        db.removeUploadTask(bucketPhotoUploadTask);

        getBucketItemManager().updateBucketItemWithPhoto(bucketItem, bucketPhoto);
    }

    private void photoUploadError(UploadTask uploadTask) {
        uploadTask.setStatus(UploadTask.Status.FAILED);
        db.saveUploadTask(uploadTask);
        view.itemChanged(uploadTask);
    }

    public void onEvent(BucketPhotoReuploadRequestEvent event) {
        eventBus.cancelEventDelivery(event);

        db.removeUploadTask(event.getTask());
        view.deleteImage(event.getTask());

        event.getTask().setStatus(UploadTask.Status.IN_PROGRESS);
        upload(event.getTask(), event.getTask().getFilePath());
    }

    protected BucketItemManager getBucketItemManager() {
        return bucketItemManager;
    }

    ////////////////////////////////////////
    /////// Photo picking
    ////////////////////////////////////////
    public void pickImage(int requestType) {
        eventBus.post(new ImagePickRequestEvent(requestType, bucketItemId.hashCode()));
    }

    public void imagePicked(ImagePickedEvent event) {
        if (event.getRequesterID() == bucketItemId.hashCode()) {
            eventBus.removeStickyEvent(event);

            if (event.getRequestType() == PickImageDelegate.REQUEST_MULTI_SELECT) {
                Queryable.from(event.getImages()).forEachR(choseImage ->
                        imageSelected(Uri.parse(choseImage.getFilePathOriginal()), event.getRequestType()));
            } else {
                Queryable.from(event.getImages()).forEachR(choseImage -> {
                    String fileThumbnail = choseImage.getFileThumbnail();
                    if (ValidationUtils.isUrl(fileThumbnail)) {
                        imageSelected(Uri.parse(fileThumbnail), event.getRequestType());
                    } else {
                        imageSelected(Uri.fromFile(new File(fileThumbnail)), event.getRequestType());
                    }
                });
            }
        }
    }

    private void imageSelected(Uri uri, int requestType) {
        String type = "";
        switch (requestType) {
            case PickImageDelegate.REQUEST_CAPTURE_PICTURE:
                type = "camera";
                break;
            case PickImageDelegate.REQUEST_MULTI_SELECT:
            case PickImageDelegate.REQUEST_PICK_PICTURE:
                type = "album";
                break;
            case PickImageDelegate.REQUEST_FACEBOOK:
                type = "facebook";
                break;
        }

        UploadTask task = new UploadTask();
        task.setStatus(UploadTask.Status.IN_PROGRESS);
        task.setFilePath(uri.toString());
        task.setType(type);
        task.setLinkedItemId(String.valueOf(bucketItemId));

        copyFileIfNeeded(task);
    }

    @Override
    public void dropView() {
        super.dropView();
        Queryable.from(photoUploadingSpiceManager.getUploadingTranferListeners())
                .forEachR(observer ->
                        observer.setTransferListener(null));
    }

    public interface View extends Presenter.View {
        void setTitle(String title);

        void setDescription(String description);

        void setTime(String time);

        void setPeople(String people);

        void setTags(String tags);

        void setStatus(boolean isCompleted);

        void done();

        void showAddPhotoDialog();

        void openFullscreen(FullScreenImagesBundle data);

        void setImages(List<BucketPhoto> photos);

        void addImages(List<UploadTask> tasks);

        void addImage(UploadTask uploadTask);

        void deleteImage(UploadTask task);

        void deleteImage(BucketPhoto bucketPhoto);

        void itemChanged(UploadTask uploadTask);

        void replace(UploadTask bucketPhotoUploadTask, BucketPhoto bucketPhoto);

        UploadTask getBucketPhotoUploadTask(String taskId);
    }
}
