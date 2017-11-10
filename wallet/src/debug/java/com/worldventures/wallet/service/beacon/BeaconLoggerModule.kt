package com.worldventures.wallet.service.beacon

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(library = true)
class BeaconLoggerModule {

   @Provides
   @Singleton
   fun provideBeaconLogger(): WalletBeaconLogger = StubWalletBeaconLogger()
}