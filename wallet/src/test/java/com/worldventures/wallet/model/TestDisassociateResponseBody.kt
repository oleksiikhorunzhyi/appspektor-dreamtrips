package com.worldventures.wallet.model

import com.worldventures.wallet.service.nxt.model.MultiResponseBody
import com.worldventures.wallet.service.nxt.model.MultiResponseElement

class TestDisassociateResponseBody : MultiResponseBody {

   override fun multiResponseElements(): MutableList<MultiResponseElement> {
      return mutableListOf()
   }
}
