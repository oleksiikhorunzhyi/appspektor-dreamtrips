package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.apptentive.android.sdk.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.bucketlist.api.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.UploadBucketPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketAddPhotoClickEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoAsCoverRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoDeleteRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoReuploadRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadCancelEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadCancelRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.facebook.view.activity.FacebookPickPhotoActivity;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;

import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class BucketItemEditPresenter extends BucketDetailsBasePresenter<BucketItemEditPresenterView> {
    public static final int MAX_CHAR_COUNT = 120;
    private Date selectedDate;

    @Inject
    protected Injector injector;

    private Integer coverId;
    private UploadBucketPhotoCommand uploadBucketPhotoCommand;

    public BucketItemEditPresenter(BucketItemEditPresenterView view, Bundle bundle) {
        super(view, bundle);
        selectedDate = bucketItem.getTarget_date();
    }

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

    private void handlePhotoPick(Uri uri) {
        BucketPhotoUploadTask task = new BucketPhotoUploadTask();
        task.setTaskId((int) System.currentTimeMillis());
        task.setBucketId(bucketItem.getId());
        task.setFilePath(uri.toString());
        view.addImage(task);
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
                            view.replace(task, bucketPhoto);
                        }
                    }
                });
    }

    public void onEvent(BucketPhotoReuploadRequestEvent event) {
        startUpload(event.getTask());
    }

    public void onEvent(BucketPhotoUploadCancelEvent event) {
        view.deleteImage(event.getTask());
    }

    public void onEvent(BucketPhotoUploadCancelRequestEvent event) {
        uploadBucketPhotoCommand.cancel();
    }

    public void onEvent(BucketAddPhotoClickEvent event) {
        view.showAddPhotoDialog();
    }

    public void onEvent(BucketPhotoAsCoverRequestEvent event) {
        coverId = event.getPhoto().getId();
    }

    public void onEvent(BucketPhotoDeleteRequestEvent event) {
        dreamSpiceManager.execute(new DeletePhotoCommand(String.valueOf(event.getPhoto().getId())), new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e(this.getClass().getSimpleName(), "", spiceException);
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                view.deleteImage(event.getPhoto());
            }
        });

    }

    @Override
    public void resume() {
        super.resume();
        List<CategoryItem> list = db.readList(SnappyRepository.CATEGORIES, CategoryItem.class);
        if (!list.isEmpty()) {
            view.setCategoryItems(list);
            view.setCategory(list.indexOf(bucketItem.getCategory()));
        }

        if (!bucketItem.getPhotos().isEmpty()) {
            view.addImages(bucketItem.getPhotos());
        }
    }

    public void saveItem() {
        if (checkEdit()) {
            BucketPostItem bucketPostItem = new BucketPostItem();
            bucketPostItem.setName(view.getTitle());
            bucketPostItem.setDescription(view.getDescription());
            bucketPostItem.setStatus(view.getStatus());
            bucketPostItem.setTags(getListFromString(view.getTags()));
            bucketPostItem.setPeople(getListFromString(view.getPeople()));
            bucketPostItem.setCategory(view.getSelectedItem());
            bucketPostItem.setDate(selectedDate);
            UpdateBucketItemCommand updateBucketItemCommand =
                    new UpdateBucketItemCommand(bucketItem.getId(), bucketPostItem);
            dreamSpiceManager.execute(updateBucketItemCommand, requestListenerUpdate);
        } else {
            view.showError();
        }
    }

    private boolean checkEdit() {
        if (view.getDescription().length() > MAX_CHAR_COUNT) {
            return false;
        } else {
            return true;
        }
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

    public void onFacebookAction(Fragment from) {
        activityRouter.openFacebookPhoto(from);
    }

    public ImagePickCallback providePhotoChooseCallback() {
        return selectImageCallback;
    }

    public void onActivityResult(Fragment fragment, int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == FacebookPickPhotoActivity.REQUEST_CODE_PICK_FB_PHOTO) {
            ChosenImage image = new Gson().fromJson(data.getStringExtra(FacebookPickPhotoActivity.RESULT_PHOTO), ChosenImage.class);
            fbCallback.onResult(fragment, image, null);
        }
    }

}
