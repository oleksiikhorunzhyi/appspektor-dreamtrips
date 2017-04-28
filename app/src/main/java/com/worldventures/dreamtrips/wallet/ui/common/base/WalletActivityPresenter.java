package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardSyncManager;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.LocationTrackingManager;

import javax.inject.Inject;

import io.techery.janet.smartcard.action.support.DisconnectAction;
import timber.log.Timber;

public class WalletActivityPresenter extends ActivityPresenter<WalletActivityPresenter.View> {

   @Inject SmartCardInteractor interactor;
   @Inject SmartCardSyncManager smartCardSyncManager; // it was init on this activity
   @Inject WalletBluetoothService bluetoothService;
   @Inject LocationTrackingManager trackingManager;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      trackingManager.track();
      interactor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand())
            .compose(bindView())
            .filter(command -> command.getResult().cardStatus().isActive())
            .flatMap(command -> interactor.connectActionPipe()
                  .createObservable(new ConnectSmartCardCommand(command.getResult().smartCardId(), false)))
            .subscribe(connectAction -> Timber.i("Success connection to smart card"), throwable -> {
            });
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
      interactor.disconnectPipe().send(new DisconnectAction());
      trackingManager.untrack();
   }

   public interface View extends ActivityPresenter.View {

      void openBluetoothSettings();
   }
}
