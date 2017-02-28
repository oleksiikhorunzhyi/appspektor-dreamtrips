package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiErrorResponse
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseElement

class TestMultiResponseBody(
      private val number: String = "1234123412341234",
      private val cvv: String = "123",
      private val track1: String = "mag_stripe_data_1",
      private val track2: String = "mag_stripe_data_2",
      private val track3: String = "mag_stripe_data_3"
) : MultiResponseBody {

   override fun multiResponseElements(): MutableList<MultiResponseElement> = mutableListOf(
         TestMultiResponseElement(number, "number"),
         TestMultiResponseElement(cvv, "cvv"),
         TestMultiResponseElement(track1, "track1"),
         TestMultiResponseElement(track2, "track2"),
         TestMultiResponseElement(track3, "track3")
   )

   class TestMultiResponseElement(private val value: String, private val refId: String, private val error: MultiErrorResponse? = null) : MultiResponseElement {
      override fun value(): String = value
      override fun referenceId(): String = refId
      override fun error(): MultiErrorResponse? = error
   }

}