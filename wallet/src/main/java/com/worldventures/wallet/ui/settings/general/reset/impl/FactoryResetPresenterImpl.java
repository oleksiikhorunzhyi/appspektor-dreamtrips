package com.worldventures.wallet.ui.settings.general.reset.impl;

import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegate;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetPresenter;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetScreen;

public class FactoryResetPresenterImpl extends WalletPresenterImpl<FactoryResetScreen> implements FactoryResetPresenter {

   private final FactoryResetDelegate factoryResetDelegate;

   public FactoryResetPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FactoryResetDelegate factoryResetDelegate) {
      super(navigator, deviceConnectionDelegate);
      this.factoryResetDelegate = factoryResetDelegate;
   }

   @Override
   public void attachView(FactoryResetScreen view) {
      super.attachView(view);
      factoryResetDelegate.bindView(getView());
      factoryResetDelegate.startRegularFactoryReset();
   }

   @Override
   public void detachView(boolean retainInstance) {
      factoryResetDelegate.cancelFactoryReset(); // todo
      super.detachView(retainInstance);
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

}
