package com.worldventures.dreamtrips.wallet.service.lostcard;

import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocationType;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.WalletLocationCommand;

import rx.subscriptions.CompositeSubscription;

public class LostCardManager {

   private final SmartCardLocationInteractor locationInteractor;
   private final LocationSyncManager jobScheduler;
   private final WalletNetworkService networkService;
   private final CompositeSubscription subscriptions;

   public LostCardManager(SmartCardLocationInteractor locationInteractor, LocationSyncManager jobScheduler, WalletNetworkService networkService) {
      this.locationInteractor = locationInteractor;
      this.jobScheduler = jobScheduler;
      this.networkService = networkService;
      this.subscriptions = new CompositeSubscription();
   }

   public void connect() {
      if (!subscriptions.hasSubscriptions() || subscriptions.isUnsubscribed()) {
         observeNetworkConnection();
         observeConnection();
      }
      if (networkService.isAvailable()) {
         jobScheduler.scheduleSync();
      }
   }

   private void observeNetworkConnection() {
      subscriptions.add(networkService.observeConnectedState()
            .subscribe(this::handleNetworkConnectivity));
   }

   private void handleNetworkConnectivity(boolean isNetworkConnected) {
      if (isNetworkConnected) {
         jobScheduler.scheduleSync();
      } else {
         jobScheduler.cancelSync();
      }
   }

   private void observeConnection() {
      subscriptions.add(locationInteractor.connectActionPipe()
            .observeSuccess()
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
