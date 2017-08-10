package com.worldventures.dreamtrips.modules.picker.view.gallery;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetMediaFromGalleryCommand;
import com.worldventures.dreamtrips.modules.picker.model.GalleryMediaPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.model.IrregularPhotoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.presenter.gallery.GalleryMediaPickerPresenter;
import com.worldventures.dreamtrips.modules.picker.util.MediaPickerStep;
import com.worldventures.dreamtrips.modules.picker.util.strategy.MediaPickerStaticItemsStrategy;
import com.worldventures.dreamtrips.modules.picker.util.strategy.PhotoPickLimitStrategy;
import com.worldventures.dreamtrips.modules.picker.util.strategy.VideoPickLimitStrategy;
import com.worldventures.dreamtrips.modules.picker.view.base.BaseMediaPickerLayout;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public class GalleryMediaPickerLayout extends BaseMediaPickerLayout<GalleryMediaPickerPresenter, GalleryMediaPickerViewModel> implements GalleryMediaPickerView {

   @Inject GalleryMediaPickerPresenter presenter;

   private final MediaPickerStaticItemsStrategy mediaPickerStaticItemsStrategy;
   private final PhotoPickLimitStrategy photoPickLimitStrategy;
   private final VideoPickLimitStrategy videoPickLimitStrategy;

   public GalleryMediaPickerLayout(MediaPickerStaticItemsStrategy mediaPickerStaticItemsStrategy,
         PhotoPickLimitStrategy pickLimitStrategy, VideoPickLimitStrategy videoPickLimitStrategy, @NonNull Context context) {
      this(mediaPickerStaticItemsStrategy, pickLimitStrategy, videoPickLimitStrategy, context, null);
   }

   public GalleryMediaPickerLayout(MediaPickerStaticItemsStrategy mediaPickerStaticItemsStrategy,
         PhotoPickLimitStrategy pickLimitStrategy, VideoPickLimitStrategy videoPickLimitStrategy, @NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      this.mediaPickerStaticItemsStrategy = mediaPickerStaticItemsStrategy;
      this.photoPickLimitStrategy = pickLimitStrategy;
      this.videoPickLimitStrategy = videoPickLimitStrategy;
   }

   @Override
   public void handleItemClick(int position) {
      if (getAdapter().getItemViewType(position) == R.layout.picker_adapter_item_photo_gallery
            || getAdapter().getItemViewType(position) == R.layout.picker_adapter_item_video_gallery) {
         updateItem(position);
         presenter.attachMedia();
      } else if (getAdapter().getItemViewType(position) == R.layout.picker_adapter_item_static) {
         handleAlternateSourcesClick(position);
      }
   }

   private void handleAlternateSourcesClick(int position) {
      final IrregularPhotoPickerViewModel item = (IrregularPhotoPickerViewModel) getAdapter().getItem(position);
      if (item.getAttachType() == IrregularPhotoPickerViewModel.CAMERA) {
         presenter.handleCameraClick();
      } else if (item.getAttachType() == IrregularPhotoPickerViewModel.FACEBOOK) {
         if (getOnNextClickListener() != null) {
            getOnNextClickListener().onNextClick(null);
         }
      }
   }

   private boolean isLimitReached(int count) {
      return photoPickLimitStrategy.photoPickLimit() > 0 && count > photoPickLimitStrategy.photoPickLimit();
   }

   private void updateItem(int position) {
      getAdapter().updateItem(position);
      boolean isLimitReached = isLimitReached(getChosenMedia().size());
      if (isLimitReached) {
         if (photoPickLimitStrategy.photoPickLimit() > 1) {
            Toast.makeText(getContext(), getContext().getString(R.string.media_picker_limit_reached,
                  String.valueOf(photoPickLimitStrategy.photoPickLimit())), Toast.LENGTH_SHORT).show();
            getAdapter().updateItem(position);
         } else {
            GalleryMediaPickerViewModel modelToRevert =
                  Queryable.from(getChosenMedia())
                        .filter(element -> getAdapter().getPositionFromItem(element) != position).firstOrDefault();
            int modelToRevertPosition = getAdapter().getPositionFromItem(modelToRevert);
            getAdapter().updateItem(modelToRevertPosition);
         }
      }
   }

   private int getAdapterOffset() {
      return mediaPickerStaticItemsStrategy.isExtraItemAvailable()
            ? mediaPickerStaticItemsStrategy.provideStaticItems().size() - 1
            : mediaPickerStaticItemsStrategy.provideStaticItems().size();
   }

   @Override
   public GalleryMediaPickerPresenter getPresenter() {
      return presenter;
   }

   @Override
   public MediaPickerStep getStep() {
      return MediaPickerStep.GALLERY;
   }

   @Override
   public String getFailedActionText() {
      return getContext().getString(R.string.media_picker_gallery_action);
   }

   @Override
   public Observable<List<GalleryMediaPickerViewModel>> attachedItems() {
      return getPresenter().attachedItems();
   }

   @Override
   public void showAttachmentTypeDialog() {
      final String[] items = new String[]{getContext().getString(R.string.camera_take_a_picture),
            getContext().getString(R.string.camera_record_a_video)};
      new MaterialDialog.Builder(getContext())
            .items(items)
            .itemsCallback((dialog, itemView, which, text) -> {
               switch (which) {
                  case 0:
                     presenter.tryOpenCameraForPhoto();
                     break;
                  case 1:
                     presenter.tryOpenCameraForVideo();
                     break;
               }
            }).show();
   }

   @Override
   public void cameraPermissionGrantedPhoto() {
      presenter.openCameraForPhoto();
   }

   @Override
   public void cameraPermissionGrantedVideo() {
      presenter.openCameraForVideo();
   }

   @Override
   public void showRationaleForCamera() {
      Toast.makeText(getContext(), R.string.permission_camera_rationale, Toast.LENGTH_SHORT).show();
   }

   @Override
   public void showDeniedForCamera() {
      Toast.makeText(getContext(), R.string.no_camera_permission, Toast.LENGTH_SHORT).show();
   }

   @Override
   public void showVideoLimitReached(int limitLength) {
      Toast.makeText(getContext(), getContext().getString(R.string.picker_video_duration_limit, limitLength), Toast.LENGTH_SHORT).show();
   }

   @Override
   public List<GalleryMediaPickerViewModel> provideStaticItems() {
      return mediaPickerStaticItemsStrategy.provideStaticItems();
   }

   @Override
   public OperationView<GetMediaFromGalleryCommand> provideGalleryOperationView() {
      return new ComposableOperationView<>(this, this);
   }

   @Override
   public List<GalleryMediaPickerViewModel> getChosenMedia() {
      return getAdapter().getChosenMedia(getAdapterOffset());
   }

   @Override
   public boolean isVideoEnabled() {
      return videoPickLimitStrategy.videoPickLimit() > 0;
   }

   @Override
   public int getVideoDurationLimit() {
      return videoPickLimitStrategy.videoDurationLimit();
   }

   @Override
   public void clearItems() {
      getAdapter().clear();
   }

   @Override
   public void onProgressChanged(int i) { /* nothing */ }
}
