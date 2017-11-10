package com.worldventures.wallet.service.command.offline_mode

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.domain.storage.disk.RecordsStorage
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletNetworkService
import com.worldventures.wallet.service.nxt.DetokenizeMultipleRecordsCommand
import com.worldventures.wallet.service.nxt.NxtInteractor
import com.worldventures.wallet.service.nxt.TokenizeMultipleRecordsCommand
import com.worldventures.wallet.util.NetworkUnavailableException
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable
import javax.inject.Inject

@CommandAction
class SwitchOfflineModeCommand : Command<Boolean>(), InjectableAction {

   @Inject lateinit var smartCardInteractor: SmartCardInteractor
   @Inject lateinit var nxtInteractor: NxtInteractor
   @Inject lateinit var networkService: WalletNetworkService
   @Inject lateinit var recordsStorage: RecordsStorage

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Boolean>) {
      val offlineModeEnabled = recordsStorage.readOfflineModeState()

      val storedRecords = recordsStorage.readRecords()
      if (!storedRecords.isEmpty()) {
         if (!networkService.isAvailable) {
            throw NetworkUnavailableException()
         }

         Observable.just(storedRecords)
               .flatMap { records -> if (offlineModeEnabled) tokenizeRecords(records) else detokenizeRecords(records) }
               .doOnNext { recordsStorage.saveRecords(it) }
               .flatMap {
                  smartCardInteractor.offlineModeStatusPipe()
                        .createObservableResult(OfflineModeStatusCommand.save(!offlineModeEnabled))
               }
               .map { it.result }
               .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
      } else {
         smartCardInteractor.offlineModeStatusPipe()
               .createObservableResult(OfflineModeStatusCommand.save(!offlineModeEnabled))
               .map{ it.result }
               .subscribe( { callback.onSuccess(it) }, { callback.onFail(it) })
      }
   }

   private fun tokenizeRecords(records: List<Record>): Observable<List<Record>> {
      return nxtInteractor.tokenizeMultipleRecordsPipe()
            .createObservableResult(TokenizeMultipleRecordsCommand(records, false))
            .map{ it.result }
   }

   private fun detokenizeRecords(records: List<Record>): Observable<List<Record>> {
      return nxtInteractor.detokenizeMultipleRecordsPipe()
            .createObservableResult(DetokenizeMultipleRecordsCommand(records, false))
            .map{ it.result }
   }

}
