package com.worldventures.wallet.service.command.wizard

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET
import com.worldventures.wallet.domain.entity.SmartCardFirmware
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.service.RecordInteractor
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.command.FetchFirmwareVersionCommand
import com.worldventures.wallet.service.command.RecordListCommand
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.command.record.AddRecordCommand
import com.worldventures.wallet.service.command.record.DefaultRecordIdCommand
import com.worldventures.wallet.util.SCFirmwareUtils.obtainRecordVersion
import com.worldventures.wallet.util.WalletFeatureHelper
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import rx.Observable
import javax.inject.Inject
import javax.inject.Named

@CommandAction
class AddDummyRecordCommand(private val onlyToCache: Boolean = false) : Command<Void>(), InjectableAction {

   @Inject lateinit var recordInteractor: RecordInteractor
   @Inject lateinit var smartCardInteractor: SmartCardInteractor
   @Inject lateinit var featureHelper: WalletFeatureHelper
   @field:[Inject Named(JANET_WALLET)] lateinit var janet: Janet

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Void>) {
      if (!featureHelper.isSampleCardMode) {
         callback.onSuccess(null)
         return
      }
      fetchFirmwareVersion()
            .flatMap { this.createDummyCards(it) }
            .flatMap { records -> sendCard(records[0], records[1]) }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun sendCard(dummyCard1: Record, dummyCard2: Record): Observable<Void> {
      // !!!! first card should be default !!!
      return if (onlyToCache) { // because synchronization of sample card is broken
         recordInteractor.cardsListPipe()
               .createObservableResult(RecordListCommand.add(dummyCard1))
               .flatMap {
                  recordInteractor.cardsListPipe()
                        .createObservableResult(RecordListCommand.add(dummyCard2))
               }
               .flatMap {
                  recordInteractor.defaultRecordIdPipe()
                        .createObservableResult(DefaultRecordIdCommand.set(DummyRecordCreator.defaultRecordId()))
               }
               .map<Void> { null }
      } else {
         addDummyCard(dummyCard1, true)
               .flatMap { addDummyCard(dummyCard2, false) }
      }
   }

   private fun fetchFirmwareVersion(): Observable<SmartCardFirmware> {
      return janet.createPipe(FetchFirmwareVersionCommand::class.java)
            .createObservableResult(FetchFirmwareVersionCommand())
            .map({ it.result })
   }

   private fun createDummyCards(firmware: SmartCardFirmware): Observable<List<Record>> {
      val version = obtainRecordVersion(firmware.nordicAppVersion)

      return smartCardInteractor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .map { (DummyRecordCreator.createRecords(it.result, version)) }
   }

   private fun addDummyCard(dummyCard: Record, isDefault: Boolean): Observable<Void> {
      return recordInteractor.addRecordPipe()
            .createObservableResult(AddRecordCommand.Builder()
                  .setRecord(dummyCard)
                  .setCvv(dummyCard.cvv)
                  .setSetAsDefaultRecord(isDefault)
                  .setRecordName(dummyCard.nickname)
                  .create()
            )
            .map<Void> { null }
   }
}
