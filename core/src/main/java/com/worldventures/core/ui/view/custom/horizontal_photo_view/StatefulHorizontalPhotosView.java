package com.worldventures.core.ui.view.custom.horizontal_photo_view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.EntityStateHolder;
import com.worldventures.core.model.ImagePathHolder;
import com.worldventures.core.ui.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.core.ui.view.custom.horizontal_photo_view.cell.AddPhotoCell;
import com.worldventures.core.ui.view.custom.horizontal_photo_view.cell.StatefulPhotoCell;
import com.worldventures.core.ui.view.custom.horizontal_photo_view.model.AddPhotoModel;

import java.util.List;

public class StatefulHorizontalPhotosView<T extends ImagePathHolder, D extends CellDelegate<EntityStateHolder<T>>>
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
            EntityStateHolder<T> itemPhotoStateHolder = (EntityStateHolder<T>) item;
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
