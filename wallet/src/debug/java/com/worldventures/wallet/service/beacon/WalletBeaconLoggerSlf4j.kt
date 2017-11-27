package com.worldventures.wallet.service.beacon

import org.slf4j.LoggerFactory
import timber.log.Timber

class WalletBeaconLoggerSlf4j : WalletBeaconLogger {

   private val TAG = "Beacon client"
   private val FILE_LOGGER = LoggerFactory.getLogger(TAG)

   override fun logBeacon(s: String, vararg args: Any?) {
      var str = s
      if (args.isNotEmpty()) {
         str = String.format(s, args)
      }
      Timber.d("%s :: %s", TAG, str)
      FILE_LOGGER.debug(str)
   }
}
