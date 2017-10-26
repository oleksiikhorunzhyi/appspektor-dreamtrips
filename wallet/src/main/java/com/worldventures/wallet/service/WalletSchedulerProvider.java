package com.worldventures.wallet.service;

import rx.Scheduler;

public interface WalletSchedulerProvider {

   Scheduler storageScheduler();
}
