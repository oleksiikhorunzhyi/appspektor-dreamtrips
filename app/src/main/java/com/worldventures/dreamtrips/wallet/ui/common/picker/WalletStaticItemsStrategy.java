package com.worldventures.dreamtrips.wallet.ui.common.picker;


import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryPhotoModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryPickerModel;

import java.util.List;

public interface WalletStaticItemsStrategy {
   List<WalletGalleryPickerModel> provideStaticItems();

   boolean isExtraItemAvailable();

   WalletGalleryPhotoModel provideExtraItem();
}
