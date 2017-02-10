package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.PostLocationCommand;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LocationSyncManager {
   private static final long SCHEDULE_TIME = 10;
   private final SmartCardLocationInteractor locationInteractor;
   private final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1);
   private ScheduledFuture scheduledLocationFuture;

   public LocationSyncManager(SmartCardLocationInteractor locationInteractor) {
      this.locationInteractor = locationInteractor;
   }

   public void scheduleSync() {
      if(scheduledLocationFuture == null
            || scheduledLocationFuture.isCancelled()
            || scheduledLocationFuture.isDone()) {
         scheduledLocationFuture =
               scheduledExecutor.scheduleAtFixedRate(new LocationTask(), 0, SCHEDULE_TIME, TimeUnit.MINUTES);
      }
   }

   public void cancelSync() {
      scheduledLocationFuture.cancel(true);
      locationInteractor.postLocationPipe().cancelLatest();
   }

   private class LocationTask implements Runnable {

      @Override
      public void run() {
         locationInteractor.postLocationPipe().send(new PostLocationCommand());
      }
   }
}
