package com.worldventures.wallet.service.nxt.util

import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.nxt.model.MultiResponseBody

class DetokenizedRecord private constructor(tokenizedRecord: Record, nxtResponses: List<MultiResponseBody>)
   : NxtRecordResponse(tokenizedRecord, nxtResponses) {

   override fun getTokenizedRecord(): Record {
      return record
   }

   override fun getDetokenizedRecord(): Record {
      return NxtBankCardHelper.getDetokenizedRecord(this, refIdPrefix)
   }

   companion object {

      fun from(tokenizedRecord: Record, nxtResponses: MultiResponseBody): DetokenizedRecord {
         return from(tokenizedRecord, listOf(nxtResponses))
      }

      fun from(tokenizedRecord: Record, nxtResponses: List<MultiResponseBody>): DetokenizedRecord {
         return DetokenizedRecord(tokenizedRecord, nxtResponses)
      }
   }

}
