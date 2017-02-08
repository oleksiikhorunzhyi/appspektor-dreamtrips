package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.PostLocationCommand;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import rx.subscriptions.CompositeSubscription;

public class LocationSyncManager {
   private static final long SCHEDULE_TIME = 10;
   private final SmartCardLocationInteractor locationInteractor;
   private final WalletNetworkService networkService;
   private final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1);
   private final CompositeSubscription networkConnectivitySubscription;
   private ScheduledFuture scheduledLocationFuture;

   public LocationSyncManager(SmartCardLocationInteractor locationInteractor, WalletNetworkService networkService) {
      this.locationInteractor = locationInteractor;
      this.networkService = networkService;
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
      scheduledLocationFuture =
            scheduledExecutor.scheduleAtFixedRate(new LocationTask(), 0, SCHEDULE_TIME, TimeUnit.MINUTES);
   }

   private void cancelTimer() {
      scheduledLocationFuture.cancel(true);
      locationInteractor.postLocationPipe().cancelLatest();
   }

   public void cancelSync() {
      if (networkConnectivitySubscription.hasSubscriptions() && !networkConnectivitySubscription.isUnsubscribed()) {
         networkConnectivitySubscription.clear();
      }
      cancelTimer();
   }

   private class LocationTask implements Runnable {

      @Override
      public void run() {
         locationInteractor.postLocationPipe().send(new PostLocationCommand());
      }
   }
}
