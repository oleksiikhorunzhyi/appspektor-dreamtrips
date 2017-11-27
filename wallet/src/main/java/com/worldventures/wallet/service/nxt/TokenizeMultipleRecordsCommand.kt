package com.worldventures.wallet.service.nxt

import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.nxt.model.MultiRequestElement
import com.worldventures.wallet.service.nxt.model.MultiResponseBody
import com.worldventures.wallet.service.nxt.util.NxtBankCardHelper
import com.worldventures.wallet.service.nxt.util.NxtRecord
import com.worldventures.wallet.service.nxt.util.TokenizedRecord
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class TokenizeMultipleRecordsCommand(records: List<Record>, skipTokenizationErrors: Boolean) : BaseMultipleRecordsCommand(records, skipTokenizationErrors, true) {

   override fun prepareMultiRequestElements(record: Record): List<MultiRequestElement> {
      return NxtBankCardHelper.getDataForTokenization(record)
   }

   override fun createResponseBody(records: List<Record>, nxtResponses: List<MultiResponseBody>): List<NxtRecord> {
      return records
            .map { record -> TokenizedRecord.from(record, nxtResponses) }
   }

}
