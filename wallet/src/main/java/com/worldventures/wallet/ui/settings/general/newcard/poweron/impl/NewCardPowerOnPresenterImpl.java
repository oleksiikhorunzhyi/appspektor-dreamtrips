package com.worldventures.wallet.ui.settings.general.newcard.poweron.impl;

import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletBluetoothService;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.service.command.reset.ResetOptions;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.newcard.poweron.NewCardPowerOnPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.poweron.NewCardPowerOnScreen;
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetDelegate;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class NewCardPowerOnPresenterImpl extends WalletPresenterImpl<NewCardPowerOnScreen> implements NewCardPowerOnPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WalletBluetoothService bluetoothService;
   private final FactoryResetDelegate factoryResetDelegate;

   public NewCardPowerOnPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WalletBluetoothService bluetoothService, FactoryResetDelegate factoryResetDelegate) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.bluetoothService = bluetoothService;
      this.factoryResetDelegate = factoryResetDelegate;
   }

   @Override
   public void attachView(NewCardPowerOnScreen view) {
      super.attachView(view);
      factoryResetDelegate.bindView(view);
      fetchSmartCardId();
   }

   private void fetchSmartCardId() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<ActiveSmartCardCommand>()
                  .onSuccess(command -> getView().setTitleWithSmartCardID(command.getResult().getSmartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable))
            );
   }

   @Override
   public void cantTurnOnSmartCard() {
      smartCardInteractor.activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<ActiveSmartCardCommand>()
                  .onSuccess(command -> getView().showConfirmationUnassignOnBackend(command.getResult()
                        .getSmartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable))
            );
   }

   @Override
   public void unassignCardOnBackend() {
      factoryResetDelegate.factoryReset(ResetOptions.builder()
            .wipePaymentCards(false)
            .wipeUserSmartCardData(false)
            .smartCardIsAvailable(false)
            .build());
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
         factoryResetDelegate.startRegularFactoryReset();
      } else {
         getNavigator().goPreCheckNewCard();
      }
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
