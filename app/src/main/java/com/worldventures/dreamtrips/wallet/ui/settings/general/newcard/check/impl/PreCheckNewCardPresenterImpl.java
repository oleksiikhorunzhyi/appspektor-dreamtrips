package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check.impl;


import android.util.Pair;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.BluetoothDisabledAction;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.BluetoothEnabledAction;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.SmartCartWillNowBeAssignedAction;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check.PreCheckNewCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check.PreCheckNewCardScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.CheckPinDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetAction;

import java.util.concurrent.TimeUnit;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class PreCheckNewCardPresenterImpl extends WalletPresenterImpl<PreCheckNewCardScreen> implements PreCheckNewCardPresenter {

   private final WalletBluetoothService bluetoothService;
   private final AnalyticsInteractor analyticsInteractor;
   private final CheckPinDelegate checkPinDelegate;

   public PreCheckNewCardPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor,
         FactoryResetInteractor factoryResetInteractor, WalletBluetoothService bluetoothService) {
      super(navigator, smartCardInteractor, networkService);
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
            getSmartCardInteractor().deviceStatePipe()
                  .observeSuccess()
                  .throttleLast(300, TimeUnit.MILLISECONDS),
            bluetoothService.observeEnablesState()
                  .startWith(bluetoothService.isEnable()),
            (smartCardCommand, bluetoothIsEnabled) -> new Pair<>(bluetoothIsEnabled, smartCardCommand.getResult()))
            .compose(bindViewIoToMainComposer())
            .subscribe(pair -> bind(pair.first, pair.second.connectionStatus()
                  .isConnected()), throwable -> Timber.e(throwable, ""));

      bluetoothService.observeEnablesState()
            .startWith(bluetoothService.isEnable())
            .take(1)
            .subscribe(bluetoothEnabled -> sendAnalyticAction(bluetoothEnabled
                  ? new BluetoothEnabledAction()
                  : new BluetoothDisabledAction()));

      getSmartCardInteractor().deviceStatePipe().send(DeviceStateCommand.fetch());
   }

   private void bind(boolean bluetoothIsEnabled, boolean smartCardConnected) {
      getView().bluetoothEnable(bluetoothIsEnabled);
      getView().setVisiblePowerSmartCardWidget(bluetoothIsEnabled);
      getView().cardConnected(smartCardConnected);
      getView().nextButtonEnabled(bluetoothIsEnabled && smartCardConnected);
   }

   @Override
   public void prepareContinueAddCard() {
      getSmartCardInteractor().activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ActiveSmartCardCommand>()
                  .onSuccess(command -> getView().showAddCardContinueDialog(command.getResult().smartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
            );
   }

   @Override
   public void navigateNext() {
      sendAnalyticAction(new SmartCartWillNowBeAssignedAction());
      checkPinDelegate.getFactoryResetDelegate().setupDelegate(getView());
   }

   private void sendAnalyticAction(WalletAnalyticsAction action) {
      analyticsInteractor
            .walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(action));
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   void retryFactoryReset() {
      checkPinDelegate.getFactoryResetDelegate().factoryReset();
   }
}
