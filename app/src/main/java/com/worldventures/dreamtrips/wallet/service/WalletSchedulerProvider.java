package com.worldventures.dreamtrips.wallet.service;

import rx.Scheduler;

public interface WalletSchedulerProvider {

   Scheduler storageScheduler();
}
