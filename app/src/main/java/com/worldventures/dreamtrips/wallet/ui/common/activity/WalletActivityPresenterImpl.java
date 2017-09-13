package com.worldventures.dreamtrips.wallet.ui.common.activity;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.worldventures.dreamtrips.modules.auth.api.command.LogoutCommand;
import com.worldventures.dreamtrips.modules.common.service.LogoutInteractor;
import com.worldventures.dreamtrips.wallet.analytics.general.SmartCardAnalyticErrorHandler;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardSyncManager;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;

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
   private final LogoutInteractor logoutInteractor;

   public WalletActivityPresenterImpl(SmartCardSyncManager smartCardSyncManager, SmartCardAnalyticErrorHandler smartCardAnalyticErrorHandler,
         SmartCardInteractor interactor, WalletBluetoothService bluetoothService, LogoutInteractor logoutInteractor) {
      this.smartCardSyncManager = smartCardSyncManager;
      this.smartCardAnalyticErrorHandler = smartCardAnalyticErrorHandler;
      this.interactor = interactor;
      this.bluetoothService = bluetoothService;
      this.logoutInteractor = logoutInteractor;
   }

   @Override
   public void attachView(WalletActivityView view) {
      interactor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand())
            .compose(view.bindToLifecycle())
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
      logoutInteractor.logoutPipe().send(new LogoutCommand());
   }

   @Override
   public void detachView(boolean retainInstance) {
      auxiliaryDisconnectSmartCard();
   }

   private void onBluetoothStateChanged(boolean state) {
      if (!state && getView() != null) getView().openBluetoothSettings();
   }

   private void auxiliaryDisconnectSmartCard() {
      interactor.deviceStatePipe().createObservableResult(DeviceStateCommand.fetch())
            .map(DeviceStateCommand::getResult)
            .filter(smartCardStatus -> smartCardStatus.connectionStatus().isConnected())
            .subscribe(smartCardStatus -> interactor.disconnectPipe().send(new DisconnectAction()),
                  throwable -> Timber.e(throwable, "Disconnect on Wallet exit"));
   }
}
