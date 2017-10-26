package com.worldventures.wallet.service;

import java.util.concurrent.Executors;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class WalletSchedulerProviderImpl implements WalletSchedulerProvider {

   @Override
   public Scheduler storageScheduler() {
      return Schedulers.from(Executors.newSingleThreadExecutor());
   }
}
