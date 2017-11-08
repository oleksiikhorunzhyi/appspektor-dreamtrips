package com.worldventures.wallet.service.command.offline_mode;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.WalletNetworkService;
import com.worldventures.wallet.service.nxt.DetokenizeMultipleRecordsCommand;
import com.worldventures.wallet.service.nxt.NxtInteractor;
import com.worldventures.wallet.service.nxt.TokenizeMultipleRecordsCommand;
import com.worldventures.wallet.util.NetworkUnavailableException;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class SwitchOfflineModeCommand extends Command<Boolean> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject NxtInteractor nxtInteractor;
   @Inject WalletNetworkService networkService;
   @Inject RecordsStorage recordsStorage;

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      boolean offlineModeEnabled = recordsStorage.readOfflineModeState();

      List<Record> storedRecords = recordsStorage.readRecords();
      if (!storedRecords.isEmpty()) {
         if (!networkService.isAvailable()) {
            throw new NetworkUnavailableException();
         }

         Observable.just(storedRecords)
               .flatMap(records -> offlineModeEnabled ? tokenizeRecords(records) : detokenizeRecords(records))
               .doOnNext(processedRecords -> recordsStorage.saveRecords(processedRecords))
               .flatMap(processedRecords -> smartCardInteractor.offlineModeStatusPipe()
                     .createObservableResult(OfflineModeStatusCommand.save(!offlineModeEnabled)))
               .map(Command::getResult)
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         smartCardInteractor.offlineModeStatusPipe()
               .createObservableResult(OfflineModeStatusCommand.save(!offlineModeEnabled))
               .map(Command::getResult)
               .subscribe(callback::onSuccess, callback::onFail);
      }
   }

   private Observable<List<Record>> tokenizeRecords(List<? extends Record> records) {
      return nxtInteractor.tokenizeMultipleRecordsPipe()
            .createObservableResult(new TokenizeMultipleRecordsCommand((List<Record>) records, false))
            .map(Command::getResult);
   }

   private Observable<List<Record>> detokenizeRecords(List<? extends Record> records) {
      return nxtInteractor.detokenizeMultipleRecordsPipe()
            .createObservableResult(new DetokenizeMultipleRecordsCommand((List<Record>) records, false))
            .map(Command::getResult);
   }

}
