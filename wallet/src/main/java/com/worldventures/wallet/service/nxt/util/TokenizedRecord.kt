package com.worldventures.wallet.service.nxt.util

import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.nxt.model.MultiResponseBody

class TokenizedRecord private constructor(detokenizedRecord: Record, nxtResponses: List<MultiResponseBody>)
   : NxtRecordResponse(detokenizedRecord, nxtResponses) {

   override fun getTokenizedRecord(): Record {
      return NxtBankCardHelper.getTokenizedRecord(this, refIdPrefix)
   }

   override fun getDetokenizedRecord(): Record {
      return record
   }

   companion object {

      fun from(detokenizedRecord: Record, nxtResponses: MultiResponseBody): TokenizedRecord {
         return from(detokenizedRecord, listOf(nxtResponses))
      }

      fun from(detokenizedRecord: Record, nxtResponses: List<MultiResponseBody>): TokenizedRecord {
         return TokenizedRecord(detokenizedRecord, nxtResponses)
      }
   }

}
