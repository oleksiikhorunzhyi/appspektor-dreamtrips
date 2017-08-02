package com.worldventures.dreamtrips.modules.picker.util.adapter.holder;

import com.worldventures.dreamtrips.databinding.PickerAdapterItemVideoGalleryBinding;
import com.worldventures.dreamtrips.modules.picker.model.GalleryVideoPickerViewModel;


public class GalleryVideoHolder extends BaseMediaPickerHolder<PickerAdapterItemVideoGalleryBinding, GalleryVideoPickerViewModel> {

   public GalleryVideoHolder(PickerAdapterItemVideoGalleryBinding dataBinding) {
      super(dataBinding);
   }

   @Override
   public void setData(GalleryVideoPickerViewModel data) {
      getDataBinding().setVideoGalleryModel(data);
   }
}
