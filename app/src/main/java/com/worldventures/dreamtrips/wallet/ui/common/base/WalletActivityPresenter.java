package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.os.Bundle;

import com.techery.spares.session.NxtSessionHolder;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardSyncManager;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateNxtSessionCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.NxtInteractor;

import javax.inject.Inject;

import io.techery.janet.smartcard.action.support.DisconnectAction;
import timber.log.Timber;

public class WalletActivityPresenter extends ActivityPresenter<WalletActivityPresenter.View> {

   @Inject SmartCardInteractor interactor;
   @Inject NxtInteractor nxtInteractor;
   @Inject SmartCardSyncManager smartCardSyncManager;
   @Inject NxtSessionHolder nxtSessionHolder;
   @Inject WalletBluetoothService bluetoothService;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      smartCardSyncManager.connect();

      interactor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand())
            .compose(bindView())
            .flatMap(command -> interactor.connectActionPipe()
                  .createObservable(new ConnectSmartCardCommand(command.getResult(), false)))
            .subscribe(connectAction -> Timber.i("Success connection to smart card"), throwable -> {
            });

      createNxtSessionIfNeeded();
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      startBluetoothTracking();
   }

   @Override
   public void dropView() {
      super.dropView();
      interactor.disconnectPipe().send(new DisconnectAction());
   }

   private void startBluetoothTracking() {
      bluetoothService.observeEnablesState()
            .startWith(bluetoothService.isEnable())
            .compose(bindView())
            .distinctUntilChanged()
            .subscribe(this::onBluetoothStateChanged);
   }

   private void onBluetoothStateChanged(boolean state) {
      if (!state && view != null) view.openBluetoothSettings();
   }

   private void createNxtSessionIfNeeded() {
      if (nxtSessionHolder.get() == null || !nxtSessionHolder.get().isPresent()) {
         nxtInteractor.createNxtSessionPipe().send(new CreateNxtSessionCommand());
      }
   }


   public interface View extends ActivityPresenter.View {

      void openBluetoothSettings();
   }

}