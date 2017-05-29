package com.worldventures.dreamtrips.modules.common.view.horizontal_photo_view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.techery.spares.module.Injector;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.common.model.EntityStateHolder;
import com.worldventures.dreamtrips.modules.common.view.horizontal_photo_view.cell.AddPhotoCell;
import com.worldventures.dreamtrips.modules.common.view.horizontal_photo_view.cell.StatefulPhotoCell;
import com.worldventures.dreamtrips.modules.tripsimages.model.AddPhotoModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;

import java.util.List;

public class StatefulHorizontalPhotosView<T extends IFullScreenObject, D extends CellDelegate<EntityStateHolder<T>>>
      extends RecyclerView {

   private IgnoreFirstItemAdapter imagesAdapter;
   private AddPhotoModel createBtnObject = new AddPhotoModel();

   private CellDelegate<AddPhotoModel> addPhotoCellDelegate;
   private D photoCellDelegate;

   private boolean showAddPhotoCell;

   public StatefulHorizontalPhotosView(Context context) {
      super(context);
   }

   public StatefulHorizontalPhotosView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
   }

   public void init(Injector injector) {
      if (isInEditMode()) return;
      injector.inject(this);
      if (imagesAdapter != null) return;

      imagesAdapter = new IgnoreFirstItemAdapter(getContext(), injector);

      imagesAdapter.registerCell(EntityStateHolder.class, getPhotocellClass());
      imagesAdapter.registerDelegate(EntityStateHolder.class, photoCellDelegate);

      if (showAddPhotoCell) {
         imagesAdapter.registerCell(AddPhotoModel.class, AddPhotoCell.class);
         imagesAdapter.registerDelegate(AddPhotoModel.class, addPhotoCellDelegate);
         createBtnObject.setVisibility(true);
         imagesAdapter.addItem(createBtnObject);
      }

      setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
      setAdapter(imagesAdapter);
   }

   protected Class<?> getPhotocellClass() {
      return StatefulPhotoCell.class;
   }

   public void addItemInProgressState(EntityStateHolder<T> photoStateHolder) {
      imagesAdapter.addItem(photoStateHolder);
      imagesAdapter.notifyDataSetChanged();
   }

   public void changeItemState(EntityStateHolder<T> photoStateHolder) {
      EntityStateHolder<T> item = findItem(photoStateHolder);
      if (item != null) {
         item.setState(photoStateHolder.state());
         item.setEntity(photoStateHolder.entity());
      } else {
         imagesAdapter.addItem(photoStateHolder);
      }
      imagesAdapter.notifyDataSetChanged();
   }

   public void removeItem(EntityStateHolder<T> photoStateHolder) {
      EntityStateHolder<T> item = findItem(photoStateHolder);
      if (item != null) imagesAdapter.remove(item);
   }

   public EntityStateHolder findItem(EntityStateHolder<T> photoStateHolder) {
      for (int i = 0; i < imagesAdapter.getCount(); i++) {
         Object item = imagesAdapter.getItem(i);
         if (item instanceof EntityStateHolder) {
            EntityStateHolder<BucketPhoto> itemPhotoStateHolder = (EntityStateHolder<BucketPhoto>) item;
            if (photoStateHolder.entity().equals(itemPhotoStateHolder.entity())) {
               return itemPhotoStateHolder;
            }
         }
      }
      return null;
   }

   public void setImages(List<EntityStateHolder<T>> images) {
      imagesAdapter.clear();
      imagesAdapter.addItems(images);
      imagesAdapter.notifyDataSetChanged();
   }

   public int getItemCount() {
      return imagesAdapter.getItemCount();
   }

   public void setPhotoCellDelegate(D delegate) {
      this.photoCellDelegate = delegate;
   }

   public void enableAddPhotoCell(CellDelegate<AddPhotoModel> addPhotoCellDelegate) {
      this.addPhotoCellDelegate = addPhotoCellDelegate;
      showAddPhotoCell = true;
   }
}
