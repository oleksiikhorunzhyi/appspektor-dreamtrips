package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;


import com.worldventures.dreamtrips.modules.media_picker.service.command.GetMediaFromGalleryCommand;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BaseWalletPickerView;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletGalleryPickerView extends BaseWalletPickerView<WalletGalleryPickerModel> {

   void showAttachmentTypeDialog();

   void cameraPermissionGrantedPhoto();

   void cameraPermissionGrantedVideo();

   void showRationaleForCamera();

   void showDeniedForCamera();

   void showVideoLimitReached(int limitLength);

   List<WalletGalleryPickerModel> provideStaticItems();

   OperationView<GetMediaFromGalleryCommand> provideGalleryOperationView();

   List<WalletGalleryPickerModel> getChosenMedia();

   boolean isVideoEnabled();

   int getVideoLimit();
}
