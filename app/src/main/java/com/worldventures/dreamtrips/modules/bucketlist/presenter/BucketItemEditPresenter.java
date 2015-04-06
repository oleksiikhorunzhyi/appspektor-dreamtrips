package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.bucketlist.api.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.api.UploadBucketPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketAddPhotoClickEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPostItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.CategoryItem;
import com.worldventures.dreamtrips.modules.facebook.view.activity.FacebookPickPhotoActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;

import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class BucketItemEditPresenter extends BucketDetailsBasePresenter<BucketItemEditPresenter.View> {

    @Inject
    Injector injector;

    public BucketItemEditPresenter(View view, Bundle bundle) {
        super(view, bundle);
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
        BucketPhotoUploadTask photoUploadTask = new BucketPhotoUploadTask();
        photoUploadTask.setTaskId((int) System.currentTimeMillis());
        photoUploadTask.setBucketId(bucketItem.getId());
        photoUploadTask.setFilePath(uri.toString());
        view.addImage(photoUploadTask);
        dreamSpiceManager.execute(new UploadBucketPhotoCommand(photoUploadTask, injector), new RequestListener<BucketPhoto>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(BucketPhoto bucketPhoto) {
                bucketItem.getPhotos().add(bucketPhoto);
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
        BucketPostItem bucketPostItem = new BucketPostItem();
        bucketPostItem.setName(view.getTitle());
        bucketPostItem.setDescription(view.getDescription());
        bucketPostItem.setStatus(view.getStatus());
        bucketPostItem.setTags(getListFromString(view.getTags()));
        bucketPostItem.setPeople(getListFromString(view.getPeople()));
        bucketPostItem.setCategory(view.getSelectedItem());
        Date date = DateTimeUtils.dateFromString(view.getTime(), DateTimeUtils.DATE_FORMAT);
        bucketPostItem.setDate(date);
        UpdateBucketItemCommand updateBucketItemCommand =
                new UpdateBucketItemCommand(bucketItem.getId(), bucketPostItem);
        dreamSpiceManager.execute(updateBucketItemCommand, requestListener);
    }

    public Date getDate() {
        if (bucketItem.getTarget_date() != null) {
            return bucketItem.getTarget_date();
        } else {
            return Calendar.getInstance().getTime();
        }
    }

    public void onDataSet(int year, int month, int day) {
        view.setTime(DateTimeUtils.convertDateToString(year, month, day));
    }

    public List<String> getListFromString(String temp) {
        if (TextUtils.isEmpty(temp)) {
            return Collections.emptyList();
        } else {
            return Queryable.from(temp.split(",")).map(String::trim).toList();
        }
    }


    public void onEvent(BucketAddPhotoClickEvent event) {
        view.showAddPhotoDialog();
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

    public interface View extends BucketDetailsBasePresenter.View {
        void setCategory(int selection);

        void setCategoryItems(List<CategoryItem> items);

        CategoryItem getSelectedItem();

        boolean getStatus();

        String getTags();

        String getPeople();

        String getTime();

        String getTitle();

        String getDescription();

        void addImages(List<BucketPhoto> images);

        void addImage(BucketPhotoUploadTask images);

        void showAddPhotoDialog();
    }


}
