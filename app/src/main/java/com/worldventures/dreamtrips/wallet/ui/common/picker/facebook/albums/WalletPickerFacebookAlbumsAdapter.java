package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.albums;

import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerHolderFactory;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.WalletPickerFacebookAdapter;

import java.util.List;


public class WalletPickerFacebookAlbumsAdapter extends WalletPickerFacebookAdapter<WalletFacebookAlbumModel> {

   public WalletPickerFacebookAlbumsAdapter(List<WalletFacebookAlbumModel> items, WalletPickerHolderFactory holderTypeFactory) {
      super(items, holderTypeFactory);
   }
}
