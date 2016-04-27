package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhotoCreationItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketAddPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoUploadCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate.BucketAddPhotoCellDelegate;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate.BucketPhotoCellDelegate;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate.BucketPhotoUploadCellDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.model.AddBucketPhotoModel;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import icepick.Icepick;


public class BucketPhotosView extends RecyclerView implements IBucketPhotoView {

    public static final int COVER_POS = 2;
    public static final int ADD_BTN_POS = 1;
    private IgnoreFirstItemAdapter imagesAdapter;
    private AddBucketPhotoModel createBtnObject = new AddBucketPhotoModel();

    @Inject
    @Global
    EventBus eventBus;

    BucketAddPhotoCellDelegate bucketAddPhotoCellDelegate;
    BucketPhotoCellDelegate bucketPhotoCellDelegate;
    BucketPhotoUploadCellDelegate bucketPhotoUploadCellDelegate;

    public BucketPhotosView(Context context) {
        super(context);
    }

    public BucketPhotosView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BucketPhotosView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void init(Injector injector) {
        injector.inject(this);
        if (imagesAdapter == null) {
            imagesAdapter = new IgnoreFirstItemAdapter(getContext(), injector);

            imagesAdapter.registerCell(BucketPhoto.class, BucketPhotoCell.class);
            imagesAdapter.registerCell(BucketPhotoCreationItem.class, BucketPhotoUploadCell.class);
            imagesAdapter.registerCell(AddBucketPhotoModel.class, BucketAddPhotoCell.class);

            imagesAdapter.registerDelegate(BucketPhoto.class, bucketPhotoCellDelegate);
            imagesAdapter.registerDelegate(BucketPhotoCreationItem.class, bucketPhotoUploadCellDelegate);
            imagesAdapter.registerDelegate(AddBucketPhotoModel.class, bucketAddPhotoCellDelegate);

            createBtnObject.setVisibility(true);
            imagesAdapter.addItem(createBtnObject);

            setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
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
                boolean equals = photo.getFSId().equals(((BucketPhoto) item).getFSId());
                if (equals) {
                    imagesAdapter.remove(item);
                    break;
                }
            }
        }
    }

    @Override
    public void deleteImage(BucketPhotoCreationItem photo) {
        for (int i = 0; i < imagesAdapter.getCount(); i++) {
            Object item = imagesAdapter.getItem(i);
            if (item instanceof BucketPhotoCreationItem && Objects.equals(photo.getFilePath(), ((BucketPhotoCreationItem) item).getFilePath())) {
                imagesAdapter.remove(item);
                break;
            }
        }
    }

    @Override
    public void replace(BucketPhotoCreationItem photoUploadTask, BucketPhoto bucketPhoto) {
        for (int i = 0; i < imagesAdapter.getCount(); i++) {
            if (photoUploadTask.equals(imagesAdapter.getItem(i))) {
                imagesAdapter.replaceItem(i, bucketPhoto);
                imagesAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public void setImages(List images) {
        imagesAdapter.clear();
        imagesAdapter.addItems(images);
        imagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void addImageToStart(BucketPhotoCreationItem image) {
        if (imagesAdapter.getItems().contains(image)) {
            imagesAdapter.notifyDataSetChanged();
            return;
        }
        //
        imagesAdapter.addItem(imagesAdapter.getCount() >= COVER_POS ? COVER_POS : ADD_BTN_POS, image);
        imagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void itemChanged(Object item) {
        imagesAdapter.notifyItemChanged(imagesAdapter.getItems().indexOf(item));
    }

    @Override
    public BucketPhotoCreationItem getBucketPhotoUploadTask(String filePath) {
        return (BucketPhotoCreationItem) Queryable.from(imagesAdapter.getItems()).firstOrDefault(element ->
                element instanceof BucketPhotoCreationItem &&
                        Objects.equals(((BucketPhotoCreationItem) element).getFilePath(), filePath));
    }

    public void setBucketPhotoCellDelegate(BucketPhotoCellDelegate bucketPhotoCellDelegate) {
        this.bucketPhotoCellDelegate = bucketPhotoCellDelegate;
    }

    public void setBucketPhotoUploadCellDelegate(BucketPhotoUploadCellDelegate bucketPhotoUploadCellDelegate) {
        this.bucketPhotoUploadCellDelegate = bucketPhotoUploadCellDelegate;
    }

    public void setBucketAddPhotoCellDelegate(BucketAddPhotoCellDelegate bucketAddPhotoCellDelegate) {
        this.bucketAddPhotoCellDelegate = bucketAddPhotoCellDelegate;
    }

}
