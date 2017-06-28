package com.worldventures.dreamtrips.wallet.ui.settings.security.removecards;

import android.content.Context;
import android.os.Bundle;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.AutoClearAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.AutoClearChangedAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;
import com.worldventures.dreamtrips.wallet.ui.settings.common.provider.AutoClearSmartCardItemProvider;

import java.util.List;

import javax.inject.Inject;

public class WalletAutoClearCardsPresenter extends WalletPresenter<WalletAutoClearCardsPresenter.Screen, WalletAutoClearCardsState> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private boolean autoClearWasChanged = false;
   private final AutoClearSmartCardItemProvider itemProvider;

   public WalletAutoClearCardsPresenter(Context context, Injector injector) {
      super(context, injector);
      itemProvider = new AutoClearSmartCardItemProvider(context);
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
      getView().setItems(itemProvider.items());
      observeSmartCard();
      observeDelayChange();
   }

   @Override
   public void detachView(boolean retainInstance) {
      if (autoClearWasChanged) {
         //known problem: this action will be sent after action from onAttachView of next screen
         trackChangedDelay();
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
               bindToView(deviceStateCommand.getResult().clearFlyeDelay());
               trackScreen();
            });
   }

   private void observeDelayChange() {
      smartCardInteractor.autoClearDelayPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetAutoClearSmartCardDelayCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> {
                     bindToView(command.getResult());
                     autoClearWasChanged = true;
                  })
                  .onFail(ErrorHandler.create(getContext()))
                  .wrap());
   }

   private void bindToView(long autoClearDelay) {
      getView().setSelectedPosition(itemProvider.getPositionForValue(autoClearDelay));
   }

   private void trackChangedDelay() {
      trackAutoClear(new AutoClearChangedAction(itemProvider.item(getView().getSelectedPosition()).getText()));
   }

   private void trackScreen() {
      trackAutoClear(new AutoClearAction(itemProvider.item(getView().getSelectedPosition()).getText()));
   }

   private void trackAutoClear(WalletAnalyticsAction autoClearAction) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(autoClearAction));
   }

   public interface Screen extends WalletScreen {

      void setItems(List<SettingsRadioModel> items);

      void setSelectedPosition(int position);

      int getSelectedPosition();
   }
}
