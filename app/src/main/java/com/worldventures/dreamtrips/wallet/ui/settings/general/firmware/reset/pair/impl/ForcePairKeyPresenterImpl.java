package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair.impl;

import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.firmware.command.ConnectForFirmwareUpdate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair.ForcePairKeyPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair.ForcePairKeyScreen;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;

public class ForcePairKeyPresenterImpl extends WalletPresenterImpl<ForcePairKeyScreen> implements ForcePairKeyPresenter {

   private final FirmwareInteractor firmwareInteractor;

   public ForcePairKeyPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, FirmwareInteractor firmwareInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.firmwareInteractor = firmwareInteractor;
   }

   @Override
   public void attachView(ForcePairKeyScreen view) {
      super.attachView(view);
      observeCreateAndConnectSmartCard();
   }

   private void observeCreateAndConnectSmartCard() {
      firmwareInteractor.connectForFirmwareUpdatePipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(firmwareInteractor.connectForFirmwareUpdatePipe()))
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationConnect())
                  .onSuccess(command -> smartCardConnected())
                  .create());
   }

   @Override
   public void tryToPairAndConnectSmartCard() {
      firmwareInteractor.connectForFirmwareUpdatePipe().send(new ConnectForFirmwareUpdate());
   }

   private void smartCardConnected() {
      getNavigator().goNewFirmwareAvailable();
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
