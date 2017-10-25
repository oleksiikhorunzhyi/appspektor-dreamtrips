package com.worldventures.core.modules.picker.util.adapter.holder;

import com.worldventures.core.databinding.PickerAdapterItemVideoGalleryBinding;
import com.worldventures.core.modules.picker.viewmodel.GalleryVideoPickerViewModel;


public class GalleryVideoHolder extends BaseMediaPickerHolder<PickerAdapterItemVideoGalleryBinding, GalleryVideoPickerViewModel> {

   public GalleryVideoHolder(PickerAdapterItemVideoGalleryBinding dataBinding) {
      super(dataBinding);
   }

   @Override
   public void setData(GalleryVideoPickerViewModel data) {
      getDataBinding().setVideoGalleryModel(data);
   }
}
