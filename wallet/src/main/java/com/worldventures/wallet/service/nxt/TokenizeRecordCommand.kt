package com.worldventures.wallet.service.nxt

import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.nxt.model.MultiRequestElement
import com.worldventures.wallet.service.nxt.model.MultiResponseBody
import com.worldventures.wallet.service.nxt.util.NxtBankCardHelper
import com.worldventures.wallet.service.nxt.util.NxtRecord
import com.worldventures.wallet.service.nxt.util.TokenizedRecord

import io.techery.janet.command.annotations.CommandAction

@CommandAction
class TokenizeRecordCommand(record: Record) : BaseRecordCommand(record, true) {

   internal override fun prepareMultiRequestElements(record: Record): List<MultiRequestElement> {
      return NxtBankCardHelper.getDataForTokenization(record)
   }

   internal override fun createResponseBody(record: Record, nxtResponse: MultiResponseBody): NxtRecord {
      return TokenizedRecord.from(record, nxtResponse)
   }

}
