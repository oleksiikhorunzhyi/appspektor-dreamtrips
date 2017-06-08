package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook;

import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BaseWalletPickerAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerHolderFactory;

import java.util.List;


public class WalletPickerFacebookAdapter<M extends WalletFacebookPickerModel> extends BaseWalletPickerAdapter<M> {

   public WalletPickerFacebookAdapter(List<M> items, WalletPickerHolderFactory holderTypeFactory) {
      super(items, holderTypeFactory);
   }
}
