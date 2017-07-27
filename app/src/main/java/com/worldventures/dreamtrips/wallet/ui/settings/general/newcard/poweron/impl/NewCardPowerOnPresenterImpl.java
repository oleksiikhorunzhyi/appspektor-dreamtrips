package com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron.impl;


import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetOptions;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron.NewCardPowerOnPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron.NewCardPowerOnScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.CheckPinDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetAction;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import timber.log.Timber;

public class NewCardPowerOnPresenterImpl extends WalletPresenterImpl<NewCardPowerOnScreen> implements NewCardPowerOnPresenter {

   private final WalletBluetoothService bluetoothService;
   private final CheckPinDelegate checkPinDelegate;

   public NewCardPowerOnPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, FactoryResetInteractor factoryResetInteractor,
         AnalyticsInteractor analyticsInteractor, WalletBluetoothService bluetoothService) {
      super(navigator, smartCardInteractor, networkService);
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
      getSmartCardInteractor().activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ActiveSmartCardCommand>()
                  .onSuccess(command -> getView().setTitleWithSmartCardID(command.getResult().smartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
            );
   }

   @Override
   public void cantTurnOnSmartCard() {
      getSmartCardInteractor().activeSmartCardPipe()
            .createObservable(new ActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ActiveSmartCardCommand>()
                  .onSuccess(command -> getView().showConfirmationUnassignOnBackend(command.getResult().smartCardId()))
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
            );
   }

   @Override
   public void unassignCardOnBackend() {
      getSmartCardInteractor().wipeSmartCardDataPipe()
            .createObservable(new WipeSmartCardDataCommand(ResetOptions.builder()
                  .wipePaymentCards(false)
                  .wipeUserSmartCardData(false)
                  .build()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideWipeOperationView())
                  .onSuccess(activeSmartCardCommand -> getNavigator().goUnassignSuccess())
                  .onFail((activeSmartCardCommand, throwable) -> Timber.e(throwable, ""))
                  .create());
   }

   @Override
   public void navigateNext() {
      getSmartCardInteractor().deviceStatePipe()
            .createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> handleConnectionSmartCard(bluetoothService.isEnable(), command.getResult()
                        .connectionStatus()
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
