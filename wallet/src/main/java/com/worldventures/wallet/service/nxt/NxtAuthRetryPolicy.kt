package com.worldventures.wallet.service.nxt

import com.worldventures.wallet.domain.session.NxtSessionHolder
import com.worldventures.wallet.service.nxt.model.MultiErrorResponse
import com.worldventures.wallet.service.nxt.model.NxtSession

class NxtAuthRetryPolicy(private val nxtSessionHolder: NxtSessionHolder) {
   private val sessionErrorCodes = listOf(11001, 11002, 10003, 10004)

   private val isCredentialExists: Boolean
      get() {
         val nxtSessionOptional = nxtSessionHolder.get()
         return if (nxtSessionOptional.isPresent) {
            val (token) = nxtSessionHolder.get().get()
            token.isNotEmpty()
         } else {
            false
         }
      }

   fun handle(createNxtSessionCall: () -> NxtSession?): Boolean {
      return handle(null, createNxtSessionCall)
   }

   fun handle(apiError: MultiErrorResponse?, createNxtSessionCall: () -> NxtSession?): Boolean {
      if (shouldRetry(apiError)) {
         val nxtSession = createNxtSessionCall.invoke()
         if (nxtSession != null) {
            handleSession(nxtSession)
            return true
         }
      }
      return false
   }

   private fun handleSession(nxtSession: NxtSession) {
      nxtSessionHolder.put(nxtSession)
   }

   private fun shouldRetry(apiError: MultiErrorResponse?): Boolean {
      return isLoginError(apiError) || !isCredentialExists
   }

   private fun isLoginError(apiError: MultiErrorResponse?): Boolean {
      return apiError != null && sessionErrorCodes.contains(apiError.code)
   }
}
