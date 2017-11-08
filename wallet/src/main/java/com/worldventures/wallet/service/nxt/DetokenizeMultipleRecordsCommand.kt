package com.worldventures.wallet.service.nxt

import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.nxt.model.MultiRequestElement
import com.worldventures.wallet.service.nxt.model.MultiResponseBody
import com.worldventures.wallet.service.nxt.util.DetokenizedRecord
import com.worldventures.wallet.service.nxt.util.NxtBankCardHelper
import com.worldventures.wallet.service.nxt.util.NxtRecord
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class DetokenizeMultipleRecordsCommand(records: List<Record>, skipTokenizationErrors: Boolean) : BaseMultipleRecordsCommand(records, skipTokenizationErrors, false) {

   internal override fun prepareMultiRequestElements(record: Record): List<MultiRequestElement> {
      return NxtBankCardHelper.getDataForDetokenization(record)
   }

   internal override fun createResponseBody(records: List<Record>, nxtResponses: List<MultiResponseBody>): List<NxtRecord> {
      return records
            .map { record -> DetokenizedRecord.from(record, nxtResponses) }
   }

}