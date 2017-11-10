package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.pin.impl;


import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.pin.EnterPinUnassignPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.pin.EnterPinUnassignScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetAction;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetDelegate;

public class EnterPinUnassignPresenterImpl extends WalletPresenterImpl<EnterPinUnassignScreen> implements EnterPinUnassignPresenter {

   private final FactoryResetDelegate factoryResetDelegate;

   public EnterPinUnassignPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FactoryResetInteractor factoryResetInteractor, WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.factoryResetDelegate = FactoryResetDelegate.create(factoryResetInteractor, analyticsInteractor,
            navigator, FactoryResetAction.NEW_CARD);
   }

   @Override
   public void attachView(EnterPinUnassignScreen view) {
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