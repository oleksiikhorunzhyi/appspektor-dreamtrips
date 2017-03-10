package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.domain.entity.record.Record
import com.worldventures.dreamtrips.wallet.service.nxt.model.ImmutableMultiResponseBody
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtRecordResponse

/**
 * Created by shliama on 2/13/17.
 */
class TestNxtRecord(record: Record,
                    response: List<MultiResponseBody> = mutableListOf(ImmutableMultiResponseBody.builder().build()),
                    redIdPrefix: String? = null)
   : NxtRecordResponse(record, response, redIdPrefix) {

   override fun getTokenizedRecord(): Record = record

   override fun getDetokenizedRecord(): Record = record

}