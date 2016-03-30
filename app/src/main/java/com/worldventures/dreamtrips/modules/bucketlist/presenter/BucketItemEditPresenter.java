package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.net.Uri;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.PhotoUploadSubscriber;
import com.worldventures.dreamtrips.core.api.UploadPurpose;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.TextUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.UploadBucketPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemPhotoAnalyticEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;

public class BucketItemEditPresenter extends BucketDetailsBasePresenter<BucketItemEditPresenterView> {

    public static final int BUCKET_MEDIA_REQUEST_ID = BucketItemEditPresenter.class.getSimpleName().hashCode();

    @Inject
    MediaPickerManager mediaPickerManager;

    private Date selectedDate;

    private boolean savingItem = false;

    PhotoUploadSubscriber photoUploadSubscriber;
    private Subscription mediaSubscription;

    public BucketItemEditPresenter(BucketBundle bundle) {
        super(bundle);
    }

    @Override
    public void takeView(BucketItemEditPresenterView view) {
        priorityEventBus = 1;
        super.takeView(view);
        photoUploadSubscriber = new PhotoUploadSubscriber();
        photoUploadingManager.getTaskChangingObservable(UploadPurpose.BUCKET_IMAGE).subscribe(photoUploadSubscriber);
        photoUploadSubscriber
                .onError(view::itemChanged)
                .onSuccess(this::addPhotoToBucketItem)
                .onProgress(view::addImage)
                .onCancel(view::deleteImage);
        //
        mediaSubscription = mediaPickerManager.toObservable()
                .filter(attachment -> attachment.requestId == BUCKET_MEDIA_REQUEST_ID)
                .subscribe(mediaAttachment -> attachImages(mediaAttachment.chosenImages, mediaAttachment.type));
    }

    @Override
    public void dropView() {
        super.dropView();
        if (!photoUploadSubscriber.isUnsubscribed()) photoUploadSubscriber.unsubscribe();
        //
        if (!mediaSubscription.isUnsubscribed()) mediaSubscription.unsubscribe();
    }

    @Override
    protected void syncUI() {
        super.syncUI();
        List<UploadTask> tasks = photoUploadingManager.getUploadTasksForLinkedItemId(UploadPurpose.BUCKET_IMAGE, bucketItemId);
        Queryable.from(tasks).forEachR(photoUploadSubscriber::onNext);
        view.addImages(tasks);
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
        saveItem(false);

        Queryable.from(chosenImages).forEachR(choseImage ->
                imageSelected(Uri.parse(choseImage.getThumbnailPath()), type));
    }

    private void imageSelected(Uri uri, int requestType) {
        String type = "";
        switch (requestType) {
            case PickImageDelegate.CAPTURE_PICTURE:
                type = "camera";
                break;
            case PickImageDelegate.PICK_PICTURE:
                type = "album";
                break;
            case PickImageDelegate.FACEBOOK:
                type = "facebook";
                break;
        }

        UploadTask task = new UploadTask();
        task.setStatus(UploadTask.Status.STARTED);
        task.setFilePath(uri.toString());
        task.setType(type);
        task.setLinkedItemId(String.valueOf(bucketItemId));

        doRequest(new CopyFileCommand(context, task.getFilePath()), filePath -> {
            task.setFilePath(filePath);
            startUpload(task);
        });
    }

    public void onUploadTaskClicked(UploadTask uploadTask) {
        if (uploadTask.getStatus().equals(UploadTask.Status.FAILED)) {
            startUpload(uploadTask);
        } else {
            cancelUpload(uploadTask);
        }
    }

    protected void startUpload(UploadTask uploadTask) {
        TrackingHelper.bucketPhotoAction(TrackingHelper.ACTION_BUCKET_PHOTO_UPLOAD_START, uploadTask.getType(), bucketItem.getType());
        eventBus.post(new BucketItemPhotoAnalyticEvent(TrackingHelper.ATTRIBUTE_UPLOAD_PHOTO, bucketItem.getUid()));
        photoUploadingManager.upload(uploadTask, UploadPurpose.BUCKET_IMAGE);
    }

    private void onError(long id, Exception ex) {
        if (view == null) return;
        //
        UploadTask bucketPhotoUploadTask = view.getBucketPhotoUploadTask(id);
        if (bucketPhotoUploadTask != null) view.itemChanged(bucketPhotoUploadTask);
    }

    private void addPhotoToBucketItem(UploadTask task) {
        doRequest(new UploadBucketPhotoCommand(bucketItemId, task),
                photo -> photoAdded(task, photo),
                spiceException -> onError(task.getId(), spiceException));
    }

    private void photoAdded(UploadTask bucketPhotoUploadTask, BucketPhoto bucketPhoto) {
        view.replace(bucketPhotoUploadTask, bucketPhoto);
        getBucketItemManager().updateBucketItemWithPhoto(bucketItem, bucketPhoto);
    }

    protected void cancelUpload(UploadTask uploadTask) {
        photoUploadingManager.cancelUpload(uploadTask);
    }
}
