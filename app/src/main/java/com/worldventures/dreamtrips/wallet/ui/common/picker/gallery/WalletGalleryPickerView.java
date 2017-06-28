package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;


import com.worldventures.dreamtrips.modules.media_picker.service.command.GetPhotosFromGalleryCommand;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BaseWalletPickerView;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletGalleryPickerView extends BaseWalletPickerView<WalletGalleryPickerModel> {

   void cameraPermissionGranted();

   void showRationaleForCamera();

   void showDeniedForCamera();

   List<WalletGalleryPickerModel> provideStaticItems();

   OperationView<GetPhotosFromGalleryCommand> provideGalleryOperationView();

   List<WalletGalleryPickerModel> getChosenPhotos();
}
