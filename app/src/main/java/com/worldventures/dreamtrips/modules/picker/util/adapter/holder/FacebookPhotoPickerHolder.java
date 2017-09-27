package com.worldventures.dreamtrips.modules.picker.util.adapter.holder;


import com.worldventures.dreamtrips.databinding.PickerAdapterItemPhotoFacebookBinding;
import com.worldventures.dreamtrips.modules.picker.model.FacebookPhotoPickerViewModel;

public class FacebookPhotoPickerHolder extends BaseMediaPickerHolder<PickerAdapterItemPhotoFacebookBinding, FacebookPhotoPickerViewModel> {

   public FacebookPhotoPickerHolder(PickerAdapterItemPhotoFacebookBinding photoFacebookBinding) {
      super(photoFacebookBinding);
   }

   @Override
   public void setData(FacebookPhotoPickerViewModel data) {
      getDataBinding().setPhotoFacebookModel(data);
   }
}
