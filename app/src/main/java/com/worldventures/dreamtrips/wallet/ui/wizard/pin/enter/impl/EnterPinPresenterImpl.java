package com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter.impl;


import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter.EnterPinDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter.EnterPinPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter.EnterPinScreen;

import java.util.concurrent.TimeUnit;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.smartcard.action.settings.CancelPinSetupAction;
import io.techery.janet.smartcard.action.settings.StartPinSetupAction;
import io.techery.janet.smartcard.event.PinSetupFinishedEvent;
import rx.android.schedulers.AndroidSchedulers;

public class EnterPinPresenterImpl extends WalletPresenterImpl<EnterPinScreen> implements EnterPinPresenter {

   private final WizardInteractor wizardInteractor;
   private final AnalyticsInteractor analyticsInteractor;

   private EnterPinDelegate enterPinDelegate;

   public EnterPinPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, WizardInteractor wizardInteractor,
         AnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.wizardInteractor = wizardInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(EnterPinScreen view) {
      super.attachView(view);
      trackScreen();
      enterPinDelegate = EnterPinDelegate.create(getView().getPinAction(), analyticsInteractor, getNavigator());
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
            .compose(bindViewIoToMainComposer())
            .delay(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()) //delay for execute save PIN data at SC
            .subscribe(new ActionStateSubscriber<PinSetupFinishedEvent>()
                  .onSuccess(event -> enterPinDelegate.pinEntered()));

      wizardInteractor.startPinSetupPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.startPinSetupPipe()))
            .compose(bindViewIoToMainComposer())
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
