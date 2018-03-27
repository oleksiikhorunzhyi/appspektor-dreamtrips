package com.worldventures.wallet.service.beacon

import org.slf4j.LoggerFactory
import timber.log.Timber

class WalletBeaconLoggerSlf4j : WalletBeaconLogger {

   override fun logBeacon(s: String, vararg args: Any?) {
      try {
         var str = s
         if (args.isNotEmpty()) {
            str = s.format(args)
         }
         Timber.d("%s :: %s", TAG, str)
         FILE_LOGGER.debug(str)
      } catch (throwable: Throwable) {
         Timber.e(throwable, TAG)
      }
   }

   companion object {
      private val TAG = "Beacon client"
      private val FILE_LOGGER = LoggerFactory.getLogger(TAG)
   }
}
