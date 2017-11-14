package com.worldventures.wallet.service

import rx.Scheduler
import rx.schedulers.Schedulers

class TestSchedulerProvider : WalletSchedulerProvider {

   override fun storageScheduler(): Scheduler = Schedulers.immediate()
}