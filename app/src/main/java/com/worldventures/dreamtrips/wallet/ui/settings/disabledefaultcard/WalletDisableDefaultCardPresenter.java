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
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;
import com.worldventures.dreamtrips.wallet.ui.settings.common.provider.DisableDefaultCardItemProvider;

import java.util.List;

import javax.inject.Inject;

public class WalletDisableDefaultCardPresenter extends WalletPresenter<WalletDisableDefaultCardPresenter.Screen, WalletDisableDefaultCardState> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final DisableDefaultCardItemProvider itemProvider;
   private boolean delayWasChanged = false;

   public WalletDisableDefaultCardPresenter(Context context, Injector injector) {
      super(context, injector);
      itemProvider = new DisableDefaultCardItemProvider(context);
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
   }

   @Override
   public void onSaveInstanceState(Bundle bundle) {
      state.setDelayWasChanged(delayWasChanged);
      super.onSaveInstanceState(bundle);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getView().setItems(itemProvider.items());
      fetchSmartCard();
      observeDelayChange();
   }

   @Override
   public void detachView(boolean retainInstance) {
      if (delayWasChanged) {
         //known problem: this action will be sent after action from onAttachView of next screen
         trackChangedDelay();
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

   private void fetchSmartCard() {
      smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> {
               bindToView(command.getResult().disableCardDelay());
               trackScreen();
            });
   }

   private void observeDelayChange() {
      smartCardInteractor.disableDefaultCardDelayPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<SetDisableDefaultCardDelayCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> {
                     bindToView(command.getResult());
                     delayWasChanged = true;
                  })
                  .onFail(ErrorHandler.create(getContext()))
                  .wrap());
   }

   private void bindToView(long disableCardDelay) {
      final int position = itemProvider.getPositionForValue(disableCardDelay);
      getView().setSelectedPosition(position);
   }

   public void trackScreen() {
      trackDisableDelay(new DisableDefaultAction(itemProvider.item(getView().getSelectedPosition()).getText()));
   }

   public void trackChangedDelay() {
      trackDisableDelay(new DisableDefaultChangedAction(itemProvider.item(getView().getSelectedPosition()).getText()));
   }

   private void trackDisableDelay(WalletAnalyticsAction disableCardDelayAction) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(disableCardDelayAction));
   }

   public interface Screen extends WalletScreen {

      void setItems(List<SettingsRadioModel> items);

      void setSelectedPosition(int position);

      int getSelectedPosition();
   }
}
