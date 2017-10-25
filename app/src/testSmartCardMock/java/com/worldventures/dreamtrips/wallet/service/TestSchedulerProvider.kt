package com.worldventures.dreamtrips.wallet.service

import rx.Scheduler
import rx.schedulers.Schedulers

class TestSchedulerProvider : WalletSchedulerProvider {

   override fun storageScheduler(): Scheduler = Schedulers.immediate()
}