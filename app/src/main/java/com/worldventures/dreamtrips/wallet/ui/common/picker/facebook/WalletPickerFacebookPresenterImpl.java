package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook;

import com.worldventures.dreamtrips.modules.facebook.service.FacebookInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BaseWalletPickerPresenterImpl;


public abstract class WalletPickerFacebookPresenterImpl<V extends WalletFacebookPickerView>
      extends BaseWalletPickerPresenterImpl<V> {

   private final FacebookInteractor facebookInteractor;

   public WalletPickerFacebookPresenterImpl(FacebookInteractor facebookInteractor) {
      this.facebookInteractor = facebookInteractor;
   }

   @Override
   public void attachView(V view) {
      super.attachView(view);
      observeItemSource();
   }

   public FacebookInteractor getFacebookInteractor() {
      return facebookInteractor;
   }

   public abstract void observeItemSource();
}
