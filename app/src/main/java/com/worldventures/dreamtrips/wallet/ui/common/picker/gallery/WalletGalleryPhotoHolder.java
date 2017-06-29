package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;

import com.worldventures.dreamtrips.databinding.PickerAdapterItemPhotoGalleryBinding;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerHolder;


public class WalletGalleryPhotoHolder extends BasePickerHolder<PickerAdapterItemPhotoGalleryBinding, WalletGalleryPhotoModel> {

   public WalletGalleryPhotoHolder(PickerAdapterItemPhotoGalleryBinding photoGalleryBinding) {
      super(photoGalleryBinding);
   }

   @Override
   public void setData(WalletGalleryPhotoModel data) {
      getDataBinding().setPhotoGalleryModel(data);
   }
}
