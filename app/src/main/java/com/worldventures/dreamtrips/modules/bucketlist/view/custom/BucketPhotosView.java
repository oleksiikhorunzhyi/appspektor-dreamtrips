package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketAddPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCellForDetails;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCellForeign;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoUploadCell;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.model.AddBucketPhotoModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import java.util.List;

import javax.inject.Provider;

import icepick.Icepick;


public class BucketPhotosView extends RecyclerView implements IBucketPhotoView {

    private IgnoreFirstItemAdapter imagesAdapter;
    private AddBucketPhotoModel createBtnObject = new AddBucketPhotoModel();

    public BucketPhotosView(Context context) {
        super(context);
    }

    public BucketPhotosView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BucketPhotosView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void init(Provider<Injector> injector, Type type) {
        if (imagesAdapter == null) {
            imagesAdapter = new IgnoreFirstItemAdapter(getContext(), injector);

            if (type == Type.EDIT) {
                imagesAdapter.registerCell(BucketPhoto.class, BucketPhotoCell.class);
            } else if (type == Type.FOREIGN) {
                imagesAdapter.registerCell(BucketPhoto.class, BucketPhotoCellForeign.class);
            } else {
                imagesAdapter.registerCell(BucketPhoto.class, BucketPhotoCellForDetails.class);
            }

            imagesAdapter.registerCell(UploadTask.class, BucketPhotoUploadCell.class);
            imagesAdapter.registerCell(AddBucketPhotoModel.class, BucketAddPhotoCell.class);
            createBtnObject.setVisibility(true);
            imagesAdapter.addItem(createBtnObject);

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
                imagesAdapter.notifyItemChanged(i);
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
    public List getImages() {
        return Queryable.from(imagesAdapter.getItems()).filter((Predicate) element -> element instanceof IFullScreenObject).toList();
    }

    @Override
    public void itemChanged(Object item) {
        imagesAdapter.notifyItemChanged(imagesAdapter.getItems().indexOf(item) + 1);
    }

    @Override
    public UploadTask getBucketPhotoUploadTask(String taskId) {
        return (UploadTask) Queryable.from(imagesAdapter.getItems()).firstOrDefault(element ->
                element instanceof UploadTask &&
                        ((UploadTask) element).getAmazonTaskId().equals(taskId));
    }

    public void hideCreateBtn() {
        createBtnObject.setVisibility(false);
    }

    public enum Type {
        DETAILS, EDIT, FOREIGN
    }

}
