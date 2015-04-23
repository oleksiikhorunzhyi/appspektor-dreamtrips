package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.MaterialDialog;
import com.apptentive.android.sdk.Log;
import com.google.gson.Gson;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.BucketImageAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketAddPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCellForDetails;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoUploadCell;
import com.worldventures.dreamtrips.modules.facebook.view.activity.FacebookPickPhotoActivity;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDialog;

import java.util.List;

public class BucketPhotosView extends RecyclerView implements IBucketPhotoView {

    private BucketImageAdapter imagesAdapter;

    private PickImageDialog pid;
    private ImagePickCallback selectImageCallback;
    private ImagePickCallback fbImageCallback;
    private Fragment fragment;

    public BucketPhotosView(Context context) {
        super(context);
    }

    public BucketPhotosView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BucketPhotosView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void init(Fragment fragment, Injector injector, Type type) {
        if (imagesAdapter == null) {
            this.fragment = fragment;

            imagesAdapter = new BucketImageAdapter(getContext(), injector);
            if (type == Type.EDIT) {
                imagesAdapter.registerCell(BucketPhoto.class, BucketPhotoCell.class);
            } else {
                imagesAdapter.registerCell(BucketPhoto.class, BucketPhotoCellForDetails.class);
            }

            imagesAdapter.registerCell(BucketPhotoUploadTask.class, BucketPhotoUploadCell.class);
            imagesAdapter.registerCell(Object.class, BucketAddPhotoCell.class);
            imagesAdapter.addItem(new Object());
            setLayoutManager(new LinearLayoutManager(
                            getContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false)
            );
            setAdapter(imagesAdapter);
        }
    }


    @Override
    public void deleteImage(BucketPhoto photo) {
        for (int i = 0; i < imagesAdapter.getCount(); i++) {
            Object item = imagesAdapter.getItem(i);
            if (item instanceof BucketPhoto) {
                boolean equals = photo.getFsId().equals(((BucketPhoto) item).getFsId());
                if (equals) {
                    imagesAdapter.remove(item);
                    imagesAdapter.notifyItemRemoved(i);
                    break;
                }
            }
        }
    }

    @Override
    public void deleteImage(BucketPhotoUploadTask photo) {
        for (int i = 0; i < imagesAdapter.getCount(); i++) {
            Object item = imagesAdapter.getItem(i);
            if (item instanceof BucketPhotoUploadTask &&
                    photo.getTaskId() == ((BucketPhotoUploadTask) item).getTaskId()) {
                imagesAdapter.remove(item);
                imagesAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }

    @Override
    public void replace(BucketPhotoUploadTask photoUploadTask, BucketPhoto bucketPhoto) {
        for (int i = 0; i < imagesAdapter.getCount(); i++) {
            if (photoUploadTask == imagesAdapter.getItem(i)) {
                imagesAdapter.replaceItem(i, bucketPhoto);
                imagesAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public void addImages(List<BucketPhoto> images) {
        imagesAdapter.clear();
        imagesAdapter.addItems(images);
        imagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void addImage(BucketPhotoUploadTask image) {
        imagesAdapter.addItem(1, image);
        imagesAdapter.notifyItemInserted(1);
    }

    public void actionFacebook() {
        Intent intent = new Intent(getContext(), FacebookPickPhotoActivity.class);
        fragment.startActivityForResult(intent, FacebookPickPhotoActivity.REQUEST_CODE_PICK_FB_PHOTO);
    }

    public void actionGallery() {
        this.pid = new PickImageDialog(getContext(), fragment);
        this.pid.setTitle("");
        this.pid.setCallback(selectImageCallback);
        this.pid.setRequestTypes(ChooserType.REQUEST_PICK_PICTURE);
        this.pid.show();
    }

    public void actionPhoto() {
        this.pid = new PickImageDialog(getContext(), fragment);
        this.pid.setTitle("");
        this.pid.setCallback(selectImageCallback);
        this.pid.setRequestTypes(ChooserType.REQUEST_CAPTURE_PICTURE);
        this.pid.show();
    }

    @Override
    public void showAddPhotoDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext());
        builder.title(getContext().getString(R.string.select_photo))
                .items(R.array.dialog_add_bucket_photo)
                .itemsCallback((dialog, view, which, text) -> {
                    switch (which) {
                        case 0:
                            actionFacebook();
                            break;
                        case 1:
                            actionPhoto();
                            break;
                        case 2:
                            actionGallery();
                            break;
                        default:
                            Log.v(this.getClass().getSimpleName(), "default");
                            break;
                    }
                }).show();
    }

    @Override
    public List getImages() {
        return imagesAdapter.getItems();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (pid != null) {
            this.pid.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == FacebookPickPhotoActivity.REQUEST_CODE_PICK_FB_PHOTO) {
            if (fbImageCallback != null) {
                ChosenImage image = new Gson().fromJson(data.getStringExtra(FacebookPickPhotoActivity.RESULT_PHOTO), ChosenImage.class);
                fbImageCallback.onResult(fragment, image, null);
            }
        }
    }

    public void setSelectImageCallback(ImagePickCallback selectImageCallback) {
        this.selectImageCallback = selectImageCallback;
    }

    public void setFbImageCallback(ImagePickCallback fbImageCallback) {
        this.fbImageCallback = fbImageCallback;
    }


    public enum Type {
        DETAILS, EDIT
    }
}
