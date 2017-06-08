package com.worldventures.dreamtrips.wallet.ui.common.picker;


import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryPhotoModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryPickerModel;

import java.util.ArrayList;
import java.util.List;

public class DefaultPhotoStaticItemsStrategy extends SimpleWalletStaticItemsStrategy {
   private final String defaultPhotoUrl;

   public DefaultPhotoStaticItemsStrategy(String defaultPhotoUrl) {
      this.defaultPhotoUrl = defaultPhotoUrl;
   }

   @Override
   public List<WalletGalleryPickerModel> provideStaticItems() {
      final List<WalletGalleryPickerModel> appendedStaticList = new ArrayList<>();
      appendedStaticList.addAll(super.provideStaticItems());
      appendedStaticList.add(provideExtraItem());
      return appendedStaticList;
   }

   @Override
   public boolean isExtraItemAvailable() {
      return true;
   }

   @Override
   public WalletGalleryPhotoModel provideExtraItem() {
      return new WalletGalleryPhotoModel(defaultPhotoUrl);
   }
}
