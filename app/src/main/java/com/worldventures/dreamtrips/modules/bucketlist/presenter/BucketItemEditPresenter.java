package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.net.Uri;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.UploadPurpose;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.TextUtils;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.UploadBucketPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemPhotoAnalyticEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.event.AttachPhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BucketItemEditPresenter extends BucketDetailsBasePresenter<BucketItemEditPresenterView> {

    private Date selectedDate;

    private boolean savingItem = false;

    public BucketItemEditPresenter(BucketBundle bundle) {
        super(bundle);
    }

    @Override
    public void takeView(BucketItemEditPresenterView view) {
        priorityEventBus = 1;
        super.takeView(view);
        photoUploadingManager.getTaskChangingObservable(UploadPurpose.BUCKET_IMAGE).subscribe(uploadTask -> stateChanged(uploadTask));

    }


    @Override
    protected void syncUI() {
        super.syncUI();
        List<UploadTask> tasks = photoUploadingManager.getUploadTasksForLinkedItemId(UploadPurpose.BUCKET_IMAGE, bucketItemId);
        Queryable.from(tasks).forEachR(task -> stateChanged(task));
        if (!tasks.isEmpty()) view.addImages(tasks);
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedDate = bucketItem.getTarget_date();
        List<CategoryItem> list = db.readList(SnappyRepository.CATEGORIES, CategoryItem.class);
        if (!list.isEmpty()) {
            view.setCategoryItems(list);
            view.setCategory(list.indexOf(bucketItem.getCategory()));
        }

        ImagePickedEvent event = eventBus.getStickyEvent(ImagePickedEvent.class);
        if (event != null) onEvent(event);
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
        if (bucketItem.getTarget_date() != null) {
            return bucketItem.getTarget_date();
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

    public void onEvent(AttachPhotoEvent event) {
        if (view.isVisibleOnScreen() && event.getRequestType() != -1)
            pickImage(event.getRequestType());
    }

    public void onEvent(ImagePickedEvent event) {
        imagePicked(event);
    }

    public void pickImage(int requestType) {
        eventBus.post(new ImagePickRequestEvent(requestType, bucketItemId.hashCode()));
    }

    public void imagePicked(ImagePickedEvent event) {
        if (event.getRequesterID() == bucketItemId.hashCode()) {
            eventBus.removeStickyEvent(event);

            attachImages(Queryable.from(event.getImages()).toList(), event.getRequestType());
        }
    }

    public void attachImages(List<ChosenImage> chosenImages, int type) {
        if (chosenImages.size() == 0) {
            return;
        }
        view.hidePhotoPicker();
        saveItem(false);

        Queryable.from(chosenImages).forEachR(choseImage ->
                imageSelected(Uri.parse(choseImage.getFileThumbnail()), type));
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

    protected void stateChanged(UploadTask uploadTask) {
        switch (uploadTask.getStatus()) {
            case COMPLETED:
                addPhotoToBucketItem(uploadTask);
                break;
            case CANCELED:
                view.deleteImage(uploadTask);
                break;
            case STARTED:
                view.addImage(uploadTask);
                break;
            case FAILED:
                view.itemChanged(uploadTask);
                break;
        }
    }
}
