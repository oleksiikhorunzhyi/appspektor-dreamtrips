package com.worldventures.dreamtrips.wallet.service.lostcard;

import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocationType;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.WalletLocationCommand;

import java.util.concurrent.TimeUnit;

import rx.subscriptions.CompositeSubscription;

public class LostCardManager {

   private final SmartCardLocationInteractor locationInteractor;
   private final LocationSyncManager jobScheduler;
   private final CompositeSubscription subscriptions;

   public LostCardManager(SmartCardLocationInteractor locationInteractor, LocationSyncManager jobScheduler) {
      this.locationInteractor = locationInteractor;
      this.jobScheduler = jobScheduler;
      this.subscriptions = new CompositeSubscription();
   }

   public void connect() {
      if (!subscriptions.hasSubscriptions() || subscriptions.isUnsubscribed()) {
         observeConnection();
      }
      jobScheduler.scheduleSync();
   }

   private void observeConnection() {
      subscriptions.add(locationInteractor.connectActionPipe()
            .observeSuccess()
            .debounce(1, TimeUnit.SECONDS)
            .subscribe(connectAction ->  triggerLocation(WalletLocationType.CONNECT)));

      subscriptions.add(locationInteractor.disconnectPipe()
            .observeSuccess()
            .subscribe(disconnectAction ->  triggerLocation(WalletLocationType.DISCONNECT)));
   }

   private void triggerLocation(WalletLocationType locationType) {
      locationInteractor.walletLocationCommandPipe().send(new WalletLocationCommand(locationType));
   }

   public void disconnect() {
      if (subscriptions.hasSubscriptions() && !subscriptions.isUnsubscribed()) {
         subscriptions.clear();
      }
      jobScheduler.cancelSync();
   }
}
