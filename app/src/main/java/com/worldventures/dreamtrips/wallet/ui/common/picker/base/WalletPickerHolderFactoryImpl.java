package com.worldventures.dreamtrips.wallet.ui.common.picker.base;


import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.databinding.PickerAdapterItemAlbumFacebookBinding;
import com.worldventures.dreamtrips.databinding.PickerAdapterItemPhotoFacebookBinding;
import com.worldventures.dreamtrips.databinding.PickerAdapterItemPhotoGalleryBinding;
import com.worldventures.dreamtrips.databinding.PickerAdapterItemStaticBinding;
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
      return R.layout.picker_adapter_item_photo_gallery;
   }

   @Override
   public int type(WalletIrregularPhotoModel model) {
      return R.layout.picker_adapter_item_static;
   }

   @Override
   public int type(WalletFacebookAlbumModel model) {
      return R.layout.picker_adapter_item_album_facebook;
   }

   @Override
   public int type(WalletFacebookPhotoModel model) {
      return R.layout.picker_adapter_item_photo_facebook;
   }

   @Override
   public BaseHolder holder(ViewGroup parent, int viewType) {
      final View itemView = LayoutInflater
            .from(parent.getContext()).inflate(viewType, parent, false);
      switch (viewType) {
         case R.layout.picker_adapter_item_photo_gallery:
            final PickerAdapterItemPhotoGalleryBinding photoGalleryBinding = DataBindingUtil.bind(itemView);
            return new WalletGalleryPhotoHolder(photoGalleryBinding);
         case R.layout.picker_adapter_item_static:
            final PickerAdapterItemStaticBinding itemStaticBinding = DataBindingUtil.bind(itemView);
            return new WalletIrregularPhotoHolder(itemStaticBinding);
         case R.layout.picker_adapter_item_album_facebook:
            final PickerAdapterItemAlbumFacebookBinding albumFacebookBinding = DataBindingUtil.bind(itemView);
            return new WalletFacebookAlbumHolder(albumFacebookBinding);
         case R.layout.picker_adapter_item_photo_facebook:
            final PickerAdapterItemPhotoFacebookBinding photoFacebookBinding = DataBindingUtil.bind(itemView);
            return new WalletFacebookPhotoHolder(photoFacebookBinding);
         default:
            throw new IllegalArgumentException();
      }
   }
}
