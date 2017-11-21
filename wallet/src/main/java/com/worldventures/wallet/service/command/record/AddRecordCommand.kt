package com.worldventures.wallet.service.command.record

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.analytics.tokenization.ActionType
import com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET
import com.worldventures.wallet.domain.entity.SDKRecord
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.domain.storage.WalletStorage
import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.command.RecordListCommand
import com.worldventures.wallet.service.command.SetDefaultCardOnDeviceCommand
import com.worldventures.wallet.util.FormatException
import com.worldventures.wallet.util.WalletValidateHelper.validateCardNameOrThrow
import com.worldventures.wallet.util.WalletValidateHelper.validateCvvOrThrow
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.janet.smartcard.action.records.AddRecordAction
import io.techery.mappery.MapperyContext
import rx.Observable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@CommandAction
class AddRecordCommand private constructor(private val record: Record, private val setAsDefaultRecord: Boolean) : Command<Record>(), InjectableAction {

   @Inject
   @field:[Named(JANET_WALLET)] lateinit var janet: Janet
   @Inject lateinit var mapperyContext: MapperyContext
   @Inject lateinit var walletStorage: WalletStorage
   @Inject lateinit var recordInteractor: RecordInteractor

   fun setAsDefaultRecord() = setAsDefaultRecord

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Record>) {
      checkCardData()

      prepareRecordForLocalStorage(record)
            .flatMap { pushRecordToSmartCard(record).map { recordId -> it.copy(id = recordId) } }
            .doOnNext { this.saveRecordLocally(it) }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun prepareRecordForLocalStorage(record: Record): Observable<Record> {
      return recordInteractor.secureRecordPipe()
            .createObservableResult(SecureRecordCommand.Builder.prepareRecordForLocalStorage(record)
                  .withAnalyticsActionType(ActionType.ADD)
                  .create())
            .map { it.result }
   }

   /**
    * Record without id -> AttachCardCommand -> Record id
    */
   private fun pushRecordToSmartCard(recordForSmartCard: Record): Observable<String> {
      val sdkRecord = mapperyContext.convert(recordForSmartCard, SDKRecord::class.java)
      return janet.createPipe(AddRecordAction::class.java)
            .createObservableResult(AddRecordAction(sdkRecord)) // id should be added in AddRecordAction
            .flatMap { saveDefaultCard(it.record.id().toString()) }
   }

   private fun saveDefaultCard(recordId: String): Observable<String> {
      return if (setAsDefaultRecord) {
         recordInteractor.setDefaultCardOnDeviceCommandPipe()
               .createObservableResult(SetDefaultCardOnDeviceCommand.setAsDefault(recordId))
               .map { recordId }
      } else {
         Observable.just(recordId)
      }
   }

   private fun saveRecordLocally(record: Record) {
      Timber.d("SC_ABS_LAYER give record: %s", record)
      recordInteractor.cardsListPipe()
            .send(RecordListCommand.add(record))
   }

   @Throws(FormatException::class)
   private fun checkCardData() {
      validateCardNameOrThrow(record.nickname)
      validateCvvOrThrow(record.cvv, record.number)
   }

   class Builder {

      private lateinit var record: Record
      private lateinit var cvv: String
      private lateinit var recordName: String
      private var setAsDefaultRecord: Boolean = false

      fun setRecord(record: Record): Builder {
         this.record = record
         return this
      }

      fun setRecordName(recordName: String): Builder {
         this.recordName = recordName
         return this
      }

      fun setCvv(cvv: String): Builder {
         this.cvv = cvv
         return this
      }

      fun setSetAsDefaultRecord(setAsDefaultRecord: Boolean): Builder {
         this.setAsDefaultRecord = setAsDefaultRecord
         return this
      }

      fun create() = AddRecordCommand(record.copy(cvv = cvv, nickname = recordName), setAsDefaultRecord)
   }
}
