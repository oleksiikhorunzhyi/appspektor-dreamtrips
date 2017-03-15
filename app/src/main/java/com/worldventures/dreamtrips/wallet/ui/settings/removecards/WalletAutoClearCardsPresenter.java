package com.worldventures.dreamtrips.wallet.ui.settings.removecards;

import android.content.Context;
import android.os.Bundle;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.settings.AutoClearAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.AutoClearChangedAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class WalletAutoClearCardsPresenter extends WalletPresenter<WalletAutoClearCardsPresenter.Screen, WalletAutoClearCardsState> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private boolean autoClearWasChanged = false;

   public WalletAutoClearCardsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   // View State
   @Override
   public void onNewViewState() {
      state = new WalletAutoClearCardsState();
   }

   @Override
   public void applyViewState() {
      super.applyViewState();
      autoClearWasChanged = state.autoClearWasChanged();
   }

   @Override
   public void onSaveInstanceState(Bundle bundle) {
      state.setAutoClearWasChanged(autoClearWasChanged);
      super.onSaveInstanceState(bundle);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeSmartCard();
      observeDelayChange();
   }

   @Override
   public void detachView(boolean retainInstance) {
      if (autoClearWasChanged) {
         //known problem: this action will be sent after action from onAttachView of next screen
         trackAutoClear(new AutoClearChangedAction(getView().getSelectedTime()));
      }
      super.detachView(retainInstance);
   }

   public void goBack() {
      navigator.goBack();
   }

   /**
    * @param delayMinutes 0 - never
    */
   void onTimeSelected(long delayMinutes) {
      smartCardInteractor.autoClearDelayPipe().send(new SetAutoClearSmartCardDelayCommand(delayMinutes));
   }

   private void observeSmartCard() {
      smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(deviceStateCommand -> {
               final long autoClearDelay = deviceStateCommand.getResult().clearFlyeDelay();
               getView().selectedTime(autoClearDelay);
               trackAutoClear(new AutoClearAction(getView().getSelectedTime()));
            });
   }

   private void observeDelayChange() {
      smartCardInteractor.autoClearDelayPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetAutoClearSmartCardDelayCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> {
                     final long autoClearDelay = command.getResult();
                     getView().selectedTime(autoClearDelay);
                     autoClearWasChanged = true;
                  })
                  .onFail(ErrorHandler.create(getContext()))
                  .wrap());
   }

   private void trackAutoClear(WalletAnalyticsAction autoClearAction) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(autoClearAction));
   }

   public interface Screen extends WalletScreen {

      void selectedTime(long minutes);

      String getSelectedTime();
   }
}
