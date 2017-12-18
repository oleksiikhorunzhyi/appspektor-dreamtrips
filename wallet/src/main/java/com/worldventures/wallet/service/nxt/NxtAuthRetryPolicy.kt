package com.worldventures.wallet.service.nxt

import com.worldventures.wallet.domain.session.NxtSessionHolder
import com.worldventures.wallet.service.nxt.model.MultiErrorResponse
import com.worldventures.wallet.service.nxt.model.NxtSession

private const val NXT_SESSION_ERROR_1 = 11001
private const val NXT_SESSION_ERROR_2 = 11002
private const val NXT_SESSION_ERROR_3 = 11003
private const val NXT_SESSION_ERROR_4 = 11004

class NxtAuthRetryPolicy(private val nxtSessionHolder: NxtSessionHolder) {
   private val sessionErrorCodes = listOf(NXT_SESSION_ERROR_1, NXT_SESSION_ERROR_2, NXT_SESSION_ERROR_3, NXT_SESSION_ERROR_4)

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

   fun handle(createNxtSessionCall: () -> NxtSession?): Boolean = handle(null, createNxtSessionCall)

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

   private fun shouldRetry(apiError: MultiErrorResponse?) = isLoginError(apiError) || !isCredentialExists

   private fun isLoginError(apiError: MultiErrorResponse?) = apiError != null && sessionErrorCodes.contains(apiError.code)
}
