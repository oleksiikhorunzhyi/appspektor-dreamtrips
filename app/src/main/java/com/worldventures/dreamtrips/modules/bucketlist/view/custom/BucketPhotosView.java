package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.UploadProgressUpdateEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadCancelRequestEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadFailedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadFinishEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketPhotoUploadStarted;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoUploadTask;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketAddPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCellForDetails;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoUploadCell;
import com.worldventures.dreamtrips.modules.membership.model.TemplatePhoto;
import com.worldventures.dreamtrips.modules.membership.view.cell.TemplatePhotoCell;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.MultiSelectPickCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDialog;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import de.greenrobot.event.EventBus;
import icepick.Icepick;
import icepick.Icicle;


public class BucketPhotosView extends RecyclerView implements IBucketPhotoView {

    @Icicle
    int pidTypeShown;
    @Icicle
    String filePath;
    @Inject
    @Global
    EventBus eventBus;

    @Inject
    SnappyRepository db;



    private IgnoreFirstItemAdapter imagesAdapter;
    private PickImageDialog pid;
    private ImagePickCallback makePhotoImageCallback;
    private ImagePickCallback chooseImageCallback;
    private ImagePickCallback fbImageCallback;
    private MultiSelectPickCallback multiSelectPickCallback;

    private DeleteButtonCallback deleteButtonCallback;
    private Fragment fragment;
    private boolean multiselectAvalbile;

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
        if (!eventBus.isRegistered(this)) eventBus.register(this);
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
        if (eventBus.isRegistered(this)) eventBus.unregister(this);
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
                break;
            }
        }
    }

    @Override
    public void replace(BucketPhotoUploadTask photoUploadTask, BucketPhoto bucketPhoto) {
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
    public void addImage(BucketPhotoUploadTask image) {
        imagesAdapter.addItem(1, image);
        imagesAdapter.notifyItemInserted(1);
    }

    @Override
    public void addImages(List<BucketPhotoUploadTask> tasks) {
        imagesAdapter.addItems(1, tasks);
        imagesAdapter.notifyDataSetChanged();
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

    private void actionFacebook() {
        pid = new PickImageDialog(getContext(), fragment);
        pid.setTitle("");
        pid.setCallback(fbImageCallback);
        pid.setRequestTypes(PickImageDialog.REQUEST_FACEBOOK);
        pid.show();
        filePath = pid.getFilePath();
        pidTypeShown = PickImageDialog.REQUEST_FACEBOOK;

    }

    private void actionGallery() {
        pid = new PickImageDialog(getContext(), fragment);
        pid.setTitle("");
        pid.setCallback(chooseImageCallback);
        pid.setRequestTypes(PickImageDialog.REQUEST_PICK_PICTURE);
        pid.show();
        filePath = pid.getFilePath();
        pidTypeShown = PickImageDialog.REQUEST_PICK_PICTURE;
    }

    private void actionCapture() {
        pid = new PickImageDialog(getContext(), fragment);
        pid.setTitle("");
        pid.setCallback(makePhotoImageCallback);
        pid.setRequestTypes(PickImageDialog.REQUEST_CAPTURE_PICTURE);
        pid.show();
        filePath = pid.getFilePath();
        pidTypeShown = PickImageDialog.REQUEST_CAPTURE_PICTURE;
    }


    private void actionMultiSelect() {
        pid = new PickImageDialog(getContext(), fragment);
        pid.setCallback(multiSelectPickCallback);
        pid.setRequestTypes(PickImageDialog.REQUEST_MULTI_SELECT);
        pid.show();
        filePath = pid.getFilePath();
        pidTypeShown = PickImageDialog.REQUEST_MULTI_SELECT;
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
                            actionFacebook();
                            break;
                        case 1:
                            actionCapture();
                            break;
                        case 2:
                            if (multiselectAvalbile) actionMultiSelect();
                            else actionGallery();
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
        return Queryable.from(imagesAdapter.getItems()).filter((Predicate) element -> element instanceof IFullScreenObject).toList();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        handlePickDialogActivityResult(requestCode, resultCode, data);
    }


    private void handlePickDialogActivityResult(int requestCode, int resultCode, Intent data) {
        if (pidTypeShown != 0) {
            if (pid == null) {
                pid = new PickImageDialog(getContext(), fragment);
                switch (pidTypeShown) {
                    case PickImageDialog.REQUEST_CAPTURE_PICTURE:
                        pid.setCallback(makePhotoImageCallback);
                        break;
                    case PickImageDialog.REQUEST_PICK_PICTURE:
                        pid.setCallback(chooseImageCallback);
                        break;
                    case PickImageDialog.REQUEST_MULTI_SELECT:
                        pid.setCallback(multiSelectPickCallback);
                        break;
                    case PickImageDialog.REQUEST_FACEBOOK:
                        pid.setCallback(fbImageCallback);
                        break;
                }
                pid.setChooserType(pidTypeShown);
                pid.setFilePath(filePath);
            }
            pidTypeShown = 0;
            pid.onActivityResult(requestCode, resultCode, data);
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


    public void setMakePhotoImageCallback(ImagePickCallback makePhotoImageCallback) {
        this.makePhotoImageCallback = makePhotoImageCallback;
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

    public void setMultiSelectPickCallback(MultiSelectPickCallback multiSelectPickCallback) {
        this.multiSelectPickCallback = multiSelectPickCallback;
    }

    public void multiSelectAvailable(boolean available) {
        this.multiselectAvalbile = available;
    }

    public enum Type {
        DETAILS, EDIT, DEFAULT
    }

    public interface DeleteButtonCallback {
        void onDelete();
    }

    public void onEventMainThread(BucketPhotoUploadStarted event) {
        imagesAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(BucketPhotoUploadFailedEvent event) {
        BucketPhotoUploadTask task = getBucketPhotoUploadTask(event.getTaskId());
        if (task != null) task.setFailed(true);
        imagesAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(BucketPhotoUploadFinishEvent event) {
        replace(event.getTask(), event.getBucketPhoto());
        imagesAdapter.notifyDataSetChanged();
    }

    private BucketPhotoUploadTask getBucketPhotoUploadTask(long taskId) {
        return (BucketPhotoUploadTask) Queryable.from(imagesAdapter.getItems()).firstOrDefault(element -> {
            boolean b = element instanceof BucketPhotoUploadTask;
            return b && ((BucketPhotoUploadTask) element).getTaskId() == taskId;
        });
    }

    public void onEvent(BucketPhotoUploadCancelRequestEvent event) {
        BucketPhotoUploadTask photoUploadTask = getBucketPhotoUploadTask(event.
                getModelObject().getTaskId());
        if (photoUploadTask != null) {
            db.removeBucketPhotoTask(photoUploadTask);
            deleteImage(photoUploadTask);
        }

    }


}
