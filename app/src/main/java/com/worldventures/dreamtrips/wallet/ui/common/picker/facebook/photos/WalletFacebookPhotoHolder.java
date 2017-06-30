package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos;


import com.worldventures.dreamtrips.databinding.PickerAdapterItemPhotoFacebookBinding;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerHolder;

public class WalletFacebookPhotoHolder extends BasePickerHolder<PickerAdapterItemPhotoFacebookBinding, WalletFacebookPhotoModel> {

   public WalletFacebookPhotoHolder(PickerAdapterItemPhotoFacebookBinding photoFacebookBinding) {
      super(photoFacebookBinding);
   }

   @Override
   public void setData(WalletFacebookPhotoModel data) {
      getDataBinding().setPhotoFacebookModel(data);
   }
}
