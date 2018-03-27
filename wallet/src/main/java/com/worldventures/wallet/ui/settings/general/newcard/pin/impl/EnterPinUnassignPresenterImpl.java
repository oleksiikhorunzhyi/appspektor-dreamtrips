package com.worldventures.wallet.ui.settings.general.newcard.pin.impl;

import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.new_smartcard.EnterPinUnAssignAction;
import com.worldventures.wallet.analytics.new_smartcard.EnterPinUnAssignEnteredAction;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.newcard.pin.EnterPinUnassignPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.pin.EnterPinUnassignScreen;
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegate;

public class EnterPinUnassignPresenterImpl extends WalletPresenterImpl<EnterPinUnassignScreen> implements EnterPinUnassignPresenter {

   private final FactoryResetDelegate factoryResetDelegate;
   private final WalletAnalyticsInteractor analyticsInteractor;

   public EnterPinUnassignPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FactoryResetDelegate factoryResetDelegate, WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.analyticsInteractor = analyticsInteractor;
      this.factoryResetDelegate = factoryResetDelegate;
   }

   @Override
   public void attachView(EnterPinUnassignScreen view) {
      super.attachView(view);
      factoryResetDelegate.bindView(getView());
      analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(new EnterPinUnAssignAction()));
      factoryResetDelegate.startRegularFactoryReset();
   }

   @Override
   public void detachView(boolean retainInstance) {
      factoryResetDelegate.cancelFactoryReset(); // todo it's not a place for a cancel action
      analyticsInteractor.analyticsActionPipe().send(new EnterPinUnAssignEnteredAction());
      super.detachView(retainInstance);
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
