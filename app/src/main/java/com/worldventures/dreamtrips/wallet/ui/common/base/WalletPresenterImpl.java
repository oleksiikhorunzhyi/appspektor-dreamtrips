package com.worldventures.dreamtrips.wallet.ui.common.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

public abstract class WalletPresenterImpl<V extends WalletScreen> extends MvpBasePresenter<V> implements WalletPresenter<V> {

   @SuppressWarnings("WeakerAccess")
   private final Navigator navigator;
   private final WalletDeviceConnectionDelegate deviceConnectionDelegate;

   public WalletPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate) {
      this.navigator = navigator;
      this.deviceConnectionDelegate = deviceConnectionDelegate;
   }

   @Override
   public void attachView(V view) {
      super.attachView(view);
      deviceConnectionDelegate.setup(view);
   }

   public Navigator getNavigator() {
      return navigator;
   }
}
