package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos;

import com.worldventures.dreamtrips.modules.facebook.service.command.GetPhotosCommand;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.WalletFacebookPickerView;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;


public interface WalletPickerFacebookPhotosView extends WalletFacebookPickerView<WalletFacebookPhotoModel> {

   String getAlbumId();

   int getPickLimit();

   List<WalletFacebookPhotoModel> getChosenPhotos();

   OperationView<GetPhotosCommand> provideOperationGetPhotos();
}
