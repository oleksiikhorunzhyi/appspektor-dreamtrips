package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseElement

class TestDisassociateResponseBody: MultiResponseBody {

   override fun multiResponseElements(): MutableList<MultiResponseElement> {
      return mutableListOf()
   }
}