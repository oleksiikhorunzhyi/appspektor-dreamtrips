package com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.settings.CancelPinSetupAction;
import io.techery.janet.smartcard.action.settings.StartPinSetupAction;
import io.techery.janet.smartcard.event.PinSetupFinishedEvent;

public class EnterPinPresenter extends WalletPresenter<EnterPinPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final EnterPinDelegate enterPinDelegate;

   EnterPinPresenter(Context context, Injector injector, Action mode) {
      super(context, injector);

      enterPinDelegate = EnterPinDelegate.create(mode, analyticsInteractor, navigator);
   }

   public void goBack() {
      navigator.goBack();
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      trackScreen();
      setupPIN();
   }

   private void trackScreen() {
      enterPinDelegate.trackScreen();
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      enterPinDelegate.prepareView(view);
      observeSetupFinishedPipe();
   }

   @Override
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      cancelSetupPIN();
   }

   private void observeSetupFinishedPipe() {
      wizardInteractor.pinSetupFinishedPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.pinSetupFinishedPipe()))
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<PinSetupFinishedEvent>()
                  .onSuccess(event -> enterPinDelegate.pinEntered()));

      wizardInteractor.startPinSetupPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.startPinSetupPipe()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().<StartPinSetupAction>operationView()).create());
   }

   void retry() {
      setupPIN();
   }

   private void setupPIN() {
      wizardInteractor.startPinSetupPipe().send(new StartPinSetupAction());
   }

   private void cancelSetupPIN() {
      wizardInteractor.cancelPinSetupPipe().send(new CancelPinSetupAction());
   }

   public interface Screen extends WalletScreen, EnterPinDelegate.PinView {

      <T> OperationView<T> operationView();
   }
}
