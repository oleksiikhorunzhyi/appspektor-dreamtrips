package com.worldventures.wallet.service.command.http

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.record.FinancialService
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.util.WalletRecordUtil
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import javax.inject.Inject

@CommandAction
class CreateRecordCommand(private val swipedCard: io.techery.janet.smartcard.model.Record) : Command<Record>(), InjectableAction {

   @Inject lateinit var mappery: MapperyContext

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Record>) {
      val record = mappery.convert(swipedCard, Record::class.java)
      callback.onSuccess(withExtraInfo(record))
   }

   private fun withExtraInfo(record: Record): Record {
      return if (WalletRecordUtil.isAmexBank(record.number)) {
         record.copy(financialService = FinancialService.AMEX)
      } else record
   }
}