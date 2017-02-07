package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.PostLocationCommand;

import java.util.Timer;
import java.util.TimerTask;

import rx.subscriptions.CompositeSubscription;

public class LocationSyncManager {
   private static final long SCHEDULE_TIME = 60000L;
   private final SmartCardLocationInteractor locationInteractor;
   private final WalletNetworkService networkService;
   private final Timer syncTimer;
   private final LocationJobTimerTask syncLocationTask;
   private final CompositeSubscription networkConnectivitySubscription;

   public LocationSyncManager(SmartCardLocationInteractor locationInteractor, WalletNetworkService networkService) {
      this.locationInteractor = locationInteractor;
      this.networkService = networkService;
      this.syncTimer = new Timer();
      this.syncLocationTask = new LocationJobTimerTask();
      this.networkConnectivitySubscription = new CompositeSubscription();
   }

   public void scheduleSync() {
      if (!networkConnectivitySubscription.hasSubscriptions() || networkConnectivitySubscription.isUnsubscribed()) {
         observeNetworkConnection();
      }
      if (networkService.isAvailable()) {
         scheduleTimer();
      }
   }

   private void observeNetworkConnection() {
      networkConnectivitySubscription.add(networkService.observeConnectedState()
            .subscribe(this::handleNetworkConnectivity));
   }

   private void handleNetworkConnectivity(boolean isConnected) {
      if (isConnected) {
         scheduleTimer();
      } else {
         cancelTimer();
      }
   }

   private void scheduleTimer() {
      syncTimer.scheduleAtFixedRate(syncLocationTask, 0, SCHEDULE_TIME);
   }

   private void cancelTimer() {
      syncLocationTask.cancel();
      syncTimer.cancel();
   }

   public void cancelSync() {
      if (networkConnectivitySubscription.hasSubscriptions() && !networkConnectivitySubscription.isUnsubscribed()) {
         networkConnectivitySubscription.clear();
      }
      cancelTimer();
   }

   private class LocationJobTimerTask extends TimerTask {

      @Override
      public void run() {
         locationInteractor.postLocationPipe().send(new PostLocationCommand());
      }

      @Override
      public boolean cancel() {
         locationInteractor.postLocationPipe().cancelLatest();
         return super.cancel();
      }
   }
}
