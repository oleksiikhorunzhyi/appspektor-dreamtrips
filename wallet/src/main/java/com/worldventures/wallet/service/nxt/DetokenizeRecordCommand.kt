package com.worldventures.wallet.service.nxt

import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.nxt.model.MultiRequestElement
import com.worldventures.wallet.service.nxt.model.MultiResponseBody
import com.worldventures.wallet.service.nxt.util.DetokenizedRecord
import com.worldventures.wallet.service.nxt.util.NxtBankCardHelper
import com.worldventures.wallet.service.nxt.util.NxtRecord

import io.techery.janet.command.annotations.CommandAction

@CommandAction
class DetokenizeRecordCommand(record: Record) : BaseRecordCommand(record, false) {

   internal override fun prepareMultiRequestElements(record: Record): List<MultiRequestElement> {
      return NxtBankCardHelper.getDataForDetokenization(record)
   }

   internal override fun createResponseBody(record: Record, nxtResponse: MultiResponseBody): NxtRecord {
      return DetokenizedRecord.from(record, nxtResponse)
   }
}