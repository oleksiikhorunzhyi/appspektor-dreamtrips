package com.worldventures.wallet.ui.settings.general.newcard.check.impl;


import android.util.Pair;

import com.worldventures.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.new_smartcard.BluetoothDisabledAction;
import com.worldventures.wallet.analytics.new_smartcard.BluetoothEnabledAction;
import com.worldventures.wallet.analytics.new_smartcard.SmartCartWillNowBeAssignedAction;
import com.worldventures.wallet.service.FactoryResetInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WalletBluetoothService;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.newcard.check.PreCheckNewCardPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.check.PreCheckNewCardScreen;
import com.worldventures.wallet.ui.settings.general.reset.CheckPinDelegate;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetAction;

import java.util.concurrent.TimeUnit;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class PreCheckNewCardPresenterImpl extends WalletPresenterImpl<PreCheckNewCardScreen> implements PreCheckNewCardPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WalletBluetoothService bluetoothService;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final CheckPinDelegate checkPinDelegate;

   public PreCheckNewCardPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WalletAnalyticsInteractor analyticsInteractor,
         FactoryResetInteractor factoryResetInteractor, WalletBluetoothService bluetoothService) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.bluetoothService = bluetoothService;
      this.analyticsInteractor = analyticsInteractor;
      checkPinDelegate = new CheckPinDelegate(smartCardInteractor, factoryResetInteractor,
            analyticsInteractor, navigator, FactoryResetAction.NEW_CARD);
   }

   @Override
   public void attachView(PreCheckNewCardScreen view) {
      super.attachView(view);
      checkPinDelegate.observePinStatus(getView());
      observeChecks();
   }

   private void observeChecks() {
      Observable.combineLatest(
            smartCardInteractor.deviceStatePipe()
                  .observeSuccess()
                  .throttleLast(300, TimeUnit.MILLISECONDS),
            bluetoothService.observeEnablesState()
                  .startWith(bluetoothService.isEnable()),
            (smartCardCommand, bluetoothIsEnabled) -> new Pair<>(bluetoothIsEnabled, smartCardCommand.getResult()))
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(pair -> bind(pair.first, pair.second.getConnectionStatus().isConnected()), Timber::e);

      bluetoothService.observeEnablesState()
            .startWith(bluetoothService.isEnable())
            .take(1)
            .subscribe(bluetoothEnabled -> sendAnalyticAction(bluetoothEnabled
                  ? new BluetoothEnabledAction()
                  : new BluetoothDisabledAction()));

      smartCardInteractor.deviceStatePipe().send(DeviceStateCommand.Companion.fetch());
   }

   private void bind(boolean bluetoothIsEnabled, boolean smartCardConnected) {
      getView().bluetoothEnable(bluetoothIsEnabled);
      getView().setVisiblePowerSmartCardWidget(bluetoothIsEnabled);
      getView().cardConnected(smartCardConnected);
      getView().nextButtonEnabled(bluetoothIsEnabled && smartCardConnected);
   }

   @Override
   public void prepareContinueAddCard() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<ActiveSmartCardCommand>()
                  .onSuccess(command -> getView().showAddCardContinueDialog(command.getResult().getSmartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable))
            );
   }

   @Override
   public void navigateNext() {
      sendAnalyticAction(new SmartCartWillNowBeAssignedAction());
      checkPinDelegate.getFactoryResetDelegate().setupDelegate(getView());
   }

   private void sendAnalyticAction(WalletAnalyticsAction action) {
      analyticsInteractor
            .walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(action));
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
