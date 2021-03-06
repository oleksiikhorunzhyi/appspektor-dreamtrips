package com.worldventures.wallet.ui.settings.security.clear.default_card.impl;

import com.worldventures.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.settings.DisableDefaultAction;
import com.worldventures.wallet.analytics.settings.DisableDefaultChangedAction;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.security.clear.common.items.DisableDefaultCardItemProvider;
import com.worldventures.wallet.ui.settings.security.clear.common.items.SettingsRadioModel;
import com.worldventures.wallet.ui.settings.security.clear.default_card.WalletDisableDefaultCardPresenter;
import com.worldventures.wallet.ui.settings.security.clear.default_card.WalletDisableDefaultCardScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class WalletDisableDefaultCardPresenterImpl extends WalletPresenterImpl<WalletDisableDefaultCardScreen> implements WalletDisableDefaultCardPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final DisableDefaultCardItemProvider itemProvider;

   public WalletDisableDefaultCardPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WalletAnalyticsInteractor analyticsInteractor, DisableDefaultCardItemProvider itemProvider) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.itemProvider = itemProvider;
   }

   @Override
   public void attachView(WalletDisableDefaultCardScreen view) {
      super.attachView(view);
      getView().setItems(itemProvider.items());
      fetchSmartCard();
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
      smartCardInteractor.disableDefaultCardDelayPipe().send(new SetDisableDefaultCardDelayCommand(delayMinutes));
   }

   private void fetchSmartCard() {
      smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.Companion.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(command -> {
               bindToView(command.getResult().getDisableCardDelay());
               trackScreen();
            });
   }

   private void observeDelayChange() {
      smartCardInteractor.disableDefaultCardDelayPipe()
            .observe()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().<SetDisableDefaultCardDelayCommand>provideOperationView())
                  .onSuccess(command -> getView().notifyDataIsSaved())
                  .create());
   }

   private void bindToView(long disableCardDelay) {
      final int position = itemProvider.getPositionForValue(disableCardDelay);
      getView().setSelectedPosition(position);
   }

   public void trackScreen() {
      final SettingsRadioModel selectedDelay = itemProvider.item(getView().getSelectedPosition());
      trackDisableDelay(new DisableDefaultAction(selectedDelay.getText()));
   }

   @Override
   public void trackChangedDelay() {
      final SettingsRadioModel selectedDelay = itemProvider.item(getView().getSelectedPosition());
      trackDisableDelay(new DisableDefaultChangedAction(selectedDelay.getText()));
   }

   private void trackDisableDelay(WalletAnalyticsAction disableCardDelayAction) {
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(disableCardDelayAction));
   }
}
