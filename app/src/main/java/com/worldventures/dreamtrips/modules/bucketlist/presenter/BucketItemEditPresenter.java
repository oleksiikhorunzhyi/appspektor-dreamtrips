package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketAddPhotoClickEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.event.AttachPhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.Calendar;
import java.util.Collections;
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
        bucketPostItem.setTags(getListFromString(view.getTags()));
        bucketPostItem.setPeople(getListFromString(view.getPeople()));
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

    public Date getDate() {
        if (bucketItem.getTarget_date() != null) {
            return bucketItem.getTarget_date();
        } else {
            return Calendar.getInstance().getTime();
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

    public List<String> getListFromString(String temp) {
        if (TextUtils.isEmpty(temp)) {
            return Collections.emptyList();
        } else {
            return Queryable.from(temp.split(",")).map(String::trim).toList();
        }
    }

    ////////////////////////////////////////
    /////// Photo picking
    ////////////////////////////////////////

    public void onEvent(BucketAddPhotoClickEvent event) {
        if (view.isVisibleOnScreen()) {
            eventBus.cancelEventDelivery(event);
            view.showPhotoPicker();
        }
    }

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
}
