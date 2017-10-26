package com.worldventures.wallet.model

import com.worldventures.wallet.service.nxt.model.NxtSession

class TestNxtSession(private val nxtSessionToken: String) : NxtSession {

   override fun token(): String = nxtSessionToken

}