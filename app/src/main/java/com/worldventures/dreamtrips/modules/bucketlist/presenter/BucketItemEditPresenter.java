package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.innahema.collections.query.functions.Converter;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.SocialUploaderyManager;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.TextUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.UploadBucketPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemPhotoAnalyticEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemUpdatedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoCreationItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import rx.Subscription;
import timber.log.Timber;

public class BucketItemEditPresenter extends BucketDetailsBasePresenter<BucketItemEditPresenterView> {

    public static final int BUCKET_MEDIA_REQUEST_ID = BucketItemEditPresenter.class.getSimpleName().hashCode();

    @Inject
    MediaPickerManager mediaPickerManager;

    private Date selectedDate;

    private boolean savingItem = false;
    @Inject
    SocialUploaderyManager uploaderyManager;
    private Subscription mediaSubscription;
    private Subscription uploaderySubscription;

    public BucketItemEditPresenter(BucketBundle bundle) {
        super(bundle);
    }

    @Override
    public void takeView(BucketItemEditPresenterView view) {
        priorityEventBus = 1;
        super.takeView(view);
        uploaderySubscription = uploaderyManager.getTaskChangingObservable()
                .cache()
                .compose(new IoToMainComposer<>())
                .subscribe(state -> {
                    BucketPhotoCreationItem uploadTask = db.getBucketPhotoCreationItem((state.action.getFilePath()));
                    if (uploadTask != null && Objects.equals(uploadTask.getBucketId(), bucketItem.getUid())) {
                        uploadTask.setStatus(state.status);
                        BucketPhotoCreationItem bucketPhotoUploadTask = view.getBucketPhotoUploadTask(uploadTask.getFilePath());
                        if (bucketPhotoUploadTask != null) {
                            bucketPhotoUploadTask.setStatus(state.status);
                        }

                        switch (state.status) {
                            case START:
                            case PROGRESS:
                                view.addImage(uploadTask);
                                break;
                            case SUCCESS:
                                uploadTask.setOriginUrl(((SimpleUploaderyCommand) state.action).getResult().getPhotoUploadResponse().getLocation());
                                addPhotoToBucketItem(uploadTask);
                                break;
                            case FAIL:
                                view.itemChanged(uploadTask);
                                break;
                        }
                    }
                }, error -> {
                    Timber.e(error, "");
                });
        //
        mediaSubscription = mediaPickerManager.toObservable()
                .filter(attachment -> attachment.requestId == BUCKET_MEDIA_REQUEST_ID)
                .subscribe(mediaAttachment -> attachImages(mediaAttachment.chosenImages, mediaAttachment.type));
    }

    @Override
    public void dropView() {
        super.dropView();
        if (!mediaSubscription.isUnsubscribed()) mediaSubscription.unsubscribe();
        if (!uploaderySubscription.isUnsubscribed()) uploaderySubscription.unsubscribe();
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedDate = bucketItem.getTargetDate();
        List<CategoryItem> list = db.readList(SnappyRepository.CATEGORIES, CategoryItem.class);
        if (!list.isEmpty()) {
            view.setCategoryItems(list);
            view.setCategory(list.indexOf(bucketItem.getCategory()));
        }
    }

    public void saveItem(boolean closeView) {
        if (closeView) view.showLoading();
        savingItem = true;
        BucketPostItem bucketPostItem = new BucketPostItem();
        bucketPostItem.setId(bucketItemId);
        bucketPostItem.setName(view.getTitle());
        bucketPostItem.setDescription(view.getDescription());
        bucketPostItem.setStatus(view.getStatus());
        bucketPostItem.setTags(TextUtils.getListFromString(view.getTags()));
        bucketPostItem.setPeople(TextUtils.getListFromString(view.getPeople()));
        bucketPostItem.setCategory(view.getSelectedItem());
        bucketPostItem.setDate(selectedDate);
        getBucketItemManager().updateBucketItem(bucketPostItem, item -> {
            if (savingItem) {
                eventBus.post(new FeedEntityChangedEvent((item)));
                savingItem = false;
                if (closeView) view.done();
            }
        }, this);
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.hideLoading();
    }

    public Date getDate() {
        if (bucketItem.getTargetDate() != null) {
            return bucketItem.getTargetDate();
        } else {
            return Calendar.getInstance().getTime();
        }
    }

