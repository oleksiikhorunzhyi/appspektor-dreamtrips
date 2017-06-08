package com.worldventures.dreamtrips.wallet.ui.common.picker.base;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.albums.WalletFacebookAlbumHolder;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.albums.WalletFacebookAlbumModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos.WalletFacebookPhotoHolder;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos.WalletFacebookPhotoModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryPhotoHolder;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryPhotoModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletIrregularPhotoHolder;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletIrregularPhotoModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseHolder;

public class WalletPickerHolderFactoryImpl implements WalletPickerHolderFactory {
   @Override
   public int type(WalletGalleryPhotoModel model) {
      return R.layout.adapter_item_photo_pick;
   }

   @Override
   public int type(WalletIrregularPhotoModel model) {
      return R.layout.adapter_item_attach_photo;
   }

   @Override
   public int type(WalletFacebookAlbumModel model) {
      return R.layout.adapter_item_facebook_album;
   }

   @Override
   public int type(WalletFacebookPhotoModel model) {
      return R.layout.adapter_item_photo_facebook;
   }

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {
      View itemView = LayoutInflater
            .from(parent.getContext()).inflate(viewType, parent, false);
      switch (viewType) {
         case R.layout.adapter_item_photo_pick:
            return new WalletGalleryPhotoHolder(itemView);
         case R.layout.adapter_item_attach_photo:
            return new WalletIrregularPhotoHolder(itemView);
         case R.layout.adapter_item_facebook_album:
            return new WalletFacebookAlbumHolder(itemView);
         case R.layout.adapter_item_photo_facebook:
            return new WalletFacebookPhotoHolder(itemView);
         default:
            throw new IllegalArgumentException();

      }
   }
}
