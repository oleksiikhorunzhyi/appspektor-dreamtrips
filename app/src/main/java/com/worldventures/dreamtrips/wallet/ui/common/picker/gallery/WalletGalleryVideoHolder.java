package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;

import com.worldventures.dreamtrips.databinding.PickerAdapterItemVideoGalleryBinding;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerHolder;


public class WalletGalleryVideoHolder extends BasePickerHolder<PickerAdapterItemVideoGalleryBinding, WalletGalleryVideoModel> {

   public WalletGalleryVideoHolder(PickerAdapterItemVideoGalleryBinding dataBinding) {
      super(dataBinding);
   }

   @Override
   public void setData(WalletGalleryVideoModel data) {
      getDataBinding().setVideoGalleryModel(data);
   }
}
