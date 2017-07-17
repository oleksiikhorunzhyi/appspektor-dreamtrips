package com.worldventures.dreamtrips.modules.picker.util.adapter.holder;

import com.worldventures.dreamtrips.databinding.PickerAdapterItemStaticBinding;
import com.worldventures.dreamtrips.modules.picker.model.IrregularPhotoPickerViewModel;


public class IrregularPhotoHolder extends BaseMediaPickerHolder<PickerAdapterItemStaticBinding, IrregularPhotoPickerViewModel> {

   public IrregularPhotoHolder(PickerAdapterItemStaticBinding itemStaticBinding) {
      super(itemStaticBinding);
   }

   @Override
   public void setData(IrregularPhotoPickerViewModel data) {
      getDataBinding().setStaticModel(data);
   }
}
