package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;

import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BaseWalletPickerAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerHolderFactory;

import java.util.ArrayList;
import java.util.List;


public class WalletPickerGalleryAdapter extends BaseWalletPickerAdapter<WalletGalleryPickerModel> {

   public WalletPickerGalleryAdapter(List<WalletGalleryPickerModel> items, WalletPickerHolderFactory holderTypeFactory) {
      super(items, holderTypeFactory);
   }

   public void clear(int removePosition) {
      int itemCount = items.size();
      List<WalletGalleryPickerModel> itemsToSave = new ArrayList<>();
      for (int i = 0; i < removePosition; i++) {
         itemsToSave.add(items.get(i));
      }
      items.clear();
      items.addAll(itemsToSave);
      notifyItemRangeRemoved(removePosition + 1, itemCount);
   }
}
