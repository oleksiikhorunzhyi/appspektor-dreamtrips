package com.worldventures.wallet.ui.settings.general.newcard.poweron.impl;


import com.worldventures.wallet.service.FactoryResetInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WalletBluetoothService;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.service.command.reset.ResetOptions;
import com.worldventures.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.newcard.poweron.NewCardPowerOnPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.poweron.NewCardPowerOnScreen;
import com.worldventures.wallet.ui.settings.general.reset.CheckPinDelegate;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetAction;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class NewCardPowerOnPresenterImpl extends WalletPresenterImpl<NewCardPowerOnScreen> implements NewCardPowerOnPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WalletBluetoothService bluetoothService;
   private final CheckPinDelegate checkPinDelegate;

   public NewCardPowerOnPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, FactoryResetInteractor factoryResetInteractor,
         WalletAnalyticsInteractor analyticsInteractor, WalletBluetoothService bluetoothService) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.bluetoothService = bluetoothService;
      this.checkPinDelegate = new CheckPinDelegate(smartCardInteractor, factoryResetInteractor, analyticsInteractor,
            navigator, FactoryResetAction.NEW_CARD);
   }

   @Override
   public void attachView(NewCardPowerOnScreen view) {
      super.attachView(view);
      checkPinDelegate.observePinStatus(getView());
      fetchSmartCardId();
   }

   private void fetchSmartCardId() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<ActiveSmartCardCommand>()
                  .onSuccess(command -> getView().setTitleWithSmartCardID(command.getResult().smartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
            );
   }

   @Override
   public void cantTurnOnSmartCard() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<ActiveSmartCardCommand>()
                  .onSuccess(command -> getView().showConfirmationUnassignOnBackend(command.getResult().smartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
            );
   }

   @Override
   public void unassignCardOnBackend() {
      smartCardInteractor.wipeSmartCardDataPipe()
            .createObservable(new WipeSmartCardDataCommand(ResetOptions.builder()
                  .wipePaymentCards(false)
                  .wipeUserSmartCardData(false)
                  .build()))
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideWipeOperationView())
                  .onSuccess(activeSmartCardCommand -> getNavigator().goUnassignSuccess())
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
                  .create());
   }

   @Override
   public void navigateNext() {
      smartCardInteractor.deviceStatePipe()
            .createObservable(DeviceStateCommand.Companion.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> handleConnectionSmartCard(bluetoothService.isEnable(), command.getResult()
                        .getConnectionStatus()
                        .isConnected()))
                  .onFail((command, throwable) -> getNavigator().goPreCheckNewCard()));
   }

   private void handleConnectionSmartCard(boolean bluetoothIsConnected, boolean smartCardConnected) {
      if (bluetoothIsConnected && smartCardConnected) {
         checkPinDelegate.getFactoryResetDelegate().setupDelegate(getView());
      } else {
         getNavigator().goPreCheckNewCard();
      }
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   void retryFactoryReset() {
      checkPinDelegate.getFactoryResetDelegate().factoryReset();
   }

}
