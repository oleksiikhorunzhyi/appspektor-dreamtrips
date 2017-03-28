package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiErrorResponse
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseElement
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper

class TestMultiResponseBody(
      private val recordIds: List<String?>,
      private val number: String = TEST_NUMBER,
      private val cvv: String = TEST_CVV,
      private val track1: String = TEST_MAG_STRIPE_DATA,
      private val track2: String = TEST_MAG_STRIPE_DATA,
      private val track3: String = TEST_MAG_STRIPE_DATA
) : MultiResponseBody {

   companion object {
      val TEST_NUMBER: String = "9876987698769876"
      val TEST_CVV: String = "987"
      val TEST_MAG_STRIPE_DATA: String = "mag_stripe_data"
   }

//   override fun multiResponseElements(): MutableList<MultiResponseElement> = mutableListOf(
//         TestMultiResponseElement(number, "number"),
//         TestMultiResponseElement(cvv, "cvv"),
//         TestMultiResponseElement(track1, "track1"),
//         TestMultiResponseElement(track2, "track2"),
//         TestMultiResponseElement(track3, "track3")
//   )


   override fun multiResponseElements(): MutableList<MultiResponseElement> {
      val responseElements = mutableListOf<MultiResponseElement>()

      recordIds.forEach { refIdPrefix ->
         responseElements.addAll(
               mutableListOf(
                     TestMultiResponseElement(number, NxtBankCardHelper.prefixRefId(NxtBankCardHelper.PAN, refIdPrefix)),
                     TestMultiResponseElement(cvv, NxtBankCardHelper.prefixRefId(NxtBankCardHelper.CVV, refIdPrefix)),
                     TestMultiResponseElement(track1, NxtBankCardHelper.prefixRefId(NxtBankCardHelper.TRACK_1, refIdPrefix)),
                     TestMultiResponseElement(track2, NxtBankCardHelper.prefixRefId(NxtBankCardHelper.TRACK_2, refIdPrefix)),
                     TestMultiResponseElement(track3, NxtBankCardHelper.prefixRefId(NxtBankCardHelper.TRACK_3, refIdPrefix))
               )
         )
      }
      return responseElements
   }

   class TestMultiResponseElement(private val value: String, private val refId: String, private val error: MultiErrorResponse? = null) : MultiResponseElement {
      override fun value(): String = value
      override fun referenceId(): String = refId
      override fun error(): MultiErrorResponse? = error
   }

}