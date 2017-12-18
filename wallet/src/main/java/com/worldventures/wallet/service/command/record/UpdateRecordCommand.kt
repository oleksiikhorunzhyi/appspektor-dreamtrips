package com.worldventures.wallet.service.command.record

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.analytics.tokenization.ActionType
import com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET
import com.worldventures.wallet.domain.entity.SDKRecord
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.command.RecordListCommand
import com.worldventures.wallet.util.FormatException
import com.worldventures.wallet.util.WalletValidateHelper.validateCardNameOrThrow
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.janet.smartcard.action.records.EditRecordAction
import io.techery.mappery.MapperyContext
import rx.Observable
import javax.inject.Inject
import javax.inject.Named

@CommandAction
class UpdateRecordCommand private constructor(val record: Record) : Command<Void>(), InjectableAction {

   @Inject
   @field:[Named(JANET_WALLET)] lateinit var janet: Janet
   @Inject lateinit var analyticsInteractor: WalletAnalyticsInteractor
   @Inject lateinit var mapperyContext: MapperyContext
   @Inject lateinit var recordInteractor: RecordInteractor

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Void>) {
      checkCardData()
      prepareRecordForSmartCard(record)
            .flatMap { this.pushRecord(it) }
            .map<Void> { null }
            .doOnNext { updateLocalRecord() }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   @Throws(FormatException::class)
   private fun checkCardData() {
      validateCardNameOrThrow(record.nickname)
   }

   private fun prepareRecordForSmartCard(record: Record): Observable<SDKRecord> {
      return recordInteractor.secureRecordPipe()
            .createObservableResult(SecureRecordCommand.Builder.prepareRecordForSmartCard(record)
                  .withAnalyticsActionType(ActionType.UPDATE)
                  .create())
            .map { mapperyContext.convert(it.result, SDKRecord::class.java) }
   }

   private fun pushRecord(record: SDKRecord): Observable<Record> {
      return janet.createPipe(EditRecordAction::class.java)
            .createObservableResult(EditRecordAction(record))
            .map { result -> mapperyContext.convert(result.record, Record::class.java) }
   }

   private fun updateLocalRecord() {
      recordInteractor.cardsListPipe().send(RecordListCommand.edit(record))
   }

   companion object {

      fun updateNickname(record: Record, nickname: String): UpdateRecordCommand {
         return UpdateRecordCommand(record.copy(nickname = nickname))
      }
   }

}
