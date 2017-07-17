package com.worldventures.dreamtrips.modules.picker.util.adapter.holder;


import com.worldventures.dreamtrips.databinding.PickerAdapterItemAlbumFacebookBinding;
import com.worldventures.dreamtrips.modules.picker.model.FacebookAlbumPickerViewModel;

public class FacebookAlbumPickerHolder extends BaseMediaPickerHolder<PickerAdapterItemAlbumFacebookBinding, FacebookAlbumPickerViewModel> {

   public FacebookAlbumPickerHolder(PickerAdapterItemAlbumFacebookBinding albumFacebookBinding) {
      super(albumFacebookBinding);
   }

   @Override
   public void setData(FacebookAlbumPickerViewModel data) {
      getDataBinding().setFacebookAlbumModel(data);
   }
}
