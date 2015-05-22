package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketAddPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCellForDetails;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoUploadCell;
import com.worldventures.dreamtrips.modules.facebook.view.activity.FacebookPickPhotoActivity;
import com.worldventures.dreamtrips.modules.membership.model.TemplatePhoto;
import com.worldventures.dreamtrips.modules.membership.view.cell.TemplatePhotoCell;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDialog;

import java.util.List;

import javax.inject.Provider;

import icepick.Icepick;
import icepick.Icicle;

public class BucketPhotosView extends RecyclerView implements IBucketPhotoView {

    private IgnoreFirstItemAdapter imagesAdapter;

    private PickImageDialog pid;
    @Icicle
    int pidTypeShown;
    @Icicle
    String filePath;
    private ImagePickCallback selectImageCallback;
    private ImagePickCallback chooseImageCallback;
    private ImagePickCallback fbImageCallback;
    private DeleteButtonCallback deleteButtonCallback;
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


    public void init(Fragment fragment, Provider<Injector> injector, Type type) {

        if (imagesAdapter == null) {
            this.fragment = fragment;

            imagesAdapter = new IgnoreFirstItemAdapter(getContext(), injector);

            if (type == Type.DEFAULT) {
                imagesAdapter.registerCell(TemplatePhoto.class, TemplatePhotoCell.class);
            } else if (type == Type.EDIT) {
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
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    protected void onDetachedFromWindow() {
        this.setAdapter(null);
        super.onDetachedFromWindow();
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

    @Override
    public void addTemplatePhoto(TemplatePhoto templatePhoto) {
        imagesAdapter.addItem(templatePhoto);
        imagesAdapter.notifyItemInserted(0);
    }

    @Override
    public void deleteAtPosition(int position) {
        imagesAdapter.remove(0);
        imagesAdapter.notifyItemRemoved(0);
    }

    public void actionFacebook() {
        Intent intent = new Intent(getContext(), FacebookPickPhotoActivity.class);
        fragment.startActivityForResult(intent, FacebookPickPhotoActivity.REQUEST_CODE_PICK_FB_PHOTO);
    }

    public void actionGallery() {
        pid = new PickImageDialog(getContext(), fragment);
        pid.setTitle("");
        pid.setCallback(chooseImageCallback);
        pid.setRequestTypes(ChooserType.REQUEST_PICK_PICTURE);
        pid.show();
        pidTypeShown = ChooserType.REQUEST_PICK_PICTURE;
    }

    public void actionPhoto() {
        pid = new PickImageDialog(getContext(), fragment);
        pid.setTitle("");
        pid.setCallback(selectImageCallback);
        pid.setRequestTypes(ChooserType.REQUEST_CAPTURE_PICTURE);
        pid.show();
        filePath = pid.getFilePath();
        pidTypeShown = ChooserType.REQUEST_CAPTURE_PICTURE;
    }

    @Override
    public void showAddPhotoDialog(boolean showDeleteButton) {
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
                            break;
                    }
                });
        if (showDeleteButton) {
            builder.positiveText(R.string.delete_photo_positiove);
            builder.callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    showDeleteDialog(getContext());
                }
            });
        }
        builder.show();
    }

    @Override
    public List getImages() {
        return Queryable.from(imagesAdapter.getItems()).filter(new Predicate() {
            @Override
            public boolean apply(Object element) {
                return element instanceof IFullScreenAvailableObject;
            }
        }).toList();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (pidTypeShown != 0) {
            if (pid == null) {
                pid = new PickImageDialog(getContext(), fragment);
                pid.setCallback(selectImageCallback);
                pid.setChooserType(pidTypeShown);
                pid.setFilePath(filePath);
            }
            pidTypeShown = 0;
            pid.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == Activity.RESULT_OK
                && requestCode == FacebookPickPhotoActivity.REQUEST_CODE_PICK_FB_PHOTO
                && fbImageCallback != null) {
            ChosenImage image = new Gson().fromJson(data.getStringExtra(FacebookPickPhotoActivity.RESULT_PHOTO), ChosenImage.class);
            fbImageCallback.onResult(fragment, image, null);
        }
    }

    @Override
    public void addFirstItem() {
        imagesAdapter.addItem(new Object());
        imagesAdapter.notifyDataSetChanged();
    }

    private void showDeleteDialog(Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.delete_photo_title)
                .content(R.string.delete_photo_text)
                .positiveText(R.string.delete_photo_positiove)
                .negativeText(R.string.delete_photo_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (deleteButtonCallback != null) {
                            deleteButtonCallback.onDelete();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }


    public void setSelectImageCallback(ImagePickCallback selectImageCallback) {
        this.selectImageCallback = selectImageCallback;
    }

    public void setChooseImageCallback(ImagePickCallback chooseImageCallback) {
        this.chooseImageCallback = chooseImageCallback;
    }

    public void setFbImageCallback(ImagePickCallback fbImageCallback) {
        this.fbImageCallback = fbImageCallback;
    }

    public void setDeleteButtonCallback(DeleteButtonCallback deleteButtonCallback) {
        this.deleteButtonCallback = deleteButtonCallback;
    }

    public enum Type {
        DETAILS, EDIT, DEFAULT
    }

    public interface DeleteButtonCallback {
        void onDelete();
    }
}
