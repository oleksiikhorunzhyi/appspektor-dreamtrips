package com.worldventures.wallet.service.lostcard;


import com.worldventures.wallet.service.SmartCardLocationInteractor;
import com.worldventures.wallet.service.lostcard.command.PostLocationCommand;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class LocationSyncManager {
   private static final long SCHEDULE_TIME = 10;
   private final SmartCardLocationInteractor locationInteractor;
   private final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1);
   private ScheduledFuture scheduledLocationFuture;

   LocationSyncManager(SmartCardLocationInteractor locationInteractor) {
      this.locationInteractor = locationInteractor;
   }

   public void scheduleSync() {
      if (scheduledLocationFuture == null
            || scheduledLocationFuture.isCancelled()
            || scheduledLocationFuture.isDone()) {
         scheduledLocationFuture
               = scheduledExecutor.scheduleAtFixedRate(new LocationTask(), 0, SCHEDULE_TIME, TimeUnit.MINUTES);
      }
   }

   void cancelSync() {
      if (scheduledLocationFuture != null) {
         scheduledLocationFuture.cancel(true);
      }
      locationInteractor.postLocationPipe().cancelLatest();
   }

   private class LocationTask implements Runnable {

      @Override
      public void run() {
         locationInteractor.postLocationPipe().send(new PostLocationCommand());
      }
   }
}
