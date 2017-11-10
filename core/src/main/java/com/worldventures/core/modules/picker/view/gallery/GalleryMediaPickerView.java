package com.worldventures.core.modules.picker.view.gallery;


import com.worldventures.core.modules.picker.command.GetMediaFromGalleryCommand;
import com.worldventures.core.modules.picker.viewmodel.GalleryMediaPickerViewModel;
import com.worldventures.core.modules.picker.view.base.BaseMediaPickerView;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface GalleryMediaPickerView extends BaseMediaPickerView<GalleryMediaPickerViewModel> {

   void showAttachmentTypeDialog();

   void cameraPermissionGrantedPhoto();

   void cameraPermissionGrantedVideo();

   void showRationaleForCamera();

   void showDeniedForCamera();

   void updateItem(int position);

   void updateItemWithSwap(int position);

   void showWrongType();

   void showPhotoLimitReached(int count);

   void showVideoLimitReached(int count);

   void showVideoDurationLimitReached(int limitLength);

   List<GalleryMediaPickerViewModel> provideStaticItems();

   OperationView<GetMediaFromGalleryCommand> provideGalleryOperationView();

   List<GalleryMediaPickerViewModel> getChosenMedia();

   boolean isVideoEnabled();

   int getVideoDurationLimit();
}