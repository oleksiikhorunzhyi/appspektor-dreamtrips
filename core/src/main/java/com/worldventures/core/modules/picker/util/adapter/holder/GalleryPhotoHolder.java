package com.worldventures.core.modules.picker.util.adapter.holder;

import com.worldventures.core.databinding.PickerAdapterItemPhotoGalleryBinding;
import com.worldventures.core.modules.picker.viewmodel.GalleryPhotoPickerViewModel;


public class GalleryPhotoHolder extends BaseMediaPickerHolder<PickerAdapterItemPhotoGalleryBinding, GalleryPhotoPickerViewModel> {

   public GalleryPhotoHolder(PickerAdapterItemPhotoGalleryBinding photoGalleryBinding) {
      super(photoGalleryBinding);
   }

   @Override
   public void setData(GalleryPhotoPickerViewModel data) {
      getDataBinding().setPhotoGalleryModel(data);
   }
}
