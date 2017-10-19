package com.worldventures.dreamtrips.wallet.ui.settings.security.clear.default_card.impl;

import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.DisableDefaultAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.DisableDefaultChangedAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.common.items.DisableDefaultCardItemProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.common.items.SettingsRadioModel;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.default_card.WalletDisableDefaultCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.default_card.WalletDisableDefaultCardScreen;

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
            .createObservableResult(DeviceStateCommand.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(command -> {
               bindToView(command.getResult().disableCardDelay());
               trackScreen();
            });
   }

   private void observeDelayChange() {
      smartCardInteractor.disableDefaultCardDelayPipe()
            .observe()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().<SetDisableDefaultCardDelayCommand>provideOperationView())
                  .onSuccess(command -> {
                     bindToView(command.getResult());
                     getView().setDelayWasChanged(true);
                  })
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
