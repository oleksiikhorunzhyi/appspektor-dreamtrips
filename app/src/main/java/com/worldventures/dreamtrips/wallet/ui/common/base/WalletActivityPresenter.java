package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardSyncManager;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.LocationTrackingManager;

import javax.inject.Inject;

import io.techery.janet.smartcard.action.support.DisconnectAction;
import timber.log.Timber;

public class WalletActivityPresenter extends ActivityPresenter<ActivityPresenter.View> {

   @Inject SmartCardInteractor interactor;
   @Inject SmartCardSyncManager smartCardSyncManager;
   @Inject LocationTrackingManager trackingManager;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      smartCardSyncManager.connect();

      interactor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand())
            .flatMap(command -> interactor.connectActionPipe()
                  .createObservable(new ConnectSmartCardCommand(command.getResult(), false)))
            .subscribe(connectAction -> {
               Timber.i("Success connection to smart card");
               checkEnableTracking();
            }, throwable -> {
            });
   }

   private void checkEnableTracking() {
      trackingManager.checkEnableTracking()
            .subscribe(enabled -> {
               if (enabled) {
                  trackingManager.track();
               }
            }, throwable -> Timber.e(throwable, ""));
   }

   @Override
   public void dropView() {
      super.dropView();
      interactor.disconnectPipe().send(new DisconnectAction());
      trackingManager.untrack();
   }
}