    @Nullable
    @Override
    protected List getBucketPhotos() {
        List bucketPhotos = new ArrayList<>(super.getBucketPhotos());
        List tasks = Queryable.from(db.getAllBucketPhotoCreationItem())
                .map(element -> {
                    element.setStatus(ActionState.Status.FAIL);
                    return element;
                })
                .filter(element -> {
                    return Objects.equals(bucketItemId, element.getBucketId());
                }).toList();
        bucketPhotos.addAll(bucketPhotos.isEmpty() ? 0 : 1, tasks);
        return bucketPhotos;
    }

    public void deletePhotoRequest(BucketPhoto bucketPhoto) {
        if (bucketItem.getPhotos().size() > 0 && bucketItem.getPhotos().contains(bucketPhoto)) {
            getBucketItemManager().deleteBucketItemPhoto(bucketPhoto,
                    bucketItem, jsonObject -> view.deleteImage(bucketPhoto), this);
        }
    }

    public void onDateSet(int year, int month, int day) {
        String date = DateTimeUtils.convertDateToString(year, month, day);
        view.setTime(date);
        setDate(DateTimeUtils.dateFromString(date));
    }

    public void setDate(Date date) {
        this.selectedDate = date;
    }

    public void onDateClear() {
        view.setTime(context.getString(R.string.someday));
        setDate(null);
    }

    ////////////////////////////////////////
    /////// Photo picking
    ////////////////////////////////////////

    public void attachImages(List<PhotoGalleryModel> chosenImages, int type) {
        if (chosenImages.size() == 0) {
            return;
        }
        view.hideMediaPicker();
        Queryable.from(chosenImages).forEachR(choseImage ->
                imageSelected(Uri.parse(choseImage.getThumbnailPath()), type));
    }

    public void onEventMainThread(BucketItemUpdatedEvent event) {
        // nothing to do. we already here
    }

    private void imageSelected(Uri uri, int requestType) {
        BucketPhotoCreationItem task = new BucketPhotoCreationItem();
        task.setStatus(ActionState.Status.START);
        task.setFilePath(uri.toString());
        task.setBucketId(String.valueOf(bucketItemId));

        doRequest(new CopyFileCommand(context, task.getFilePath()), filePath -> {
            task.setFilePath(filePath);
            startUpload(task);
        });
    }

    public void onUploadTaskClicked(BucketPhotoCreationItem uploadTask) {
        if (uploadTask.getStatus().equals(ActionState.Status.FAIL)) {
            startUpload(uploadTask);
        } else {
            cancelUpload(uploadTask);
        }
    }

    protected void startUpload(BucketPhotoCreationItem uploadTask) {
        TrackingHelper.bucketPhotoAction(TrackingHelper.ACTION_BUCKET_PHOTO_UPLOAD_START, "", bucketItem.getType());
        eventBus.post(new BucketItemPhotoAnalyticEvent(TrackingHelper.ATTRIBUTE_UPLOAD_PHOTO, bucketItem.getUid()));
        uploaderyManager.upload(uploadTask.getFilePath());
        uploadTask.setBucketId(bucketItem.getUid());
        db.saveBucketPhotoCreationItem(uploadTask);
    }

    private void onError(String filePath, Exception ex) {
        if (view == null) return;
        //
        BucketPhotoCreationItem bucketPhotoUploadTask = view.getBucketPhotoUploadTask(filePath);
        if (bucketPhotoUploadTask != null) view.itemChanged(bucketPhotoUploadTask);
    }

    private void addPhotoToBucketItem(BucketPhotoCreationItem task) {
        doRequest(new UploadBucketPhotoCommand(bucketItemId, task),
                photo -> photoAdded(task, photo),
                spiceException -> onError(task.getFilePath(), spiceException));
    }

    private void photoAdded(BucketPhotoCreationItem bucketPhotoUploadTask, BucketPhoto bucketPhoto) {
        view.replace(bucketPhotoUploadTask, bucketPhoto);
        db.removeBucketPhotoCreationItem(bucketPhotoUploadTask);
        getBucketItemManager().updateBucketItemWithPhoto(bucketItem, bucketPhoto);
    }

    protected void cancelUpload(BucketPhotoCreationItem uploadTask) {
        db.removeBucketPhotoCreationItem(uploadTask);
        view.deleteImage(uploadTask);
    }
}
