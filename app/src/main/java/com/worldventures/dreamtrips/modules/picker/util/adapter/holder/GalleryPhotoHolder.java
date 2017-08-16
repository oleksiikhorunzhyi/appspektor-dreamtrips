package com.worldventures.dreamtrips.modules.picker.util.adapter.holder;

import com.worldventures.dreamtrips.databinding.PickerAdapterItemPhotoGalleryBinding;
import com.worldventures.dreamtrips.modules.picker.model.GalleryPhotoPickerViewModel;


public class GalleryPhotoHolder extends BaseMediaPickerHolder<PickerAdapterItemPhotoGalleryBinding, GalleryPhotoPickerViewModel> {

   public GalleryPhotoHolder(PickerAdapterItemPhotoGalleryBinding photoGalleryBinding) {
      super(photoGalleryBinding);
   }

   @Override
   public void setData(GalleryPhotoPickerViewModel data) {
      getDataBinding().setPhotoGalleryModel(data);
   }
}
