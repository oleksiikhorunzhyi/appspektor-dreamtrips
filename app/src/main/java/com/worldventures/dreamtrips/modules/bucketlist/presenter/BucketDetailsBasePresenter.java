package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.net.Uri;
import android.os.Bundle;

import com.apptentive.android.sdk.Log;
import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.events.FSUploadEvent;
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
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketBasePostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketCoverModel;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketActivity;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.IBucketPhotoView;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class BucketDetailsBasePresenter<V extends BucketDetailsBasePresenter.View> extends Presenter<V> {

    @Inject
    protected SnappyRepository db;

    protected BucketTabsFragment.Type type;
    protected BucketItem bucketItem;

    protected List<BucketItem> items = new ArrayList<>();

    @Inject
    protected Injector injector;

    protected Integer coverId;
    protected RequestListener<BucketItem> requestListenerUpdate = new RequestListener<BucketItem>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            view.informUser(R.string.bucket_item_edit_error);
        }

        @Override
        public void onRequestSuccess(BucketItem bucketItemUpdated) {
            onSuccess(bucketItemUpdated);
        }
    };
    private UploadBucketPhotoCommand uploadBucketPhotoCommand;
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

    public BucketDetailsBasePresenter(V view, Bundle bundle) {
        super(view);
        type = (BucketTabsFragment.Type)
                bundle.getSerializable(BucketActivity.EXTRA_TYPE);
        bucketItem = (BucketItem)
                bundle.getSerializable(BucketActivity.EXTRA_ITEM);
    }

    private void handlePhotoPick(Uri uri) {
        BucketPhotoUploadTask task = new BucketPhotoUploadTask();
        task.setTaskId((int) System.currentTimeMillis());
        task.setBucketId(bucketItem.getId());
        task.setFilePath(uri.toString());
        view.getBucketPhotosView().addImage(task);
        startUpload(task);
    }

    private void startUpload(final BucketPhotoUploadTask task) {
        uploadBucketPhotoCommand = new UploadBucketPhotoCommand(task, injector);
        dreamSpiceManager.execute(uploadBucketPhotoCommand,
                task.getTaskId(),
                DurationInMillis.ONE_MINUTE,
                new RequestListener<BucketPhoto>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        Log.e(this.getClass().getSimpleName(), "", spiceException);
                    }

                    @Override
                    public void onRequestSuccess(BucketPhoto bucketPhoto) {
                        if (bucketPhoto != null) {
                            bucketItem.getPhotos().add(bucketPhoto);
                            resaveItem(bucketItem);
                            view.getBucketPhotosView().replace(task, bucketPhoto);
                        }
                    }
                });
    }

    public void onEvent(BucketPhotoReuploadRequestEvent event) {
        eventBus.cancelEventDelivery(event);
        startUpload(event.getTask());
    }

    public void onEvent(BucketPhotoUploadCancelEvent event) {
        view.getBucketPhotosView().deleteImage(event.getTask());
    }

    public void onEvent(BucketPhotoUploadCancelRequestEvent event) {
        eventBus.cancelEventDelivery(event);
        uploadBucketPhotoCommand.cancel();
    }

    public void onEvent(BucketAddPhotoClickEvent event) {
        eventBus.cancelEventDelivery(event);
        view.getBucketPhotosView().showAddPhotoDialog();
    }

    public void onEvent(BucketPhotoFullscreenRequestEvent event) {
        eventBus.postSticky(FSUploadEvent.create(Type.BUCKET_PHOTOS, view.getBucketPhotosView().getImages()));

        List objects = view.getBucketPhotosView().getImages();
        Object obj = objects.get(event.getPosition());
        if (!(obj instanceof BucketPhotoUploadTask)) {
            openFullScreen(event.getPosition());
        }
    }

    public void openFullScreen(int position) {
        this.activityRouter.openFullScreenPhoto(position, Type.BUCKET_PHOTOS);
    }

    @Override
    public void destroyView() {
        super.destroyView();
        eventBus.unregister(this);
    }

    public void onEvent(BucketPhotoAsCoverRequestEvent event) {
        eventBus.cancelEventDelivery(event);
        coverId = event.getPhoto().getId();
        saveCover();
    }

    private void saveCover() {
        BucketCoverModel bucketCoverModel = new BucketCoverModel();
        bucketCoverModel.setCoverId(coverId);
        bucketCoverModel.setStatus(bucketItem.getStatus());
        bucketCoverModel.setType(bucketItem.getType());
        bucketCoverModel.setId(bucketItem.getId());
        saveBucketItem(bucketCoverModel);
    }

    protected void saveBucketItem(BucketBasePostItem bucketBasePostItem) {
        UpdateBucketItemCommand updateBucketItemCommand =
                new UpdateBucketItemCommand(bucketItem.getId(), bucketBasePostItem);
        dreamSpiceManager.execute(updateBucketItemCommand, requestListenerUpdate);
    }

    public void onEvent(BucketPhotoDeleteRequestEvent event) {
        eventBus.cancelEventDelivery(event);
        dreamSpiceManager.execute(new DeleteBucketPhotoCommand(String.valueOf(event.getPhoto().getFsId()), bucketItem.getId()), new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(this.getClass().getSimpleName(), "", spiceException);
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                view.getBucketPhotosView().deleteImage(event.getPhoto());
            }
        });
    }

    public void onEvent(BucketItemUpdatedEvent event) {
        bucketItem = event.getBucketItem();
        syncUI();
    }

    @Override
    public void resume() {
        super.resume();
        items.clear();
        items.addAll(db.readBucketList(type.name()));
        syncUI();

        List<BucketPhoto> photos = bucketItem.getPhotos();
        if (!photos.isEmpty()) {
            Collections.sort(photos, (lhs, rhs) -> rhs.getId() - lhs.getId());
            view.getBucketPhotosView().addImages(photos);
        }
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

    private void resaveItem(BucketItem bucketItemUpdated) {
        int i = items.indexOf(bucketItemUpdated);
        items.remove(items.indexOf(bucketItemUpdated));
        items.add(i, bucketItemUpdated);
        db.saveBucketList(items, type.name());
        eventBus.post(new BucketItemUpdatedEvent(bucketItemUpdated));
    }

    public ImagePickCallback getPhotoChooseCallback() {
        return selectImageCallback;
    }

    public ImagePickCallback getFbCallback() {
        return fbCallback;
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
