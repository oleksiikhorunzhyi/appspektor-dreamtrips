package com.worldventures.core.modules.picker.util.adapter.holder;


import com.worldventures.core.databinding.PickerAdapterItemAlbumFacebookBinding;
import com.worldventures.core.modules.picker.viewmodel.FacebookAlbumPickerViewModel;

public class FacebookAlbumPickerHolder extends BaseMediaPickerHolder<PickerAdapterItemAlbumFacebookBinding, FacebookAlbumPickerViewModel> {

   public FacebookAlbumPickerHolder(PickerAdapterItemAlbumFacebookBinding albumFacebookBinding) {
      super(albumFacebookBinding);
   }

   @Override
   public void setData(FacebookAlbumPickerViewModel data) {
      getDataBinding().setFacebookAlbumModel(data);
   }
}
