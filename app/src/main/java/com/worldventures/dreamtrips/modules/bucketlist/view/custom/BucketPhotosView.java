package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketAddPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCellForDetails;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoUploadCell;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import icepick.Icepick;
import icepick.Icicle;


public class BucketPhotosView extends RecyclerView implements IBucketPhotoView {

    @Icicle
    int pidTypeShown;
    @Icicle
    String filePath;

    @Inject
    SnappyRepository db;

    private IgnoreFirstItemAdapter imagesAdapter;

    private DeleteButtonCallback deleteButtonCallback;
    private PickImageDelegate.ImagePickCallback captureImageCallback;
    private PickImageDelegate.ImagePickCallback chooseImageCallback;
    private PickImageDelegate.ImagePickCallback mulitImageCallback;
    private PickImageDelegate.ImagePickCallback fbCallback;

    private boolean multiselectAvalbile;

    private PickImageDelegate pickImageDelegate;

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
        injector.get().inject(this);

        pickImageDelegate = new PickImageDelegate(fragment);
        pickImageDelegate.setRequestType(pidTypeShown);
        pickImageDelegate.setFilePath(filePath);
        setCallback();

        if (imagesAdapter == null) {
            imagesAdapter = new IgnoreFirstItemAdapter(getContext(), injector);

            if (type == Type.EDIT) {
                imagesAdapter.registerCell(BucketPhoto.class, BucketPhotoCell.class);
            } else {
                imagesAdapter.registerCell(BucketPhoto.class, BucketPhotoCellForDetails.class);
            }

            imagesAdapter.registerCell(UploadTask.class, BucketPhotoUploadCell.class);
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
                    break;
                }
            }
        }
    }

    @Override
    public void deleteImage(UploadTask photo) {
        for (int i = 0; i < imagesAdapter.getCount(); i++) {
            Object item = imagesAdapter.getItem(i);
            if (item instanceof UploadTask &&
                    photo.getAmazonTaskId().equals(((UploadTask) item).getAmazonTaskId())) {
                imagesAdapter.remove(item);
                break;
            }
        }
    }

    @Override
    public void replace(UploadTask photoUploadTask, BucketPhoto bucketPhoto) {
        for (int i = 0; i < imagesAdapter.getCount(); i++) {
            if (photoUploadTask.equals(imagesAdapter.getItem(i))) {
                imagesAdapter.replaceItem(i, bucketPhoto);
                imagesAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void setImages(List<BucketPhoto> images) {
        imagesAdapter.clear();
        imagesAdapter.addItems(images);
        imagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void addImage(UploadTask image) {
        imagesAdapter.addItem(1, image);
        imagesAdapter.notifyItemInserted(1);
    }

    @Override
    public void addImages(List<UploadTask> tasks) {
        imagesAdapter.addItems(1, tasks);
        imagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void deleteAtPosition(int position) {
        imagesAdapter.remove(0);
        imagesAdapter.notifyItemRemoved(0);
    }

    @Override
    public void showAddPhotoDialog(boolean showDeleteButton) {
        int items = multiselectAvalbile ?
                R.array.dialog_add_bucket_photo_multiselect :
                R.array.dialog_add_bucket_photo;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext());
        builder.title(getContext().getString(R.string.select_photo))
                .items(items)
                .itemsCallback((dialog, view, which, text) -> {
                    switch (which) {
                        case 0:
                            pidTypeShown = PickImageDelegate.REQUEST_FACEBOOK;
                            break;
                        case 1:
                            pidTypeShown = PickImageDelegate.REQUEST_CAPTURE_PICTURE;
                            break;
                        case 2:
                            if (multiselectAvalbile)
                                pidTypeShown = PickImageDelegate.REQUEST_MULTI_SELECT;
                            else pidTypeShown = PickImageDelegate.REQUEST_PICK_PICTURE;
                            break;
                        default:
                            break;
                    }

                    setCallback();
                    pickImageDelegate.setRequestType(pidTypeShown);
                    pickImageDelegate.show();

                    filePath = pickImageDelegate.getFilePath();
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


    private void setCallback() {
        switch (pidTypeShown) {
            case PickImageDelegate.REQUEST_FACEBOOK:
                pickImageDelegate.setImageCallback(fbCallback);
                break;
            case PickImageDelegate.REQUEST_MULTI_SELECT:
                pickImageDelegate.setImageCallback(mulitImageCallback);
                break;
            case PickImageDelegate.REQUEST_PICK_PICTURE:
                pickImageDelegate.setImageCallback(chooseImageCallback);
                break;
            case PickImageDelegate.REQUEST_CAPTURE_PICTURE:
                pickImageDelegate.setImageCallback(captureImageCallback);
                break;
        }
    }

    @Override
    public List getImages() {
        return Queryable.from(imagesAdapter.getItems()).filter((Predicate) element -> element instanceof IFullScreenObject).toList();
    }

    @Override
    public void itemChanged(Object item) {
        imagesAdapter.notifyItemChanged(imagesAdapter.getItems().indexOf(item) + 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        handlePickDialogActivityResult(requestCode, resultCode, data);
    }


    private void handlePickDialogActivityResult(int requestCode, int resultCode, Intent data) {
        if (pidTypeShown != 0) {
            pickImageDelegate.setFilePath(filePath);
            pickImageDelegate.setRequestType(pidTypeShown);
            setCallback();
            pickImageDelegate.onActivityResult(requestCode, resultCode, data);
            pidTypeShown = 0;
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


    public void setCaptureImageCallback(PickImageDelegate.ImagePickCallback captureImageCallback) {
        this.captureImageCallback = captureImageCallback;
    }

    public void setChooseImageCallback(PickImageDelegate.ImagePickCallback chooseImageCallback) {
        this.chooseImageCallback = chooseImageCallback;
    }

    public void setMulitImageCallback(PickImageDelegate.ImagePickCallback mulitImageCallback) {
        this.mulitImageCallback = mulitImageCallback;
    }

    public void setFbCallback(PickImageDelegate.ImagePickCallback fbCallback) {
        this.fbCallback = fbCallback;
    }

    public void setDeleteButtonCallback(DeleteButtonCallback deleteButtonCallback) {
        this.deleteButtonCallback = deleteButtonCallback;
    }

    public void multiSelectAvailable(boolean available) {
        this.multiselectAvalbile = available;
    }

    @Override
    public UploadTask getBucketPhotoUploadTask(String filePath) {
        return (UploadTask) Queryable.from(imagesAdapter.getItems()).firstOrDefault(element ->
                element instanceof UploadTask &&
                        ((UploadTask) element).getFilePath().equals(filePath));
    }

    public enum Type {
        DETAILS, EDIT, DEFAULT
    }

    public interface DeleteButtonCallback {
        void onDelete();
    }

}
