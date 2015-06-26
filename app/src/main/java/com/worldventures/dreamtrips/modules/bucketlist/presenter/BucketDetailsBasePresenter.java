package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.net.Uri;
import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.events.PhotoDeletedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.DeleteBucketPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.UploadBucketPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketAddPhotoClickEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoAsCoverRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoDeleteRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoFullscreenRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoReuploadRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadCancelEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadCancelRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.CoverSetEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketBasePostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketCoverModel;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
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
    protected SnappyRepository db;

    protected BucketTabsPresenter.BucketType type;
    protected BucketItem bucketItem;

    protected List<BucketItem> items = new ArrayList<>();


    @Inject
    @ForApplication
    protected Injector injector;

    private UploadBucketPhotoCommand uploadBucketPhotoCommand;

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
        bucketItem = (BucketItem)
                bundle.getSerializable(BucketActivity.EXTRA_ITEM);
    }

    private void handlePhotoPick(Uri uri, String type) {
        BucketPhotoUploadTask task = new BucketPhotoUploadTask();
        task.setTaskId(System.currentTimeMillis());
        task.setBucketId(bucketItem.getId());
        task.setFilePath(uri.toString());
        task.setSelectionType(type);
        view.getBucketPhotosView().addImage(task);
        TrackingHelper.bucketPhotoAction(TrackingHelper.ACTION_BUCKET_PHOTO_UPLOAD_START, type, bucketItem.getType());
        startUpload(task);
    }

    private void startUpload(final BucketPhotoUploadTask task) {
        uploadBucketPhotoCommand = new UploadBucketPhotoCommand(task, bucketItem, type, injector);
        videoCachingSpiceManager.execute(uploadBucketPhotoCommand, new RequestListener<BucketPhoto>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                view.informUser(R.string.bucket_photo_upload_error);
            }

            @Override
            public void onRequestSuccess(BucketPhoto bucketPhoto) {
                if (bucketPhoto != null) {
                    TrackingHelper.bucketPhotoAction(TrackingHelper.ACTION_BUCKET_PHOTO_UPLOAD_FINISH, task.getSelectionType(), bucketItem.getType());
                }
            }
        });
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

    public void onEvent(BucketPhotoUploadCancelRequestEvent event) {
        eventBus.cancelEventDelivery(event);
        uploadBucketPhotoCommand.cancel();
    }

    public void onEvent(BucketAddPhotoClickEvent event) {
        eventBus.cancelEventDelivery(event);
        view.getBucketPhotosView().showAddPhotoDialog(false);
    }

    public void onEvent(BucketPhotoFullscreenRequestEvent event) {

        List objects = view.getBucketPhotosView().getImages();
        Object obj = objects.get(event.getPosition());
        if (!(obj instanceof BucketPhotoUploadTask)) {
            openFullScreen(event.getPosition());
        }
    }

    public void openFullScreen(int position) {
        if (!view.getBucketPhotosView().getImages().isEmpty()) {
            List<IFullScreenObject> photos = new ArrayList<>();
            Queryable.from(bucketItem.getPhotos()).forEachR(photo ->
                    photo.setIsCover(bucketItem.getCoverPhoto().getId() == photo.getId()));
            photos.addAll(bucketItem.getPhotos());
            db.savePhotoEntityList(Type.BUCKET_PHOTOS, photos);
            this.activityRouter.openFullScreenPhoto(position, Type.BUCKET_PHOTOS);
        }
    }

    @Override
    public void dropView() {
        super.dropView();
        eventBus.unregister(this);
    }

    public void onEvent(BucketPhotoAsCoverRequestEvent event) {
        if (bucketItem.getPhotos().contains(event.getPhoto())) {
            eventBus.cancelEventDelivery(event);
            saveCover(event.getPhoto().getId());
        }
    }

    private void saveCover(int coverID) {
        BucketCoverModel bucketCoverModel = new BucketCoverModel();
        bucketCoverModel.setCoverId(coverID);
        bucketCoverModel.setStatus(bucketItem.getStatus());
        bucketCoverModel.setType(bucketItem.getType());
        bucketCoverModel.setId(String.valueOf(bucketItem.getId()));
        eventBus.post(new CoverSetEvent(coverID));
        saveBucketItem(bucketCoverModel);
    }

    protected void saveBucketItem(BucketBasePostItem bucketBasePostItem) {
        UpdateBucketItemCommand updateBucketItemCommand =
                new UpdateBucketItemCommand(bucketItem.getId(), bucketBasePostItem);
        doRequest(updateBucketItemCommand, this::onSuccess);
    }

    public void onEvent(BucketPhotoDeleteRequestEvent event) {
        eventBus.cancelEventDelivery(event);
        doRequest(new DeleteBucketPhotoCommand(event.getPhoto().getFsId(),
                        bucketItem.getId()),
                (jsonObject) -> {
                    deleted(event.getPhoto());
                    bucketItem.getPhotos().remove(event.getPhoto());
                    resaveItem(bucketItem);
                    view.getBucketPhotosView().deleteImage(event.getPhoto());
                }, this);
    }

    protected void deleted(BucketPhoto bucketPhoto) {
        bucketItem.getPhotos().remove(bucketPhoto);

        if (bucketItem.getCoverPhoto() != null &&
                bucketItem.getCoverPhoto().equals(bucketPhoto)) {
            bucketItem.setCoverPhoto(bucketItem.getFirstPhoto());
        }

        resaveItem(bucketItem);
        view.getBucketPhotosView().deleteImage(bucketPhoto);
    }

    public void onEventMainThread(BucketItemUpdatedEvent event) {
        bucketItem = event.getBucketItem();
        syncUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        items.clear();
        items.addAll(db.readBucketList(type.name()));
        syncUI();

        List<BucketPhoto> photos = bucketItem.getPhotos();
        if (!photos.isEmpty()) {
            Collections.sort(photos, (lhs, rhs) -> rhs.getId() - lhs.getId());
            view.getBucketPhotosView().setImages(photos);
        }
        List<BucketPhotoUploadTask> tasks = db.getBucketPhotoTasksBy(bucketItem.getId());
        Collections.reverse(tasks);
        view.getBucketPhotosView().addImages(tasks);

    }

    protected void syncUI() {
        view.setTitle(bucketItem.getName());
        view.setDescription(bucketItem.getDescription());
        view.setStatus(bucketItem.isDone());
        view.setPeople(bucketItem.getFriends());
        view.setTags(bucketItem.getBucketTags());
        view.setTime(DateTimeUtils.convertDateToReference(context, bucketItem.getTarget_date()));
    }

    protected void onSuccess(BucketItem bucketItemUpdated) {
        resaveItem(bucketItemUpdated);
    }

    private void resaveItem(BucketItem updatedItem) {
        int oldPosition = items.indexOf(updatedItem);
        BucketItem oldItem = items.get(oldPosition);
        int newPosition = (oldItem.isDone() && !updatedItem.isDone()) ? 0 : oldPosition;
        items.remove(oldPosition);
        items.add(newPosition, updatedItem);
        db.saveBucketList(items, type.name());
        eventBus.post(new BucketItemUpdatedEvent(updatedItem));
    }

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


    public interface View extends Presenter.View {
        void setTitle(String title);

        void setDescription(String description);

        void setTime(String time);

        void setPeople(String people);

        void setTags(String tags);

        void setStatus(boolean isCompleted);

        void done();

        IBucketPhotoView getBucketPhotosView();
    }
}
