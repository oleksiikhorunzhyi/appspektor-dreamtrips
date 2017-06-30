package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;

import com.worldventures.dreamtrips.databinding.PickerAdapterItemStaticBinding;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerHolder;


public class WalletIrregularPhotoHolder extends BasePickerHolder<PickerAdapterItemStaticBinding, WalletIrregularPhotoModel> {

   public WalletIrregularPhotoHolder(PickerAdapterItemStaticBinding itemStaticBinding) {
      super(itemStaticBinding);
   }

   @Override
   public void setData(WalletIrregularPhotoModel data) {
      getDataBinding().setStaticModel(data);
   }
}
