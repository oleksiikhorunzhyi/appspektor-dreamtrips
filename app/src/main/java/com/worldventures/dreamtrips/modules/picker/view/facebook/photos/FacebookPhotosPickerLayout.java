package com.worldventures.dreamtrips.modules.picker.view.facebook.photos;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Toast;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetPhotosCommand;
import com.worldventures.dreamtrips.modules.picker.model.FacebookPhotoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.presenter.facebook.photos.FacebookPhotosPickerPresenter;
import com.worldventures.dreamtrips.modules.picker.util.MediaPickerStep;
import com.worldventures.dreamtrips.modules.picker.util.strategy.PhotoPickLimitStrategy;
import com.worldventures.dreamtrips.modules.picker.view.facebook.FacebookMediaPickerLayout;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public class FacebookPhotosPickerLayout extends FacebookMediaPickerLayout<FacebookPhotosPickerPresenter, FacebookPhotoPickerViewModel> implements FacebookPhotosPickerView {

   public static final String FB_ALBUM_ID = "fb_album_id";

   @Inject FacebookPhotosPickerPresenter presenter;

   private final PhotoPickLimitStrategy photoPickLimitStrategy;

   public FacebookPhotosPickerLayout(PhotoPickLimitStrategy photoPickLimitStrategy, @NonNull Context context) {
      this(photoPickLimitStrategy, context, null);
   }

   public FacebookPhotosPickerLayout(PhotoPickLimitStrategy photoPickLimitStrategy, @NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      this.photoPickLimitStrategy = photoPickLimitStrategy;
   }

   @Override
   public void clearItems() {
      getAdapter().clear();
   }

   @Override
   public FacebookPhotosPickerPresenter getPresenter() {
      return presenter;
   }

   @Override
   public MediaPickerStep getStep() {
      return MediaPickerStep.FB_PHOTOS;
   }

   @Override
   public String getFailedActionText() {
      return getContext().getString(R.string.media_picker_fb_photos_actions);
   }

   @Override
   public Observable<List<FacebookPhotoPickerViewModel>> attachedItems() {
      return getPresenter().attachedItems();
   }

   @Override
   public String getAlbumId() {
      if (getArguments() != null && getArguments().containsKey(FB_ALBUM_ID)) {
         return getArguments().getString(FB_ALBUM_ID);
      } else {
         return null;
      }
   }

   @Override
   public int getPickLimit() {
      return photoPickLimitStrategy.photoPickLimit();
   }

   @Override
   public List<FacebookPhotoPickerViewModel> getChosenPhotos() {
      return getAdapter().getChosenMedia();
   }

   @Override
   public OperationView<GetPhotosCommand> provideOperationGetPhotos() {
      return new ComposableOperationView<>(this, this);
   }

   @Override
   public void handleItemClick(int position) {
      if (getAdapter().getItemViewType(position) == R.layout.picker_adapter_item_photo_facebook) {
         updateItem(position);
         getPresenter().attachImages();
      }
   }

   private void updateItem(int position) {
      getAdapter().updateItem(position);
      boolean isLimitReached = isLimitReached(getChosenPhotos().size());
      if (isLimitReached) {
         if (photoPickLimitStrategy.photoPickLimit() > 1) {
            Toast.makeText(getContext(), getContext().getString(R.string.media_picker_limit_reached,
                  String.valueOf(photoPickLimitStrategy.photoPickLimit())), Toast.LENGTH_SHORT).show();
            getAdapter().updateItem(position);
         } else {
            FacebookPhotoPickerViewModel modelToRevert =
                  Queryable.from(getChosenPhotos())
                        .filter(element -> getAdapter().getPositionFromItem(element) != position).firstOrDefault();
            int modelToRevertPosition = getAdapter().getPositionFromItem(modelToRevert);
            getAdapter().updateItem(modelToRevertPosition);
         }
      }
   }

   private boolean isLimitReached(int count) {
      return photoPickLimitStrategy.photoPickLimit() > 0 && count > photoPickLimitStrategy.photoPickLimit();
   }
}
