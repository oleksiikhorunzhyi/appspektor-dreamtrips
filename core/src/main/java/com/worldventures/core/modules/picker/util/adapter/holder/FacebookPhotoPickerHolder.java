package com.worldventures.core.modules.picker.util.adapter.holder;


import com.worldventures.core.databinding.PickerAdapterItemPhotoFacebookBinding;
import com.worldventures.core.modules.picker.viewmodel.FacebookPhotoPickerViewModel;

public class FacebookPhotoPickerHolder extends BaseMediaPickerHolder<PickerAdapterItemPhotoFacebookBinding, FacebookPhotoPickerViewModel> {

   public FacebookPhotoPickerHolder(PickerAdapterItemPhotoFacebookBinding photoFacebookBinding) {
      super(photoFacebookBinding);
   }

   @Override
   public void setData(FacebookPhotoPickerViewModel data) {
      getDataBinding().setPhotoFacebookModel(data);
   }
}
