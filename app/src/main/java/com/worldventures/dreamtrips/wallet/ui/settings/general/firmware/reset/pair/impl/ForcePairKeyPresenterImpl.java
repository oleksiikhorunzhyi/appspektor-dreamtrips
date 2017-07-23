package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair.impl;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.firmware.command.ConnectForFirmwareUpdate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair.ForcePairKeyPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair.ForcePairKeyScreen;
import com.worldventures.dreamtrips.wallet.util.SmartCardConnectException;

public class ForcePairKeyPresenterImpl extends WalletPresenterImpl<ForcePairKeyScreen> implements ForcePairKeyPresenter {

   private final FirmwareInteractor firmwareInteractor;
   private final ErrorHandlerFactory errorHandlerFactory;

   public ForcePairKeyPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, FirmwareInteractor firmwareInteractor, ErrorHandlerFactory errorHandlerFactory) {
      super(navigator, smartCardInteractor, networkService);
      this.firmwareInteractor = firmwareInteractor;
      this.errorHandlerFactory = errorHandlerFactory;
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
            .subscribe(OperationActionStateSubscriberWrapper.<ConnectForFirmwareUpdate>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> smartCardConnected())
                  .onFail(errorHandlerFactory.<ConnectForFirmwareUpdate>builder()
                        .handle(SmartCardConnectException.class, R.string.wallet_smartcard_connection_error)
                        .defaultAction(command -> goBack())
                        .build())
                  .wrap());
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
