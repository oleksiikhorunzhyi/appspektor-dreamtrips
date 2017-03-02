package com.worldventures.dreamtrips.wallet.ui.settings.disabledefaultcard;

import android.content.Context;
import android.os.Bundle;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.DisableDefaultAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.DisableDefaultChangedAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class WalletDisableDefaultCardPresenter extends WalletPresenter<WalletDisableDefaultCardPresenter.Screen, WalletDisableDefaultCardState> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   private boolean delayWasChanged = false;
   private long newDisableDelay;

   public WalletDisableDefaultCardPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   // View State
   @Override
   public void onNewViewState() {
      state = new WalletDisableDefaultCardState();
   }

   @Override
   public void applyViewState() {
      super.applyViewState();
      delayWasChanged = state.delayWasChanged();
      newDisableDelay = state.getNewDisableDelay();
   }

   @Override
   public void onSaveInstanceState(Bundle bundle) {
      state.setDelayWasChanged(delayWasChanged);
      state.setNewDisableDelay(newDisableDelay);
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
      if (delayWasChanged) {
         trackDisableDelay(new DisableDefaultChangedAction(newDisableDelay));
      }
      super.detachView(retainInstance);
   }

   void goBack() {
      navigator.goBack();
   }

   /**
    * @param delayMinutes 0 - never
    */
   void onTimeSelected(long delayMinutes) {
      smartCardInteractor.disableDefaultCardDelayPipe().send(new SetDisableDefaultCardDelayCommand(delayMinutes));
   }

   private void observeSmartCard() {
      smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(deviceStateCommand -> {
               final long disableCardDelay = deviceStateCommand.getResult().disableCardDelay();
               getView().selectedTime(disableCardDelay);
               trackDisableDelay(new DisableDefaultAction(disableCardDelay));
            });
   }

   private void observeDelayChange() {
      smartCardInteractor.disableDefaultCardDelayPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetDisableDefaultCardDelayCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> {
                     final long disableCardDelay = command.getResult();
                     getView().selectedTime(disableCardDelay);
                     delayWasChanged = true;
                     newDisableDelay = disableCardDelay;
                  })
                  .onFail(ErrorHandler.create(getContext()))
                  .wrap());
   }

   private void trackDisableDelay(WalletAnalyticsAction disableCardDelayAction) {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(disableCardDelayAction);
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   public interface Screen extends WalletScreen {

      void selectedTime(long minutes);
   }
}
