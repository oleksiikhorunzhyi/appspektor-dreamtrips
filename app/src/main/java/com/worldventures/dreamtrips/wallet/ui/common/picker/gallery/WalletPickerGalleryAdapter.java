package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;

import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BaseWalletPickerAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerHolderFactory;

import java.util.List;


public class WalletPickerGalleryAdapter extends BaseWalletPickerAdapter<WalletGalleryPickerModel> {

   public WalletPickerGalleryAdapter(List<WalletGalleryPickerModel> items, WalletPickerHolderFactory holderTypeFactory) {
      super(items, holderTypeFactory);
   }
}
