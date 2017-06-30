package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.albums;


import com.worldventures.dreamtrips.databinding.PickerAdapterItemAlbumFacebookBinding;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerHolder;

public class WalletFacebookAlbumHolder extends BasePickerHolder<PickerAdapterItemAlbumFacebookBinding, WalletFacebookAlbumModel> {

   public WalletFacebookAlbumHolder(PickerAdapterItemAlbumFacebookBinding albumFacebookBinding) {
      super(albumFacebookBinding);
   }

   @Override
   public void setData(WalletFacebookAlbumModel data) {
      getDataBinding().setFacebookAlbumModel(data);
   }
}
