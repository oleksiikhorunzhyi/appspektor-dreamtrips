package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.albums;

import com.worldventures.dreamtrips.modules.facebook.service.command.GetAlbumsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.WalletFacebookPickerView;

import io.techery.janet.operationsubscriber.view.OperationView;


public interface WalletPickerFacebookAlbumView extends WalletFacebookPickerView<WalletFacebookAlbumModel> {

   OperationView<GetAlbumsCommand> provideOperationGetAlbums();

   void goBack();
}
