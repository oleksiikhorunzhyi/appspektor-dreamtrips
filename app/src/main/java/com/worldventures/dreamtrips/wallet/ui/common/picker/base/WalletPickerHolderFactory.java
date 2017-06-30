package com.worldventures.dreamtrips.wallet.ui.common.picker.base;

import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.albums.WalletFacebookAlbumModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos.WalletFacebookPhotoModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryPhotoModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryVideoModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletIrregularPhotoModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.HolderTypeFactory;


public interface WalletPickerHolderFactory extends HolderTypeFactory {

   int type(WalletGalleryPhotoModel model);

   int type(WalletGalleryVideoModel model);

   int type(WalletIrregularPhotoModel model);

   int type(WalletFacebookAlbumModel model);

   int type(WalletFacebookPhotoModel model);
}
