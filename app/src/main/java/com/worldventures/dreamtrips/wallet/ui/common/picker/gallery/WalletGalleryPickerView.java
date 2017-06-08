package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;


import com.worldventures.dreamtrips.modules.common.command.GetPhotosFromGalleryCommand;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BaseWalletPickerView;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletGalleryPickerView extends BaseWalletPickerView<WalletGalleryPickerModel> {

   void cameraPermissionGranted();

   void showRationaleForCamera();

   void showDeniedForCamera();

   boolean isExtraItemAvailable();

   WalletGalleryPhotoModel getExtraItem();

   OperationView<GetPhotosFromGalleryCommand> provideGalleryOperationView();

   List<WalletGalleryPickerModel> getChosenPhotos();
}
