package com.worldventures.dreamtrips.wallet.ui.common.picker;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryPhotoModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryPickerModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletIrregularPhotoModel;

import java.util.ArrayList;
import java.util.List;

public class SimpleWalletStaticItemsStrategy implements WalletStaticItemsStrategy {

   @Override
   public List<WalletGalleryPickerModel> provideStaticItems() {
      List<WalletGalleryPickerModel> staticItems = new ArrayList<>();
      staticItems.add(new WalletIrregularPhotoModel(WalletIrregularPhotoModel.CAMERA, R.drawable.ic_picker_camera, R.string.camera, R.color.share_camera_color));
      staticItems.add(new WalletIrregularPhotoModel(WalletIrregularPhotoModel.FACEBOOK, R.drawable.fb_logo, R.string.add_from_facebook, R.color.facebook_color));
      return staticItems;
   }

   @Override
   public boolean isExtraItemAvailable() {
      return false;
   }

   @Override
   public WalletGalleryPhotoModel provideExtraItem() {
      throw new UnsupportedOperationException("Simple strategy doesn't support extra item");
   }
}
