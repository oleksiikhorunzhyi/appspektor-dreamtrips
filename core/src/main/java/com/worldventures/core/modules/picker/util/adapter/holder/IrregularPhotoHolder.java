package com.worldventures.core.modules.picker.util.adapter.holder;

import com.worldventures.core.databinding.PickerAdapterItemStaticBinding;
import com.worldventures.core.modules.picker.viewmodel.IrregularPhotoPickerViewModel;


public class IrregularPhotoHolder extends BaseMediaPickerHolder<PickerAdapterItemStaticBinding, IrregularPhotoPickerViewModel> {

   public IrregularPhotoHolder(PickerAdapterItemStaticBinding itemStaticBinding) {
      super(itemStaticBinding);
   }

   @Override
   public void setData(IrregularPhotoPickerViewModel data) {
      getDataBinding().setStaticModel(data);
   }
}
