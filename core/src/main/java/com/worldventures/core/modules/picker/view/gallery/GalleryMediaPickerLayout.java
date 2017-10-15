package com.worldventures.core.modules.picker.view.gallery;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.AttributeSet;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.R;
import com.worldventures.core.modules.picker.command.GetMediaFromGalleryCommand;
import com.worldventures.core.modules.picker.presenter.gallery.GalleryMediaPickerPresenter;
import com.worldventures.core.modules.picker.util.MediaPickerStep;
import com.worldventures.core.modules.picker.util.strategy.MediaPickerStaticItemsStrategy;
import com.worldventures.core.modules.picker.util.strategy.PhotoPickLimitStrategy;
import com.worldventures.core.modules.picker.util.strategy.VideoPickLimitStrategy;
import com.worldventures.core.modules.picker.view.base.BaseMediaPickerLayout;
import com.worldventures.core.modules.picker.viewmodel.GalleryMediaPickerViewModel;
import com.worldventures.core.modules.picker.viewmodel.IrregularPhotoPickerViewModel;
import com.worldventures.core.utils.QuantityHelper;

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
   protected void initView() {
      super.initView();

      final DefaultItemAnimator gridAnimator = new DefaultItemAnimator();
      gridAnimator.setSupportsChangeAnimations(false);
      pickerRecyclerView.setItemAnimator(gridAnimator);
   }

   @Override
   public void addItems(List<GalleryMediaPickerViewModel> items) {
      pickerRecyclerView.scheduleLayoutAnimation();
      super.addItems(items);
   }

   @Override
   public void handleItemClick(int position) {
      if (getAdapter().getItemViewType(position) == R.layout.picker_adapter_item_photo_gallery
            || getAdapter().getItemViewType(position) == R.layout.picker_adapter_item_video_gallery) {
         getPresenter().itemPicked(getAdapter().getItem(position), position, videoPickLimitStrategy, photoPickLimitStrategy);
      } else if (getAdapter().getItemViewType(position) == R.layout.picker_adapter_item_static) {
         handleAlternateSourcesClick(position);
      }
   }

   @Override
   public void updateItem(int position) {
      getAdapter().updateItem(position);
   }

   @Override
   public void updateItemWithSwap(int position) {
      getAdapter().updateItem(position);
      GalleryMediaPickerViewModel modelToRevert = Queryable.from(getChosenMedia())
            .filter(element -> getAdapter().getPositionFromItem(element) != position).firstOrDefault();
      int modelToRevertPosition = getAdapter().getPositionFromItem(modelToRevert);
      getAdapter().updateItem(modelToRevertPosition);
   }

   private void handleAlternateSourcesClick(int position) {
      final IrregularPhotoPickerViewModel item = (IrregularPhotoPickerViewModel) getAdapter().getItem(position);
      if (item.getAttachType() == IrregularPhotoPickerViewModel.CAMERA) {
         presenter.handleCameraClick();
      } else if (item.getAttachType() == IrregularPhotoPickerViewModel.FACEBOOK
            && getOnNextClickListener() != null) {
         getOnNextClickListener().onNextClick(null);
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
                  default:
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
   public void showWrongType() {
      Toast.makeText(getContext(), getContext().getString(R.string.picker_two_media_type_error), Toast.LENGTH_SHORT)
            .show();

   }

   @Override
   public void showPhotoLimitReached(int count) {
      Toast.makeText(getContext(), getContext().getString(QuantityHelper.chooseResource(count, R.string.picker_photo_limit,
            R.string.picker_photo_limit_plural), count), Toast.LENGTH_SHORT).show();
   }

   @Override
   public void showVideoDurationLimitReached(int limitLength) {
      Toast.makeText(getContext(), getContext().getString(R.string.picker_video_length_limit, limitLength), Toast.LENGTH_SHORT)
            .show();

   }

   @Override
   public void showVideoLimitReached(int count) {
      Toast.makeText(getContext(), getContext().getString(QuantityHelper.chooseResource(count, R.string.picker_video_limit,
            R.string.picker_video_limit_plural), count), Toast.LENGTH_SHORT)
            .show();
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
