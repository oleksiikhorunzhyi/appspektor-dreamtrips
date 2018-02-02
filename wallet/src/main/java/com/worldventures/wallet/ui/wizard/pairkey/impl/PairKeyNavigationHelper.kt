package com.worldventures.wallet.ui.wizard.pairkey.impl

import android.os.Handler
import timber.log.Timber

private typealias SuccessCallback = (smartCardId: String) -> Unit
private typealias FailedCallback = () -> Unit

abstract class PairKeyNavigationHelper {

   var successPairing: SuccessCallback? = null
   var failedPairing: FailedCallback? = null

   abstract fun onConnect(smartCardId: String)

   abstract fun onDisconnect()

   abstract fun onViewDetach()
}

private const val PAIRING_DELAY_MS = 700L

internal class PairKeyNavigationHelperImpl : PairKeyNavigationHelper() {

   private val handler = Handler()
   private var handlerSuccessMessage: Runnable? = null

   override fun onConnect(smartCardId: String) {
      val message = Runnable {
         successPairing?.invoke(smartCardId)
         Timber.d("message is sent")
         handlerSuccessMessage = null
      }
      handler.postDelayed(message, PAIRING_DELAY_MS)
      Timber.d("onConnect")
      handlerSuccessMessage = message
   }

   override fun onDisconnect() {
      Timber.d("onDisconnect")
      handler.post {
         removeSuccessMessage()
         Timber.d("message is removed")
         failedPairing?.invoke()
      }
   }

   override fun onViewDetach() {
      removeSuccessMessage()
   }

   private fun removeSuccessMessage() {
      handlerSuccessMessage?.let {
         handler.removeCallbacksAndMessages(null)
      }
   }
}
