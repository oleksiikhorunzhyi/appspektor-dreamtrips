package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos;

import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerHolderFactory;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.WalletPickerFacebookAdapter;

import java.util.List;


public class WalletPickerFacebookPhotosAdapter extends WalletPickerFacebookAdapter<WalletFacebookPhotoModel> {

   public WalletPickerFacebookPhotosAdapter(List<WalletFacebookPhotoModel> items, WalletPickerHolderFactory holderTypeFactory) {
      super(items, holderTypeFactory);
   }
}
