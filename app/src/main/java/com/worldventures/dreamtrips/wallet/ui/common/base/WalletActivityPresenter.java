package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.wallet.analytics.general.SmartCardAnalyticErrorHandler;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardSyncManager;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import timber.log.Timber;

public class WalletActivityPresenter extends ActivityPresenter<WalletActivityPresenter.View> {

   // Initialization
   @Inject SmartCardSyncManager smartCardSyncManager;
   @Inject SmartCardAnalyticErrorHandler smartCardAnalyticErrorHandler;
   //

   @Inject SmartCardInteractor interactor;
   @Inject WalletBluetoothService bluetoothService;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      interactor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand())
            .compose(bindView())
            .map(Command::getResult)
            .filter(smartCard -> smartCard.cardStatus().isActive())
            .flatMap(smartCard -> interactor.connectActionPipe()
                  .createObservable(new ConnectSmartCardCommand(smartCard.smartCardId())))
            .subscribe(connectAction -> Timber.i("Success connection to Smart Card"),
                  throwable -> Timber.e(throwable, "Connect to Smart Card on Wallet enter"));
   }

   @Override
   public void onStart() {
      super.onStart();
      startBluetoothTracking();
   }

   private void startBluetoothTracking() {
      bluetoothService.observeEnablesState()
            .startWith(bluetoothService.isEnable())
            .compose(bindUntilStop())
            .distinctUntilChanged()
            .subscribe(this::onBluetoothStateChanged);
   }

   private void onBluetoothStateChanged(boolean state) {
      if (!state && view != null) view.openBluetoothSettings();
   }

   @Override
   public void dropView() {
      super.dropView();
      auxiliaryDisconnectSmartCard();
   }

   private void auxiliaryDisconnectSmartCard() {
      interactor.deviceStatePipe().createObservableResult(DeviceStateCommand.fetch())
            .map(DeviceStateCommand::getResult)
            .filter(smartCardStatus -> smartCardStatus.connectionStatus().isConnected())
            .subscribe(smartCardStatus -> interactor.disconnectPipe().send(new DisconnectAction()),
                  throwable -> Timber.e(throwable, "Disconnect on Wallet exit"));
   }

   public interface View extends ActivityPresenter.View {

      void openBluetoothSettings();
   }
}
