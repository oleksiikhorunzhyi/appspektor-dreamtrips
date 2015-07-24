package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.net.Uri;
import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.UploadBucketPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketAddPhotoClickEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoAsCoverRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoDeleteRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoFullscreenRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoReuploadRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadCancelEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadCancelRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.manager.BucketItemManager;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.bucketlist.util.BucketItemInfoUtil;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.IBucketPhotoView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.MultiSelectPickCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class BucketDetailsBasePresenter<V extends BucketDetailsBasePresenter.View> extends Presenter<V> {

    @Inject
    BucketItemManager bucketItemManager;
    @Inject
    SnappyRepository db;

    @Inject
    @ForApplication
    Injector injector;


    protected BucketTabsPresenter.BucketType type;
    protected int bucketItemId;

    protected BucketItem bucketItem;

    protected ImagePickCallback selectImageCallback = (fragment, image, error) -> {
        if (error != null) {
            view.informUser(error);
        } else {
            Uri uri = Uri.fromFile(new File(image.getFileThumbnail()));
            handlePhotoPick(uri, "camera");
        }
    };
    protected ImagePickCallback chooseImageCallback = (fragment, image, error) -> {
        if (error != null) {
            view.informUser(error);
        } else {
            Uri uri = Uri.fromFile(new File(image.getFileThumbnail()));
            handlePhotoPick(uri, "album");
        }
    };
    protected MultiSelectPickCallback multiSelectPickCallback = (fm, list, error) -> {
        for (Uri uri : list) {
            handlePhotoPick(Uri.fromFile(new File(uri.toString())), "album");
        }
    };
    protected ImagePickCallback fbCallback = (fragment, image, error) -> {
        if (error != null) {
            view.informUser(error);
        } else {
            Uri uri = Uri.parse(image.getFilePathOriginal());
            handlePhotoPick(uri, "facebook");
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
    }

    @Override
    public void onResume() {
        super.onResume();
        bucketItem = bucketItemManager.getBucketItem(type, bucketItemId);
        bucketItemManager.setDreamSpiceManager(dreamSpiceManager);

        syncUI();
    }

    public void onEventMainThread(BucketItemUpdatedEvent event) {
        bucketItem = bucketItemManager.getBucketItem(type, bucketItemId);
        syncUI();
    }

    public void onEvent(BucketPhotoUploadCancelRequestEvent event) {
        mediaSpiceManager.cancel(BucketPhoto.class, event.getModelObject().getTaskId());
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
        List<BucketPhotoUploadTask> tasks = db.getBucketPhotoTasksBy(bucketItem.getId());
        Collections.reverse(tasks);
        view.getBucketPhotosView().addImages(tasks);
    }


    //////////////////////////////
    ///////// Photo upload staff
    //////////////////////////////

    public ImagePickCallback getGalleryChooseCallback() {
        return chooseImageCallback;
    }

    public ImagePickCallback getPhotoChooseCallback() {
        return selectImageCallback;
    }

    public ImagePickCallback getFbCallback() {
        return fbCallback;
    }

    public MultiSelectPickCallback getMultiSelectPickCallback() {
        return multiSelectPickCallback;
    }

    private void handlePhotoPick(Uri uri, String type) {
        BucketPhotoUploadTask task = new BucketPhotoUploadTask();
        task.setTaskId(System.currentTimeMillis());
        task.setBucketId(bucketItem.getId());
        task.setFilePath(uri.toString());
        task.setSelectionType(type);
        TrackingHelper.bucketPhotoAction(TrackingHelper.ACTION_BUCKET_PHOTO_UPLOAD_START, type, bucketItem.getType());
        startUpload(task);

        if (view != null)
            view.getBucketPhotosView().addImage(task);
    }

    private void startUpload(final BucketPhotoUploadTask task) {
        if (!mediaSpiceManager.isStarted()) mediaSpiceManager.start(context);
        mediaSpiceManager.uploadPhoto(task, bucketItem, type, injector, this);
    }

    public void onEvent(BucketPhotoReuploadRequestEvent event) {
        eventBus.cancelEventDelivery(event);
        TrackingHelper.bucketPhotoAction(TrackingHelper.ACTION_BUCKET_PHOTO_UPLOAD_START,
                event.getTask().getSelectionType(),
                bucketItem.getType());
        startUpload(event.getTask());
    }

    public void onEvent(BucketPhotoUploadCancelEvent event) {
        view.getBucketPhotosView().deleteImage(event.getTask());
        TrackingHelper.bucketPhotoAction(TrackingHelper.ACTION_BUCKET_PHOTO_UPLOAD_CANCEL,
                event.getTask().getSelectionType(),
                bucketItem.getType());
    }

    public void onEvent(BucketAddPhotoClickEvent event) {
        eventBus.cancelEventDelivery(event);
        view.getBucketPhotosView().showAddPhotoDialog(false);
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
            Queryable.from(bucketItem.getPhotos()).forEachR(photo ->
                    photo.setIsCover(bucketItem.getCoverPhoto().getId() == photo.getId()));
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
