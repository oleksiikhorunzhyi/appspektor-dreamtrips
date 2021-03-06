package com.worldventures.wallet.ui.wizard.pin.enter.impl;

import com.worldventures.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WizardInteractor;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.wizard.pin.enter.EnterPinDelegate;
import com.worldventures.wallet.ui.wizard.pin.enter.EnterPinPresenter;
import com.worldventures.wallet.ui.wizard.pin.enter.EnterPinScreen;

import java.util.concurrent.TimeUnit;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.smartcard.action.settings.CancelPinSetupAction;
import io.techery.janet.smartcard.action.settings.StartPinSetupAction;
import io.techery.janet.smartcard.event.PinSetupFinishedEvent;
import rx.android.schedulers.AndroidSchedulers;

public class EnterPinPresenterImpl extends WalletPresenterImpl<EnterPinScreen> implements EnterPinPresenter {

   private final WizardInteractor wizardInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;

   private EnterPinDelegate enterPinDelegate;

   public EnterPinPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WizardInteractor wizardInteractor,
         WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.wizardInteractor = wizardInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(EnterPinScreen view) {
      super.attachView(view);
      enterPinDelegate = EnterPinDelegate.create(getView().getPinAction(), analyticsInteractor, getNavigator());
      trackScreen();
      enterPinDelegate.prepareView(view);
      observeSetupFinishedPipe();
      setupPIN();
   }

   private void trackScreen() {
      enterPinDelegate.trackScreen();
   }

   private void observeSetupFinishedPipe() {
      wizardInteractor.pinSetupFinishedPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.pinSetupFinishedPipe()))
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .delay(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()) //delay for execute save PIN data at SC
            .subscribe(new ActionStateSubscriber<PinSetupFinishedEvent>()
                  .onSuccess(event -> enterPinDelegate.pinEntered()));

      wizardInteractor.startPinSetupPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.startPinSetupPipe()))
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().<StartPinSetupAction>operationView()).create());
   }

   @Override
   public void goBack() {
      cancelSetupPIN();
      getNavigator().goBack();
   }

   @Override
   public void retry() {
      setupPIN();
   }

   private void setupPIN() {
      wizardInteractor.startPinSetupPipe().send(new StartPinSetupAction());
   }

   @Override
   public void cancelSetupPIN() {
      wizardInteractor.cancelPinSetupPipe().send(new CancelPinSetupAction());
   }
}
