package com.worldventures.wallet.service.nxt.util

import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.nxt.model.MultiErrorResponse
import com.worldventures.wallet.service.nxt.model.MultiResponseBody
import java.util.HashMap

abstract class NxtRecordResponse protected constructor(
      val record: Record, nxtResponses: List<MultiResponseBody>) : NxtRecord {

   val nxtValues: MutableMap<String, String> = HashMap()
   val nxtErrors: MutableMap<String, MultiErrorResponse> = HashMap()

   val refIdPrefix: String? = record.id

   init {
      for (body in nxtResponses) {
         for ((referenceId, value, error) in body.multiResponseElements) {
            value?.let { nxtValues.put(referenceId, it) }
            error?.let { nxtErrors.put(referenceId, it) }
         }
      }
   }

   override fun getResponseErrors() = NxtBankCardHelper.getResponseErrors(this, refIdPrefix)

}
