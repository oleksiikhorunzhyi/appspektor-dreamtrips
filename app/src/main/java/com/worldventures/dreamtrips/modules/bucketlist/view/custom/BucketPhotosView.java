package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketAddPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoUploadCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate.BucketAddPhotoCellDelegate;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate.BucketPhotoUploadCellDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.model.AddBucketPhotoModel;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import icepick.Icepick;


public class BucketPhotosView extends RecyclerView implements IBucketPhotoView {

   public static final int COVER_POS = 2;
   public static final int ADD_BTN_POS = 1;
   private IgnoreFirstItemAdapter imagesAdapter;
   private AddBucketPhotoModel createBtnObject = new AddBucketPhotoModel();

   @Inject @Global EventBus eventBus;

   BucketAddPhotoCellDelegate bucketAddPhotoCellDelegate;
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

         imagesAdapter.registerCell(EntityStateHolder.class, BucketPhotoUploadCell.class);
         imagesAdapter.registerCell(AddBucketPhotoModel.class, BucketAddPhotoCell.class);

         imagesAdapter.registerDelegate(EntityStateHolder.class, bucketPhotoUploadCellDelegate);
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
   public void addItemInProgressState(EntityStateHolder<BucketPhoto> photoStateHolder) {
      imagesAdapter.addItem(photoStateHolder);
      imagesAdapter.notifyDataSetChanged();
   }

   @Override
   public void changeItemState(EntityStateHolder<BucketPhoto> photoEntityStateHolder) {
      removeItem(photoEntityStateHolder);
      imagesAdapter.addItem(photoEntityStateHolder);
      imagesAdapter.notifyDataSetChanged();
   }

   @Override
   public void removeItem(EntityStateHolder<BucketPhoto> photoStateHolder) {
      for (int i = 0; i < imagesAdapter.getCount(); i++) {
         Object item = imagesAdapter.getItem(i);
         if (item instanceof EntityStateHolder) {
            EntityStateHolder<BucketPhoto> itemPhotoStateHolder = (EntityStateHolder<BucketPhoto>) item;
            boolean equals = photoStateHolder.equals(itemPhotoStateHolder);
            if (equals) {
               imagesAdapter.remove(item);
               break;
            }
         }
      }
   }

   @Override
   public void setImages(List<EntityStateHolder<BucketPhoto>> images) {
      imagesAdapter.clear();
      imagesAdapter.addItems(images);
      imagesAdapter.notifyDataSetChanged();
   }

   public void setBucketPhotoUploadCellDelegate(BucketPhotoUploadCellDelegate bucketPhotoUploadCellDelegate) {
      this.bucketPhotoUploadCellDelegate = bucketPhotoUploadCellDelegate;
   }

   public void setBucketAddPhotoCellDelegate(BucketAddPhotoCellDelegate bucketAddPhotoCellDelegate) {
      this.bucketAddPhotoCellDelegate = bucketAddPhotoCellDelegate;
   }
}