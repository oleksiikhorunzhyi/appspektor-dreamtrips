package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.net.Uri;
import android.os.Bundle;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.BucketListModule;
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
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.IBucketPhotoView;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullScreenPhotoWrapperFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

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
    SnappyRepository db;

    protected BucketTabsPresenter.BucketType type;
    protected String bucketItemId;
    protected BucketItem extraBucketItem;

    protected BucketItem bucketItem;

    List<UploadTask> tasks;

    public BucketDetailsBasePresenter(Bundle bundle) {
        super();
        type = (BucketTabsPresenter.BucketType)
                bundle.getSerializable(BucketListModule.EXTRA_TYPE);
        bucketItemId = bundle.getString(BucketListModule.EXTRA_ITEM_ID);
        extraBucketItem = (BucketItem) bundle.getSerializable(BucketListModule.EXTRA_ITEM);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBucketItemManager().setDreamSpiceManager(dreamSpiceManager);
        restoreBucketItem();
        syncUI();

        if (tasks != null)
            Queryable.from(tasks).forEachR(task -> {
                TransferObserver transferObserver = photoUploadingSpiceManager
                        .getTransferById(task.getAmazonTaskId());
                transferObserver.setTransferListener(this);
                onStateChanged(transferObserver.getId(), transferObserver.getState());
            });

        ImagePickedEvent event = eventBus.getStickyEvent(ImagePickedEvent.class);
        if (event != null) {
            imagePicked(event);
        }
    }

    private void restoreBucketItem() {
        bucketItem = getBucketItemManager().getBucketItem(type, bucketItemId);
        if (bucketItem == null) {
            bucketItem = extraBucketItem;
        }
    }

    public void onEventMainThread(BucketItemUpdatedEvent event) {
        restoreBucketItem();
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
            view.getBucketPhotosView().setImages(photos);
        }

        tasks = db.getUploadTasksForId(bucketItemId);
        Collections.reverse(tasks);

        view.getBucketPhotosView().addImages(tasks);
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
        if ((bucketItem.getPhotos().contains(selectedPhoto))) {
            ArrayList<IFullScreenObject> photos = new ArrayList<>();
            if (bucketItem.getCoverPhoto() != null) {
                Queryable.from(bucketItem.getPhotos()).forEachR(photo ->
                        photo.setIsCover(photo.getFsId().equals(bucketItem.getCoverPhoto().getFsId())));
            }
            photos.addAll(bucketItem.getPhotos());

            Bundle args = new Bundle();
            args.putSerializable(FullScreenPhotoWrapperFragment.EXTRA_POSITION, photos.indexOf(selectedPhoto));
            args.putSerializable(FullScreenPhotoWrapperFragment.EXTRA_TYPE, TripImagesListFragment.Type.FIXED_LIST);
            args.putSerializable(FullScreenPhotoWrapperFragment.EXTRA_FIXED_LIST, photos);
            view.openFullscreen(args);
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
        view.getBucketPhotosView().deleteImage(bucketPhoto);
    }

    ////////////////////////////////////////
    /////// Photo upload
    ////////////////////////////////////////

    private void copyFileIfNeeded(UploadTask task) {
        eventBus.removeStickyEvent(ImagePickedEvent.class);
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

        TransferObserver transferObserver = photoUploadingSpiceManager.upload(uploadTask);

        uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));

        db.saveUploadTask(uploadTask);

        transferObserver.setTransferListener(this);
    }

    @Override
    public void onStateChanged(int id, TransferState state) {
        if (view != null)
            if (state.equals(TransferState.COMPLETED)) {
                UploadTask bucketPhotoUploadTask = view.getBucketPhotosView()
                        .getBucketPhotoUploadTask(String.valueOf(id));
                bucketPhotoUploadTask.setOriginUrl(photoUploadingSpiceManager
                        .getResultUrl(bucketPhotoUploadTask));
                bucketPhotoUploadTask.setStatus(UploadTask.Status.COMPLETED);
                addPhotoToBucketItem(bucketPhotoUploadTask);
            } else if (state.equals(TransferState.FAILED)) {
                photoUploadError(String.valueOf(id));
            }
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
    }

    @Override
    public void onError(int id, Exception ex) {
        photoUploadError(String.valueOf(id));
    }

    private void addPhotoToBucketItem(UploadTask task) {
        doRequest(new UploadBucketPhotoCommand(bucketItemId, task),
                photo -> photoAdded(task, photo),
                spiceException -> photoUploadError(task.getAmazonTaskId()));
    }

    private void photoAdded(UploadTask bucketPhotoUploadTask, BucketPhoto bucketPhoto) {
        view.getBucketPhotosView().replace(bucketPhotoUploadTask, bucketPhoto);
        db.removeUploadTask(bucketPhotoUploadTask);

        getBucketItemManager().updateBucketItemWithPhoto(bucketItem, bucketPhoto);
    }

    private void photoUploadError(String taskId) {
        UploadTask bucketPhotoUploadTask = view.getBucketPhotosView()
                .getBucketPhotoUploadTask(taskId);
        bucketPhotoUploadTask.setStatus(UploadTask.Status.FAILED);
        db.saveUploadTask(bucketPhotoUploadTask);
        view.getBucketPhotosView().itemChanged(bucketPhotoUploadTask);
    }

    public void onEvent(BucketPhotoReuploadRequestEvent event) {
        eventBus.cancelEventDelivery(event);

        db.removeUploadTask(event.getTask());
        view.getBucketPhotosView().deleteImage(event.getTask());

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
            Queryable.from(event.getImages()).forEachR(choseImage ->
                    imageSelected(Uri.parse(choseImage.getFilePathOriginal()), event.getRequestType()));
        }
    }

    private void imageSelected(Uri uri, int requestType) {
        String type = "";
        switch (requestType) {
            case PickImageDelegate.REQUEST_CAPTURE_PICTURE:
                type = "camera";
                break;
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

        IBucketPhotoView getBucketPhotosView();

        void openFullscreen(Bundle args);
    }
}
