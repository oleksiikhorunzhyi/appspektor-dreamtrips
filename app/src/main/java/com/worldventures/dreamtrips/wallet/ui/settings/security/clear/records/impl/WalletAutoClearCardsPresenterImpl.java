package com.worldventures.dreamtrips.wallet.ui.settings.security.clear.records.impl;

import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.AutoClearAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.AutoClearChangedAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.common.items.AutoClearSmartCardItemProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.common.items.SettingsRadioModel;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.records.WalletAutoClearCardsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.records.WalletAutoClearCardsScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;

public class WalletAutoClearCardsPresenterImpl extends WalletPresenterImpl<WalletAutoClearCardsScreen> implements WalletAutoClearCardsPresenter {

   private final WalletAnalyticsInteractor analyticsInteractor;
   private final AutoClearSmartCardItemProvider itemProvider;

   public WalletAutoClearCardsPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, WalletAnalyticsInteractor analyticsInteractor, AutoClearSmartCardItemProvider autoClearSmartCardItemProvider) {
      super(navigator, smartCardInteractor, networkService);
      this.analyticsInteractor = analyticsInteractor;
      this.itemProvider = autoClearSmartCardItemProvider;
   }

   @Override
   public void attachView(WalletAutoClearCardsScreen view) {
      super.attachView(view);
      getView().setItems(itemProvider.items());
      observeSmartCard();
      observeDelayChange();
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   /**
    * @param delayMinutes 0 - never
    */
   @Override
   public void onTimeSelected(long delayMinutes) {
      getSmartCardInteractor().autoClearDelayPipe().send(new SetAutoClearSmartCardDelayCommand(delayMinutes));
   }

   private void observeSmartCard() {
      getSmartCardInteractor().deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(deviceStateCommand -> {
               bindToView(deviceStateCommand.getResult().clearFlyeDelay());
               trackScreen();
            });
   }

   private void observeDelayChange() {
      getSmartCardInteractor().autoClearDelayPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().<SetAutoClearSmartCardDelayCommand>provideOperationView())
                  .onSuccess(command -> {
                     bindToView(command.getResult());
                     getView().setDelayWasChanged(true);
                  })
                  .create());
   }

   private void bindToView(long autoClearDelay) {
      getView().setSelectedPosition(itemProvider.getPositionForValue(autoClearDelay));
   }

   @Override
   public void trackChangedDelay() {
      final SettingsRadioModel selectedDelay = itemProvider.item(getView().getSelectedPosition());
      trackAutoClear(new AutoClearChangedAction(selectedDelay.getText()));
   }

   private void trackScreen() {
      final SettingsRadioModel selectedDelay = itemProvider.item(getView().getSelectedPosition());
      trackAutoClear(new AutoClearAction(selectedDelay.getText()));
   }

   private void trackAutoClear(WalletAnalyticsAction autoClearAction) {
      analyticsInteractor.walletAnalyticsPipe().send(new WalletAnalyticsCommand(autoClearAction));
   }
}
