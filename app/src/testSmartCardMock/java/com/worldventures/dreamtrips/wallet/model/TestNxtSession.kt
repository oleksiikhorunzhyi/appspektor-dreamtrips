package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.service.nxt.model.NxtSession

class TestNxtSession(private val nxtSessionToken: String) : NxtSession {

   override fun token(): String = nxtSessionToken

}