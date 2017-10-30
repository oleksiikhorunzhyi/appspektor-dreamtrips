package com.worldventures.wallet.ui.common.activity;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.worldventures.core.modules.auth.api.command.LogoutCommand;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.wallet.analytics.general.SmartCardAnalyticErrorHandler;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.SmartCardSyncManager;
import com.worldventures.wallet.service.WalletBluetoothService;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;

import io.techery.janet.Command;
import io.techery.janet.smartcard.action.support.DisconnectAction;
import rx.Observable;
import timber.log.Timber;

public class WalletActivityPresenterImpl extends MvpBasePresenter<WalletActivityView> implements WalletActivityPresenter {

   // Initialization begin
   @SuppressWarnings("all") private final SmartCardSyncManager smartCardSyncManager;
   @SuppressWarnings("all") private final SmartCardAnalyticErrorHandler smartCardAnalyticErrorHandler;
   //  Initialization end

   private final SmartCardInteractor interactor;
   private final WalletBluetoothService bluetoothService;
   private final AuthInteractor authInteractor;

   public WalletActivityPresenterImpl(SmartCardSyncManager smartCardSyncManager, SmartCardAnalyticErrorHandler smartCardAnalyticErrorHandler,
         SmartCardInteractor interactor, WalletBluetoothService bluetoothService, AuthInteractor authInteractor) {
      this.smartCardSyncManager = smartCardSyncManager;
      this.smartCardAnalyticErrorHandler = smartCardAnalyticErrorHandler;
      this.interactor = interactor;
      this.bluetoothService = bluetoothService;
      this.authInteractor = authInteractor;
   }

   @Override
   public void attachView(WalletActivityView view) {
      interactor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand())
            .compose(view.bindUntilDetach())
            .map(Command::getResult)
            .filter(smartCard -> smartCard.cardStatus().isActive())
            .flatMap(smartCard -> interactor.connectActionPipe()
                  .createObservable(new ConnectSmartCardCommand(smartCard.smartCardId())))
            .subscribe(connectAction -> Timber.i("Success connection to Smart Card"),
                  throwable -> Timber.e(throwable, "Connect to Smart Card on Wallet enter"));
   }

   public void bindToBluetooth(Observable<Void> terminateObservable) {
      bluetoothService.observeEnablesState()
            .startWith(bluetoothService.isEnable())
            .takeUntil(terminateObservable)
            .distinctUntilChanged()
            .subscribe(this::onBluetoothStateChanged);
   }

   public void logout() {
      authInteractor.logoutPipe().send(new LogoutCommand());
   }

   @Override
   public void detachView() {
      auxiliaryDisconnectSmartCard();
   }

   private void onBluetoothStateChanged(boolean state) {
      if (!state && getView() != null) {
         getView().openBluetoothSettings();
      }
   }

   private void auxiliaryDisconnectSmartCard() {
      interactor.deviceStatePipe().createObservableResult(DeviceStateCommand.fetch())
            .map(DeviceStateCommand::getResult)
            .filter(smartCardStatus -> smartCardStatus.connectionStatus().isConnected())
            .subscribe(smartCardStatus -> interactor.disconnectPipe().send(new DisconnectAction()),
                  throwable -> Timber.e(throwable, "Disconnect on Wallet exit"));
   }
}
