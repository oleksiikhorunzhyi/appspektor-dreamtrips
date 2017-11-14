package com.worldventures.wallet.ui.settings.general.reset.impl;


import com.worldventures.wallet.service.FactoryResetInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetAction;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetDelegate;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetPresenter;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetScreen;

public class FactoryResetPresenterImpl extends WalletPresenterImpl<FactoryResetScreen> implements FactoryResetPresenter {

   private final FactoryResetDelegate factoryResetDelegate;

   public FactoryResetPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor, FactoryResetInteractor factoryResetInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.factoryResetDelegate = FactoryResetDelegate.create(factoryResetInteractor, analyticsInteractor,
            navigator, FactoryResetAction.GENERAL);
   }

   @Override
   public void attachView(FactoryResetScreen view) {
      super.attachView(view);
      factoryResetDelegate.bindView(getView());
   }

   @Override
   public void detachView(boolean retainInstance) {
      factoryResetDelegate.cancelFactoryReset();
      super.detachView(retainInstance);
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

}
