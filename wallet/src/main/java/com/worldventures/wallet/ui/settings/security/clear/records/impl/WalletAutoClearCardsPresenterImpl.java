package com.worldventures.wallet.ui.settings.security.clear.records.impl;

import com.worldventures.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.settings.AutoClearAction;
import com.worldventures.wallet.analytics.settings.AutoClearChangedAction;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.security.clear.common.items.AutoClearSmartCardItemProvider;
import com.worldventures.wallet.ui.settings.security.clear.common.items.SettingsRadioModel;
import com.worldventures.wallet.ui.settings.security.clear.records.WalletAutoClearCardsPresenter;
import com.worldventures.wallet.ui.settings.security.clear.records.WalletAutoClearCardsScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class WalletAutoClearCardsPresenterImpl extends WalletPresenterImpl<WalletAutoClearCardsScreen> implements WalletAutoClearCardsPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final AutoClearSmartCardItemProvider itemProvider;

   public WalletAutoClearCardsPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WalletAnalyticsInteractor analyticsInteractor,
         AutoClearSmartCardItemProvider autoClearSmartCardItemProvider) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
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
      smartCardInteractor.autoClearDelayPipe().send(new SetAutoClearSmartCardDelayCommand(delayMinutes));
   }

   private void observeSmartCard() {
      smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.Companion.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(deviceStateCommand -> {
               bindToView(deviceStateCommand.getResult().getClearFlyeDelay());
               trackScreen();
            });
   }

   private void observeDelayChange() {
      smartCardInteractor.autoClearDelayPipe()
            .observe()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
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
